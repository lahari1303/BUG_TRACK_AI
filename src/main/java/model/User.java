package model;

public class User {
    protected int id;
    protected String name;
    protected String role;

    public User(int id, String name, String role) {
        this.id = id;
        this.name = name;
        this.role = role;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }

    @Override
    public String toString() {
        return "User: " + name + " (" + role + ")";
    }
}
