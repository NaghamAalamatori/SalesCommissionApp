package com.example.salescommissionapp.models;

public class SalesPerson {
    private int id;
    private String name;
    private String employeeNumber;
    private String region;
    private String photoPath;
    private long registrationDate;

    public SalesPerson(int id, String name, String employeeNumber, String region, String photoPath) {
        this.id = id;
        this.name = name;
        this.employeeNumber = employeeNumber;
        this.region = region;
        this.photoPath = photoPath;
        this.registrationDate = System.currentTimeMillis();
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getEmployeeNumber() { return employeeNumber; }
    public void setEmployeeNumber(String employeeNumber) { this.employeeNumber = employeeNumber; }
    
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
    
    public String getPhotoPath() { return photoPath; }
    public void setPhotoPath(String photoPath) { this.photoPath = photoPath; }
    
    public long getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(long registrationDate) { this.registrationDate = registrationDate; }

    @Override
    public String toString() {
        return name + " (" + employeeNumber + ")";
    }
} 