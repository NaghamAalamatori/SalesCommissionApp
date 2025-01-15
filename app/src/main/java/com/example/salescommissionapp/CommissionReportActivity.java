package com.example.salescommissionapp;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.salescommissionapp.adapters.CommissionAdapter;
import com.example.salescommissionapp.adapters.SalesPersonAdapter;
import com.example.salescommissionapp.db.DatabaseHelper;
import com.example.salescommissionapp.models.Sale;
import com.example.salescommissionapp.models.SalesPerson;
import java.util.Calendar;
import java.util.List;

public class CommissionReportActivity extends BaseActivity {
    private Spinner spinnerSalesPerson;
    private Spinner spinnerMonth;
    private Spinner spinnerYear;
    private Button buttonSearch;
    private RecyclerView recyclerViewCommissions;
    private TextView textViewTotalCommission;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commission_report);
        setupNavigationDrawer();

        databaseHelper = new DatabaseHelper(this);
        initializeViews();
        setupSpinners();
        setupRecyclerView();
        setupSearchButton();
    }

    private void initializeViews() {
        spinnerSalesPerson = findViewById(R.id.spinnerSalesPerson);
        spinnerMonth = findViewById(R.id.spinnerMonth);
        spinnerYear = findViewById(R.id.spinnerYear);
        buttonSearch = findViewById(R.id.buttonSearch);
        recyclerViewCommissions = findViewById(R.id.recyclerViewCommissions);
        textViewTotalCommission = findViewById(R.id.textViewTotalCommission);
        recyclerViewCommissions.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupSpinners() {
        // Setup Sales Person Spinner
        List<SalesPerson> salesPersons = databaseHelper.getAllSalesPersons();
        SalesPersonAdapter.SalesPersonSpinnerAdapter salesPersonAdapter = 
            new SalesPersonAdapter.SalesPersonSpinnerAdapter(this, salesPersons);
        spinnerSalesPerson.setAdapter(salesPersonAdapter);

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

    private void setupSearchButton() {
        buttonSearch.setOnClickListener(v -> {
            SalesPerson selectedPerson = (SalesPerson) spinnerSalesPerson.getSelectedItem();
            int selectedMonth = spinnerMonth.getSelectedItemPosition() + 1;
            int selectedYear = Integer.parseInt(spinnerYear.getSelectedItem().toString());

            List<Sale> sales = databaseHelper.getSalesByPersonMonthYear(
                selectedPerson.getId(),
                selectedMonth,
                selectedYear
            );

            double totalCommission = 0;
            for (Sale sale : sales) {
                totalCommission += sale.getCommission();
            }

            String formattedTotal = String.format("%.2f", totalCommission);
            textViewTotalCommission.setText(getString(R.string.total_commission_format, formattedTotal));

            CommissionAdapter adapter = new CommissionAdapter(sales, selectedPerson);
            recyclerViewCommissions.setAdapter(adapter);
        });
    }

    private void setupRecyclerView() {
        recyclerViewCommissions.setLayoutManager(new LinearLayoutManager(this));
    }
} 