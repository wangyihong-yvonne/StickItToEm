package edu.neu.madcourse.stickittoem;

public class UserItem {

    private final int imageSource;
    private final String username;
    private final String description;

    public UserItem(int imageSource, String itemName, String itemDesc) {
        this.imageSource = imageSource;
        this.username = itemName;
        this.description = itemDesc;
    }

    public String getUsername() {
        return username;
    }

    public String getDescription() {
        return description;
    }

    public int getImageSource() {
        return imageSource;
    }

    @Override
    public String toString() {
        return "UserItem{" +
                "imageSource=" + imageSource +
                ", username='" + username + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}