import java.util.ArrayList;
import java.util.HashMap;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONObject;



public class Task2 {
    public static class API {
        ArrayList<Topology> topologies = new ArrayList<Topology>();
        // Req 1
        public void readJSON(String fileName) {
            Topology topology = new Topology();
            topology.readJSON(fileName);
            topologies.add(topology);
        }

        // Req 2.1
        public void writeJSON(String topologyId) {
            Topology topology = getTopologyWithId(topologyId);
            if (topology != null) {
                topology.writeJSON();
            }
        }

        // Req 3
        public ArrayList<Topology> quaryTopologies() {
            return topologies;
        }

        // Req 4
        public void deleteTopology(String topologyId) {
            Topology topology = getTopologyWithId(topologyId);
            if (topology != null)
                topologies.remove(topology);
        }

        // Req 5
        public ArrayList<Component> quaryDevices(String topologyId) {
            ArrayList<Component> components = new ArrayList<Component>();
            Topology topology = getTopologyWithId(topologyId);
            if (topology != null)
                components = topology.getComponents();
            return components;
        }

        // Req 6.1
        public ArrayList<Component> quaryDevicesWithNetlistNode(String topologyId, String nodeId) {
            ArrayList<Component> components = new ArrayList<Component>();
            Topology topology = getTopologyWithId(topologyId);
            if (topology != null)
                components = topology.quaryDevicesWithNetlistNode(nodeId);
            return components;
        }

        // Helper Functions
        public void addTopology(Topology topology){
            topologies.add(topology);
        }

        public Topology getTopologyWithId(String topologyId) {
            for (Topology topology : topologies) {
                if (topology.getId().equals(topologyId)) {
                    return topology;
                }
            }
            return null;
        }

    }

    public static class Topology {
        ArrayList<Component> components = new ArrayList<Component>();
        String id = "";

        public Topology() {
        }

        public Topology(String id, ArrayList<Component> components) {
            this.id = id;
            this.components = components;
        }

        // Req 1.2
        public void readJSON(String fileName){
            String data = "";
            try {
                File file = new File(fileName);
                Scanner reader = new Scanner(file);
                while (reader.hasNextLine()) {
                  data += reader.nextLine();
                }
                reader.close();

            } catch (FileNotFoundException e) {
              System.out.println("An error occurred.");
              e.printStackTrace();
            }

            try {
                JSONObject jsonObj =  new JSONObject(data);
                this.id = jsonObj.get("id").toString();
                JSONArray jsonArray = jsonObj.getJSONArray("components");
                for (int i = 0; i < jsonArray.length(); i++) {
                    Component component = new Component();
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    component.setId(jsonObject.get("id").toString());
                    component.setType(jsonObject.get("type").toString());
                    Value value = new Value();
                    String key = component.getValueTypeFromType();

                    value.setDefaultValue(jsonObject.getJSONObject(key).getDouble("default"));
                    value.setMinValue(jsonObject.getJSONObject(key).getDouble("min"));
                    value.setMaxValue(jsonObject.getJSONObject(key).getDouble("max"));
                    component.setValue(value);

                    JSONObject netlistJSON = jsonObject.getJSONObject("netlist");
                    HashMap<String, String> netlist = new HashMap<String, String>();

                    for(int j = 0; j < netlistJSON.length(); j++){
                        netlist.put(netlistJSON.names().get(j).toString(),
                                    netlistJSON.get(netlistJSON.names().get(j).toString()).toString());
                    }
                    component.setNetlist(netlist);
                    components.add(component);
                }
            } catch (Exception e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
        }

        // Req 2.2
        public void writeJSON() {
            try {
                FileWriter writer = new FileWriter(id + ".JSON");
                String JSON = "{\"id\":\"" + id + "\",\"components\": [";
                for (Component component : components) {
                    JSON += component.getJSON();
                }
                if(components.size() > 0)
                    JSON = JSON.substring(0, JSON.length()-2);
                JSON += "] }";
                writer.write(JSON);
                writer.close();
                System.out.println("Success");
            } catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
        }

        // Req 6.2
        public ArrayList<Component> quaryDevicesWithNetlistNode(String nodeId) {
            ArrayList<Component> filteredComponents = new ArrayList<Component>();
            for (Component component : this.components) {
                if (component.hasNode(nodeId)) {
                    filteredComponents.add(component);
                }
            }
            return filteredComponents;
        }

        // Helper Functions
        public ArrayList<Component> getComponents() {
            return components;
        }

        public String getId() {
            return id;
        }

        public void setComponents(ArrayList<Component> components) {
            this.components = components;
        }

        public void setId(String id) {
            this.id = id;
        }

        public void addComponent(Component component) {
            components.add(component);
        }
    }

    public static class Component {
        String type = "";
        String id = "";
        Value value = new Value();
        HashMap<String, String> netlist = new HashMap<>();

        public Component() {
        }

        public Component(String type, String id, Value value, HashMap<String, String> netlist) {
            this.type = type;
            this.id = id;
            this.value = value;
            this.netlist = netlist;
        }

        // Req 2.3
        public String getJSON() {
            String JSON = "{\"type\":\"" + type + "\",\"id\":\"" + id + "\",\"" + getValueTypeFromType() + "\":" + value.toString() + ",\"netlist\":{";
            for (String key : netlist.keySet()) {
                JSON += "\"" + key + "\":\"" + netlist.get(key) + "\", ";
            }
            if(netlist.keySet().size() > 0)
                JSON = JSON.substring(0, JSON.length()-2);
            JSON += "}}, ";
            return JSON;
        }

        // Req 6.3
        public Boolean hasNode(String node) {
            for (String key : netlist.keySet()) {
                if (netlist.get(key).equals(node))
                    return true;
            }
            return false;
        }

        // Helper Functions
        public String getValueTypeFromType(){
            switch(type)
            {
                case "resistor":
                    return "resistance";
                case "capacitor":
                    return "capacitance";
                case "inductor":
                    return "inductance";
                case "nmos":
                case "pmos":
                    return "m(l)";
            }
            return "value";
        }

        public String getNode(String name) {
            return netlist.get(name);
        }

        public void addNode(String name, String node) {
            netlist.put(name, node);
        }

        public void setValue(Value value) {
            this.value = value;
        }

        public void setType(String type) {
            this.type = type;
        }

        public void setId(String id) {
            this.id = id;
        }

        public void setNetlist(HashMap<String, String> netlist) {
            this.netlist = netlist;
        }

        public Value getValue() {
            return value;
        }

        public String getType() {
            return type;
        }

        public String getId() {
            return id;
        }

        public HashMap<String, String> getNetlist() {
            return netlist;
        }

    }

    public static class Value {
        double defaultValue = 0;
        double minValue = 0;
        double maxValue = 0;

        public Value() {
        }

        public Value(double defaultValue, double minValue, double maxValue) {
            this.defaultValue = defaultValue;
            this.minValue = minValue;
            this.maxValue = maxValue;
        }

        // Req 2.4
        public String toString() {
            return ("{\"default\":" + defaultValue + ",\"min\":" + minValue + ",\"max\":"
                    + maxValue + "}");
        }

        public double getDefaultValue() {
            return defaultValue;
        }

        public double getMinValue() {
            return minValue;
        }

        public double getMaxValue() {
            return maxValue;
        }

        public void setDefaultValue(double defaultValue) {
            this.defaultValue = defaultValue;
        }

        public void setMinValue(double minValue) {
            this.minValue = minValue;
        }

        public void setMaxValue(double maxValue) {
            this.maxValue = maxValue;
        }
    }

    public static void main(String[] args) {
        API api = new API();

        // Testing Requirements: 
        // Req 1 & 2
        // top1.JSON should be identical to input.JSON
        api.readJSON("input.JSON");
        api.writeJSON("top1");


        // Req 5
        // should have 2 components (resistor, nmos) in top1
        ArrayList<Component> components = api.quaryDevices("top1");
        System.out.println("Req 5: " + components.size());
        for (Component component : components) {
            System.out.println(component.getType() + " " + component.getId());
        }

        // Req 6
        // should have 2 components (resistor, nmos) connected to n1
        ArrayList<Component> components2 = api.quaryDevicesWithNetlistNode("top1", "n1");
        System.out.println("Req 6: " + components2.size());
        for (Component component : components2) {
            System.out.println(component.getType() + " " + component.getId());
        }

        // Req 3 & 4
        // should output 1 and then 0
        System.out.println("Req 3,4: " + api.quaryTopologies().size());
        api.deleteTopology(api.quaryTopologies().get(0).getId());
        System.out.println("Req 3,4: " + api.quaryTopologies().size());
    }
}
