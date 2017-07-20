package homeautomation.circularblue.com.homeautomation;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by rohan on 31-03-2017.
 */

public class Hub {
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    String id;
    HashMap<String,Node> nodes;
    Hub(String id){
        this.id = id;
        nodes = new HashMap<String,Node>();
        addNode("4234567890");
    }
    public void addNode(String id){
        nodes.put(id,new Node(id));
    }
    public Node getNode(){

        Node node =  nodes.get("4234567890");
        if(node!=null){
            return node;
        }
        return null;
    }
    public boolean hasNode(){
        return nodes.containsKey("4234567890");
    }
}
