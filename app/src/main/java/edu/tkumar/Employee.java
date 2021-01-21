package edu.tkumar;

import android.util.Log;

import java.io.Serializable;

public class Employee implements Serializable {
    private String firstName;
    private String lastName;
    private String username;
    private String department;
    private String story;
    private String position;
    private String password;
    private String remainingPointsToAward;
    private String location;
    private String imageBytes;
    private Reward reward;

    private static final String TAG = "Employee";

    public Employee(String firstName, String lastName, String username, String department, String story, String position, String password, String remainingPointsToAward, String location, String imageBytes, Reward reward) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.department = department;
        this.story = story;
        this.position = position;
        this.password = password;
        this.remainingPointsToAward = remainingPointsToAward;
        this.location = location;
        this.imageBytes = imageBytes;
        this.reward = reward;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUsername() {
        return username;
    }

    public String getDepartment() {
        return department;
    }

    public String getStory() {
        return story;
    }

    public String getPosition() {
        return position;
    }

    public String getPassword() {
        return password;
    }

    public String getRemainingPointsToAward() {
        return remainingPointsToAward;
    }

    public String getLocation() {
        return location;
    }

    public String getImageBytes() {
        return imageBytes;
    }

    public Reward getReward() {
        return reward;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", username='" + username + '\'' +
                ", department='" + department + '\'' +
                ", story='" + story + '\'' +
                ", position='" + position + '\'' +
                ", password='" + password + '\'' +
                ", remainingPointsToAward='" + remainingPointsToAward + '\'' +
                ", location='" + location + '\'' +
                ", imageBytes='" + imageBytes + '\'' +
                ", reward=" + reward +
                '}';
    }
}
