package com.allonsy.android.ftp;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.allonsy.android.ftp.R;

public abstract class SingleFragmentActivity extends AppCompatActivity {

     //Button btnGoRssiRealtime, btnGoRssiSimulation;

    protected abstract Fragment createFragment();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

//        btnGoRssiRealtime = (Button) findViewById(R.id.btn_go_rssi_realtime);
//        btnGoRssiSimulation = (Button) findViewById(R.id.btn_go_rssi_simulation);
//
//        btnGoRssiRealtime.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                launchRealtimeActivity();
//            }
//        });
//
//        btnGoRssiSimulation.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                launchSimulationActivity();
//            }
//        });

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        if (fragment == null) {
            fragment = createFragment();
            fm.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }
    }



    private void launchSimulationActivity()  {
        Intent intent = new Intent(this, RssiSimulationActivity.class);
        startActivity(intent);
    }

    private void launchRealtimeActivity()  {
        Intent intent = new Intent(this, RssiRealtimeActivity.class);
        startActivity(intent);
    }
}
