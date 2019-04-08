package com.dheeraj.projectwifi;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


import java.sql.Time;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class wificonnected extends AppCompatActivity implements LocationListener {

    WifiManager wifiManager;
    WifiInfo connection;
    String valid = "00:1e:a6:96:8c:e8";
    Timer timer = new Timer();
    final Handler handler = new Handler();
    TimerTask doAsyncTask;
    long start,end,sub;
    Date starttime,endtime;
    String androidid;
    LocationManager locationManager;
    TextView locationText;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wificonnected);
        locationText = findViewById(R.id.locationText);
        start = System.currentTimeMillis();
        starttime = Calendar.getInstance().getTime();
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);

        }

        doAsyncTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                        connection = wifiManager.getConnectionInfo();
                        String getBssid = connection.getBSSID();
                        if(!valid.equals(getBssid)) {
                            stoptimer();
                        }
                        else
                        {
                            getLocation();
                           // Toast.makeText(wificonnected.this, "Yss", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        };
        timer.schedule(doAsyncTask,0,4000);


    }


    public void stoptimer()
    {
        end = System.currentTimeMillis();
        sub =end - start;
        //Toast.makeText(wificonnected.this, Long.toString(sub) , Toast.LENGTH_SHORT).show();
        endtime = Calendar.getInstance().getTime();
        //Toast.makeText(wificonnected.this, currenttime.toString(), Toast.LENGTH_SHORT).show();
        androidid = Settings.Secure.getString(getApplicationContext().getContentResolver(),Settings.Secure.ANDROID_ID);
        //Toast.makeText(wificonnected.this, androidid, Toast.LENGTH_SHORT).show();

        RequestQueue queue = Volley.newRequestQueue(wificonnected.this);
        String url ="http://creationdevs.in/wifiProject/status.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(wificonnected.this, response, Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(wificonnected.this, "Error on URL", Toast.LENGTH_SHORT).show();
                    }
                }
        )
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<String,String>();
                map.put("device_id",androidid);
                map.put("start_time",starttime.toString());
                map.put("end_time",endtime.toString());
                map.put("time_diff",Long.toString(sub));
                return map;
            }

        };
        queue.add(stringRequest);
        timer.cancel();
        timer = null;
        Toast.makeText(wificonnected.this, "Alert : Unauthorized Network ! Disconnected!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(wificonnected.this, MainActivity.class);
        startActivity(intent);
    }




    void getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5, this);
        }
        catch(SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {

        locationText.setText("Latitude: " + location.getLatitude() + "\n Longitude: " + location.getLongitude());

        Location startPoint = new Location("locationA");
        startPoint.setLatitude(18.4889113);
        startPoint.setLongitude(73.8139163);

        Location endPoint = new Location("locationA");
        endPoint.setLatitude(location.getLatitude());
        endPoint.setLongitude(location.getLongitude());

        double distance = startPoint.distanceTo(endPoint);
       // Toast.makeText(this, (int) distance, Toast.LENGTH_SHORT).show();
        locationText.setText(locationText.getText()+ "\n Distance: " +Double.toString(distance));

        if(distance>5.00)
        {
            //locationText.setText("Alert! More than 5 mtrs");

            Toast.makeText(wificonnected.this,"Above 5 mtrs", Toast.LENGTH_SHORT).show();
            stoptimer();
        }

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(wificonnected.this, "Please Enable GPS and Internet", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

}
