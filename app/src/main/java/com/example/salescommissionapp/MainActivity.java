package com.example.salescommissionapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends BaseActivity {
    private Button btnManageSalesPersons;
    private Button btnEnterSales;
    private Button btnViewSalesReport;
    private Button btnViewCommissionReport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        setupNavigationDrawer();
        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        btnManageSalesPersons = findViewById(R.id.btnManageSalesPersons);
        btnEnterSales = findViewById(R.id.btnEnterSales);
        btnViewSalesReport = findViewById(R.id.btnViewSalesReport);
        btnViewCommissionReport = findViewById(R.id.btnViewCommissionReport);
    }

    private void setupClickListeners() {
        btnManageSalesPersons.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SalesPersonActivity.class);
            startActivity(intent);
        });
            
        btnEnterSales.setOnClickListener(v -> 
            startActivity(new Intent(MainActivity.this, SalesEntryActivity.class)));
            
        btnViewSalesReport.setOnClickListener(v -> 
            startActivity(new Intent(MainActivity.this, SalesReportActivity.class)));
            
        btnViewCommissionReport.setOnClickListener(v -> 
            startActivity(new Intent(MainActivity.this, CommissionReportActivity.class)));
    }
}