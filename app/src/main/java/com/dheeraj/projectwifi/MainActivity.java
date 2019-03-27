package com.dheeraj.projectwifi;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
TextView textView;
Button button;
WifiManager wifiManager;
WifiInfo connection;
String valid = "00:1e:a6:96:8c:e8";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                connection = wifiManager.getConnectionInfo();
                String getBssid = connection.getBSSID();
                //Toast.makeText(MainActivity.this, "SSID"+connection.getSSID()+"Bssid"+connection.getBSSID(), Toast.LENGTH_SHORT).show();

                if(valid.equals(getBssid))
                {
                    Intent intent = new Intent(MainActivity.this,wificonnected.class);
                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(MainActivity.this, "Connected to Different network ", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

}
