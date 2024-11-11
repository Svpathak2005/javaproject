import java.io.*;
import java.sql.*;
import java.util.*;
import org.json.JSONArray;
import org.json.JSONObject;

public class sample {

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/water";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASSWORD = "";

    public static void main(String[] args) {
        String csvFile = "modified_sample_data.csv";
        storeCSVToDatabase(csvFile);
        processAndCalculateAverages();
        updateWaterQuality("NARSIMEHTA TALAV- JUNAGADH", 7.0, 25.0, 3.0);
        deleteWaterQuality("NAUPADA SWAMPS");
    }

    private static void storeCSVToDatabase(String csvFile) {
        String insertQuery = "INSERT INTO water_quality (location, state, ph, temperature, turbidity) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             BufferedReader br = new BufferedReader(new FileReader(csvFile));
             PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {

            br.readLine();
            String line;

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);

                if (data.length >= 5) {
                    String location = data[0].trim();
                    String state = data[1].trim();
                    try {
                        double ph = Double.parseDouble(data[2].trim());
                        double temperature = Double.parseDouble(data[3].trim());
                        double turbidity = Double.parseDouble(data[4].trim());

                        pstmt.setString(1, location);
                        pstmt.setString(2, state);
                        pstmt.setDouble(3, ph);
                        pstmt.setDouble(4, temperature);
                        pstmt.setDouble(5, turbidity);
                        pstmt.executeUpdate();
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid data: " + Arrays.toString(data));
                    }
                }
            }
            System.out.println("CSV data successfully stored in MySQL database.");

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    private static void processAndCalculateAverages() {
        String selectQuery = "SELECT state, location, ph, temperature, turbidity FROM water_quality";
        Map<String, StateData> stateDataMap = new HashMap<>();
        JSONArray locationsArray = new JSONArray();

        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(selectQuery)) {

            while (rs.next()) {
                String state = rs.getString("state");
                String location = rs.getString("location");
                double ph = rs.getDouble("ph");
                double temperature = rs.getDouble("temperature");
                double turbidity = rs.getDouble("turbidity");

                String waterQuality = checkWaterQuality(ph, temperature, turbidity);

                JSONObject locationData = new JSONObject();
                locationData.put("location", location);
                locationData.put("state", state);
                locationData.put("ph", ph);
                locationData.put("temperature", temperature);
                locationData.put("turbidity", turbidity);
                locationData.put("waterQuality", waterQuality);
                locationsArray.put(locationData);

                stateDataMap.putIfAbsent(state, new StateData());
                StateData stateData = stateDataMap.get(state);
                stateData.sumPh += ph;
                stateData.sumTemperature += temperature;
                stateData.sumTurbidity += turbidity;
                stateData.count++;
            }

            JSONObject averages = new JSONObject();
            for (Map.Entry<String, StateData> entry : stateDataMap.entrySet()) {
                String state = entry.getKey();
                StateData stateData = entry.getValue();
                averages.put(state, new JSONObject()
                        .put("averagePh", stateData.sumPh / stateData.count)
                        .put("averageTemperature", stateData.sumTemperature / stateData.count)
                        .put("averageTurbidity", stateData.sumTurbidity / stateData.count)
                );
            }

            JSONObject outputJson = new JSONObject();
            outputJson.put("locations", locationsArray);
            outputJson.put("averages", averages);

            try (FileWriter file = new FileWriter("averages.json")) {
                file.write(outputJson.toString(4));
            }

            System.out.println("Data written to averages.json");

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    private static String checkWaterQuality(double ph, double temperature, double turbidity) {
        if (ph < 6.5 || ph > 8.5) {
            return "Poor";
        }
        if (temperature > 30) {
            return "Poor";
        }
        if (turbidity > 5) {
            return "Poor";
        }
        return "Good";
    }

    private static void updateWaterQuality(String location, double ph, double temperature, double turbidity) {
        String updateQuery = "UPDATE water_quality SET ph = ?, temperature = ?, turbidity = ? WHERE location = ?";

        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {

            pstmt.setDouble(1, ph);
            pstmt.setDouble(2, temperature);
            pstmt.setDouble(3, turbidity);
            pstmt.setString(4, location);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Water quality updated successfully for location: " + location);
            } else {
                System.out.println("No records found for location: " + location);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void deleteWaterQuality(String location) {
        String deleteQuery = "DELETE FROM water_quality WHERE location = ?";

        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(deleteQuery)) {

            pstmt.setString(1, location);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Water quality data deleted successfully for location: " + location);
            } else {
                System.out.println("No records found for location: " + location);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static class StateData {
        double sumPh = 0;
        double sumTemperature = 0;
        double sumTurbidity = 0;
        int count = 0;
    }
}
