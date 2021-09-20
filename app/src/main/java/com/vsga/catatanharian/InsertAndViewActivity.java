package com.vsga.catatanharian;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;

public class InsertAndViewActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_STORAGE = 100;
    int eventID = 0;
    EditText etFileName, etContent;
    Button btnSave;
    String fileName = "";
    String tempCatatan = "";

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_and_view);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        etFileName = findViewById(R.id.etFileName);
        etContent = findViewById(R.id.etContent);
        btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(v -> {
            eventID = 2;
            if (Build.VERSION.SDK_INT >= 23) {
                if (periksaIzinPenyimpanan()) {
                    tampilkanDialogKonfirmasiPenyimpanan();
                }
            } else {
                tampilkanDialogKonfirmasiPenyimpanan();
            }
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            fileName = extras.getString("filename");
            etFileName.setText(fileName);
            getSupportActionBar().setTitle("Ubah Catatan");
        } else {
            getSupportActionBar().setTitle("Tambah Catatan");
        }
        eventID = 1;
        if (Build.VERSION.SDK_INT >= 23) {
            if (periksaIzinPenyimpanan()) {
                bacaFile();
            }
        } else {
            bacaFile();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (eventID == 1) {
                        bacaFile();
                    } else {
                        tampilkanDialogKonfirmasiPenyimpanan();
                    }
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (!tempCatatan.equals(etContent.getText().toString().trim())) {
            tampilkanDialogKonfirmasiPenyimpanan();
        }
        super.onBackPressed();
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

    void bacaFile() {
        String path = getFilesDir().toString() + "/VSGA";
        File file = new File(path, etFileName.getText().toString().trim());
        if (file.exists()) {
            StringBuilder text = new StringBuilder();
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line = br.readLine();
                while (line != null) {
                    text.append(line);
                    line = br.readLine();
                }
                br.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            tempCatatan = text.toString();
            etContent.setText(tempCatatan);
        }
    }

    void buatDanUbah() {
        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            return;
        }

        String path = getFilesDir().toString() + "/VSGA";
        File parent = new File(path);
        if (parent.exists()) {
            File file = new File(path, etFileName.getText().toString().trim());
            FileOutputStream outputStream = null;
            try {
                file.createNewFile();
                outputStream = new FileOutputStream(file);
                OutputStreamWriter streamWriter = new OutputStreamWriter(outputStream);
                streamWriter.append(etContent.getText().toString().trim());
                streamWriter.flush();
                streamWriter.close();
                outputStream.flush();
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.e("TES", path);
            boolean i = parent.mkdirs();
            Log.e("TES", "mkdir : " + i);
            File file = new File(path, etFileName.getText().toString().trim());
            FileOutputStream outputStream = null;
            try {
                file.createNewFile();
                outputStream = new FileOutputStream(file);
                OutputStreamWriter streamWriter = new OutputStreamWriter(outputStream);
                streamWriter.append(etContent.getText().toString().trim());
                streamWriter.flush();
                streamWriter.close();
                outputStream.flush();
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    void tampilkanDialogKonfirmasiPenyimpanan() {
        new AlertDialog.Builder(this)
                .setTitle("Simpan Catatan")
                .setMessage("Apakah Anda yakin ingin menyimpan Catatan ini?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    buatDanUbah();
                    dialog.dismiss();
                    finish();
                })
                .setNegativeButton(android.R.string.no, null).show();
    }
}