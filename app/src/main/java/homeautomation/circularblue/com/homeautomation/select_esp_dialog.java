package homeautomation.circularblue.com.homeautomation;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class select_esp_dialog extends AppCompatActivity {

    Button ok_button;
    Singleton singleton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_esp_dialog);
        ok_button = (Button) findViewById(R.id.ok_button);
        singleton = Singleton.getInstance();
        singleton.hideProgressDialog();
        init();
    }
    private void init(){

        init_listners();

    }
    private void init_listners(){
        ok_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });
    }
}

