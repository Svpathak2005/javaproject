import java.io.*;
import java.util.*;
import org.json.JSONArray;
import org.json.JSONObject;

public class sample {
    public static void main(String[] args) throws IOException {
        String csvFile = "modified_sample_data.csv";  // Your CSV file path
        String line;
        String cvsSplitBy = ",";

        Map<String, StateData> stateDataMap = new HashMap<>();
        JSONArray locationsArray = new JSONArray();  // Array to store individual locations

        // Read CSV and process data
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            // Skip header
            br.readLine();
            
            while ((line = br.readLine()) != null) {
                // Handle quoted commas and split by actual commas
                String[] data = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1); // Regex to account for quoted commas

                if (data.length >= 5) {
                    String state = data[1].trim();
                    String location = data[0].trim();
                    try {
                        double turbidity = Double.parseDouble(data[4].trim());
                        double temperature = Double.parseDouble(data[2].trim());
                        double ph = Double.parseDouble(data[3].trim());

                        // Add each location's data to JSON array
                        JSONObject locationData = new JSONObject();
                        locationData.put("location", location);
                        locationData.put("state", state);
                        locationData.put("turbidity", turbidity);
                        locationData.put("temperature", temperature);
                        locationData.put("ph", ph);
                        locationsArray.put(locationData);

                        // Update the state data for averages calculation
                        stateDataMap.putIfAbsent(state, new StateData());
                        StateData stateData = stateDataMap.get(state);
                        stateData.sumTurbidity += turbidity;
                        stateData.sumTemperature += temperature;
                        stateData.sumPh += ph;
                        stateData.count++;
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid data: " + Arrays.toString(data));
                    }
                }
            }
        }

        // Prepare JSON data
        JSONObject averages = new JSONObject();
        for (Map.Entry<String, StateData> entry : stateDataMap.entrySet()) {
            String state = entry.getKey();
            StateData stateData = entry.getValue();
            averages.put(state, new JSONObject()
                    .put("averageTurbidity", stateData.sumTurbidity / stateData.count)
                    .put("averageTemperature", stateData.sumTemperature / stateData.count)
                    .put("averagePh", stateData.sumPh / stateData.count)
            );
        }

        // Complete JSON structure
        JSONObject outputJson = new JSONObject();
        outputJson.put("locations", locationsArray);
        outputJson.put("averages", averages);

        // Write JSON to file
        try (FileWriter file = new FileWriter("averages.json")) {
            file.write(outputJson.toString(4)); // Pretty print JSON with indentation
        }

        System.out.println("Data written to averages.json");
    }

    static class StateData {
        double sumTurbidity = 0;
        double sumTemperature = 0;
        double sumPh = 0;
        int count = 0;
    }
}
