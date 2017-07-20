package homeautomation.circularblue.com.homeautomation;

import java.util.HashMap;

/**
 * Created by rohan on 03-04-2017.
 */

public class Node {

    HashMap<String,Device> devices;

    public Node(String id){
        setId(id);
        devices = new HashMap<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    String id;

    public void addDevice(String id,boolean state){
        devices.put(id,new Device(id,state));
    }
    public void addDevice(String id){
        devices.put(id,new Device(id));
    }
    public Device getDevice(String id){
        return devices.get(id);
    }
    public boolean hasDevice(String id){
        return devices.containsKey(id);
    }
}
