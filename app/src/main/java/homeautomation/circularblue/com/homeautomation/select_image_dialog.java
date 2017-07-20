package homeautomation.circularblue.com.homeautomation;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class select_image_dialog extends AppCompatActivity {

    GridLayout grid;
    Button done_button;
    ImageView img_v;
    String img_name;
    EditText editText_device_name;
    String deviceId;
    Singleton singleton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_image_dialog);
        grid = (GridLayout) findViewById(R.id.images_grid);
        done_button = (Button) findViewById(R.id.button_done);
        editText_device_name = (EditText) findViewById(R.id.device_name_editext);
        singleton = Singleton.getInstance();
        loadImages();

        Bundle bundle = getIntent().getExtras();

        if(bundle.getString("deviceId")!= null)
        {
          deviceId = bundle.getString("deviceId");
        }

        done_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editText_device_name.getText().toString();
                if(!name.isEmpty()) {
                    singleton.getHub().getNode().getDevice(deviceId).setName(name);
                }
                if(img_name!=null) {
                    if (!img_name.isEmpty()) {
                        singleton.getHub().getNode().getDevice(deviceId).setIcon(img_name+"a");
                    }
                }
                Intent resultIntent = new Intent();
                resultIntent.putExtra("deviceId",deviceId);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });
    }
    private void loadImages(){

            for(int i=0;i<20;i++) {
                String name = "ic_" + i;
                int id = getResources().getIdentifier(name,"drawable",getPackageName());
                try{
                    addImageView(getResources().getDrawable(id),name);
                }catch (Exception e){

                }
            }
    }
    private void addImageView(final Drawable resource, final String name){

        //ImageView Setup
        final ImageView imageView = new ImageView(this);

//setting image resource
        imageView.setImageDrawable(resource);

//setting image position
        GridLayout.LayoutParams lp =  new GridLayout.LayoutParams();
        lp.width=100;
        lp.height=100;
        lp.setMargins(10,10,10,10);

        imageView.setLayoutParams(lp);
        imageView.setPadding(2,2,2,2);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                img_name = name;

                if(img_v!=null){
                    img_v.setBackgroundColor(Color.TRANSPARENT);
                }
                img_v =(ImageView) v;
                img_v.setBackgroundColor(Color.WHITE);

                //Toast.makeText(select_image_dialog.this, "selected", Toast.LENGTH_SHORT).show();
            }
        });
        grid.addView(imageView);
    }



}
