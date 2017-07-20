package homeautomation.circularblue.com.homeautomation;

import android.os.Handler;
import android.util.Log;

/**
 * Created by rohan on 07-06-2017.
 */
public class Timer_ {
    public static final int MSG_HIDE_PBAR = 0;
    static long PBAR_DELAY = 10 * 1000; //Delay is is milliseconds
    private Handler mHandler;

    public Timer_() {
//        this.mHandler = mHandler;
        Log.d("debug","Timer object created");
    }
    public void SetLooper(Handler.Callback looper){
        mHandler = new Handler(looper);
        Log.d("debug","Looper has set" + looper.getClass().getName());

    }
    public void Start(int delayInSeconds,int what){
        PBAR_DELAY = delayInSeconds*1000;
        mHandler.sendEmptyMessageDelayed(what, PBAR_DELAY);
        Log.d("debug","TIME START FOR : " + what);
    }
    public void Stop(){
        mHandler.removeCallbacksAndMessages(null);
        Log.d("debug","TIME STOP FOR ALL");

    }
    public void Stop(int what){
        mHandler.removeMessages(what);
        Log.d("debug","TIME STOP FOR : " + what);

    }

}