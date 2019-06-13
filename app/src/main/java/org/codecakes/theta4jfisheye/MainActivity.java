package org.codecakes.theta4jfisheye;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import com.theta360.pluginlibrary.activity.PluginActivity;
import com.theta360.pluginlibrary.callback.KeyCallback;
import com.theta360.pluginlibrary.receiver.KeyReceiver;

import org.theta4j.webapi.ImageStitching;
import org.theta4j.webapi.Theta;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.theta4j.webapi.Options.IMAGE_STITCHING;


public class MainActivity extends PluginActivity {
    final Theta theta = Theta.createForPlugin();
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private ExecutorService pictureExecutor = Executors.newSingleThreadExecutor();

    private int delay = 6000;
    private int currentPicture = 0;
    private int maxPicture = 3;
    final String TAG = "FISHEYE";

    private KeyCallback keyCallback = new KeyCallback() {
        @Override
        public void onKeyDown(int keyCode, KeyEvent keyEvent) {
            if (keyCode == KeyReceiver.KEYCODE_CAMERA) {
                executor.submit(() -> {
                    Log.d(TAG, "turn off stitching");
                    try {
                        theta.setOption(IMAGE_STITCHING, ImageStitching.NONE);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        }

        @Override
        public void onKeyUp(int keyCode, KeyEvent keyEvent) {
            if (keyCode == KeyReceiver.KEYCODE_CAMERA) {
                pictureExecutor.submit(() -> {
                    Log.d("FISHEYE", "start interval take picture");
                    while (currentPicture < maxPicture) {
                        try {
                            theta.takePicture();
                            currentPicture = currentPicture + 1;
                            Log.d(TAG, "current picture = " + String.valueOf(currentPicture));
                            Thread.sleep(delay);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }

                    }

                });

                currentPicture = 0;
            }
        }

        @Override
        public void onKeyLongPress(int keyCode, KeyEvent keyEvent) {

        }

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MainActivity.super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setAutoClose(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setKeyCallback(keyCallback);

        if (isApConnected()) {

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}

