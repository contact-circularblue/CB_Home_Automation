//package homeautomation.circularblue.com.homeautomation;
//
//import android.app.Activity;
//import android.app.ProgressDialog;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.net.wifi.ScanResult;
//import android.net.wifi.WifiConfiguration;
//import android.net.wifi.WifiManager;
//import android.os.Handler;
//import android.util.Log;
//import android.view.View;
//import android.widget.AdapterView;
//import android.widget.ListView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;
//
///**
// * Created by rohan on 29-03-2017.
// */
//
//public class Wifi extends Activity {
//
//    WifiManager mWifiManager;
//    ListView wifiScanResList;
//    WifiArrayAdapter wifiArrayAdapter;
//    ArrayList<String> wifi_results;
//    DialogProgress dialogProgress;
//    boolean wasEnabled;
//    String current_ssid;
//    ProgressDialog pd;
//    public void Wifi(){
//
//
//    }
//    private final BroadcastReceiver mWifiScanReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context c, Intent intent) {
//            if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
//                List<ScanResult> mScanResults = mWifiManager.getScanResults();
////                dialogProgress.dismiss();
//                // add your logic here
//                wifi_results.clear();
//                Iterator<ScanResult> wifiList = mScanResults.iterator();
//                while (wifiList.hasNext()){
//                    String ssid = wifiList.next().SSID;
//                    try{
//                        String sub_ssid = ssid.substring(0,3);
//
//                        if( ssid.length() > 3  && sub_ssid.equals("ESP")){
//                            Log.d("debug",ssid);
//                            wifi_results.add(ssid);
//                        }
//                    }catch (Exception e){
//
//                    }
//
//
//
//
//                }
//                wifiArrayAdapter = new WifiArrayAdapter(ConfigHub.this, wifi_results);
//                wifiScanResList.setAdapter(wifiArrayAdapter);
//                wifiScanResList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                        // DO something if the user clicked on the item
////                        Intent intent = new Intent(ConfigHub.this,DialogWifi.class);
//                        View v = parent.getChildAt(position);
//                        current_ssid =  ((TextView) v.findViewById(R.id.textview_ssid)).getText().toString();
//                        connect_wifi(current_ssid,"thereisnospoon");
////                        startActivityForResult(intent,0);
//                    }
//                });
//            }
//        }
//    };
//    private void connect_wifi(String ssid,String key){
//        final Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//                String ssid="_NA_";
//                try {
//                    ssid = mWifiManager.getConnectionInfo().getSSID().toString().trim().substring(1, mWifiManager.getConnectionInfo().getSSID().toString().trim().length() - 1);
//                }catch (Exception e){}
//                if(ssid.equals(current_ssid)){
//                    Toast.makeText(ConfigHub.this, "Connected", Toast.LENGTH_SHORT).show();
//                }else{
//                    Toast.makeText(ConfigHub.this, "Unable to connect", Toast.LENGTH_SHORT).show();
//                }
//                pd.hide();
//            }
//        }, 5000);
//        pd.setMessage("Please wait");
//        pd.setCancelable(false);
//        pd.show();
//
//        WifiConfiguration wifiConfig = new WifiConfiguration();
//        wifiConfig.SSID = String.format("\"%s\"", ssid);
//        wifiConfig.preSharedKey = String.format("\"%s\"", key);
//
////        Toast.makeText(this, "SSID: " + ssid + " PASW: " + key , Toast.LENGTH_SHORT).show();
//        int netId = mWifiManager.addNetwork(wifiConfig);
//        mWifiManager.disconnect();
//        mWifiManager.enableNetwork(netId, true);
//        mWifiManager.reconnect();
//    }
//    public void scan(){
//        mWifiManager = (WifiManager)  getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//        registerReceiver(mWifiScanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
//        mWifiManager.startScan();
//    }
//}
