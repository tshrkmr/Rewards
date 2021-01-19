package edu.tkumar;

public class CreateProfileAPIRunnable implements Runnable{

    private CreateProfileActivity createProfileActivity;
    private String firstName, lastName, userName, department, story, position, password, remainingPointsToAward, location, imageBytes;

    public CreateProfileAPIRunnable(CreateProfileActivity createProfileActivity, String firstName, String lastName, String userName, String department, String story, String position, String password, String remainingPointsToAward, String location, String imageBytes) {
        this.createProfileActivity = createProfileActivity;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.department = department;
        this.story = story;
        this.position = position;
        this.password = password;
        this.remainingPointsToAward = remainingPointsToAward;
        this.location = location;
        this.imageBytes = imageBytes;
    }

    @Override
    public void run() {

    }
}
