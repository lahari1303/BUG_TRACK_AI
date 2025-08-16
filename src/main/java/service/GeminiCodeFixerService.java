package service;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONObject;

//import main.BugTracker;

public class GeminiCodeFixerService {
    private static final String API_KEY = "AIzaSyCFsbOkMrjOtvSjznzv8fRoS_aX4OEMXsE"; // Replace with your Gemini key
    private static final String MODEL = "gemini-1.5-flash";

    public String getFixedCode(String userCode) {
        try {
            URL url = new URL(
                    "https://generativelanguage.googleapis.com/v1/models/" + MODEL + ":generateContent?key=" + API_KEY);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");

            String prompt = "Fix and optimize this Java code:\n" + userCode;
            String jsonRequest = """
                    {
                      "contents": [{
                        "parts": [{
                          "text": "%s"
                        }]
                      }]
                    }
                    """.formatted(prompt.replace("\"", "\\\""));

            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonRequest.getBytes());
                os.flush();
            }

            int responseCode = conn.getResponseCode();

            Scanner scanner;
            if (responseCode == 200) {
                scanner = new Scanner(conn.getInputStream());
            } else {
                scanner = new Scanner(conn.getErrorStream());
            }

            StringBuilder response = new StringBuilder();
            while (scanner.hasNext()) {
                response.append(scanner.nextLine());
            }
            scanner.close();

            if (responseCode != 200) {
                return "❌ Gemini API Error: " + response.toString();
            }

            // Parse the fixed code from JSON response
            JSONObject json = new JSONObject(response.toString());
            JSONArray candidates = json.getJSONArray("candidates");
            JSONObject content = candidates.getJSONObject(0).getJSONObject("content");
            JSONArray parts = content.getJSONArray("parts");
            return parts.getJSONObject(0).getString("text");

        } catch (Exception e) {
            return "❌ Exception: " + e.getMessage();
        }
    }

    public void saveToFile(String code, String filename) {
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write(code);
            System.out.println("✅ Code saved to " + filename);
        } catch (IOException e) {
            System.out.println("❌ Error saving file: " + e.getMessage());
        }
    }

    @SuppressWarnings("deprecation")
    public void runJavaFile(String filename) {
        try {
            // Compile
            Process compile = Runtime.getRuntime().exec("javac " + filename);
            compile.waitFor();

            // Extract class name
            String className = filename.replace(".java", "");

            // Run
            Process run = Runtime.getRuntime().exec("java " + className);
            BufferedReader reader = new BufferedReader(new InputStreamReader(run.getInputStream()));
            String line;
            System.out.println("▶️ Output:");
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

        } catch (Exception e) {
            System.out.println("❌ Error running code: " + e.getMessage());
        }
    }
}