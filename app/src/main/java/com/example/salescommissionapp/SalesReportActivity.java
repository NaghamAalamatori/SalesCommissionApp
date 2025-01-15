package com.example.salescommissionapp;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.salescommissionapp.adapters.SalesPersonAdapter;
import com.example.salescommissionapp.db.DatabaseHelper;
import com.example.salescommissionapp.models.Sale;
import com.example.salescommissionapp.models.SalesPerson;
import com.example.salescommissionapp.adapters.SalesAdapter;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class SalesReportActivity extends BaseActivity {
    private Spinner spinnerSalesPerson;
    private TextView textViewEmployeeNumber;
    private TextView textViewHomeRegion;
    private ImageView imageViewPhoto;
    private Spinner spinnerMonth;
    private Spinner spinnerYear;
    private Button buttonSearch;
    private RecyclerView recyclerViewSales;
    private TextView textViewTotalSales;
    private DatabaseHelper databaseHelper;
    private SalesPerson selectedSalesPerson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_report);
        setupNavigationDrawer();

        databaseHelper = new DatabaseHelper(this);
        initializeViews();
        setupSpinners();
        setupRecyclerView();
        setupSearchButton();
    }

    private void initializeViews() {
        spinnerSalesPerson = findViewById(R.id.spinnerSalesPerson);
        textViewEmployeeNumber = findViewById(R.id.textViewEmployeeNumber);
        textViewHomeRegion = findViewById(R.id.textViewHomeRegion);
        imageViewPhoto = findViewById(R.id.imageViewPhoto);
        spinnerMonth = findViewById(R.id.spinnerMonth);
        spinnerYear = findViewById(R.id.spinnerYear);
        buttonSearch = findViewById(R.id.buttonSearch);
        recyclerViewSales = findViewById(R.id.recyclerViewSales);
        textViewTotalSales = findViewById(R.id.textViewTotalSales);

        recyclerViewSales.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupSpinners() {
        // Setup Sales Person Spinner
        List<SalesPerson> salesPersons = databaseHelper.getAllSalesPersons();
        SalesPersonAdapter.SalesPersonSpinnerAdapter salesPersonAdapter =
            new SalesPersonAdapter.SalesPersonSpinnerAdapter(this, salesPersons);
        spinnerSalesPerson.setAdapter(salesPersonAdapter);

        spinnerSalesPerson.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedSalesPerson = (SalesPerson) parent.getItemAtPosition(position);
                updateSalesPersonDetails(selectedSalesPerson);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                clearSalesPersonDetails();
            }
        });

        // Setup Month Spinner
        ArrayAdapter<CharSequence> monthAdapter = ArrayAdapter.createFromResource(this,
            R.array.months, android.R.layout.simple_spinner_item);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMonth.setAdapter(monthAdapter);

        // Setup Year Spinner
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        String[] years = new String[5];
        for (int i = 0; i < 5; i++) {
            years[i] = String.valueOf(currentYear - i);
        }
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this,
            android.R.layout.simple_spinner_item, years);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerYear.setAdapter(yearAdapter);
    }

    private void updateSalesPersonDetails(SalesPerson salesPerson) {
        if (salesPerson != null) {
            textViewEmployeeNumber.setText(getString(R.string.employee_number_format, salesPerson.getEmployeeNumber()));
            textViewHomeRegion.setText(getString(R.string.home_region_format, salesPerson.getRegion()));
            if (salesPerson.getPhotoPath() != null) {
                try {
                    imageViewPhoto.setImageURI(Uri.parse(salesPerson.getPhotoPath()));
                } catch (SecurityException e) {
                    imageViewPhoto.setImageResource(R.drawable.ic_person);
                }
            } else {
                imageViewPhoto.setImageResource(R.drawable.ic_person);
            }
        }
    }

    private void clearSalesPersonDetails() {
        textViewEmployeeNumber.setText(getString(R.string.employee_number_format, ""));
        textViewHomeRegion.setText(getString(R.string.home_region_format, ""));
        imageViewPhoto.setImageResource(R.drawable.ic_person);
    }

    private void setupRecyclerView() {
        recyclerViewSales.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupSearchButton() {
        buttonSearch.setOnClickListener(v -> {
            loadSalesData();
        });
    }

    private void loadSalesData() {
        int selectedMonth = spinnerMonth.getSelectedItemPosition() + 1;
        int selectedYear = Integer.parseInt(spinnerYear.getSelectedItem().toString());
        SalesPerson selectedPerson = (SalesPerson) spinnerSalesPerson.getSelectedItem();

        if (selectedPerson != null) {
            List<Sale> sales = databaseHelper.getSalesByPersonMonthYear(
                selectedPerson.getId(),
                selectedMonth,
                selectedYear
            );

            double totalLebanonSales = 0;
            double totalCoastalSales = 0;
            double totalNorthernSales = 0;
            double totalSouthernSales = 0;
            double totalEasternSales = 0;

            for (Sale sale : sales) {
                totalLebanonSales += sale.getLebanonSales();
                totalCoastalSales += sale.getCoastalSales();
                totalNorthernSales += sale.getNorthernSales();
                totalSouthernSales += sale.getSouthernSales();
                totalEasternSales += sale.getEasternSales();
            }

            // Format the total sales for each region
            String totalSalesText = String.format(Locale.getDefault(),
                "%s\n%s\n%s\n%s\n%s\n\n%s",
                getString(R.string.lebanon_sales_format, formatAmount(totalLebanonSales)),
                getString(R.string.coastal_sales_format, formatAmount(totalCoastalSales)),
                getString(R.string.northern_sales_format, formatAmount(totalNorthernSales)),
                getString(R.string.southern_sales_format, formatAmount(totalSouthernSales)),
                getString(R.string.eastern_sales_format, formatAmount(totalEasternSales)),
                getString(R.string.total_sales_format, 
                    formatAmount(totalLebanonSales + totalCoastalSales + totalNorthernSales + 
                               totalSouthernSales + totalEasternSales))
            );

            textViewTotalSales.setText(totalSalesText);

            // Update RecyclerView with sales data
            SalesAdapter adapter = new SalesAdapter(sales);
            recyclerViewSales.setAdapter(adapter);
        }
    }

    private String formatAmount(double amount) {
        return String.format(Locale.getDefault(), "%,.2f", amount);
    }
} 