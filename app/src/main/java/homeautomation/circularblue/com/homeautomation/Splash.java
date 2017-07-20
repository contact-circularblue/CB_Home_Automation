package homeautomation.circularblue.com.homeautomation;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class Splash extends AppCompatActivity {

    Singleton singleton;
    String uri = "http://104.131.44.88:3000/";
//    String uri = "http://192.168.1.19:3000/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        Init();
    }
    private void Init(){
        initTimers();
        initVars();
        initSocketIO();
        singleton.initPrefs(getSharedPreferences("prefs", Context.MODE_PRIVATE));
    }
    private void initTimers(){
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if(singleton.hasHubInPrefs()){
                    singleton.setHub(singleton.getHubIdFromPrefs());
                    SocketEvents.ADD_DEVICE();
                }else {
                    Intent intent = new Intent(Splash.this, Config_activity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        }, 5000);
    }
    private void initVars(){

        singleton = Singleton.getInstance();

    }
    private void initSocketIO(){
        try {
            singleton.socket = IO.socket(uri);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        singleton.socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(Splash.this, "connected", Toast.LENGTH_SHORT).show();
//                        SocketEvents.ADD_DEVICE();
                    }
                });
            }
        });

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
                      //      Toast.makeText(Splash.this, "Hub added", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Splash.this, Devices_Activity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }else{
                            hubNotFoundDialog();
                        }
                    }
                });
            }
        });


        singleton.socket.connect();
    }
    private void hubNotFoundDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(Splash.this);
        builder.setTitle("Connect to Edge")
                .setMessage("Edge doesn't exist/offline")
                .setPositiveButton("Retry",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                SocketEvents.ADD_DEVICE();
                            }
                        })
                .setNegativeButton("Reconfigure",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(Splash.this, Config_activity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                singleton.removePrefs();
                                startActivity(intent);
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }


}
