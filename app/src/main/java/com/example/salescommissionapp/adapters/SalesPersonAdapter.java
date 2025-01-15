    package com.example.salescommissionapp.adapters;

    import android.content.Context;
    import android.net.Uri;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.ArrayAdapter;
    import android.widget.ImageButton;
    import android.widget.ImageView;
    import android.widget.TextView;

    import androidx.annotation.NonNull;
    import androidx.recyclerview.widget.RecyclerView;

    import com.example.salescommissionapp.R;
    import com.example.salescommissionapp.models.SalesPerson;

    import java.text.SimpleDateFormat;
    import java.util.Date;
    import java.util.List;
    import java.util.Locale;

    public class SalesPersonAdapter extends RecyclerView.Adapter<SalesPersonAdapter.ViewHolder> {
        private List<SalesPerson> salesPersonList;
        private OnSalesPersonClickListener listener;

        public interface OnSalesPersonClickListener {
            void onEditClick(SalesPerson salesPerson);
            void onDeleteClick(SalesPerson salesPerson);
        }

        public SalesPersonAdapter(List<SalesPerson> salesPersonList, OnSalesPersonClickListener listener) {
            this.salesPersonList = salesPersonList;
            this.listener = listener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_sales_person, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            SalesPerson salesPerson = salesPersonList.get(position);
            Context context = holder.itemView.getContext();

            holder.textViewName.setText(salesPerson.getName());
            holder.textViewEmployeeNumber.setText(context.getString(R.string.employee_number_format,
                salesPerson.getEmployeeNumber()));
            holder.textViewRegion.setText(context.getString(R.string.home_region_format,
                salesPerson.getRegion()));

            // Format and display registration date
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String registrationDate = dateFormat.format(new Date(salesPerson.getRegistrationDate()));
            holder.textViewRegistrationDate.setText(context.getString(R.string.registration_date_format,
                registrationDate));

            if (salesPerson.getPhotoPath() != null) {
                try {
                    holder.imageViewPhoto.setImageURI(Uri.parse(salesPerson.getPhotoPath()));
                } catch (SecurityException e) {
                    holder.imageViewPhoto.setImageResource(R.drawable.applogo);
                }
            } else {
                holder.imageViewPhoto.setImageResource(R.drawable.applogo);
            }

            holder.buttonEdit.setOnClickListener(v -> listener.onEditClick(salesPerson));
            holder.buttonDelete.setOnClickListener(v -> listener.onDeleteClick(salesPerson));
        }

        @Override
        public int getItemCount() {
            return salesPersonList.size();
        }

        public void updateData(List<SalesPerson> newList) {
            this.salesPersonList = newList;
            notifyDataSetChanged();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            private ImageView imageViewPhoto;
            private TextView textViewName;
            private TextView textViewEmployeeNumber;
            private TextView textViewRegion;
            private TextView textViewRegistrationDate;
            private ImageButton buttonEdit;
            private ImageButton buttonDelete;

            ViewHolder(View itemView) {
                super(itemView);
                imageViewPhoto = itemView.findViewById(R.id.imageViewPhoto);
                textViewName = itemView.findViewById(R.id.textViewName);
                textViewEmployeeNumber = itemView.findViewById(R.id.textViewEmployeeNumber);
                textViewRegion = itemView.findViewById(R.id.textViewRegion);
                textViewRegistrationDate = itemView.findViewById(R.id.textViewRegistrationDate);
                buttonEdit = itemView.findViewById(R.id.buttonEdit);
                buttonDelete = itemView.findViewById(R.id.buttonDelete);
            }

            void bind(SalesPerson salesPerson) {
                textViewName.setText(salesPerson.getName());
                textViewEmployeeNumber.setText(salesPerson.getEmployeeNumber());
                textViewRegion.setText(salesPerson.getRegion());

                if (salesPerson.getPhotoPath() != null) {
                    try {
                        imageViewPhoto.setImageURI(Uri.parse(salesPerson.getPhotoPath()));
                    } catch (SecurityException e) {
                        imageViewPhoto.setImageResource(R.drawable.applogo);
                    }
                } else {
                    imageViewPhoto.setImageResource(R.drawable.applogo);
                }

                buttonEdit.setOnClickListener(v -> listener.onEditClick(salesPerson));
                buttonDelete.setOnClickListener(v -> listener.onDeleteClick(salesPerson));
            }
        }

        public static class SalesPersonSpinnerAdapter extends ArrayAdapter<SalesPerson> {
            public SalesPersonSpinnerAdapter(Context context, List<SalesPerson> salesPersons) {
                super(context, android.R.layout.simple_spinner_item, salesPersons);
                setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text = (TextView) view.findViewById(android.R.id.text1);
                SalesPerson salesPerson = getItem(position);
                if (salesPerson != null) {
                    text.setText(String.format("%s (%s)",
                        salesPerson.getName(),
                        salesPerson.getEmployeeNumber()));
                }
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView text = (TextView) view.findViewById(android.R.id.text1);
                SalesPerson salesPerson = getItem(position);
                if (salesPerson != null) {
                    text.setText(String.format("%s (%s)",
                        salesPerson.getName(),
                        salesPerson.getEmployeeNumber()));
                }
                return view;
            }
        }
    }