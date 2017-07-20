package homeautomation.circularblue.com.homeautomation;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.rd.PageIndicatorView;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.emitter.Emitter;

public class ConnectToESP_Dialog extends AppCompatActivity {

    Button connect_button,init_button;
    TextView ssid_textView;
    String hubId;
    String ssid;
    RequestQueue queue;
    Singleton singleton;

    WifiManager mWifiManager;
    String url ="http://192.168.4.1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_to_esp__dialog);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new CustomPagerAdapter(this));

        PageIndicatorView pageIndicatorView = (PageIndicatorView) findViewById(R.id.pageIndicatorView);
        pageIndicatorView.setViewPager(viewPager);
        hubId = getIntent().getExtras().getString("HubId");
        singleton = Singleton.getInstance();
        ssid = "EDGE" + hubId;
        singleton.setEsp_ssid(ssid);
        queue = Volley.newRequestQueue(ConnectToESP_Dialog.this);
        init_socket_on_events();
    }
    protected void onStart(){

        super.onStart();

        Toast.makeText(this, "onStart", Toast.LENGTH_SHORT).show();

        mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        String ssid = mWifiManager.getConnectionInfo().getSSID().toString().trim().substring(1, mWifiManager.getConnectionInfo().getSSID().toString().trim().length() - 1);
        if(ssid.equals(this.ssid)){
            Toast.makeText(this, "connected", Toast.LENGTH_SHORT).show();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    singleton.showProgressDialog(ConnectToESP_Dialog.this);

                }
            });
            connect();
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.d("debug","ADD DEVICE");
                    // Do something after 5s = 5000ms
                    singleton.setHub(hubId);
                    SocketEvents.ADD_DEVICE();
                }
            }, 30000);
        }else{
            Toast.makeText(this, "not connected", Toast.LENGTH_SHORT).show();
        }
    }
    protected void onPause(){
        super.onPause();
        Toast.makeText(this, "onPause", Toast.LENGTH_SHORT).show();
    }
    protected void onStop(){
        super.onStop();
        Toast.makeText(this, "onStop", Toast.LENGTH_SHORT).show();
    }
    protected void onRestart(){
        super.onRestart();
        Toast.makeText(this, "onRestart", Toast.LENGTH_SHORT).show();
    }
    protected void onResume(){
        super.onResume();
        Toast.makeText(this, "onResume", Toast.LENGTH_SHORT).show();
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
                            // Toast.makeText(Config_activity.this, "Hub added", Toast.LENGTH_SHORT).show();
                            DevicesScreen();
                        }else{
                            deviceNotFoundDialog();
                            ///Toast.makeText(Config_activity.this, "Hub Not added", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //progressDialog.hide();

                    }
                });
            }
        });
    }

    private void connect(){
        Log.d("debug","connect");

// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
//                        mTextView.setText("Response is: "+response);
                        Toast.makeText(ConnectToESP_Dialog.this, "Response is: "+response, Toast.LENGTH_SHORT).show();
                        sendWiFiDetails();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                mTextView.setText("cant connect");
                Toast.makeText(ConnectToESP_Dialog.this,"cant connect", Toast.LENGTH_SHORT).show();
            }
        });
// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
    private void sendWiFiDetails(){

        String ssid = "Circular Blue";
        String password = "mightycartoon";

        ssid = ssid.replace(" ","%20");
        password = password.replace(" ","%20");

        Log.d("debug","ssid " + ssid);
        Log.d("debug","password " + password);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("ssid",ssid);
            jsonObject.put("password",password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = this.url + "/" + jsonObject.toString();

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // your response
//                        mTextView.setText("Response is: " + response);
                        Toast.makeText(ConnectToESP_Dialog.this, "Response is: " + response, Toast.LENGTH_SHORT).show();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error
                Toast.makeText(ConnectToESP_Dialog.this, "Error is: " +  error.getMessage(), Toast.LENGTH_SHORT).show();
//                mTextView.setText("Error is: " +  error.getMessage());
            }
        }){
            @Override
            public byte[] getBody() throws AuthFailureError {

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("ssid","Circular Blue");
                    jsonObject.put("password","mightycartoon");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String your_string_json = jsonObject.toString(); // put your json
                return your_string_json.getBytes();
            }
        };
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
//        requestt_urlQueue.start();
    }
    private void DevicesScreen(){

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                singleton.hideProgressDialog();
            }
        });
        Intent intent = new Intent(ConnectToESP_Dialog.this,Devices_Activity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
    public void deviceNotFoundDialog(){
        new AlertDialog.Builder(this)
                .setTitle("Oops!")
                .setMessage("Couldn't find the device. Please make sure your device is online")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

}
