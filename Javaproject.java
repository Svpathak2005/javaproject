package javaproject;

public class demo {package javaproject;


    import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Javaproject {
    public static void main(String[] args) {
        String csvFile = "./csvdata.csv"; 
        String line;
        String delimiter = ",";  

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            // Read the header
            if ((line = br.readLine()) != null) {
                System.out.println("Header: " + line);
            }
            // Read the data
            while ((line = br.readLine()) != null) {
                String[] data = line.split(delimiter);
                // Parse the data into appropriate types
                double pH = Double.parseDouble(data[0]);
                double temperature = Double.parseDouble(data[1]);
                double turbidity = Double.parseDouble(data[2]);
                double conductivity = Double.parseDouble(data[3]);
                double dosage = Double.parseDouble(data[4]);
if (pH < 6.5 || pH > 8.5){
            
                System.out.printf("HARMFULL  pH: %.2f, Temperature: %.2f, Turbidity: %.2f, Conductivity: %.2f, Dosage: %.2f%n",
                        pH, temperature, turbidity, conductivity, dosage);
            }else{
                System.out.printf("GOOD  pH: %.2f, Temperature: %.2f, Turbidity: %.2f, Conductivity: %.2f, Dosage: %.2f%n",
                pH, temperature, turbidity, conductivity, dosage);
            }
        }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            System.err.println("Error parsing number from the CSV file: " + e.getMessage());
        }
    }
}

    
}

    
}
