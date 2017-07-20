package homeautomation.circularblue.com.homeautomation;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by rohan on 03-04-2017.
 */


public class SocketEvents {



    public static final String EVENT_ADD_DEVICE = "addDevice";
    public static final String EVENT_NODE_CHANGE ="Node_change";
    public static final String EVENT_NODE_DEVICES = "Node_devices";


    public static void  ADD_DEVICE(){

        Singleton singleton= Singleton.getInstance();

        JSONObject json = new JSONObject();
        try {
            json.put("deviceType","Mobile");
            json.put("uniqueID",singleton.getHub().getId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        singleton.socket.emit(SocketEvents.EVENT_ADD_DEVICE,json);
    }
    public static void NODE_CHANGE(String nodeId,String deviceId,String deviceState){

        Singleton singleton= Singleton.getInstance();

        JSONObject json = new JSONObject();
        try {
            json.put("nodeId",nodeId);
            json.put("deviceId",deviceId);
            json.put("deviceState",deviceState);

        }catch (JSONException e) {
            e.printStackTrace();
        }
        singleton.socket.emit(SocketEvents.EVENT_NODE_CHANGE,json);
    }
    public static void NODE_DEVICES(String nodeId){
        Singleton singleton= Singleton.getInstance();

        JSONObject json = new JSONObject();
        try {
            json.put("nodeId",nodeId);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        singleton.socket.emit(SocketEvents.EVENT_NODE_DEVICES,json);
    }

}
