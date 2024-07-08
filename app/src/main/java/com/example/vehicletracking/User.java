package com.example.vehicletracking;

public class User {
    private String userName,userNumber,userEmail,userPassword,userId;

    public User() {

    }

    public User(String userName, String userNumber, String userEmail, String userPassword,String userId) {
        this.userName = userName;
        this.userNumber = userNumber;
        this.userEmail = userEmail;
        this.userPassword = userPassword;
        this.userId=userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserNumber() {
        return userNumber;
    }

    public void setUserNumber(String userNumber) {
        this.userNumber = userNumber;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
