package homeautomation.circularblue.com.homeautomation;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.wifi.*;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.socket.emitter.Emitter;

public class ConfigHub extends AppCompatActivity{

    private static final int PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION = 0;
    WifiManager mWifiManager;
    ListView wifiScanResList;
    WifiArrayAdapter wifiArrayAdapter;
    ArrayList<String> wifi_results;
    ArrayList<Float> wifi_results_strength;
    DialogProgress dialogProgress;
    boolean wasEnabled;
    Toolbar myToolbar;
    CountDownTimer countDownTimer=null;
    int connectionToEspTries=0;
    String url ="http://192.168.4.1";
    boolean reponse_recived=false;
    private Response.Listener<String> mListener=null;
    private Response.Listener<Boolean> mListener_bool=null;
    LocationManager locationManager;

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_WIFI = 0x12345;

    public String getCurrentSSID() {
        return current_ssid;
    }

    public void setCurrentSSID(String current_ssid) {
        this.current_ssid = current_ssid;
    }

    String current_ssid;
 //   ProgressDialog pd;

    public int getWifiType() {
        return wifi_type;
    }

    public void setWifiType(int wifi_type) {
        //set either ESP or home wifi
        this.wifi_type = wifi_type;
    }
    int wifi_type;
    Singleton singleton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_hub);

        init();
        showSelectWifiDialog();
//        pd.setMessage("Please wait");
//        pd.show()
        singleton.showProgressDialog(ConfigHub.this);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
   }
   protected void onStart(){
       super.onStart();
   }
    protected void onResume(){
        super.onResume();
        registerReceiver(mWifiScanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        startScan();
        init_socket_on_events();
    }
    protected void onPause(){
        super.onPause();
        unregisterReceiver(mWifiScanReceiver);
        socket_off_events();
    }
    protected void onStop(){
        super.onStop();
    }
    public boolean onKeyDown(int keyCode, KeyEvent event){

        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
            mWifiManager.setWifiEnabled(wasEnabled);
            this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_wifi_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.action_refresh:
                startScan();
                break;
            default:
                break;
        }
        return true;
    }
    private final BroadcastReceiver mWifiScanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent intent) {
            if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                final List<ScanResult> mScanResults = mWifiManager.getScanResults();
//                if(mScanResults.isEmpty()){
//                }else{
//                    Log.d("debug","SIZE : " +String.valueOf(mScanResults.size()));
//                }

//                dialogProgress.dismiss();
                // add your logic here
                wifi_results.clear();

                Iterator<ScanResult> wifiList = mScanResults.iterator();


                while (wifiList.hasNext()){

                    ScanResult scanResult =   wifiList.next();
                    String ssid = scanResult.SSID;
                    int level = WifiManager.calculateSignalLevel(scanResult.level, 4);

                    Log.d("debug","LEVEL = " + level);

                    try{
                        String sub_ssid = ssid.substring(0,4);
                        if(wifi_type==R.string.wifi_type_esp){
                            if( ssid.length() > 3  && sub_ssid.equals(getString(R.string.wifi_type_esp))){
                                Log.d("debug",ssid);
                                wifi_results.add(ssid);
                            }
                        }else{
                            if( ssid.length() > 0 && !sub_ssid.equals("EDGE")){
                                Log.d("debug",ssid);
                                wifi_results.add(ssid);
                            }
                        }
                    }catch (Exception e){

                    }
                }
                wifiArrayAdapter = new WifiArrayAdapter(ConfigHub.this, wifi_results);
                wifiScanResList.setAdapter(wifiArrayAdapter);
                wifiScanResList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        String ssid = ((TextView) parent.getChildAt(position).findViewById(R.id.textview_ssid)).getText().toString().trim();
                     //   Toast.makeText(ConfigHub.this, "ssid: " + ssid, Toast.LENGTH_SHORT).show();
                        setCurrentSSID(ssid);
                       // Toast.makeText(ConfigHub.this, "SSID :" + getCurrentSSID(), Toast.LENGTH_SHORT).show();
                        if(wifi_type==R.string.wifi_type_esp){
                            connect_wifi(getCurrentSSID(),"thereisnospoon");
                        }else{
                            showPasswordDialog();
                        }
                    }
                });
            }
            if( wifiScanResList.getCount()==0){

                if(!isLocationEnabled() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    locationDialog();

                }else {
                    noWifiNetworksDialog();
                }
            }
            //           pd.hide();
            singleton.hideProgressDialog();
        }
    };
    private void init(){
        init_toolbar();
        init_views();
        init_vars();
        init_wifi();
  }
    private void init_socket_on_events(){
        singleton.socket.on(SocketEvents.EVENT_ADD_DEVICE, new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                final String[] success = {null};
                final String[] message = {null};
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject json = (JSONObject) args[0];
                        try {
                            success[0] = json.getString("success");
                            message[0] = json.getString("message");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                       if(success[0].equals("true")){
                        //   Toast.makeText(ConfigHub.this, "Hub added", Toast.LENGTH_SHORT).show();
                           connectionToEspTries=0;
                           DevicesScreen();
                       }else{
                           singleton.showProgressDialog(ConfigHub.this);
                           Toast.makeText(ConfigHub.this, "connection to Edge", Toast.LENGTH_SHORT).show();
                       }
                    }
                });
            }
        });
    }
    private void socket_off_events(){
        singleton.socket.off(SocketEvents.EVENT_ADD_DEVICE);
    }
    private void init_toolbar(){
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
//        myToolbar.setTitle("Home Wifi");
        getSupportActionBar().setTitle("Select Home Wifi");
    }
    private void init_views(){
        wifiScanResList = (ListView)findViewById(R.id.wifiScanResList);
    }
    private void init_vars(){
        wifi_results= new ArrayList<String>();
        singleton = Singleton.getInstance();
  //      pd = new ProgressDialog(ConfigHub.this);//,R.style.CustomProgressDialog);
    }
    private void init_wifi(){

        setWifiType(R.string.wifi_type_none);
        mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        enable_wifi();
    }
    private void enable_wifi(){
        wasEnabled = mWifiManager.isWifiEnabled();
        mWifiManager.setWifiEnabled(true);
        while(!mWifiManager.isWifiEnabled()){}
    }
    private void connectToHomeWifi(Response.Listener<Boolean> listener) {

//        final ProgressDialog p = new ProgressDialog(ConfigHub.this);
//        p.show();
        singleton.showProgressDialog(ConfigHub.this);

        mListener_bool = listener;
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                String ssid="_NA_";
                try {
                    ssid = mWifiManager.getConnectionInfo().getSSID().toString().trim().substring(1, mWifiManager.getConnectionInfo().getSSID().toString().trim().length() - 1);
                }catch (Exception e){}

                if(ssid.equals(singleton.homeWifiDetails.getSSID())){
                        mListener_bool.onResponse(true);
//                        p.hide();
                //    singleton.hideProgressDialog();
                }
            }
        }, 5000);
        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = String.format("\"%s\"", singleton.homeWifiDetails.getSSID());
        wifiConfig.preSharedKey = String.format("\"%s\"", singleton.homeWifiDetails.getPassword());
        mWifiManager.disconnect();
        int netId = mWifiManager.addNetwork(wifiConfig);
        mWifiManager.enableNetwork(netId, true);
        mWifiManager.reconnect();
    }
//    public void connectToEsp(){
//
//                        if(!singleton.hasHomeWifiDetails()){
//                            Toast.makeText(this, "Connect to ESP: Wifi Details not found", Toast.LENGTH_SHORT).show();
//                            return;
//                        }
//
//                        singleton.esp.connect(ConfigHub.this, new Response.Listener<String>() {
//                            @Override
//                            public void onResponse(String response) {
//                                Log.d("debug","response" + response);
//                                if(response!=null)
//                                {
//                                    singleton.showProgressDialog(ConfigHub.this);
//                                    singleton.esp.sendWiFiDetails(ConfigHub.this, singleton.homeWifiDetails.getSSID(), singleton.homeWifiDetails.getPassword(), new Response.Listener<String>() {
//                                        @Override
//                                        public void onResponse(String response) {
//                                            if (response != null) {
//                                            //    Toast.makeText(ConfigHub.this, response, Toast.LENGTH_SHORT).show();
//                                                singleton.setHub(response.trim());
//                                                if(singleton.hasHub()){
//                                                    connectToHomeWifi( new Response.Listener<Boolean>() {
//                                                            @Override
//                                                            public void onResponse(Boolean response) {
//                                                                if(response.equals(true)){
//                                                                    connectToHub();
//                                                                }
//                                                            }
//                                                    });
//                                                }
//                                            } else {
//                                                Toast.makeText(ConfigHub.this, "Connect to ESP->Send wifi details: response is null ", Toast.LENGTH_SHORT).show();
//                                            }
//                                        }
//                                    });
//                                }else{
//                                    if(connectionToEspTries<3){
//                                        connectionToEspTries++;
//                                        connect_wifi(getCurrentSSID(),"thereisnospoon");
//                                    }else {
//                                        connectionToEspTries=0;
//                                        cantConnectedToEspAlert();
//                                    }
//                                 //   Toast.makeText(ConfigHub.this, "Connect to ESP: response is null", Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        });
//    }
    private void connect_wifi(String ssid,String key){
//        singleton.setProgressDialogHidable(false);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                String ssid="_NA_";
                try {
                    ssid = mWifiManager.getConnectionInfo().getSSID().toString().trim().substring(1, mWifiManager.getConnectionInfo().getSSID().toString().trim().length() - 1);
                }catch (Exception e){}
                if(ssid.equals(getCurrentSSID())) {
                    Toast.makeText(ConfigHub.this, "User Authenticated", Toast.LENGTH_SHORT).show();
                    singleton.hideProgressDialog();
                    Intent intent = new Intent(ConfigHub.this,HubId_Dialog.class);
                    startActivityForResult(intent,3);
//                    singleton.setProgressDialogHidable(true);
//                    wifiScanResList.clearChoices();
//                    if (getWifiType() == R.string.wifi_type_none) {
//                        //    setWifiType(R.string.wifi_type_esp);
//                        //   showSelectEspDialog();
//                    } else {
//                        // connectToEsp();
//                    }
                }
//                else{
//
//                    if(getWifiType()!=R.string.wifi_type_esp){
//                        singleton.removeHomeWifiDetails();
//                    }
//                    singleton.setProgressDialogHidable(true);
//                    cantConnectToNetworkDialog();
           //         Toast.makeText(ConfigHub.this, "Unable to connect", Toast.LENGTH_SHORT).show();
//                    mWifiManager.reconnect();
//                    pd.hide();
              //      singleton.hideProgressDialog();
//                }
            }
        }, 10000);

//         singleton.showProgressDialog(ConfigHub.this);
        mWifiManager.disconnect();
//        int netId_= mWifiManag er.getConnectionInfo().getNetworkId();
//        mWifiManager.removeNetwork(netId_);
        mWifiManager.saveConfiguration();
        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = String.format("\"%s\"", ssid);
        wifiConfig.preSharedKey = String.format("\"%s\"", key);
//        Toast.makeText(this, "SSID: " + ssid + " PASW: " + key , Toast.LENGTH_SHORT).show();
        int netId = mWifiManager.addNetwork(wifiConfig);
        mWifiManager.enableNetwork(netId, true);
        mWifiManager.reconnect();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String password =  data.getStringExtra("password");
                singleton.setHomeWifiDetails(getCurrentSSID(),password);
                connect_wifi(getCurrentSSID(),password);
            }
        }
        if(requestCode == 1){
            if(resultCode == RESULT_OK){
                wifiScanResList.clearChoices();
                startScan();
            }
        }
        if(requestCode == 2){
            if(resultCode == RESULT_OK){
                startScan();
                getSupportActionBar().setTitle("Select Device Wifi");
            }
        }
        if(requestCode == 3){
            if(resultCode == RESULT_OK){
                String deviceId =  data.getStringExtra("deviceId");
                if(!deviceId.isEmpty()) {
                    Toast.makeText(this, deviceId, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this,ConnectToESP_Dialog.class);
                    intent.putExtra("HubId",deviceId);
                    startActivity(intent);
                }else {
//                    Toast.makeText(this, deviceId, Toast.LENGTH_SHORT).show();
                }

            }
        }
    }
    private void showPasswordDialog(){
        Intent intent = new Intent(ConfigHub.this,DialogWifi.class);
        startActivityForResult(intent,0);
    }
    private void showSelectWifiDialog() {
        Intent intent = new Intent(ConfigHub.this, select_wifi_dialog.class);
        startActivityForResult(intent, 1);
    }
    private void showSelectEspDialog() {
//       if(pd.isShowing()) {
//           pd.hide();
//       }
      //  singleton.hideProgressDialog();
        Intent intent = new Intent(ConfigHub.this, select_esp_dialog.class);
        startActivityForResult(intent, 2);
    }
    private void askPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_WIFI);

        } else {
            startScan();
        }
    }

    private void startScan(){
//        askPermission();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},MY_PERMISSIONS_REQUEST_ACCESS_WIFI);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        }else{
            mWifiManager.startScan();
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                singleton.showProgressDialog(ConfigHub.this);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_WIFI: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! do the
                    // calendar task you need to do.

                } else {

                    ConfigScreen();

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'switch' lines to check for other
            // permissions this app might request
        }
    }
    private  void DevicesScreen(){
        socket_off_events();
//        if(pd.isShowing()){
//            pd.hide();
//        }
      //  singleton.hideProgressDialog();
        if(countDownTimer!=null) {
            countDownTimer.cancel();
        }
        Intent intent = new Intent(ConfigHub.this,Devices_Activity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
    private void ConfigScreen(){
        Intent intent = new Intent(ConfigHub.this,Config_activity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
    private void connectToHub(){

        Log.d("debug","connect to hub");


        countDownTimer =  new CountDownTimer(120*1000, 1*1000) {

            public void onTick(long millisUntilFinished) {
//                Log.d("debug",String.valueOf(millisUntilFinished));
                Log.d("debug","seconds remaining: " + millisUntilFinished / 1000);

                long secondsRemaining = millisUntilFinished / 1000;
                if((secondsRemaining%5) == 0){
                   // Toast.makeText(ConfigHub.this, "Trying to add device", Toast.LENGTH_SHORT).show();
                    SocketEvents.ADD_DEVICE();
                }
            }

            public void onFinish() {
                Log.d("debug","Countdown finished");
                Toast.makeText(ConfigHub.this, "Can't connect to the hub", Toast.LENGTH_SHORT).show();

            }
        }.start();
    }
    private void cantConnectedToEspAlert(){

        new AlertDialog.Builder(this)
                .setTitle("Oops!")
                .setMessage("Unable to connect with the required hardware")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete

                    }
                })
                .setNegativeButton("Retry", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                       // connectToEsp();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
    private void cantConnectToNetworkDialog(){

        new AlertDialog.Builder(this)
                .setTitle("Oops!")
                .setMessage("Unable to connect")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

    }
    private void  errorConnectingWifiDialog(){
        new AlertDialog.Builder(this)
                .setTitle("Oops!")
                .setMessage("Unable to connect with the required hardware")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete

                    }
                })
                .setNegativeButton("Retry", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                        if(wifi_type==R.string.wifi_type_esp) {
                            connect_wifi(getCurrentSSID(), "thereisnospoon");
                        }

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
    private void noWifiNetworksDialog(){

        new AlertDialog.Builder(this)
                .setMessage("No Wifi Networks found")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        ConfigScreen();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
    private void locationDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(ConfigHub.this);
        builder.setTitle("Location services not available")
                .setMessage("Location services is required to list nearby wifi networks.Turn on location services")
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                            }
                        })
                .setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                ConfigScreen();
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }
    protected boolean isLocationEnabled(){
        String le = Context.LOCATION_SERVICE;
        locationManager = (LocationManager) getSystemService(le);
        if(!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            return false;
        } else {
            return true;
        }
    }

}

