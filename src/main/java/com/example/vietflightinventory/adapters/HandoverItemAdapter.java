// src/main/java/com/example/vietflightinventory/adapters/HandoverItemAdapter.java
package com.example.vietflightinventory.adapters;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vietflightinventory.R;
import com.example.vietflightinventory.models.HandoverItem;

import java.util.List;
import java.util.Locale;

public class HandoverItemAdapter extends RecyclerView.Adapter<HandoverItemAdapter.ViewHolder> {

    private Context context;
    private List<HandoverItem> items;
    private boolean isReceiveMode;

    public HandoverItemAdapter(Context context, List<HandoverItem> items, boolean isReceiveMode) {
        this.context = context;
        this.items = items;
        this.isReceiveMode = isReceiveMode;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_handover_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HandoverItem item = items.get(position);

        holder.tvProductName.setText(item.getProductName());
        holder.tvUnitPrice.setText(String.format(Locale.getDefault(), "%,.0f VNĐ", item.getUnitPrice()));
        holder.tvInitialQuantity.setText(String.valueOf(item.getInitialQuantityFromStaff()));

        // Set product image if available
        if (item.getProductImageUrl() != null && !item.getProductImageUrl().isEmpty()) {
            // Load image from URL using your preferred image loading library
            // For now, use placeholder
            holder.ivProductImage.setImageResource(R.drawable.ic_launcher_foreground);
        } else {
            holder.ivProductImage.setImageResource(R.drawable.ic_launcher_foreground);
        }

        if (isReceiveMode) {
            // Show editable received quantity
            holder.edtReceivedQuantity.setVisibility(View.VISIBLE);
            holder.tvReceivedQuantity.setVisibility(View.GONE);

            holder.edtReceivedQuantity.setText(String.valueOf(item.getActualReceivedByFAQuantity()));

            holder.edtReceivedQuantity.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    try {
                        int quantity = Integer.parseInt(s.toString());
                        item.setActualReceivedByFAQuantity(quantity);
                    } catch (NumberFormatException e) {
                        item.setActualReceivedByFAQuantity(0);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        } else {
            // Show read-only received quantity
            holder.edtReceivedQuantity.setVisibility(View.GONE);
            holder.tvReceivedQuantity.setVisibility(View.VISIBLE);
            holder.tvReceivedQuantity.setText(String.valueOf(item.getActualReceivedByFAQuantity()));
        }

        // Calculate and show total value
        double totalValue = item.getUnitPrice() * item.getInitialQuantityFromStaff();
        holder.tvTotalValue.setText(String.format(Locale.getDefault(), "%,.0f VNĐ", totalValue));
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    public List<HandoverItem> getUpdatedItems() {
        return items;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProductImage;
        TextView tvProductName, tvUnitPrice, tvInitialQuantity, tvReceivedQuantity, tvTotalValue;
        EditText edtReceivedQuantity;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvUnitPrice = itemView.findViewById(R.id.tvUnitPrice);
            tvInitialQuantity = itemView.findViewById(R.id.tvInitialQuantity);
            tvReceivedQuantity = itemView.findViewById(R.id.tvReceivedQuantity);
            edtReceivedQuantity = itemView.findViewById(R.id.edtReceivedQuantity);
            tvTotalValue = itemView.findViewById(R.id.tvTotalValue);
        }
    }
}