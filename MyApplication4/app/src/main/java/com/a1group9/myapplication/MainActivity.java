package com.a1group9.myapplication;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private SeekBar seekBar;
    private Handler handler = new Handler();
    private TextView tvSongTitle;
    private final String[] songFiles = {
            "songs/song1.mp3",
            "songs/song2.mp3",
            "songs/song3.mp3",
            "songs/song4.mp3",
            "songs/song5.mp3"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mediaPlayer = new MediaPlayer();
        seekBar = findViewById(R.id.seekBar);
        tvSongTitle = findViewById(R.id.tvSongTitle);
        Spinner songSpinner = findViewById(R.id.songSpinner);
        Button btnPlay = findViewById(R.id.btnPlay);
        Button btnPause = findViewById(R.id.btnPause);

        songSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                stopPlayer();
                loadSong(position);
                tvSongTitle.setText(parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnPlay.setOnClickListener(v -> {
            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.start();
                updateSeekBar();
            }
        });

        btnPause.setOnClickListener(v -> {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress * 1000);
                }
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void loadSong(int position) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(getAssets().openFd(songFiles[position]));
            mediaPlayer.prepare();
            seekBar.setMax(mediaPlayer.getDuration() / 1000);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateSeekBar() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            int currentPosition = mediaPlayer.getCurrentPosition() / 1000;
            seekBar.setProgress(currentPosition);
            handler.postDelayed(this::updateSeekBar, 1000);
        }
    }

    private void stopPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.reset();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        handler.removeCallbacksAndMessages(null);
    }
}