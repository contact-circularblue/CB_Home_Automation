package homeautomation.circularblue.com.homeautomation;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.emitter.Emitter;

public class Config_activity extends AppCompatActivity {

    Button setup_new_device_button;
    Button setup_existing_device;
    Singleton singleton;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_activity);
        init();

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("Add Device");
    }
    private void init(){
            init_vars();
            init_listners();
    }
    private void init_vars(){
        singleton = Singleton.getInstance();
        setup_existing_device = (Button) findViewById(R.id.existing_device_button);
        setup_new_device_button = (Button) findViewById(R.id.new_device_button);
        progressDialog = new ProgressDialog(this);
    }
    private void init_listners(){
        setup_new_device_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Config_activity.this,ConfigHub.class);
                startActivity(intent);
            }
        });

        setup_existing_device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Config_activity.this,HubId_Dialog.class);
                startActivityForResult(intent,0);
            }
        });
    }
    private void showHubIdDialog(){

    }
    private void setupHubScreen(){
        Intent intent = new Intent(Config_activity.this,ConfigHub.class);
        startActivity(intent);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String deviceId =  data.getStringExtra("deviceId");

                singleton.setHub(deviceId);
                SocketEvents.ADD_DEVICE();

            }
        }
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
                        progressDialog.hide();

                    }
                });
            }
        });
    }
    private void DevicesScreen(){
        Intent intent = new Intent(Config_activity.this,Devices_Activity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
    private void socket_off_events(){
        singleton.socket.off(SocketEvents.EVENT_ADD_DEVICE);
    }
    protected void onStart(){
        super.onStart();
    }
    public void onResume(){
        super.onResume();
        init_socket_on_events();
    }
    public void onStop(){
        super.onStop();
    }
    public void onPause(){
        super.onPause();
        socket_off_events();
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
