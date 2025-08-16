package model;

public class Bug {
    private int id;
    private String title;
    private String description;
    private String status;
    private String severity;

    public Bug(int id, String title, String description, String severity) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.severity = severity;
        this.status = "Open"; // default status
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public String getSeverity() {
        return severity;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "[" + id + "] " + title + " - " + severity + " (" + status + ")";
    }
}
