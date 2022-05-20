import java.util.ArrayList;
import java.util.HashMap;
import java.io.FileWriter;
import java.io.IOException;

public class Task2 {
    public static class API {
        ArrayList<Topology> topologies = new ArrayList<Topology>();

        
        // Req 1
        public void ReedJSON(String fileName) {

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
            String JSON = "{\"type\":\"" + type + "\",\"id\":\"" + id;
            switch(type)
            {
                case "resistor":
                    JSON += "\",\"resistance\":";
                    break;
                case "capacitor":
                    JSON += "\",\"capacitance\":";
                    break;
                case "inductor":
                    JSON += "\",\"inductance\":";
                    break;
                case "nmos":
                case "pmos":
                    JSON += "\",\"m(l)\":";
                    break;
            }
            JSON += value.toString() + ",\"netlist\":{";
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
        Value resistance = new Value(100, 10, 1000);
        Value m1 = new Value(1.5, 1, 2);
        
        Component resistor = new Component();
        Component nmos = new Component();

        resistor.setValue(resistance);
        resistor.setType("resistor");
        resistor.setId("res1");
        resistor.addNode("t1", "vdd");
        resistor.addNode("t2", "n1");

        nmos.setValue(m1);
        nmos.setType("nmos");
        nmos.setId("m1");
        nmos.addNode("drain", "n1");
        nmos.addNode("gate", "vin");
        nmos.addNode("source", "vss");

        Topology topology = new Topology();
        topology.setId("top1");
        topology.addComponent(resistor);
        topology.addComponent(nmos);

        topology.writeJSON();
    }
}
