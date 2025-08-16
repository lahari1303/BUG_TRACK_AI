package service;

public class CodeAIService {

    public String analyzeAndFixCode(String code) {
        StringBuilder fixedCode = new StringBuilder();
        String[] lines = code.split("\\n");

        for (String line : lines) {
            String trimmed = line.trim();

            // Fix 1: Replace print() with System.out.println()
            if (trimmed.matches("print\\s*\\(.*\\)\\s*;?")) {
                line = line.replace("print", "System.out.println");
                line += " // 🔧 Fixed print to System.out.println";
            }

            // Fix 2: Add missing semicolons
            if (!trimmed.isEmpty()
                    && !trimmed.endsWith(";")
                    && !trimmed.endsWith("{")
                    && !trimmed.endsWith("}")
                    && !trimmed.startsWith("//")
                    && !trimmed.contains("if")
                    && !trimmed.contains("else")
                    && !trimmed.contains("for")
                    && !trimmed.contains("while")
                    && !trimmed.contains("class")
                    && !trimmed.contains("public")
                    && !trimmed.contains("static")
                    && !trimmed.contains("void")) {
                line = line + "; // 🔧 Added missing semicolon";
            }

            // Fix 3: Warn about uninitialized variables
            if (trimmed.matches("int\\s+[a-zA-Z_][a-zA-Z0-9_]*\\s*;")) {
                line += " // ⚠️ Variable declared but not initialized";
            }

            fixedCode.append(line).append("\n");
        }

        return fixedCode.toString();
    }
}
