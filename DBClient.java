import java.io.BufferedReader;
import java.io.InputStreamReader;

public class DBClient {
    /**
     
Executes an SQL query via the Python wrapper and returns the result
@param sql The SQL query to execute
@return The query result as a string*/
public static String executeQuery(String sql) {
    try {// Run Python script with query as argument
        ProcessBuilder pb = new ProcessBuilder("python", "db_wrapper.py", sql);
        Process process = pb.start();

            // Capture output from Python
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            StringBuilder result = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                result.append(line).append("\n");
            }

            // Wait for process to complete
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                // Read error stream if process failed
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                StringBuilder errorMessage = new StringBuilder();
                while ((line = errorReader.readLine()) != null) {
                    errorMessage.append(line).append("\n");
                }
                System.err.println("Python process error: " + errorMessage);
            }

            return result.toString().trim();
        } 
        catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    // For testing
    public static void main(String[] args) {
        System.out.println("Testing DBClient with query: SELECT COUNT() from inventory");
        System.out.println(DBClient.executeQuery("SELECT COUNT() from inventory"));
    }
}