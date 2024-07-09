package com.example.vehicletracking;

public class BusModel {
    private String busName, busModel, busNumber, busLongitude, busLatitude, busImage, busDocID, CurrentUserID,busRealAddress;

    public BusModel() {
    }

    public BusModel(String busName, String busModel, String busNumber, String busLongitude, String busLatitude, String busImage, String busDocID, String currentUserID,String busRealAddress) {
        this.busName = busName;
        this.busModel = busModel;
        this.busNumber = busNumber;
        this.busLongitude = busLongitude;
        this.busLatitude = busLatitude;
        this.busImage = busImage;
        this.busDocID = busDocID;
        this.CurrentUserID = currentUserID;
        this.busRealAddress=busRealAddress;
    }



    public String getBusRealAddress() {
        return busRealAddress;
    }

    public void setBusRealAddress(String busRealAddress) {
        this.busRealAddress = busRealAddress;
    }

    public String getBusNumber() {
        return busNumber;
    }

    public void setBusNumber(String busNumber) {
        this.busNumber = busNumber;
    }

    public String getBusImage() {
        return busImage;
    }

    public void setBusImage(String busImage) {
        this.busImage = busImage;
    }

    public String getCurrentUserID() {
        return CurrentUserID;
    }

    public void setCurrentUserID(String currentUserID) {
        CurrentUserID = currentUserID;
    }

    public String getBusDocID() {
        return busDocID;
    }

    public void setBusDocID(String busDocID) {
        this.busDocID = busDocID;
    }

    public String getBusName() {
        return busName;
    }

    public void setBusName(String busName) {
        this.busName = busName;
    }

    public String getBusModel() {
        return busModel;
    }

    public void setBusModel(String busModel) {
        this.busModel = busModel;
    }

    public String getBusLongitude() {
        return busLongitude;
    }

    public void setBusLongitude(String busLongitude) {
        this.busLongitude = busLongitude;
    }

    public String getBusLatitude() {
        return busLatitude;
    }

    public void setBusLatitude(String busLatitude) {
        this.busLatitude = busLatitude;
    }

    @Override
    public String toString() {
        return "BusModel{" +
                "busName='" + busName + '\'' +
                ", busModel='" + busModel + '\'' +
                ", busNumber='" + busNumber + '\'' +
                ", busLongitude='" + busLongitude + '\'' +
                ", busLatitude='" + busLatitude + '\'' +
                ", busImage='" + busImage + '\'' +
                ", busDocID='" + busDocID + '\'' +
                ", CurrentUserID='" + CurrentUserID + '\'' +
                ", busRealAddress='" + busRealAddress + '\'' +
                '}';
    }
}

