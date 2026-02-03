package com.example.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    WifiManager wifiManager;
    static final int PERMISSION_REQUEST = 100;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wifiManager = (WifiManager) getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);

        Button btnScan = (Button) findViewById(R.id.btnCheckWifi);

        btnScan.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                if (ActivityCompat.checkSelfPermission(
                        MainActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(
                            MainActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            PERMISSION_REQUEST
                    );

                } else {
                    lancerScanWifi();
                }
            }
        });
    }

    private void lancerScanWifi() {

        boolean success = wifiManager.startScan();

        if (success) {
            List<ScanResult> results = wifiManager.getScanResults();
            StringBuilder texte = new StringBuilder();

            for (ScanResult reseau : results) {

                String securite = "OPEN (Faible)";
                String cap = reseau.capabilities;

                if (cap.contains("WPA3")) {
                    securite = "WPA3";
                } else if (cap.contains("WPA2")) {
                    securite = "WPA2";
                } else if (cap.contains("WEP")) {
                    securite = "WEP (Faible)";
                } else if (cap.contains("WPA")) {
                    securite = "WPA";
                }

                texte.append("SSID : ").append(reseau.SSID).append("\n");
                texte.append("Sécurité : ").append(securite).append("\n");
                texte.append("Signal : ").append(reseau.level).append(" dBm\n\n");
            }

            Intent intent = new Intent(MainActivity.this, ResultActivity.class);
            intent.putExtra("networks", texte.toString());
            startActivity(intent);

        } else {
            Toast.makeText(
                    this,
                    "Scan Wi-Fi échoué, verifier que le wifi est bien activé",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }



    public void onRequestPermissionsResult(
            int requestCode,
            String[] permissions,
            int[] grantResults) {

        if (requestCode == PERMISSION_REQUEST) {

            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                lancerScanWifi();

            } else {
                Toast.makeText(
                        this,
                        "Permission refusée",
                        Toast.LENGTH_SHORT
                ).show();
            }
        }
    }
}
