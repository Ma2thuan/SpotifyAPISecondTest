package com.example.spotifyapisecondtest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.example.spotifyapisecondtest.Class.Song;
import com.example.spotifyapisecondtest.Connectors.SongService;
import com.example.spotifyapisecondtest.databinding.ActivityMainBinding;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;

    private TextView userView;
    private TextView songView;
    private Button addBtn;
    private Song song;
    private SongService songService;
    private ArrayList<Song> recentlyPlayedTracks;
    private SpotifyAppRemote spotifyAppRemote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        songService = new SongService(getApplicationContext());
        userView = findViewById(R.id.txtUser);
        songView = findViewById(R.id.txtSong);
        addBtn = findViewById(R.id.btnAdd);


        SharedPreferences sharedPreferences = this.getSharedPreferences("SPOTIFY", 0);
        userView.setText(sharedPreferences.getString("userid", "No User"));

        getTracks();
    }



    private void getTracks() {
        songService.getRecentlyPlayedTracks(() -> {
            recentlyPlayedTracks = songService.getSongs();
            updateSong();
            //playSong();
        });
    }

    private void updateSong() {
        if (recentlyPlayedTracks.size() > 0) {
            songView.setText(recentlyPlayedTracks.get(0).getName());
            song = recentlyPlayedTracks.get(0);
        }
    }



}