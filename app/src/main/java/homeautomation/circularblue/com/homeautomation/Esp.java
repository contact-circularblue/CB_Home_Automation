package homeautomation.circularblue.com.homeautomation;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by rohan on 31-03-2017.
 */

public class Esp {
    String url ="http://192.168.4.1";
    boolean reponse_recived=false;
    private Response.Listener<String> mListener=null;



    public void connect(Context context,Response.Listener<String> listener){
        RequestQueue queue = Volley.newRequestQueue(context);
        final boolean[] response_ = {false};
        mListener = listener;
        Log.d("debug","connect to esp");

// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                   //    response_[0] = true;
                   //    reponse_recived= true;

                       mListener.onResponse(response.trim());

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
             //  response_[0] = false;// error.getMessage();
                mListener.onResponse(null);
            }
        });
// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public void sendWiFiDetails(Context context,String ssid_,String password_,Response.Listener<String> listener){

        Log.d("debug", "SSID : " + ssid_);
        Log.d("debug", "PASSWORD : " + password_);
        String ssid = ssid_;
        String password = password_;
        mListener = listener;
        final String[] response_ = {null};

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
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = this.url + "/" + jsonObject.toString();

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // your response
                 //       response_[0] = response.trim();
                        mListener.onResponse(response.trim());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error
               //     response_[0] = null;// error.getMessage();
                mListener.onResponse(null);
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

}
