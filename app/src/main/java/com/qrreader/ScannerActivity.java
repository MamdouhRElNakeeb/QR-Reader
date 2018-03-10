package com.qrreader;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Patterns;
import android.util.SparseArray;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.barcode.Barcode;

import java.util.List;

import info.androidhive.barcode.BarcodeReader;


public class ScannerActivity extends AppCompatActivity implements BarcodeReader.BarcodeReaderListener{

    LinearLayout scannerLL;
    TextView camPermissionLblTV;

    BarcodeReader barcodeReader;


    boolean camPermissionGranted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.scanner_activity);

        scannerLL = findViewById(R.id.scannerLL);
        camPermissionLblTV = findViewById(R.id.camPermissionLblTV);

        setupToolbar();
        setupQRReader();

        askForPermission(Manifest.permission.CAMERA,11);
    }

    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {

                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);

            } else {

                ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
            }
        } else {
            camPermissionGranted = true;
            setupQRReader();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED){

            if (requestCode == 11){
                camPermissionGranted = true;
                setupQRReader();
            }

            Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupQRReader(){

        if (camPermissionGranted){

            camPermissionLblTV.setVisibility(View.GONE);

            barcodeReader = (BarcodeReader) getSupportFragmentManager().findFragmentById(R.id.barcode_fragment);

        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (camPermissionGranted) {

            setupQRReader();
            barcodeReader.resumeScanning();

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (camPermissionGranted){

            setupQRReader();
            barcodeReader.pauseScanning();

        }
    }

    private void setupToolbar(){

        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

    }

    @Override
    public void onScanned(Barcode barcode) {
        Log.e("QRCode", "onScanned: " + barcode.displayValue);
        barcodeReader.playBeep();

        String code = barcode.displayValue;

        if (!code.isEmpty() && Patterns.WEB_URL.matcher(code).matches()){

            Intent intent = new Intent(ScannerActivity.this, ProductDetailsActivity.class);
            intent.putExtra("url", code);
            startActivity(intent);

        }

    }

    @Override
    public void onScannedMultiple(List<Barcode> barcodes) {

    }

    @Override
    public void onBitmapScanned(SparseArray<Barcode> sparseArray) {

    }

    @Override
    public void onScanError(String errorMessage) {

    }

    @Override
    public void onCameraPermissionDenied() {
        Toast.makeText(getApplicationContext(), "Camera permission denied!", Toast.LENGTH_SHORT).show();
    }

}
