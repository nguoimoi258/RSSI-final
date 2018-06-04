package com.allonsy.android.ftp;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Button;

public class FTPListActivity extends SingleFragmentActivity  implements FTPListFragment.MyListener {


    @Override
    protected Fragment createFragment() {
        return new FTPListFragment();
    }

    public void finishActivity(){
        finish();
    }


}