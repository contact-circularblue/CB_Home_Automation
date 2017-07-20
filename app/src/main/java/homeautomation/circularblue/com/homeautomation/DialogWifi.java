package homeautomation.circularblue.com.homeautomation;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class DialogWifi extends AppCompatActivity {

    Button button_authenticate;
    EditText editText_password;
    CheckBox checkBox_showpassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_dialog_wifi);
        button_authenticate = (Button) findViewById(R.id.button_authenticate);
        editText_password = (EditText) findViewById(R.id.edittext_password);
        checkBox_showpassword = (CheckBox) findViewById(R.id.checkBox_showpassword);
        button_authenticate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editText_password.getText().toString().isEmpty()){
                    Toast.makeText(DialogWifi.this, "Enter password", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent resultIntent = new Intent();
                // TODO Add extras or a data URI to this intent as appropriate.
                Log.d("debug","password : " + editText_password.getText().toString().trim());
                resultIntent.putExtra("password",editText_password.getText().toString().trim());
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.checkBox_showpassword:
                if (checked){
                    editText_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);

                }else{
                    editText_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                break;
        }
    }


}

