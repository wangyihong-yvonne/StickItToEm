package edu.neu.madcourse.stickittoem;

public class User {
    String name;
    String token;

    public User(String name, String token) {
        this.name = name;
        this.token = token;
    }

    public User() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "User: " + name;
    }
}
