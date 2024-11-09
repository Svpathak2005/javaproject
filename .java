import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class code {
    public static Connection connect() {
        Connection connection = null;
        
        try {
            // Load the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Establish the connection
            connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/java_project", // Database URL
                "root", // MySQL username
                "Svpathak@2005" // MySQL password
            );
            
            System.out.println("Connection to database successful!");
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC Driver not found!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Error connecting to database!");
            e.printStackTrace();
        }
        
        return connection;
    }
    
    public static void fetchData() {
        Connection connection = connect();
        
        if (connection != null) {
            try {
                // Create a statement
                Statement statement = connection.createStatement();
                
                // Execute the query
                ResultSet resultSet = statement.executeQuery("SELECT * FROM water_quality");
                
                // Process the result set
                while (resultSet.next()) {
                    double pH = resultSet.getDouble("pH");
                    double temperature = resultSet.getDouble("temperature");
                    double turbidity = resultSet.getDouble("turbidity");

                    System.out.println("ID: " + "" + ", pH: " + pH + ", Temperature: " + temperature + ", Turbidity: " + turbidity);
                }
                
                // Close resources
                resultSet.close();
                statement.close();
                connection.close();
            } catch (SQLException e) {
                System.out.println("Error executing query!");
                e.printStackTrace();
            }
        }
    }
    
    public static void main(String[] args) {
        fetchData();
    }
}
