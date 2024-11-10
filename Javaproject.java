import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Javaproject {

    // Class to store cumulative values and counts for pH, temperature, and turbidity
    static class StateData {
        double totalPh = 0;
        double totalTemp = 0;
        double totalTurbidity = 0;
        int count = 0;

        void addData(double pH, double temp, double turbidity) {
            totalPh += pH;
            totalTemp += temp;
            totalTurbidity += turbidity;
            count++;
        }

        double getAveragePh() {
            return totalPh / count;
        }

        double getAverageTemp() {
            return totalTemp / count;
        }

        double getAverageTurbidity() {
            return totalTurbidity / count;
        }
    }

    public static void main(String[] args) {
        String csvFile = "output_data1.csv"; // Path to your CSV file
        String line;
        String delimiter = ",";

        // Predefined list of valid Indian states
        Set<String> validStates = new HashSet<>();
        validStates.add("Andhra Pradesh");
        validStates.add("Arunachal Pradesh");
        validStates.add("Assam");
        validStates.add("Bihar");
        validStates.add("Chhattisgarh");
        validStates.add("Goa");
        validStates.add("Gujarat");
        validStates.add("Haryana");
        validStates.add("Himachal Pradesh");
        validStates.add("Jharkhand");
        validStates.add("Karnataka");
        validStates.add("Kerala");
        validStates.add("Madhya Pradesh");
        validStates.add("Maharashtra");
        validStates.add("Manipur");
        validStates.add("Meghalaya");
        validStates.add("Mizoram");
        validStates.add("Nagaland");
        validStates.add("Odisha");
        validStates.add("Punjab");
        validStates.add("Rajasthan");
        validStates.add("Sikkim");
        validStates.add("Tamil Nadu");
        validStates.add("Telangana");
        validStates.add("Tripura");
        validStates.add("Uttar Pradesh");
        validStates.add("Uttarakhand");
        validStates.add("West Bengal");

        // Map to hold the cumulative data for each state
        Map<String, StateData> stateDataMap = new HashMap<>();

        // Create a JSON object to store the output
        JSONObject outputJson = new JSONObject();
        JSONArray stateDataArray = new JSONArray();

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            // Read the header
            if ((line = br.readLine()) != null) {
                System.out.println("Header: " + line); // Optional: Check your CSV header
            }

            // Read the data
            while ((line = br.readLine()) != null) {
                // Strip leading and trailing whitespaces and split the line by comma
                String[] data = line.split(delimiter);

                // Ensure there are enough fields
                if (data.length < 6) {  // Adjusted for 6 columns (monitoringLocation, state, pH, temperature, turbidity)
                    System.err.println("Insufficient data in line: " + line);
                    continue;
                }

                // Parse the data into appropriate types
                String monitoringLocation = data[0].trim();  // Monitoring location
                String state = data[1].trim();  // Trim spaces around state names

                // Check if the state is valid
                if (!validStates.contains(state)) {
                    System.out.println("Skipping invalid state: " + state);
                    continue; // Skip invalid entries
                }

                // Handle possible empty or invalid values in the CSV
                double pH = parseDouble(data[2]);
                double temperature = parseDouble(data[3]);
                double turbidity = parseDouble(data[4]);

                // Check for harmful conditions
                boolean isHarmful = false;

                // Check pH
                if (pH < 6.5 || pH > 8.5) {
                    isHarmful = true;
                }

                // Check temperature
                if (temperature < 0 || temperature > 35) {
                    isHarmful = true;
                }

                // Check turbidity
                if (turbidity > 5) {
                    isHarmful = true;
                }

                // Add the data to the stateDataMap for averaging later
                stateDataMap
                    .computeIfAbsent(state, k -> new StateData()) // Create StateData if not present
                    .addData(pH, temperature, turbidity);

                // Create JSON object for this line of data
                JSONObject stateJson = new JSONObject();
                stateJson.put("monitoringLocation", monitoringLocation);  // Add monitoringLocation
                stateJson.put("state", state);
                stateJson.put("pH", pH);
                stateJson.put("temperature", temperature);
                stateJson.put("turbidity", turbidity);
                stateJson.put("isHarmful", isHarmful);

                // Add the JSON object to the stateDataArray
                stateDataArray.put(stateJson);
            }

            // Store the calculated averages for each state in the JSON output
            JSONArray stateAverageArray = new JSONArray();
            for (Map.Entry<String, StateData> entry : stateDataMap.entrySet()) {
                String state = entry.getKey();
                StateData stateData = entry.getValue();

                JSONObject averageData = new JSONObject();
                averageData.put("state", state);
                averageData.put("averagePh", stateData.getAveragePh());
                averageData.put("averageTemperature", stateData.getAverageTemp());
                averageData.put("averageTurbidity", stateData.getAverageTurbidity());

                stateAverageArray.put(averageData);
            }

            // Add both state data and averages to the output JSON object
            outputJson.put("stateData", stateDataArray);
            outputJson.put("stateAverages", stateAverageArray);

            // Write the output JSON to a file
            try (FileWriter file = new FileWriter("output.json")) {
                file.write(outputJson.toString(4)); // Pretty print with indentation
                System.out.println("Successfully written JSON to output.json");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Helper method to safely parse a double from a string
    private static double parseDouble(String value) {
        if (value == null || value.trim().isEmpty()) {
            System.err.println("Warning: Empty or null value encountered, defaulting to 0.0");
            return 0.0; // Default value for missing or invalid data
        }
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            System.err.println("Error parsing number: " + e.getMessage());
            return 0.0; // Default value for parsing error
        }
    }
}
