package homeautomation.circularblue.com.homeautomation;

/**
 * Created by rohan on 03-04-2017.
 */

public class Device {
    String id;
    String icon_id;
    String name;

    public Device(String id){
        this.state = false;
        this.id = id;
        this.icon_id = "ic_10a";
        setName("Device " +id);
    }
    public Device(String id,boolean state){
        this.state = state;
        this.id = id;
        this.icon_id = "ic_10a";
        setName("Device " +id);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public void setIcon(String icon_id){
        this.icon_id  = icon_id;
    }
    public String getIcon(){
        return icon_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }




    boolean state;
}
