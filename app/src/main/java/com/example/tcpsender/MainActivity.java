
package com.example.tcpsender;

import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    Button startBtn, stopBtn;
    EditText vehicleInput;

    Handler handler = new Handler();
    boolean isSending = false;

    String SERVER_IP = "192.168.1.100";
    int SERVER_PORT = 5000;

    Runnable sendTask = new Runnable() {
        @Override
        public void run() {
            if (isSending) {

                double lat = 0.0;
                double lon = 0.0;

                LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
                try {
                    Location loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (loc != null) {
                        lat = loc.getLatitude();
                        lon = loc.getLongitude();
                    }
                } catch (SecurityException e) {
                    e.printStackTrace();
                }

                String vehicle = vehicleInput.getText().toString();

                String imei = Settings.Secure.getString(
                        getContentResolver(),
                        Settings.Secure.ANDROID_ID
                );

                String date = new SimpleDateFormat("ddMMyyyy", Locale.getDefault()).format(new Date());
                String time = new SimpleDateFormat("HHmmss", Locale.getDefault()).format(new Date());

                String packet = "$NRM,WTEX,1.ONTC,NR,01,L," + imei + "," + vehicle + ",1," + date + "," + time + "," + lat + ",N," + lon + ",E,000.0,193.16,...()*3E";

                TcpSender.sendPacket(SERVER_IP, SERVER_PORT, packet);

                handler.postDelayed(this, 10000);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startBtn = findViewById(R.id.startBtn);
        stopBtn = findViewById(R.id.stopBtn);
        vehicleInput = findViewById(R.id.vehicleInput);

        startBtn.setOnClickListener(v -> {
            isSending = true;
            handler.post(sendTask);
        });

        stopBtn.setOnClickListener(v -> {
            isSending = false;
            handler.removeCallbacks(sendTask);
        });
    }
}
