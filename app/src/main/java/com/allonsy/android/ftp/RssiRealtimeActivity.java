package com.allonsy.android.ftp;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.opencsv.CSVWriter;

import java.io.FileWriter;

public class RssiRealtimeActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int REQUEST_LOCATION = 1;
    private static int i;
    Button btnGetRSSI, btnGoBack, btnExportCsv;
    TextView txtRSSI;

    LocationManager locationManager;
    String latitude, longitude;
    String[] header = new String[]{"Latitude","Longitude","SignalStrength\n"};

    CSVWriter writer = null;



    public int mSignalStrength;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rssi_realtime);

        Toast.makeText(this,"Mode RealTime: CSV created",Toast.LENGTH_SHORT).show();

        CreateCSV();
        i=0;
        try
        {
            writer.writeNext(header);
        }
        catch (Exception e)
        {
            Log.d("TAG", e.getMessage());
        }

        btnGoBack = (Button) findViewById(R.id.btn_go_back);

        btnGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    writer.close();
                    Toast.makeText(getApplication(),"CSV closed",Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Log.d("TAG", e.getMessage());
                }
                finish();
            }
        });

        isStoragePermissionGranted();

        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        txtRSSI = (TextView)findViewById(R.id.txt_rssi);
        btnGetRSSI = (Button)findViewById(R.id.btn_get_rssi);
        btnExportCsv = (Button)findViewById((R.id.btn_export_csv));

        MyPhoneStateListener mPhoneStatelistener = new MyPhoneStateListener();
        TelephonyManager mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        mTelephonyManager.listen(mPhoneStatelistener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);

    }


    @Override
    public void onClick(View v) {

        GetInFormation();
    }

    public void OnClickExportCsv(View v){
        try{
            writer.close();
            Toast.makeText(getApplication(),"CSV closed",Toast.LENGTH_SHORT).show();
        }
        catch (Exception e){
            Log.d("TAG", e.getMessage());
        }
    }


    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("TAG","Permission is granted");
                return true;
            } else {

                Log.v("TAG","Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v("TAG","Permission is granted");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            Log.v("TAG","Permission: "+permissions[0]+ "was "+grantResults[0]);
            //resume tasks needing this permission
        }
    }

    private void GetInFormation(){
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

        } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            getRSSI();

            txtRSSI.setText("Your current location is"+ "\n" + "Lattitude = " + latitude
                    + "\n" + "Longitude = " + longitude +  "\n" + "SignalStrength = " + mSignalStrength+"dBm");
            try{
                i++;
                writer.writeNext(new String[]{latitude,longitude,mSignalStrength+"\n"});
                Toast.makeText(getApplication(),"CSV writed: " +i,Toast.LENGTH_SHORT).show();
            }
            catch (Exception e){
                Log.d("TAG",e.getMessage());
            }
        }
    }

    private void getRSSI() {
        if (ActivityCompat.checkSelfPermission(RssiRealtimeActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (RssiRealtimeActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(RssiRealtimeActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        } else {
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            Location location1 = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            Location location2 = locationManager.getLastKnownLocation(LocationManager. PASSIVE_PROVIDER);

            if (location != null) {
                double latti = location.getLatitude();
                double longi = location.getLongitude();
                latitude = String.valueOf(latti);
                longitude = String.valueOf(longi);

            } else  if (location1 != null) {
                double latti = location1.getLatitude();
                double longi = location1.getLongitude();
                latitude = String.valueOf(latti);
                longitude = String.valueOf(longi);

            } else  if (location2 != null) {
                double latti = location2.getLatitude();
                double longi = location2.getLongitude();
                latitude = String.valueOf(latti);
                longitude = String.valueOf(longi);

            }else{

                Toast.makeText(this,"Unble to Trace your location",Toast.LENGTH_SHORT).show();

            }
        }
    }

    protected void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please Turn ON your GPS Connection")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();


    }



    public class MyPhoneStateListener extends PhoneStateListener {
        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);
            if (signalStrength.isGsm()) {
                if (signalStrength.getGsmSignalStrength() != 99)
                    mSignalStrength = signalStrength.getGsmSignalStrength() * 2 - 113;
                else
                    mSignalStrength = signalStrength.getGsmSignalStrength();
            } else {
                mSignalStrength = signalStrength.getCdmaDbm();
            }
            String a1 = mSignalStrength + "";

        }

    }

    private void CreateCSV() {
        try
        {
            writer = new CSVWriter(new FileWriter("/sdcard/signal.csv"), ',');
//            writer.writeNext(header);
        }
        catch (Exception e)
        {
            Log.d("TAG", e.getMessage());
        }
    }
}
