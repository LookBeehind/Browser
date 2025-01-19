package com.oliviu.lab1;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.PopupWindow;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        populateListFromJson();
    }

    public void onSearch(View view) {
        TextView txtSearch = findViewById(R.id.request);
        String query = txtSearch.getText().toString();

        if (query.isEmpty()) {
            showErrorPopup();
        } else {
            addItem(query);
            String url = "https://www.google.com/search?q=" + Uri.encode(query);
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        }
    }

    public void onCopy(View view) {
        TextView txtSearch = findViewById(R.id.request);
        String query = txtSearch.getText().toString();

        if (query.isEmpty()) {
            showErrorPopup();
        } else {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            String url = "https://www.google.com/search?q=" + Uri.encode(query);
            android.content.ClipData clip = android.content.ClipData.newPlainText("label", url);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "URL copied to Clipboard", Toast.LENGTH_LONG).show();
        }
    }

    public void onAdd(View view) {
        TextView txtSearch = findViewById(R.id.request);
        String query = txtSearch.getText().toString();

        if (query.isEmpty()) {
            showErrorPopup();
        } else {
            addItem(query);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void onDelete(View view) {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        CustomObjectAdapter adapter = (CustomObjectAdapter) recyclerView.getAdapter();

        if (adapter != null) {
            adapter.clearList();
            adapter.notifyDataSetChanged();
            saveListToJson();
        }
    }

    public void onSave(View view) {
        saveListToJson();
        Toast.makeText(this, "List saved successfully!", Toast.LENGTH_SHORT).show();
    }

    private void saveListToJson() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        CustomObjectAdapter adapter = (CustomObjectAdapter) recyclerView.getAdapter();

        if (adapter != null) {
            List<CustomObject> customObjectList = adapter.getCustomObjectList();
            JSONArray jsonArray = new JSONArray();

            for (CustomObject customObject : customObjectList) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("title", customObject.getTitle());
                    jsonObject.put("description", customObject.getDescription());
                    jsonArray.put(jsonObject);
                } catch (JSONException e) {
                    Log.e("TAG", "Error message", e);
                }
            }

            File file = new File(getFilesDir(), "history.json");
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(jsonArray.toString());
            } catch (IOException e) {
                Log.e("TAG", "Error message", e);
            }
        }
    }

    private void populateListFromJson() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        List<CustomObject> customObjectList = new ArrayList<>();
        File file = new File(getFilesDir(), "history.json");

        if (file.exists()) {
            try (FileReader reader = new FileReader(file); BufferedReader bufferedReader = new BufferedReader(reader)) {
                StringBuilder jsonBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    jsonBuilder.append(line);
                }

                JSONArray jsonArray = new JSONArray(jsonBuilder.toString());
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String title = jsonObject.getString("title");
                    String description = jsonObject.getString("description");
                    customObjectList.add(new CustomObject(title, description));
                }
            } catch (IOException | JSONException e) {
                Log.e("TAG", "Error message", e);
            }
        }

        CustomObjectAdapter adapter = new CustomObjectAdapter(customObjectList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        if (recyclerView.getItemDecorationCount() == 0) {
            recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void addItem(String query) {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        if (recyclerView.getItemDecorationCount() == 0) {
            recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        }

        CustomObjectAdapter adapter = (CustomObjectAdapter) recyclerView.getAdapter();

        List<CustomObject> customObjectList;
        if (adapter == null) {
            customObjectList = new ArrayList<>();
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter = new CustomObjectAdapter(customObjectList);
            recyclerView.setAdapter(adapter);
        } else {
            customObjectList = adapter.getCustomObjectList();
        }

        boolean exists = false;
        for (CustomObject obj : customObjectList) {
            if (obj.getTitle().equals(query)) {
                exists = true;
                break;
            }
        }

        if (!exists) {
            customObjectList.add(new CustomObject(query, "https://www.google.com/search?q=" + Uri.encode(query)));
            adapter.notifyDataSetChanged();
            saveListToJson();
        } else {
            Toast.makeText(this, "Item already exists in the list!", Toast.LENGTH_SHORT).show();
        }
    }
    private void showErrorPopup() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View popupView = inflater.inflate(R.layout.popup_layout, null);

        PopupWindow popupWindow = new PopupWindow(popupView,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT);

        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.update();
        popupWindow.showAtLocation(findViewById(android.R.id.content), android.view.Gravity.CENTER, 0, 0);

        new android.os.Handler().postDelayed(popupWindow::dismiss, 10000);
    }
}
