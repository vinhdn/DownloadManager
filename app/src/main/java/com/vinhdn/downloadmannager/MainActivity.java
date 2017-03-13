package com.vinhdn.downloadmannager;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.vinhdn.downloadmannager.base.DownloadManager;
import com.vinhdn.downloadmannager.base.Downloader;
import com.vinhdn.downloadmannager.base.HttpDownloader;

import java.io.File;
import java.util.Observable;
import java.util.Observer;

public class MainActivity extends AppCompatActivity implements Observer{

    private String link = "http://mirror.downloadvn.com/videolan/vlc/2.2.4/macosx/vlc-2.2.4.dmg";
    private String folder = "/sdcard/" + "DownloadManager/";
    private Downloader downloader;

    TextView progressTv, statusTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressTv = (TextView) findViewById(R.id.progressTv);
        statusTv = (TextView) findViewById(R.id.statusTv);
        checkPermisstion();
        File file = new File(folder);
        file.mkdirs();
        findViewById(R.id.downloadBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownloadManager downloadManager = DownloadManager.getInstance();
                downloader = downloadManager.createDownload(DownloadManager.verifyURL(link), folder);
                downloader.addObserver(MainActivity.this);
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean checkPermisstion() {
        boolean iPer = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        69);
                iPer = false;
            }
        }
        return iPer;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 69 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            new Handler().post(new Runnable() {
                public void run() {

                }
            });
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        if (downloader != null && downloader.equals(o) && statusTv != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int state = downloader.getState();
                    switch (state) {
                        case Downloader.DOWNLOADING:
                            statusTv.setText("Status: " + "Downloading");
                            break;
                        case Downloader.PAUSED:
                            statusTv.setText("Status: " + "Pause");
                            break;
                        case Downloader.ERROR:
                            statusTv.setText("Status: " + "Error");
                            break;
                        case Downloader.COMPLETED:
                            statusTv.setText("Status: " + "Completed");
                            break;
                        default: // COMPLETE or CANCELLED
                            statusTv.setText("Status: " + "Canceled");
                    }
                }
            });

        } else {
            // No download is selected in table.
        }
    }
}
