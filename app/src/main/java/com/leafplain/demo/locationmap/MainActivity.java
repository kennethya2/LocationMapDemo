package com.leafplain.demo.locationmap;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import static com.leafplain.demo.locationmap.util.location.FusedLocationUpdate.LOCATION_FINE_PERMS;

public class MainActivity extends AppCompatActivity {

    private static final String TAG     = "MainActivity";

    private AppCompatActivity mContext;
    private Button mOpenMapBTN;

    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_main);
        mContext = this;

        mOpenMapBTN = (Button) findViewById(R.id.openMapBTN);
        mOpenMapBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= 23) { // need permission check
                    if (ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(mContext, LOCATION_FINE_PERMS, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                        Log.i(TAG, "requestPermissions !!!");
                    }else{ // permission Granted
                        openLocationMap();
                    }
                    return;
                }
                openLocationMap();
            }
        });
    }

    private void openLocationMap(){
        Log.d(TAG, "openLocationMap");
        Intent intent = new Intent();
        intent.setClass(mContext, LocationMapActivity.class);
        mContext.startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Permission Granted");
                openLocationMap();
            } else {
                Log.d(TAG, "Permission Denied");
                if(Build.VERSION.SDK_INT >= 23) {
                    boolean showRationale = shouldShowRequestPermissionRationale(permissions[0]);
                    if (!showRationale) { // user also CHECKED "never ask again"
                        showToast();
                        showDialog();
                    }
                }
            }
        }
    }

    private void showToast(){
        Toast.makeText(mContext, "需前往app設定以開啟位置資訊權限", Toast.LENGTH_LONG).show();
    }

    private void showDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        String title = "需給予位置資訊";
        String alertDisplay = "需給予位置資訊 \n是否前往app設定以開啟位置資訊權限？";
        builder.setTitle(title);
        builder.setMessage(alertDisplay);
        builder.setPositiveButton("確定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                goResetAppPermission();
            }
        });
        builder.setNegativeButton("取消",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        Dialog mDialog = builder.create();
        mDialog.show();
    }
    private void goResetAppPermission(){
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MainActivity.this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }
}
