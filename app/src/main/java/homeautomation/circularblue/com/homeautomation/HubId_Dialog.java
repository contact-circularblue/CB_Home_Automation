package homeautomation.circularblue.com.homeautomation;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DialogTitle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

public class HubId_Dialog extends AppCompatActivity {

    Button button_add_device;
    Button button_scan_qr;
    EditText editText_hubId;
    CheckBox checkBox_showpassword;
    Singleton singleton;
    private IntentIntegrator qrScan;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_hub_id__dialog);
        init();
        setTitle("        Let's get it started");
        qrScan = new IntentIntegrator(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
    public void init(){
        init_vars();
        init_listners();
    }
    public void init_vars(){
        button_add_device = (Button) findViewById(R.id.button_authenticate);
        button_scan_qr = (Button) findViewById(R.id.scan_qr_button);
        editText_hubId = (EditText) findViewById(R.id.edittext_password);
        checkBox_showpassword = (CheckBox) findViewById(R.id.checkBox_showpassword);
        singleton = Singleton.getInstance();
    }
    public void init_listners(){
        button_add_device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editText_hubId.getText().toString().isEmpty()){
                    Toast.makeText(HubId_Dialog.this, "Enter Device id", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent resultIntent = new Intent();
                resultIntent.putExtra("deviceId", editText_hubId.getText().toString().trim());
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });
        button_scan_qr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(HubId_Dialog.this, "scan qr", Toast.LENGTH_SHORT).show();
                        qrScan.initiateScan();
                    }
                });
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            //if qrcode has nothing in it
            if (result.getContents() == null) {
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
            } else {
                //if qr contains data
                try {
                    //converting the data to json
                    JSONObject obj = new JSONObject(result.getContents());

                    String hubId =  obj.getString("HubId");
                    if(hubId!=null){
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("deviceId", hubId);
                        setResult(Activity.RESULT_OK, resultIntent);
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    //if control comes here
                    //that means the encoded format not matches
                    //in this case you can display whatever data is available on the qrcode
                    //to a toast
                    //result.getContents()
                    Toast.makeText(this,"Hub Unvailable/Offline", Toast.LENGTH_LONG).show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }




}
