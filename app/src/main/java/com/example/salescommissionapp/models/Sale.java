package com.example.salescommissionapp.models;

public class Sale {
    private int id;
    private int salesPersonId;
    private double lebanonSales;
    private double coastalSales;
    private double northernSales;
    private double southernSales;
    private double easternSales;
    private int month;
    private int year;
    private double commission;

    public Sale(int id, int salesPersonId, double lebanonSales, double coastalSales, 
                double northernSales, double southernSales, double easternSales, 
                int month, int year) {
        this.id = id;
        this.salesPersonId = salesPersonId;
        this.lebanonSales = lebanonSales;
        this.coastalSales = coastalSales;
        this.northernSales = northernSales;
        this.southernSales = southernSales;
        this.easternSales = easternSales;
        this.month = month;
        this.year = year;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getSalesPersonId() { return salesPersonId; }
    public void setSalesPersonId(int salesPersonId) { this.salesPersonId = salesPersonId; }

    public double getLebanonSales() { return lebanonSales; }
    public void setLebanonSales(double lebanonSales) { this.lebanonSales = lebanonSales; }

    public double getCoastalSales() { return coastalSales; }
    public void setCoastalSales(double coastalSales) { this.coastalSales = coastalSales; }

    public double getNorthernSales() { return northernSales; }
    public void setNorthernSales(double northernSales) { this.northernSales = northernSales; }

    public double getSouthernSales() { return southernSales; }
    public void setSouthernSales(double southernSales) { this.southernSales = southernSales; }

    public double getEasternSales() { return easternSales; }
    public void setEasternSales(double easternSales) { this.easternSales = easternSales; }

    public int getMonth() { return month; }
    public void setMonth(int month) { this.month = month; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public double getCommission() { return commission; }
    public void setCommission(double commission) { this.commission = commission; }
} 