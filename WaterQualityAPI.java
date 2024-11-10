import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class WaterQualityAPI {
    public static void main(String[] args) throws IOException {
        // Set up the HTTP server on port 8000
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/water-quality", new WaterQualityHandler());
        server.setExecutor(null);
        server.start();
        System.out.println("Server started on port 8000");
    }

    static class WaterQualityHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Database connection parameters
            String jdbcUrl = "jdbc:mysql://localhost:3306/java_project";
            String username = "root"; // replace with your MySQL username
            String password = "Svpathak@2005"; // replace with your MySQL password

            StringBuilder responseBuilder = new StringBuilder();

            try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
                 Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery("SELECT * FROM water_quality")) {

                // Build the response string with data from each row
                while (resultSet.next()) {
                    responseBuilder.append("Monitoring Location: ").append(resultSet.getString("Monitoring_Location"))
                            .append(", State: ").append(resultSet.getString("State"))
                            .append(", pH: ").append(resultSet.getFloat("pH"))
                            .append(", Temperature: ").append(resultSet.getFloat("Temperature"))
                            .append(", Turbidity: ").append(resultSet.getFloat("Turbidity"))
                            .append("\n");
                }
            } catch (Exception e) {
                e.printStackTrace();
                responseBuilder.append("Error retrieving data");
            }

            // Send the response
            String response = responseBuilder.toString();
            exchange.getResponseHeaders().set("Content-Type", "text/plain");
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}
