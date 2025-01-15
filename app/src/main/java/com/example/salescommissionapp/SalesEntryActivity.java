package com.example.salescommissionapp;

import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.example.salescommissionapp.adapters.SalesPersonAdapter;
import com.google.android.material.textfield.TextInputEditText;
import com.example.salescommissionapp.db.DatabaseHelper;
import com.example.salescommissionapp.models.SalesPerson;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class SalesEntryActivity extends BaseActivity {
    private Spinner spinnerSalesPerson;
    private TextView textViewEmployeeNumber;
    private TextView textViewHomeRegion;
    private ImageView imageViewPhoto;
    private TextInputEditText editTextLebanonSales;
    private TextInputEditText editTextCoastalSales;
    private TextInputEditText editTextNorthernSales;
    private TextInputEditText editTextSouthernSales;
    private TextInputEditText editTextEasternSales;
    private Button buttonSave;
    private DatabaseHelper databaseHelper;
    private SalesPerson selectedSalesPerson;
    private Spinner spinnerMonth;
    private Spinner spinnerYear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_entry);
        setupNavigationDrawer();

        databaseHelper = new DatabaseHelper(this);
        initializeViews();
        setupSpinners();
        setupSaveButton();
    }

    private void initializeViews() {
        spinnerSalesPerson = findViewById(R.id.spinnerSalesPerson);
        textViewEmployeeNumber = findViewById(R.id.textViewEmployeeNumber);
        textViewHomeRegion = findViewById(R.id.textViewHomeRegion);
        imageViewPhoto = findViewById(R.id.imageViewPhoto);
        editTextLebanonSales = findViewById(R.id.editTextLebanonSales);
        editTextCoastalSales = findViewById(R.id.editTextCoastalSales);
        editTextNorthernSales = findViewById(R.id.editTextNorthernSales);
        editTextSouthernSales = findViewById(R.id.editTextSouthernSales);
        editTextEasternSales = findViewById(R.id.editTextEasternSales);
        spinnerMonth = findViewById(R.id.spinnerMonth);
        spinnerYear = findViewById(R.id.spinnerYear);
        buttonSave = findViewById(R.id.buttonSave);
        databaseHelper = new DatabaseHelper(this);

        // Set text color for text fields
        textViewEmployeeNumber.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
        textViewHomeRegion.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
        editTextLebanonSales.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
        editTextCoastalSales.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
        editTextNorthernSales.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
        editTextSouthernSales.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
        editTextEasternSales.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
        buttonSave.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
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

        // Set listeners
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
    }

    private void updateSalesPersonDetails(SalesPerson salesPerson) {
        if (salesPerson != null) {
            textViewEmployeeNumber.setText(getString(R.string.employee_number_format,
                    salesPerson.getEmployeeNumber()));
            textViewHomeRegion.setText(getString(R.string.home_region_format, salesPerson.getRegion()));
            if (salesPerson.getPhotoPath() != null) {
                try {
                    imageViewPhoto.setImageURI(Uri.parse(salesPerson.getPhotoPath()));
                } catch (SecurityException e) {
                    imageViewPhoto.setImageResource(R.drawable.applogo);
                }
            } else {
                imageViewPhoto.setImageResource(R.drawable.applogo);
            }
        }
    }

    private void clearSalesPersonDetails() {
        textViewEmployeeNumber.setText("");
        textViewHomeRegion.setText("");
        imageViewPhoto.setImageResource(R.drawable.applogo);
    }

    private void setupSaveButton() {
        buttonSave.setOnClickListener(v -> saveSalesEntry());
    }

    private void saveSalesEntry() {
        if (selectedSalesPerson == null) {
            Toast.makeText(this, R.string.fill_all_fields, Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double lebanonSales = Double.parseDouble(editTextLebanonSales.getText().toString());
            double coastalSales = Double.parseDouble(editTextCoastalSales.getText().toString());
            double northernSales = Double.parseDouble(editTextNorthernSales.getText().toString());
            double southernSales = Double.parseDouble(editTextSouthernSales.getText().toString());
            double easternSales = Double.parseDouble(editTextEasternSales.getText().toString());

            int selectedMonth = spinnerMonth.getSelectedItemPosition() + 1;
            int selectedYear = Integer.parseInt(spinnerYear.getSelectedItem().toString());

            // Check if commission exists for this sales person, month and year
            if (databaseHelper.hasCommissionEntry(selectedSalesPerson.getId(), selectedMonth, selectedYear)) {
                // Play notification sound
                ToneGenerator toneGen = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
                toneGen.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200);

                AlertDialog alertDialog = new AlertDialog.Builder(this)
                        .setTitle(R.string.duplicate_commission_title)
                        .setMessage(R.string.duplicate_commission_message)
                        .setPositiveButton(R.string.yes, (dialog, which) -> {
                            // Delete existing commission and save new one
                            databaseHelper.deleteExistingCommission(
                                    selectedSalesPerson.getId(),
                                    selectedMonth,
                                    selectedYear
                            );
                            saveNewCommission(
                                    lebanonSales, coastalSales, northernSales,
                                    southernSales, easternSales,
                                    selectedMonth, selectedYear
                            );
                        })
                        .setNegativeButton(R.string.no, null)
                        .create();

                alertDialog.setOnShowListener(dialog -> {
                    // Set button text colors to text_primary color
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                            .setTextColor(ContextCompat.getColor(this, R.color.text_primary));
                    alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                            .setTextColor(ContextCompat.getColor(this, R.color.text_primary));
                });


                alertDialog.show();
            } else {
                // No existing commission, save directly
                saveNewCommission(
                        lebanonSales, coastalSales, northernSales,
                        southernSales, easternSales,
                        selectedMonth, selectedYear
                );
            }

        } catch (NumberFormatException e) {
            Toast.makeText(this, R.string.invalid_amount, Toast.LENGTH_SHORT).show();
        }
    }

    private void saveNewCommission(double lebanonSales, double coastalSales,
                                   double northernSales, double southernSales,
                                   double easternSales, int month, int year) {
        // Insert sale record with individual region sales
        long saleId = databaseHelper.insertSale(
                selectedSalesPerson.getId(),
                lebanonSales,
                coastalSales,
                northernSales,
                southernSales,
                easternSales,
                month,
                year
        );

        // Calculate and save commission
        double commission = calculateCommission(
                lebanonSales, coastalSales, northernSales,
                southernSales, easternSales,
                selectedSalesPerson.getRegion()
        );

        databaseHelper.insertCommission(saleId, commission);
        Toast.makeText(this, R.string.sale_saved, Toast.LENGTH_SHORT).show();
        clearForm();
    }

    private double calculateCommission(double lebanonSales, double coastalSales,
                                       double northernSales, double southernSales,
                                       double easternSales, String homeRegion) {
        // Commission rates
        final double BASE_RATE_HOME_REGION = 0.05;  // 5% for home region up to 100M
        final double BASE_RATE_OTHER_REGION = 0.03; // 3% for other regions up to 100M
        final double HIGH_RATE_HOME_REGION = 0.07;  // 7% for home region above 100M
        final double HIGH_RATE_OTHER_REGION = 0.04; // 4% for other regions above 100M
        final double THRESHOLD = 100_000_000;       // 100 million threshold

        double totalCommission = 0;
        double regionCommission;

        // Map to store region names and their corresponding sales
        Map<String, Double> regionSales = new HashMap<>();
        regionSales.put("Lebanon", lebanonSales);
        regionSales.put("Coastal", coastalSales);
        regionSales.put("Northern", northernSales);
        regionSales.put("Southern", southernSales);
        regionSales.put("Eastern", easternSales);

        // Calculate commission for each region
        for (Map.Entry<String, Double> entry : regionSales.entrySet()) {
            String region = entry.getKey();
            double salesAmount = entry.getValue();

            if (salesAmount <= 0) continue;

            // Check if this is the delegate's home region
            boolean isHomeRegion = region.equals(homeRegion);
            regionCommission = 0;

            if (salesAmount <= THRESHOLD) {
                // Sales up to 100M
                regionCommission = salesAmount *
                        (isHomeRegion ? BASE_RATE_HOME_REGION : BASE_RATE_OTHER_REGION);
            } else {
                // First 100M at base rate
                regionCommission = THRESHOLD *
                        (isHomeRegion ? BASE_RATE_HOME_REGION : BASE_RATE_OTHER_REGION);
                // Remaining amount at higher rate
                regionCommission += (salesAmount - THRESHOLD) *
                        (isHomeRegion ? HIGH_RATE_HOME_REGION : HIGH_RATE_OTHER_REGION);
            }

            totalCommission += regionCommission;
        }

        return totalCommission;
    }

    private void clearForm() {
        editTextLebanonSales.setText("");
        editTextCoastalSales.setText("");
        editTextNorthernSales.setText("");
        editTextSouthernSales.setText("");
        editTextEasternSales.setText("");
        spinnerSalesPerson.setSelection(0);
    }
}
