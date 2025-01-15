package com.example.salescommissionapp;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;

public class BaseActivity extends AppCompatActivity {
    protected DrawerLayout drawerLayout;
    protected NavigationView navigationView;
    protected ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void setupNavigationDrawer() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        
        actionBarDrawerToggle = new ActionBarDrawerToggle(
            this, 
            drawerLayout, 
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        navigationView.setNavigationItemSelectedListener(item -> {
            Intent intent = null;
            
            int itemId = item.getItemId();
            if (itemId == R.id.nav_manage_sales_persons) {
                intent = new Intent(this, SalesPersonActivity.class);
            } else if (itemId == R.id.nav_sales_entry) {
                intent = new Intent(this, SalesEntryActivity.class);
            } else if (itemId == R.id.nav_sales_report) {
                intent = new Intent(this, SalesReportActivity.class);
            } else if (itemId == R.id.nav_commission_report) {
                intent = new Intent(this, CommissionReportActivity.class);
            }

            if (intent != null && !this.getClass().equals(intent.getComponent().getClass())) {
                startActivity(intent);
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.closeDrawer(GravityCompat.START);
            }
            
            return true;
        });
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
} 