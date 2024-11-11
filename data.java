import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class dataconnection {

    private static final String url = "jdbc:mysql://localhost:3306/java_project?useSSL=false";
    private static final String USER = "root";
    private static final String PASSWORD = "Svpathak@2005";

    public static void main(String[] args) {
        createRecord("Lake Tahoe", "California", 7.2f, 15.5f, 0.8f);
        createRecord("Lake Bandra", "India", 6.2f, 20.5f, 0.8f);

        readRecords();
        
        updateRecord(3, "Updated Location2", 7.7f);
        deleteRecord(1);

        calculateAverages();
        categorizeAndDisplayRecords();
    }

    public static void createRecord(String location, String state, float pH, float temperature, float turbidity) {
        String query = "INSERT INTO water_quality_crud (Monitoring_Location, State, pH, Temperature, Turbidity) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(url, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, location);
            statement.setString(2, state);
            statement.setFloat(3, pH);
            statement.setFloat(4, temperature);
            statement.setFloat(5, turbidity);

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("A new record was inserted successfully! " + rowsInserted);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Read: Retrieve all records
    public static void readRecords() {
        String query = "SELECT * FROM water_quality_crud;";

        try (Connection connection = DriverManager.getConnection(url, USER, PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                System.out.println(
                        "ID: " + resultSet.getInt("id") +
                        ", Location: " + resultSet.getString("Monitoring_Location") +
                        ", State: " + resultSet.getString("State") +
                        ", pH: " + resultSet.getFloat("pH") +
                        ", Temperature: " + resultSet.getFloat("Temperature") +
                        ", Turbidity: " + resultSet.getFloat("Turbidity"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Update: Update an existing record by ID
    public static void updateRecord(int id, String newLocation, float newPH) {
        String updateQuery = "UPDATE water_quality_crud SET Monitoring_Location = ?, pH = ? WHERE id = ?";

        try (Connection connection = DriverManager.getConnection(url, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(updateQuery)) {

            statement.setString(1, newLocation);
            statement.setFloat(2, newPH);
            statement.setInt(3, id);

            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Record updated successfully!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Delete: Delete a record by ID
    public static void deleteRecord(int id) {
        String deleteQuery = "DELETE FROM water_quality_crud WHERE id = ?";

        try (Connection connection = DriverManager.getConnection(url, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(deleteQuery)) {

            statement.setInt(1, id);

            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Record deleted successfully!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Calculate averages for pH, temperature, and turbidity
    public static void calculateAverages() {
        String query = "SELECT AVG(pH) AS avg_pH, AVG(Temperature) AS avg_Temperature, AVG(Turbidity) AS avg_Turbidity FROM water_quality_crud;";

        try (Connection connection = DriverManager.getConnection(url, USER, PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            if (resultSet.next()) {
                float avgPH = resultSet.getFloat("avg_pH");
                float avgTemperature = resultSet.getFloat("avg_Temperature");
                float avgTurbidity = resultSet.getFloat("avg_Turbidity");

                System.out.println("Average pH: " + avgPH);
                System.out.println("Average Temperature: " + avgTemperature);
                System.out.println("Average Turbidity: " + avgTurbidity);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Categorize records as "harmful" or "useful" based on criteria for pH, temperature, and turbidity
    public static void categorizeAndDisplayRecords() {
        String query = "SELECT * FROM water_quality_crud;";

        try (Connection connection = DriverManager.getConnection(url, USER, PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String location = resultSet.getString("Monitoring_Location");
                float pH = resultSet.getFloat("pH");
                float temperature = resultSet.getFloat("Temperature");
                float turbidity = resultSet.getFloat("Turbidity");

                // Define criteria for categorization
                String pHCategory = (pH >= 6.5 && pH <= 8.5) ? "Useful" : "Harmful";
                String tempCategory = (temperature >= 10 && temperature <= 25) ? "Useful" : "Harmful";
                String turbidityCategory = (turbidity <= 1) ? "Useful" : "Harmful";

                System.out.println("ID: " + id + ", Location: " + location);
                System.out.println("  pH: " + pH + " (" + pHCategory + ")");
                System.out.println("  Temperature: " + temperature + " (" + tempCategory + ")");
                System.out.println("  Turbidity: " + turbidity + " (" + turbidityCategory + ")");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
