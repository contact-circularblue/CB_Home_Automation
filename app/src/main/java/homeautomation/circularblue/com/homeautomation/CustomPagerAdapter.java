package homeautomation.circularblue.com.homeautomation;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by rohan on 07-05-2017.
 */

public class CustomPagerAdapter extends PagerAdapter {

    private Context mContext;
    private Singleton singleton;

    public CustomPagerAdapter(Context context) {
        mContext = context;
    }

    public Object instantiateItem(ViewGroup collection, int position) {
        ModelObject modelObject = ModelObject.values()[position];
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewGroup layout = (ViewGroup) inflater.inflate(modelObject.getLayoutResId(), collection, false);
        singleton  = Singleton.getInstance();
        switch(position){
            case 0:
                TextView ssid_textView = (TextView) layout.findViewById(R.id.ssid_textView);
                ssid_textView.setText(singleton.getEsp_ssid());
                break;
            case 1:
                break;
            case 2:
                Button button =(Button) layout.findViewById(R.id.connect_button);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(mContext, "click", Toast.LENGTH_SHORT).show();
                                      mContext.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                });


                break;
        }

        Log.d("debug","ResId" + modelObject.getLayoutResId());

        collection.addView(layout);
        return layout;
    }


    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    @Override
    public int getCount() {
        return ModelObject.values().length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        ModelObject customPagerEnum = ModelObject.values()[position];

        return mContext.getString(customPagerEnum.getTitleResId());
    }

}
