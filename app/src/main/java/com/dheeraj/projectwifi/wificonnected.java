package com.dheeraj.projectwifi;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CalendarView;
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
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class wificonnected extends AppCompatActivity {

    WifiManager wifiManager;
    WifiInfo connection;
    String valid = "00:1e:a6:96:8c:e8";
    Timer timer = new Timer();
    final Handler handler = new Handler();
    TimerTask doAsyncTask;
    long start,end,sub;
    Date starttime,endtime;
    String androidid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wificonnected);

        start = System.currentTimeMillis();
        starttime = Calendar.getInstance().getTime();
        /*
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                connection = wifiManager.getConnectionInfo();
                String getBssid = connection.getBSSID();
                if(!valid.equals(getBssid)) {
                    Toast.makeText(wificonnected.this, "Alert : Unauthorized Network ! Disconnected!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(wificonnected.this, MainActivity.class);
                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(wificonnected.this, "Yss", Toast.LENGTH_SHORT).show();
                }
            }
        },5000);
        */
        /*
        Timer t = new Timer();
        t.scheduleAtFixedRate(
                new TimerTask() {
                    @Override
                    public void run() {
                        wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                        connection = wifiManager.getConnectionInfo();
                        String getBssid = connection.getBSSID();
                        if(!valid.equals(getBssid)) {
                            //Toast.makeText(wificonnected.this, "Alert : Unauthorized Network ! Disconnected!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(wificonnected.this, MainActivity.class);
                            startActivity(intent);

                        }
                        /*
                        else
                        {
                            Toast.makeText(wificonnected.this, "Yss", Toast.LENGTH_SHORT).show();
                        }

                    }
                },0,2000
        );
        */

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
                            Toast.makeText(wificonnected.this, "Yss", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        };
        timer.schedule(doAsyncTask,0,2000);


    }
    public void stoptimer()
    {
        //to get end time


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


        //to stop timer
        timer.cancel();
        timer = null;
        Toast.makeText(wificonnected.this, "Alert : Unauthorized Network ! Disconnected!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(wificonnected.this, MainActivity.class);
        startActivity(intent);
    }
}
