package main;

import db.DBConnection;
import model.Bug;
// import model.Developer;
import service.AIService;
import service.BugService;
import service.CodeAIService;
import service.GeminiCodeFixerService;
import service.GptCodeFixerService;
import exception.BugAlreadyExistsException;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class BugTracker {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        BugService bugService = new BugService();
        AIService aiService = new AIService();

        System.out.println("🎯 Welcome to BugTrackAI!");

        try {
            DBConnection.getConnection();
            System.out.println("✅ Connected to MySQL database!");
        } catch (SQLException e) {
            System.out.println("❌ Connection error: " + e.getMessage());
            // return;
        }

        while (true) {
            System.out.println("\nChoose an action:");
            System.out.println("1. Add Bug");
            System.out.println("2. List Bugs");
            System.out.println("3. Update Bug");
            System.out.println("4. Delete Bug");
            System.out.println("5. Exit");
            System.out.println("6. Analyze Java Code (AI Bot)");
            System.out.println("7. Fix Java Code with GPT-4 AI");
            System.out.println("8. Fix Java Code with Gemini AI (Free)");

            System.out.print("Enter choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    System.out.print("Bug ID: ");
                    int id = scanner.nextInt();
                    scanner.nextLine();
                    System.out.print("Title: ");
                    String title = scanner.nextLine();
                    System.out.print("Description: ");
                    String desc = scanner.nextLine();
                    System.out.print("Severity (Low/Medium/High): ");
                    String severity = scanner.nextLine();

                    Bug bug = new Bug(id, title, desc, severity);
                    try {
                        bugService.addBugToDatabase(bug);
                        System.out.println(aiService.analyzeBug(bug));
                    } catch (BugAlreadyExistsException e) {
                        System.out.println("⚠️ " + e.getMessage());
                    }
                    break;

                case 2:
                    System.out.println("\n📋 Bug List:");
                    List<Bug> bugs = bugService.getAllBugs();
                    bugs.stream().forEach(System.out::println);
                    break;

                case 3:
                    System.out.print("Bug ID to update: ");
                    int updateId = scanner.nextInt();
                    scanner.nextLine();
                    System.out.print("New Status: ");
                    String newStatus = scanner.nextLine();
                    System.out.print("New Severity: ");
                    String newSev = scanner.nextLine();
                    bugService.updateBugStatusAndSeverity(updateId, newStatus, newSev);
                    break;

                case 4:
                    System.out.print("Bug ID to delete: ");
                    int deleteId = scanner.nextInt();
                    scanner.nextLine();
                    bugService.deleteBugById(deleteId);
                    break;

                case 5:
                    System.out.println("👋 Exiting BugTrackAI. Bye!");
                    scanner.close();
                    return;
                case 6:
                    System.out.println("📥 Paste your Java code (end with 'END'):");
                    StringBuilder codeBuilder = new StringBuilder();
                    String line;
                    while (!(line = scanner.nextLine()).equals("END")) {
                        codeBuilder.append(line).append("\n");
                    }
                    CodeAIService codeAI = new CodeAIService();
                    String analysis = codeAI.analyzeAndFixCode(codeBuilder.toString());
                    System.out.println("\n🤖 AI Analysis:\n" + analysis);
                    break;
                case 7:
                    System.out.println("📥 Paste your buggy Java code (end with 'END'):");
                    StringBuilder rawCode = new StringBuilder();
                    String lineIn;
                    while (!(lineIn = scanner.nextLine()).equals("END")) {
                        rawCode.append(lineIn).append("\n");
                    }

                    GptCodeFixerService gptFixer = new GptCodeFixerService();
                    String fixedCode = gptFixer.getFixedCode(rawCode.toString());
                    System.out.println("\n🤖 GPT-4 Fixed Code:\n" + fixedCode);
                    break;
                case 8:
                    System.out.println("📥 Paste your buggy Java code (end with 'END'):");
                    StringBuilder geminiCode = new StringBuilder();
                    String gemLine;
                    while (!(gemLine = scanner.nextLine()).equals("END")) {
                        geminiCode.append(gemLine).append("\n");
                    }

                    GeminiCodeFixerService geminiFixer = new GeminiCodeFixerService();
                    String geminiFixed = geminiFixer.getFixedCode(geminiCode.toString());
                    String fixed = extractCodeBlock(geminiFixed);

                    geminiFixer.saveToFile(fixed, "TestCode.java");
                    geminiFixer.runJavaFile("TestCode.java");
                    System.out.println("\n🤖 Gemini AI Fixed Code:\n" + geminiFixed);
                    break;

                default:
                    System.out.println("❌ Invalid choice. Try again.");
            }
        }
    }

    private static String extractCodeBlock(String response) {
        int start = response.indexOf("```java");
        int end = response.indexOf("```", start + 7);
        if (start != -1 && end != -1) {
            return response.substring(start + 7, end).trim();
        }
        return response; // fallback
    }

}
