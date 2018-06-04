package com.allonsy.android.ftp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class RssiSimulationActivity extends AppCompatActivity implements View.OnClickListener{

    private Button btnGoBackSim, btnGetRSSISim;
    TextView txtRSSISim;

    String latitude, longitude;
    int mSignalStrength;

    final double latitudeMin = 8.50, latitudeMax = 23.33;
    final double longitudeMin = 102.16, longitudeMax = 109.50;
    final int signalStrengthMin = -91, signalStrengthMax = -47;

    private Random random = new Random();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rssi_simulation);

        Toast.makeText(this,"Mode Simulation",Toast.LENGTH_SHORT).show();

        btnGoBackSim = (Button) findViewById(R.id.btn_go_back_sim);
        btnGetRSSISim = (Button) findViewById(R.id.btn_get_rssi_sim);
        txtRSSISim = (TextView) findViewById(R.id.txt_rssi_sim);

        latitude = nextDouble(latitudeMin, latitudeMax) +"";
        longitude = nextDouble(longitudeMin, longitudeMax) +"";
        // Go back main activity
        btnGoBackSim.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
        mSignalStrength = nextInt(signalStrengthMin, signalStrengthMax);
        String [] entries = {latitude, longitude, mSignalStrength+""};

        txtRSSISim.setText("Your current location is"+ "\n" + "Lattitude = " + latitude
                + "\n" + "Longitude = " + longitude +  "\n" + "SignalStrength = " + mSignalStrength+"dBm");
    }

    public double nextDouble(double min, double max){
        return random.nextDouble() * (max - min) + min;
    }

    public int  nextInt(int min, int max){
        return random.nextInt(max - min + 1) + min;
    }
}