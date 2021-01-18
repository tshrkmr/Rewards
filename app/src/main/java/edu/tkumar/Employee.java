package edu.tkumar;

import android.util.Log;

import java.io.Serializable;

public class Employee implements Serializable {
    private String name;
    private String department;
    private String title;
    private double points;
    private String story;
    private static final String TAG = "Employee";

    public Employee(String name, String department, String title, int points, String story) {
        this.name = name;
        this.department = department;
        this.title = title;
        this.points = points;
        this.story = story;
        Log.d(TAG, "Employee: employee created");
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public void setStory(String story) {
        this.story = story;
    }

    public String getName() {
        return name;
    }

    public String getDepartment() {
        return department;
    }

    public String getTitle() {
        return title;
    }

    public double getPoints() {
        return points;
    }

    public String getStory() {
        return story;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "name='" + name + '\'' +
                ", department='" + department + '\'' +
                ", position='" + title + '\'' +
                ", points=" + points +
                ", story='" + story + '\'' +
                '}';
    }
}
