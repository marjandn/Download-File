package dnejad.marjan.downloadfile;

/**
 * Created by Marjan.Dnejad
 * on 2/21/2018.
 */

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    public static final String MESSAGE_PROGRESS = "message_progress";
    public static final String DOWNLOAD_FAILED = "download_failed";
    private static final int PERMISSION_REQUEST_CODE = 1;

    ProgressBar mProgressBar;
    TextView mProgressText;

    public static String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProgressBar = findViewById(R.id.progress);
        mProgressText = findViewById(R.id.progress_text);

        Button mDownloadButton=findViewById(R.id.btn_download);
        mDownloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadFile();
            }
        });

//        Intent intent = getIntent();
//        String url=intent.getStringExtra("URL");
        url = "Picture/Image";
        registerReceiver();
    }


    /*
    * when click on download button
    */
    public void downloadFile() {
        if (checkPermission()) {
            startDownload();
        } else {
            requestPermission();
        }
    }


    /*
    * run DownloadService class
    */
    private void startDownload() {

        Intent intent = new Intent(this, DownloadService.class);
        startService(intent);

    }

    /*
    * register Broadcast
    */
    private void registerReceiver() {
        LocalBroadcastManager bManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MESSAGE_PROGRESS);
        intentFilter.addAction(DOWNLOAD_FAILED);
        bManager.registerReceiver(broadcastReceiver, intentFilter);

    }


    /*
    * config Broadcast
    */
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(MESSAGE_PROGRESS)) {

                Download download = intent.getParcelableExtra("download");
                mProgressBar.setProgress(download.getProgress());
                if (download.getProgress() == 100) {

                    mProgressText.setText("File Download Complete");

                } else {

                    mProgressText.setText(String.format("Downloaded (%d/%d) MB", download.getCurrentFileSize(), download.getTotalFileSize()));

                }
            }else if (intent.getAction().equals(DOWNLOAD_FAILED)){
                Toast.makeText(context, "FAILED", Toast.LENGTH_SHORT).show();
                Log.e("Download Service","Download Failed");
            }
        }
    };

    /*
    * because we want to save downloaded file in external storage
    * so we need the runtime permission for WRITE EXTERNAL STORAGE
    */
    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {

            return true;

        } else {

            return false;
        }
    }
    private void requestPermission() {

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    startDownload();
                } else {

                    Snackbar.make(findViewById(R.id.coordinatorLayout), "Permission Denied, Please allow to proceed !", Snackbar.LENGTH_LONG).show();

                }
                break;
        }
    }

}


