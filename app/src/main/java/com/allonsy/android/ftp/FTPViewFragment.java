package com.allonsy.android.ftp;


import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static android.content.ContentValues.TAG;

public class FTPViewFragment extends Fragment {

    private FTP mFTP;
    private String mPassword;
	private TextView mConnectionName;
	private TextView mServerIpPort;
	private TextView mServerUsername;
	private TextView mDestination;
	private TextView mSources;
    List<String> sources;


    private static final String ARG_FTP_ID = "ftp_id";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        UUID ftpId = (UUID) getArguments().getSerializable(ARG_FTP_ID);
        mFTP = FTPLab.get(getActivity()).getFTP(ftpId);
        mPassword  = FTPLab.get(getActivity()).retrieveServerPassword(mFTP);

    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_ftp_view, container, false);

        mConnectionName = (TextView) v.findViewById(R.id.view_ftp_connection_name);
		mServerIpPort = (TextView) v.findViewById(R.id.view_ftp_server_ip_port);
		mServerUsername = (TextView) v.findViewById(R.id.view_ftp_server_username);
		mDestination = (TextView) v.findViewById(R.id.view_ftp_server_destination);
		mSources = (TextView) v.findViewById(R.id.view_ftp_source_list);

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_ftp_view, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_ftp_run:
                if (!isMyServiceRunning(FTPService.class)) {
                    Intent intent1 = FTPService.newIntent(getActivity(), mFTP.getId());
                    getContext().startService(intent1);
                } else
                    Toast.makeText(getActivity(), "An FTP transfer already running", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_item_ftp_edit:
                Intent intent2 = FTPEditActivity.newIntent(getActivity(), mFTP);
                startActivityForResult(intent2,FTPEditFragment.UPDATE_FTP);
                return true;
            case R.id.menu_item_ftp_delete:
                showConfirmDeleteDialogue();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateUI()
    {
        if(mFTP!=null)
        {

            mConnectionName.setText("Connection Name : " + mFTP.getConnectionName());
            mServerIpPort.setText("Ip/port : " + mFTP.getServerIP() + ":" + mFTP.getServerPort());
            mServerUsername.setText("Username : " + mFTP.getServerUsername());
            mDestination.setText("Destination : " + mFTP.getDestination());


            sources = mFTP.getSources();
            String sourcesString="";
            for(int i=0;i!=sources.size();i++)
            {
                sourcesString+=sources.get(i) + "\n";
            }
            mSources.setText(sourcesString);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == FTPEditFragment.UPDATE_FTP) {

            String returnValue = data.getStringExtra(FTPEditFragment.RETURN_STATE);
            if(returnValue!=null) {
                if (returnValue.equals("0")){
                    Toast.makeText(getActivity(), "cancelled",
                            Toast.LENGTH_SHORT).show();
                }
                else if (returnValue.equals("1")) {

                    FTP ftp = (FTP) data.getSerializableExtra(FTPEditFragment.FTP_OBJECT);
                    String password = data.getStringExtra(FTPEditFragment.FTP_PASSWORD);
                    if(ftp!=null && password!=null) {
                        Toast.makeText(getActivity(), "saved",
                                Toast.LENGTH_SHORT).show();
                        mFTP=ftp;
                        mPassword=password;
                        FTPLab.get(getActivity()).updateFTP(ftp,password);
                    }
                }
            }

        }
    }


    public static FTPViewFragment newInstance(UUID ftpId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_FTP_ID, ftpId);
        FTPViewFragment fragment = new FTPViewFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void showConfirmDeleteDialogue() {

        new AlertDialog.Builder(getActivity())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Delete")
                .setMessage("Are you sure?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FTPLab.get(getActivity()).deleteFTP(mFTP);
                        getActivity().finish();
                    }

                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }

                })
                .show();
    }

}
