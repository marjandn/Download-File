package dnejad.marjan.downloadfile;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Marjan.Dnejad
 * on 2/21/2018.
 */


// https://www.learn2crack.com/2016/05/downloading-file-using-retrofit.html
public class DownloadService extends IntentService {

    public DownloadService() {
        super("Download Service");
    }

    private NotificationCompat.Builder notificationBuilder;
    private NotificationManager notificationManager;
    private int totalFileSize;

    File outputFile;

    @Override
    protected void onHandleIntent(Intent intent) {

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_download)
                .setContentTitle("Download")
                .setContentText("Downloading File")
                .setAutoCancel(true);
        notificationManager.notify(0, notificationBuilder.build());

        initDownload();

    }


    private void initDownload() {

//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl("https://download.learn2crack.com/")
//                .build();
//
//        TaskService retrofitInterface = retrofit.create(TaskService.class);

        Call<ResponseBody> request = new ServiceGenerator().getService().downloadFile(MainActivity.url);

        try {

            downloadFile(request.execute());

        } catch (IOException e) {

            e.printStackTrace();
            Log.e("Download Service", e.toString());

            /*
            * call to Broadcast to know that download had failed and do sth
            */
            Intent intent = new Intent(MainActivity.DOWNLOAD_FAILED);
            LocalBroadcastManager.getInstance(DownloadService.this).sendBroadcast(intent);
        }
    }


    private void downloadFile(Response response) throws IOException {
        ResponseBody body = (ResponseBody) response.body();

        if (body != null) {

            /*
            * this line is for get NAME of downloaded file (i used asp.net mvc and return special name for file)
            *
            * 4 is index of  attachment; filename=xxx
            */
            String s = response.headers().value(4); //

            // to extract xxx from s
            String fis = s.substring(s.indexOf("=") + 1);

            int count;
            byte data[] = new byte[1024 * 4];
            long fileSize = body.contentLength();
            InputStream bis = new BufferedInputStream(body.byteStream(), 1024 * 8);
            //save it in Download directory
            outputFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    fis + ".jpg");
            OutputStream output = new FileOutputStream(outputFile);
            long total = 0;
            long startTime = System.currentTimeMillis();
            int timeCount = 1;
            while ((count = bis.read(data)) != -1) {

                total += count;
                totalFileSize = (int) (fileSize / (Math.pow(1024, 2)));
                double current = Math.round(total / (Math.pow(1024, 2)));

                int progress = (int) ((total * 100) / fileSize);

                long currentTime = System.currentTimeMillis() - startTime;

                Download download = new Download();
                download.setTotalFileSize(totalFileSize);

                if (currentTime > 1000 * timeCount) {
                    download.setCurrentFileSize((int) current);
                    download.setProgress(progress);
                    sendNotification(download);
                    timeCount++;
                }

                output.write(data, 0, count);
            }
            onDownloadComplete();
            output.flush();
            output.close();
            bis.close();
        } else {

            /*
            * call to failed download
            */
            Intent intent = new Intent(MainActivity.DOWNLOAD_FAILED);
            LocalBroadcastManager.getInstance(DownloadService.this).sendBroadcast(intent);
        }
    }

    /*
    * show progress change in notification
    */
    private void sendNotification(Download download) {
        sendSuccessIntent(download);
        notificationBuilder.setProgress(100, download.getProgress(), false);
        notificationBuilder.setContentText("Downloading file " + download.getCurrentFileSize() + "/" + totalFileSize + " MB");
        notificationManager.notify(0, notificationBuilder.build());
    }

    /*
    * if download was success then call MainActivity to show download changed
    */
    private void sendSuccessIntent(Download download) {
        Intent intent = new Intent(MainActivity.MESSAGE_PROGRESS);
        intent.putExtra("download", download);
        LocalBroadcastManager.getInstance(DownloadService.this).sendBroadcast(intent);
    }

    private void onDownloadComplete() {

        //https://stackoverflow.com/questions/38200282/android-os-fileuriexposedexception-file-storage-emulated-0-test-txt-exposed
        Uri photoURI = FileProvider.getUriForFile(getApplicationContext(), getApplication().getApplicationContext().getPackageName()
                + ".dnejad.marjan.downloadfile.provider", outputFile);

        /*
        * when click on finished notification
        * show image in Gallery Mode
        */
        Intent intent = new Intent(Intent.ACTION_VIEW, photoURI);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        PendingIntent contentIntent =
                PendingIntent.getActivity(getApplicationContext(),
                        0,
                        intent,
                        PendingIntent.FLAG_ONE_SHOT
                );
        notificationBuilder.setContentIntent(contentIntent);

        Download download = new Download();
        download.setProgress(100);
        sendSuccessIntent(download);

        notificationManager.cancel(0);
        notificationBuilder.setProgress(0, 0, false);
        notificationBuilder.setContentText("File Downloaded");
        notificationManager.notify(0, notificationBuilder.build());

    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        notificationManager.cancel(0);
    }

}
