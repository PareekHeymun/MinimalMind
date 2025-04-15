package com.a1group9.myapplication;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.QuickContactBadge;
import android.widget.SeekBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;
public class MainActivity extends AppCompatActivity {
    private MediaPlayer mp;
    private Button btnPause, btnPlay, btnStop;
    private SeekBar seekBar;
    private Handler seekHandler = new Handler();
    private final Runnable updateSeekBar = new Runnable() {
        @Override
        public void run() {
            if (mp != null) {
                int currentPosition = mp.getCurrentPosition();
                seekBar.setProgress(currentPosition);
                if (mp.isPlaying()) {
                    seekHandler.postDelayed(this, 1000);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnPlay = findViewById(R.id.btnPlay);
        btnPause = findViewById(R.id.btnPause);
        btnStop = findViewById(R.id.btnStop);
        seekBar = findViewById(R.id.seekBar);

        // Initialize buttons as disabled
        btnPlay.setEnabled(false);
        btnPause.setEnabled(false);
        btnStop.setEnabled(false);

        mp = new MediaPlayer();
        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mp.setLooping(true); // Set the MediaPlayer to loop

        String aPath = "android.resource://" + getPackageName() + "/raw/song3";
        Uri audioURI = Uri.parse(aPath);

        try {
            mp.setDataSource(this, audioURI);
            mp.setOnPreparedListener(mp -> {
                btnPlay.setEnabled(true);
                seekBar.setMax(mp.getDuration());
                updateSeekBar.run();
            });
            mp.setOnErrorListener((mp, what, extra) -> {
                // Handle error
                return true;
            });
            mp.setOnCompletionListener(mp -> {
                // This will be called even when looping, but setLooping(true) ensures it restarts
                seekBar.setProgress(0);
            });
            mp.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // SeekBar change listener
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mp != null) {
                    mp.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                seekHandler.removeCallbacks(updateSeekBar);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mp != null && mp.isPlaying()) {
                    updateSeekBar.run();
                }
            }
        });

        btnPause.setOnClickListener(v -> {
            if (mp.isPlaying()) {
                mp.pause();
                btnPause.setEnabled(false);
                btnPlay.setEnabled(true);
                seekHandler.removeCallbacks(updateSeekBar);
            }
        });

        btnPlay.setOnClickListener(v -> {
            if (!mp.isPlaying()) {
                mp.start();
                btnPlay.setEnabled(false);
                btnPause.setEnabled(true);
                btnStop.setEnabled(true);
                updateSeekBar.run();
            }
        });

        btnStop.setOnClickListener(v -> {
            if (mp.isPlaying()) {
                mp.stop();
                seekHandler.removeCallbacks(updateSeekBar);
                mp.prepareAsync();
                mp.seekTo(0);
                seekBar.setProgress(0);
                btnPlay.setEnabled(true);
                btnPause.setEnabled(false);
                btnStop.setEnabled(false);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        seekHandler.removeCallbacks(updateSeekBar);
        if (mp != null) {
            mp.release();
            mp = null;
        }
    }
}