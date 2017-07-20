package homeautomation.circularblue.com.homeautomation;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.app.Application;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;

import com.android.volley.Response;

import io.socket.client.IO;
import io.socket.client.Socket;

import java.net.URISyntaxException;

/**
 * Created by rohan on 29-03-2017.
 */

// File Name: Singleton.java

public class Singleton {


    private static Singleton singleton = new Singleton( );
    public WifiDetails homeWifiDetails;
    public Esp esp;
    public Hub hub;
    public Socket socket;
    public String esp_ssid;
    private ProgressDialog progressDialog;
    private boolean progressDialogHidable=true;
    Handler pdCanceller;// = new Handler();
    private Response.Listener<Boolean> mListener_bool=null;
    Timer_ timer;

    private Singleton() {
        esp  = new Esp();
        timer = new Timer_();


    }
    SharedPreferences sharedpreferences;


    /* Static 'instance' method */
    public static Singleton getInstance( ) {
        return singleton;
    }
    public String getEsp_ssid() {
        return esp_ssid;
    }
    public void setEsp_ssid(String esp_ssid) {
        this.esp_ssid = esp_ssid;
    }

    public void setHomeWifiDetails(String current_ssid, String password) {
        homeWifiDetails = new WifiDetails(current_ssid,password);
    }
    public void removeHomeWifiDetails(){
        homeWifiDetails = null;
    }
    public boolean hasHomeWifiDetails(){
        if(homeWifiDetails!=null){
            return true;
        }
        return false;
    }
    public void setHub(String id){
        hub = new Hub(id);
    }
    public Hub getHub(){
        return hub;
    }
    public boolean hasHub(){
        if(hub!=null){
            return true;
        }
        return false;
    }

    public void StartCountdown(long lastForSeconds,long intervalInSeconds){
        CountDownTimer countDownTimer=null;
       countDownTimer =  new CountDownTimer(lastForSeconds*1000, intervalInSeconds*1000) {

            public void onTick(long millisUntilFinished) {
//                Log.d("debug",String.valueOf(millisUntilFinished));
                Log.d("debug","seconds remaining: " + millisUntilFinished / 1000);

                long secondsRemaining = millisUntilFinished / 1000;
                if((secondsRemaining%30) == 0){
                    SocketEvents.ADD_DEVICE();
                }
            }

            public void onFinish() {
                Log.d("debug","Countdown finished");

            }
        }.start();
    }
    public void showProgressDialog(Context context,long time,Response.Listener<Boolean> listener){


        if(progressDialog!=null){

            if(progressDialog.isShowing()) {
                try{
                    progressDialog.dismiss();

                }catch (Exception e){

                }
            }
            progressDialog=null;
        }
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please Wait");
        progressDialog.setCancelable(false);
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {

            }
        });

        try{
            progressDialog.show();
        }catch (Exception e){

        }
        mListener_bool = listener;

        Log.d("debug","show progress bar");

        Runnable progressRunnable = new Runnable(){
            @Override
            public void run(){
                hideProgressDialog();
                mListener_bool.onResponse(true);
            }
        };
        pdCanceller = new Handler();
       // pdCanceller.postDelayed(progressRunnable, time);
    }
    public void showProgressDialog(Context context){

        if(progressDialog!=null){
            progressDialog.dismiss();
            progressDialog=null;
        }
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please Wait");
        try {
            progressDialog.show();
        }catch (Exception e){

        }
        Log.d("debug","show progress bar");
    }
    public void hideProgressDialog(){
        if(progressDialogHidable) {

            if (progressDialog != null) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                    Log.d("debug", "hide progress dialog");
                  //  pdCanceller.removeCallbacksAndMessages(null);

                }
            }
        }
    }
    public void setProgressDialogHidable(boolean b){
        progressDialogHidable = b;
    }
    public void initPrefs(SharedPreferences p){
        sharedpreferences = p;
    }
    public void setStringToPrefs(String key,String value){
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(key,value);
        editor.commit();
    }
    public String getStringFromPrefs(String key){
        return sharedpreferences.getString(key,null);
    }
    public void saveHubIdToPrefs(){
        String hubId =  getHub().getId();
        setStringToPrefs("HubId",hubId);
    }
    public String getHubIdFromPrefs(){
        return getStringFromPrefs("HubId");
    }
    public void removeKeyFromPrefs(String hubId){
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.remove(hubId);
        editor.commit();
    }
    public void removeHubIdFromPrefs(){
        removeKeyFromPrefs("HubId");
    }
    public boolean hasKeyInPrefs(String key){
       return sharedpreferences.contains(key);
    }
    public boolean hasHubInPrefs(){
        return hasKeyInPrefs("HubId");
    }
    public void removePrefs(){
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.clear();
        editor.commit();
    }
    public void setDeviceNameToPrefs(String name){
        setStringToPrefs("DeviceId",name);
    }
    public String getDeviceNameFromPrefs(){
        return  getStringFromPrefs("DeviceId");
    }
    public boolean hasDeviceNameInPrefs(){
        return hasKeyInPrefs("DeviceId");
    }

    public void setIconNameToPrefs(String name){
        setStringToPrefs("DeviceIc",name);
    }
    public String getIconNameFromPrefs(){
        return  getStringFromPrefs("DeviceIc");
    }
    public boolean hasIconIdInPrefs(){
        return hasKeyInPrefs("DeviceIc");
    }



}
class WifiDetails{
    public WifiDetails(String current_ssid, String password) {
        this.ssid = current_ssid;
        this.password = password;
    }

    public String getSSID() {
        return ssid;
    }

    public void setSSID(String ssid) {
        this.ssid = ssid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }



    String ssid;
    String password;
}