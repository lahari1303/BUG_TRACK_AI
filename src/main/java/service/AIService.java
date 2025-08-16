package service;

import model.Bug;

public class AIService {

    public String analyzeBug(Bug bug) {
        String description = bug.getDescription().toLowerCase();

        if (description.contains("login") || description.contains("log in")) {
            return "🧠 Suggestion: Check user credentials or authentication service.";
        } else if (description.contains("null pointer")) {
            return "🧠 Suggestion: Possible uninitialized variable. Use null checks.";
        } else if (description.contains("timeout")) {
            return "🧠 Suggestion: Check for network delays or server overload.";
        } else if (description.contains("database")) {
            return "🧠 Suggestion: Check your DB connection string and credentials.";
        } else if (description.contains("crash") || description.contains("exception")) {
            return "🧠 Suggestion: Check stack trace for detailed exception and fix source.";
        } else {
            return "🧠 Suggestion: No specific fix found. Please debug with logs and breakpoints.";
        }
    }
}
