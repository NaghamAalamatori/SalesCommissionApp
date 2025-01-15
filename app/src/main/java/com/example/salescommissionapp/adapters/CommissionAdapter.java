package com.example.salescommissionapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.salescommissionapp.R;
import com.example.salescommissionapp.models.Sale;
import com.example.salescommissionapp.models.SalesPerson;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class CommissionAdapter extends RecyclerView.Adapter<CommissionAdapter.CommissionViewHolder> {
    private List<Sale> sales;
    private SalesPerson salesPerson;
    private SimpleDateFormat dateFormat;
    private double totalSales = 0;
    private double totalCommission = 0;

    public CommissionAdapter(List<Sale> sales, SalesPerson salesPerson) {
        this.sales = sales;
        this.salesPerson = salesPerson;
        this.dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        calculateTotals();
    }

    private void calculateTotals() {
        totalSales = 0;
        totalCommission = 0;
        for (Sale sale : sales) {
            double saleTotal = sale.getLebanonSales() +
                    sale.getCoastalSales() +
                    sale.getNorthernSales() +
                    sale.getSouthernSales() +
                    sale.getEasternSales();
            totalSales += saleTotal;
            totalCommission += sale.getCommission();
        }
    }

    public double getTotalSales() {
        return totalSales;
    }

    public double getTotalCommission() {
        return totalCommission;
    }

    @NonNull
    @Override
    public CommissionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_commission, parent, false);
        return new CommissionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommissionViewHolder holder, int position) {
        Sale sale = sales.get(position);
        Context context = holder.itemView.getContext();

        holder.textViewEmployeeNumber.setText(String.format("Delegate Number: %s", salesPerson.getEmployeeNumber()));
        holder.textViewEmployeeName.setText(String.format("Delegate Name: %s", salesPerson.getName()));
        holder.textViewPeriod.setText(String.format("Month: %d\nYear: %d", sale.getMonth(), sale.getYear()));
        String registrationDate = dateFormat.format(salesPerson.getRegistrationDate());
        holder.textViewRegistrationDate.setText(String.format("Registration Date: %s", registrationDate));

        holder.textViewLebanonSales.setText(String.format("Lebanon: %.0f S.P", sale.getLebanonSales()));
        holder.textViewCoastalSales.setText(String.format("Coastal Region: %.0f S.P", sale.getCoastalSales()));
        holder.textViewNorthernSales.setText(String.format("Northern Region: %.0f S.P", sale.getNorthernSales()));
        holder.textViewSouthernSales.setText(String.format("Southern Region: %.0f S.P", sale.getSouthernSales()));
        holder.textViewEasternSales.setText(String.format("Eastern Region: %.0f S.P", sale.getEasternSales()));

        holder.textViewMonthlyCommission.setText(String.format("Monthly Commission: %.0f ٍS.P", sale.getCommission()));
    }

    @Override
    public int getItemCount() {
        return sales.size();
    }

    static class CommissionViewHolder extends RecyclerView.ViewHolder {
        TextView textViewEmployeeName;
        TextView textViewEmployeeNumber;
        TextView textViewRegistrationDate;
        TextView textViewPeriod;
        TextView textViewLebanonSales;
        TextView textViewCoastalSales;
        TextView textViewNorthernSales;
        TextView textViewSouthernSales;
        TextView textViewEasternSales;
        TextView textViewMonthlyCommission;

        public CommissionViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewEmployeeName = itemView.findViewById(R.id.textViewEmployeeName);
            textViewEmployeeNumber = itemView.findViewById(R.id.textViewEmployeeNumber);
            textViewRegistrationDate = itemView.findViewById(R.id.textViewRegistrationDate);
            textViewPeriod = itemView.findViewById(R.id.textViewPeriod);
            textViewLebanonSales = itemView.findViewById(R.id.textViewLebanonSales);
            textViewCoastalSales = itemView.findViewById(R.id.textViewCoastalSales);
            textViewNorthernSales = itemView.findViewById(R.id.textViewNorthernSales);
            textViewSouthernSales = itemView.findViewById(R.id.textViewSouthernSales);
            textViewEasternSales = itemView.findViewById(R.id.textViewEasternSales);
            textViewMonthlyCommission = itemView.findViewById(R.id.textViewMonthlyCommission);
        }
    }
}
