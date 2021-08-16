package com.vsga.catatanharian;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_STORAGE = 100;
    ListView listView;
    FloatingActionButton fabAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fabAdd = findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, InsertAndViewActivity.class)));

        listView = findViewById(R.id.listView);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(MainActivity.this, InsertAndViewActivity.class);
            Map<String, Object> data = (Map<String, Object>) parent.getAdapter().getItem(position);
            intent.putExtra("filename", data.get("name").toString());
            Toast.makeText(MainActivity.this, "You clicked : " + data.get("name"), Toast.LENGTH_SHORT).show();
            startActivity(intent);
        });

        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            Map<String, Object> data = (Map<String, Object>) parent.getAdapter().getItem(position);
            tampilkanDialogKonfirmasiHapusCatatan(data.get("name").toString());
            return true;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= 23) {
            if (periksaIzinPenyimpanan()) {
                mengambilListFilePadaFolder();
            }
        } else {
            mengambilListFilePadaFolder();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mengambilListFilePadaFolder();
                }
                break;
        }
    }

    public boolean periksaIzinPenyimpanan() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE);
                return false;
            }
        } else {
            return true;
        }
    }

    void mengambilListFilePadaFolder() {
        String path = getFilesDir().toString() + "/VSGA";
        File directory = new File(path);

        if (directory.exists()) {
            File[] files = directory.listFiles();
            String[] fileNames = new String[files.length];
            String[] dateCreated = new String[files.length];
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
            ArrayList<Map<String, String>> itemDataList = new ArrayList<>();
            for (int i = 0; i < files.length; i++) {
                fileNames[i] = files[i].getName();
                Date lastModDate = new Date(files[i].lastModified());
                dateCreated[i] = simpleDateFormat.format(lastModDate);
                Map<String, String> itemMap = new HashMap<>();
                itemMap.put("name", fileNames[i]);
                itemMap.put("date", dateCreated[i]);
                itemDataList.add(itemMap);
            }

            SimpleAdapter simpleAdapter = new SimpleAdapter(
                    this,
                    itemDataList,
                    android.R.layout.simple_list_item_2,
                    new String[]{"name", "date"},
                    new int[]{android.R.id.text1, android.R.id.text2}
            );
            listView.setAdapter(simpleAdapter);
            simpleAdapter.notifyDataSetChanged();
        }
    }

    void tampilkanDialogKonfirmasiHapusCatatan(String fileName) {
        new AlertDialog.Builder(this)
                .setTitle("Hapus Catatan Ini?")
                .setMessage("Apakah Anda yakin ingin menghapus Catatan " + fileName + "?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, (dialog, which) -> hapusFile(fileName))
                .setNegativeButton(android.R.string.no, null).show();
    }

    void hapusFile(String fileName) {
        String path = getFilesDir().toString() + "/VSGA";
        File file = new File(path, fileName);
        if (file.exists()) {
            file.delete();
        }
        mengambilListFilePadaFolder();
    }
}