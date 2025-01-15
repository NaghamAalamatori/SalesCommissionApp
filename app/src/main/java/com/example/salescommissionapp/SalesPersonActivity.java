package com.example.salescommissionapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.example.salescommissionapp.adapters.SalesPersonAdapter;
import com.example.salescommissionapp.db.DatabaseHelper;
import com.example.salescommissionapp.models.SalesPerson;
import java.util.ArrayList;
import java.util.List;
import android.Manifest;
import android.content.pm.PackageManager;
import android.widget.Toast;
import androidx.annotation.NonNull;

public class SalesPersonActivity extends BaseActivity implements SalesPersonAdapter.OnSalesPersonClickListener {
    private RecyclerView recyclerView;
    private FloatingActionButton fabAddSalesPerson;
    private DatabaseHelper databaseHelper;
    private List<SalesPerson> salesPersonList;
    private SalesPersonAdapter adapter;
    private String currentPhotoPath;
    private ActivityResultLauncher<Intent> photoPickerLauncher;
    private static final int PERMISSION_REQUEST_CODE = 123;
    private ImageView currentImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_person);
        setupNavigationDrawer();

        databaseHelper = new DatabaseHelper(this);
        initializeViews();
        checkPermissions();
        setupPhotoPickerLauncher();
        setupRecyclerView();
        setupFabClickListener();
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, reload the sales persons
                loadSalesPersons();
            } else {
                // Permission denied, show a message to the user
                Toast.makeText(this, R.string.storage_permission_required,
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private void setupPhotoPickerLauncher() {
        photoPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedImage = result.getData().getData();
                        currentPhotoPath = selectedImage.toString();
                        // Update the ImageView with selected photo
                        if (currentImageView != null) {
                            try {
                                currentImageView.setImageURI(selectedImage);
                                // Take persist permissions for the URI
                                getContentResolver().takePersistableUriPermission(selectedImage,
                                        Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            } catch (SecurityException e) {
                                currentImageView.setImageResource(R.drawable.ic_person);
                            }
                        }
                    }
                });
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.recyclerViewSalesPersons);
        fabAddSalesPerson = findViewById(R.id.fabAddSalesPerson);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        salesPersonList = new ArrayList<>();
        adapter = new SalesPersonAdapter(salesPersonList, this);
        recyclerView.setAdapter(adapter);
        loadSalesPersons();
    }

    private void setupFabClickListener() {
        fabAddSalesPerson.setOnClickListener(view -> showAddSalesPersonDialog());
    }

    private void loadSalesPersons() {
        salesPersonList.clear();
        salesPersonList.addAll(databaseHelper.getAllSalesPersons());
        adapter.notifyDataSetChanged();
    }

    private void showAddSalesPersonDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_sales_person, null);

        EditText editTextName = dialogView.findViewById(R.id.editTextName);
        EditText editTextEmployeeNumber = dialogView.findViewById(R.id.editTextEmployeeNumber);
        Spinner spinnerRegion = dialogView.findViewById(R.id.spinnerRegion);
        ImageView imageViewPhoto = dialogView.findViewById(R.id.imageViewPhoto);
        Button buttonSelectPhoto = dialogView.findViewById(R.id.buttonSelectPhoto);

        // Store reference to current ImageView
        currentImageView = imageViewPhoto;

        // Setup region spinner
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.regions, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRegion.setAdapter(spinnerAdapter);

        buttonSelectPhoto.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("image/*");
            photoPickerLauncher.launch(intent);
        });

        AlertDialog dialog = builder.setView(dialogView)
                .setTitle(R.string.add_sales_person)
                .setPositiveButton(R.string.save, null)
                .setNegativeButton(R.string.cancel, null)
                .create();

        dialog.setOnShowListener(dialogInterface -> {
            Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);

            // Set button text color to text_primary
            positiveButton.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
            negativeButton.setTextColor(ContextCompat.getColor(this, R.color.text_primary));

            positiveButton.setOnClickListener(v -> {
                String name = editTextName.getText().toString().trim();
                String employeeNumber = editTextEmployeeNumber.getText().toString().trim();
                String region = spinnerRegion.getSelectedItem().toString();

                if (!name.isEmpty() && !employeeNumber.isEmpty()) {
                    SalesPerson salesPerson = new SalesPerson(
                            0, name, employeeNumber, region,
                            currentPhotoPath != null ? currentPhotoPath : ""
                    );
                    salesPerson.setRegistrationDate(System.currentTimeMillis());

                    long id = databaseHelper.addSalesPerson(salesPerson);
                    if (id != -1) {
                        salesPerson.setId((int) id);
                        salesPersonList.add(salesPerson);
                        adapter.notifyItemInserted(salesPersonList.size() - 1);
                        Toast.makeText(this, R.string.sales_person_added, Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                } else {
                    Toast.makeText(this, R.string.please_fill_all_fields, Toast.LENGTH_SHORT).show();
                }
            });

            negativeButton.setOnClickListener(v -> dialog.dismiss());
        });

        dialog.show();
    }

    @Override
    public void onEditClick(SalesPerson salesPerson) {
        showSalesPersonDialog(salesPerson);

    }

    @Override
    public void onDeleteClick(SalesPerson salesPerson) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.delete_confirmation)
                .setPositiveButton(R.string.yes, (dialogInterface, which) -> {
                    databaseHelper.deleteSalesPerson(salesPerson.getId());
                    loadSalesPersons();
                })
                .setNegativeButton(R.string.no, (dialogInterface, which) -> dialogInterface.dismiss())
                .create();

        dialog.show();

        // Set color for positive and negative buttons
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);

        positiveButton.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
        negativeButton.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
    }



    private void showSalesPersonDialog(SalesPerson salesPerson) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_sales_person, null);

        EditText editTextName = dialogView.findViewById(R.id.editTextName);
        EditText editTextEmployeeNumber = dialogView.findViewById(R.id.editTextEmployeeNumber);
        Spinner spinnerRegion = dialogView.findViewById(R.id.spinnerRegion);
        ImageView imageViewPhoto = dialogView.findViewById(R.id.imageViewPhoto);
        Button buttonSelectPhoto = dialogView.findViewById(R.id.buttonSelectPhoto);

        // Pre-fill existing data
        editTextName.setText(salesPerson.getName());
        editTextEmployeeNumber.setText(salesPerson.getEmployeeNumber());

        // Setup region spinner
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.regions, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRegion.setAdapter(spinnerAdapter);

        // Set selected region
        int position = spinnerAdapter.getPosition(salesPerson.getRegion());
        spinnerRegion.setSelection(position);

        // Load existing photo
        if (salesPerson.getPhotoPath() != null && !salesPerson.getPhotoPath().isEmpty()) {
            try {
                imageViewPhoto.setImageURI(Uri.parse(salesPerson.getPhotoPath()));
                currentPhotoPath = salesPerson.getPhotoPath();
            } catch (SecurityException e) {
                imageViewPhoto.setImageResource(R.drawable.applogo);
            }
        }

        buttonSelectPhoto.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("image/*");
            photoPickerLauncher.launch(intent);
        });

        AlertDialog dialog = builder.setView(dialogView)
                .setTitle(R.string.edit_sales_person)
                .setPositiveButton(R.string.save, null)
                .setNegativeButton(R.string.cancel, null)
                .create();

        dialog.setOnShowListener(dialogInterface -> {
            Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);

            // Set button text color to text_primary
            positiveButton.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
            negativeButton.setTextColor(ContextCompat.getColor(this, R.color.text_primary));

            positiveButton.setOnClickListener(v -> {
                String name = editTextName.getText().toString().trim();
                String employeeNumber = editTextEmployeeNumber.getText().toString().trim();
                String region = spinnerRegion.getSelectedItem().toString();

                if (!name.isEmpty() && !employeeNumber.isEmpty()) {
                    salesPerson.setName(name);
                    salesPerson.setEmployeeNumber(employeeNumber);
                    salesPerson.setRegion(region);
                    salesPerson.setPhotoPath(currentPhotoPath);

                    if (databaseHelper.updateSalesPerson(salesPerson) > 0) {
                        loadSalesPersons();
                        Toast.makeText(this, R.string.sales_person_updated, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, R.string.please_fill_all_fields, Toast.LENGTH_SHORT).show();
                }
            });

            negativeButton.setOnClickListener(v -> dialog.dismiss());
        });

        dialog.show();
    }
}
