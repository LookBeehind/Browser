package com.oliviu.lab1;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CustomObjectAdapter extends RecyclerView.Adapter<CustomObjectAdapter.CustomObjectViewHolder> {
    private final List<CustomObject> customObjectList;

    public CustomObjectAdapter(List<CustomObject> customObjectList) {
        this.customObjectList = customObjectList;
    }

    @NonNull
    @Override
    public CustomObjectViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_object_item, parent, false);
        return new CustomObjectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomObjectViewHolder holder, int position) {
        CustomObject customObject = customObjectList.get(position);
        holder.title.setText(customObject.getTitle());
        holder.description.setText(customObject.getDescription());

        holder.searchButton.setOnClickListener(v -> onSearchItem(holder.itemView.getContext(), customObject));
        holder.removeButton.setOnClickListener(v -> onRemoveItem(holder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return customObjectList.size();
    }

    public List<CustomObject> getCustomObjectList() {
        return customObjectList;
    }

    public void clearList() {
        customObjectList.clear();
    }

    private void onSearchItem(Context context, CustomObject customObject) {
        String url = customObject.getDescription();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        context.startActivity(intent);
    }

    private void onRemoveItem(int position) {
        customObjectList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, customObjectList.size());
    }

    public static class CustomObjectViewHolder extends RecyclerView.ViewHolder {
        TextView title, description;
        ImageButton searchButton, removeButton;

        public CustomObjectViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            searchButton = itemView.findViewById(R.id.searchItem);
            removeButton = itemView.findViewById(R.id.removeItem);
        }
    }
}
