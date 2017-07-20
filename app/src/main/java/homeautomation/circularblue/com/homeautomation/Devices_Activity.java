package homeautomation.circularblue.com.homeautomation;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import io.socket.emitter.Emitter;

public class Devices_Activity extends AppCompatActivity implements Handler.Callback {


    Switch switches[];
    ImageView imageView[];
    int ids[] = {R.id.switch1};//,R.id.switch2,R.id.switch3,R.id.switch4};
    int imageViews[] ={R.id.image1};//,R.id.image2,R.id.image3,R.id.image4};
    Singleton singleton;
    Hub hub;
    Node node;
    TextView textView_device1;
    ProgressDialog progressDialog;
    int deviceCount = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices_);
        singleton = Singleton.getInstance();
        switches = new Switch[deviceCount];
        imageView = new ImageView[deviceCount];
        textView_device1 = (TextView) findViewById(R.id.textView_device1);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("please wait");

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("Appliances");
        singleton.timer.SetLooper(this);


        if(singleton.hasHub()) {
            hub = singleton.getHub();

            if(hub.hasNode()){
                node = hub.getNode();
            }else{
                Toast.makeText(this, "node not available", Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(this, "hub not available", Toast.LENGTH_SHORT).show();
        }
        if(hub.hasNode()){
            for(int i=1;i<=deviceCount;i++){
                String id= String.valueOf(i);
                if(!node.hasDevice(id)){
                    node.addDevice(id);

                    if(singleton.hasDeviceNameInPrefs())
                    {
                        String name = singleton.getDeviceNameFromPrefs();
                        node.getDevice(id).setName(name);
                    }
                    if(singleton.hasIconIdInPrefs())
                    {
                        String name = singleton.getIconNameFromPrefs();
                        node.getDevice(id).setIcon(name);
                    }
                }
            }
        }
        for(int i=0;i<deviceCount;i++){

            switches[i] = (Switch) findViewById(ids[i]);
            final int finalI1 = i;


            switches[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Switch s = (Switch)v;
                   // s.isChecked()
                    String deviceId= String.valueOf(finalI1 +1);
                    String deviceState = String.valueOf(s.isChecked());
                    SocketEvents.NODE_CHANGE(node.getId(),deviceId,deviceState);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            singleton.timer.Start(10, TimerEvent.NODE_CHANGE);
                            singleton.showProgressDialog(Devices_Activity.this,10000,new Response.Listener<Boolean>() {
                                @Override
                                public void onResponse(Boolean response) {
                                    if (response.equals(true)) {
                                        // connectToHub();
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                s.setChecked(!s.isChecked());
                                                Toast.makeText(Devices_Activity.this, "Error", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    });
                }
            });

//            switches[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                @Override
//                public void onCheckedChanged(final CompoundButton compoundButton, final boolean b) {
//                  ///  Toast.makeText(Devices_Activity.this, String.valueOf(b), Toast.LENGTH_SHORT).show();
//                    String deviceId= String.valueOf(finalI1 +1);
//                    String deviceState = String.valueOf(b);
//                    SocketEvents.NODE_CHANGE(node.getId(),deviceId,deviceState);
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//
//                            singleton.showProgressDialog(Devices_Activity.this,3000,new Response.Listener<Boolean>() {
//                                @Override
//                                public void onResponse(Boolean response) {
//                                    if (response.equals(true)) {
//                                       // connectToHub();
//                                        runOnUiThread(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                compoundButton.setChecked(!b);
//                                                Toast.makeText(Devices_Activity.this, "eroor", Toast.LENGTH_SHORT).show();
//                                            }
//                                        });
//                                    }
//                                }
//                            });
//                        }
//                    });
//
//                }
//            });

            imageView[i] = (ImageView) findViewById(imageViews[i]);
            final int finalI = i;
            imageView[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editScreen(finalI);
                }
            });

            String device_id= String.valueOf(i+1);

            if(node.hasDevice(device_id)){
                Device device =  node.getDevice(device_id);
                String icon_id =  device.getIcon();
                String name = device.getName();
                setImage(icon_id,i);
                setName(name,i);
            }
        }
        singleton.socket.off(SocketEvents.EVENT_NODE_CHANGE);
        singleton.socket.on(SocketEvents.EVENT_NODE_CHANGE, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d("debug",args[0].toString());

                JSONObject json = (JSONObject) args[0];
                try {
                    final String deviceId =  json.getString("deviceId");
                    String nodeId = json.getString("nodeId");
                    final String deviceState = json.getString("deviceState");
                    final int index = (Integer.parseInt(deviceId)-1);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if(index<=deviceCount) {

                                switches[index].setChecked(Boolean.valueOf(deviceState));
                                singleton.hideProgressDialog();
                                singleton.timer.Stop(TimerEvent.NODE_CHANGE);
                            }
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        singleton.socket.off("");
        singleton.socket.on(SocketEvents.EVENT_NODE_DEVICES, new Emitter.Listener() {
            @Override
            public void call(Object... args) {

                Log.d("debug",args[0].toString());

                JSONArray json = (JSONArray) args[0];

                try {

                    for(int i=0;i<deviceCount;i++){

                        JSONObject object = (JSONObject)json.get(i);
//                        Log.d("debug","Id = " + object.getString("id"));
//                        Log.d("debug","State = " + object.getString("state"));
                        final boolean state =  Boolean.valueOf(object.getString("state"));
                        Log.d("debug","State " + String.valueOf(state));
                        final int finalI = i;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                switches[finalI].setChecked(state);
                            }
                        });
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


        singleton.saveHubIdToPrefs();

    }
    protected void onResume(){
        super.onResume();
     //   Log.d("debug","NodeId " + node.getId());
        SocketEvents.NODE_DEVICES(node.getId());

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_devices_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.action_logout:
                logoutAlert();
//                Toast.makeText(this, "Refresh selected", Toast.LENGTH_SHORT)
//                        .show();
                break;
            default:
                break;
        }
        return true;
    }
    private void editScreen(int index){
        Intent intent = new Intent(Devices_Activity.this,select_image_dialog.class);
        intent.putExtra("deviceId", String.valueOf(index+1));
        startActivityForResult(intent,0);
    }
    private void setImage(String icon_id ,int index){
        int id = getResources().getIdentifier(icon_id,"drawable",getPackageName());
        imageView[index].setImageDrawable( getResources().getDrawable(id));
    }
    private void setName(String name,int index){
      //  switches[index].setText(name);
        textView_device1.setText(name);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {

             //   Toast.makeText(this, "Result", Toast.LENGTH_SHORT).show();

                String deviceId =  data.getStringExtra("deviceId");
                int deviceId_int = Integer.parseInt(deviceId);
                deviceId_int--;
                Device device = node.getDevice(deviceId);
                setName(device.getName(),deviceId_int);
                setImage(device.getIcon(),deviceId_int);
                singleton.setDeviceNameToPrefs(device.getName());
                singleton.setIconNameToPrefs(device.getIcon());
            }
        }
        if(requestCode == 1){
            if(resultCode == RESULT_OK){

            }
        }
    }
    private void logoutAlert(){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("Are you sure you want to logout?");
        builder1.setCancelable(true);
        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        logout();
                        dialog.cancel();
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }
    private void logout(){

        singleton.removePrefs();
        Intent intent = new Intent(Devices_Activity.this,Config_activity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public boolean handleMessage(Message msg){
        switch (msg.what){
            case TimerEvent.Node_devices_IR:
                break;
            case TimerEvent.NODE_CHANGE:
                Toast.makeText(this, "Request failed", Toast.LENGTH_SHORT).show();
                singleton.hideProgressDialog();
                switches[0].setChecked(!switches[0].isChecked());

                break;
            case TimerEvent.Node_IR_delete:
                break;
        }
        return true;
    }
}
