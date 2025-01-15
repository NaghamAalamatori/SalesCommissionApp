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
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class SalesAdapter extends RecyclerView.Adapter<SalesAdapter.SalesViewHolder> {
    private List<Sale> sales;
    private SimpleDateFormat dateFormat;

    public SalesAdapter(List<Sale> sales) {
        this.sales = sales;
        this.dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
    }

    @NonNull
    @Override
    public SalesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sale, parent, false);
        return new SalesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SalesViewHolder holder, int position) {
        Sale sale = sales.get(position);
        Context context = holder.itemView.getContext();

        // Set date
        holder.textViewDate.setText(String.format("%d/%d", sale.getMonth(), sale.getYear()));

        // Calculate total sales for all regions
        double totalSales = sale.getLebanonSales() +
                          sale.getCoastalSales() +
                          sale.getNorthernSales() +
                          sale.getSouthernSales() +
                          sale.getEasternSales();

        // Format and display the total amount
        holder.textViewAmount.setText(String.format(Locale.getDefault(), "%.2f LS", totalSales));

        // Create a summary of sales by region
        StringBuilder regionSummary = new StringBuilder();
        if (sale.getLebanonSales() > 0) {
            regionSummary.append(context.getString(R.string.region_lebanon)).append(", ");
        }
        if (sale.getCoastalSales() > 0) {
            regionSummary.append(context.getString(R.string.region_coastal)).append(", ");
        }
        if (sale.getNorthernSales() > 0) {
            regionSummary.append(context.getString(R.string.region_northern)).append(", ");
        }
        if (sale.getSouthernSales() > 0) {
            regionSummary.append(context.getString(R.string.region_southern)).append(", ");
        }
        if (sale.getEasternSales() > 0) {
            regionSummary.append(context.getString(R.string.region_eastern)).append(", ");
        }

        // Remove trailing comma and space if exists
        String regions = regionSummary.toString();
        if (regions.endsWith(", ")) {
            regions = regions.substring(0, regions.length() - 2);
        }

        holder.textViewRegion.setText(regions);
    }

    @Override
    public int getItemCount() {
        return sales.size();
    }

    static class SalesViewHolder extends RecyclerView.ViewHolder {
        TextView textViewDate;
        TextView textViewRegion;
        TextView textViewAmount;

        public SalesViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            textViewRegion = itemView.findViewById(R.id.textViewRegion);
            textViewAmount = itemView.findViewById(R.id.textViewAmount);
        }
    }
} 