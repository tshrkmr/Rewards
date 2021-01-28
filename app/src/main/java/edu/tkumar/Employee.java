package edu.tkumar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
    private String loggedInUserName;
    private int pointsAwarded;
    private Reward reward;
    private List<Reward> rewardList = new ArrayList<>();
    private List<Employee> employeeList = new ArrayList<>();
    private static final String TAG = "Employee";

    public Employee(String firstName, String lastName, String username, String department, String story, String position, String imageBytes) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.department = department;
        this.story = story;
        this.position = position;
        this.imageBytes = imageBytes;
    }

    public void setPointsAwarded(int pointsAwarded) {
        this.pointsAwarded = pointsAwarded;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public void setStory(String story) {
        this.story = story;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRemainingPointsToAward(String remainingPointsToAward) {
        this.remainingPointsToAward = remainingPointsToAward;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setImageBytes(String imageBytes) {
        this.imageBytes = imageBytes;
    }

    public void setLoggedInUserName(String name){
        this.loggedInUserName = name;
    }

    public int getPointsAwarded() {
        return pointsAwarded;
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

    public String getLoggedInUserName(){
        return loggedInUserName;
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
