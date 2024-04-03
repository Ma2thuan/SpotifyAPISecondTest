package com.example.spotifyapisecondtest;

import static com.spotify.sdk.android.auth.AccountsQueryParameters.CLIENT_ID;
import static com.spotify.sdk.android.auth.AccountsQueryParameters.REDIRECT_URI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.spotifyapisecondtest.Class.Song;
import com.example.spotifyapisecondtest.Connectors.SongService;
import com.example.spotifyapisecondtest.databinding.ActivityMainBinding;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;

    private TextView userView;
    private TextView songView;
    private TextView songID;
    private Button addBtn;
    private Button findBtn;
    private Song song;
    private SongService songService;
    private ArrayList<Song> recentlyPlayedTracks;
    private ArrayList<Song> recentlyFoundTracks;
    private SpotifyAppRemote mSpotifyAppRemote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        songService = new SongService(getApplicationContext());
        userView = findViewById(R.id.txtUser);
        songView = findViewById(R.id.txtSong);
        songID = findViewById(R.id.songID);


        addBtn = findViewById(R.id.btnAdd);
        addBtn.setOnClickListener(addListener);

        findBtn = findViewById(R.id.btnFind);
        findBtn.setOnClickListener(addFinder);



        SharedPreferences sharedPreferences = this.getSharedPreferences("SPOTIFY", 0);
        userView.setText(sharedPreferences.getString("userid", "No User"));

        getTracks();
    }




    private View.OnClickListener addListener = v -> {
        songService.addSongToLibrary(this.song);
        if (recentlyPlayedTracks.size() > 0) {
            recentlyPlayedTracks.remove(0);
        }
        updateSong();
    };

    private View.OnClickListener addFinder = v -> {
        songService.findSong(binding.nameFindSong.toString());
        if (recentlyFoundTracks.size() > 0) {
            recentlyFoundTracks.remove(0);
        }
        updateSong();
    };

    // Run and Update song Data
    private void getTracks() {
        songService.getRecentlyPlayedTracks(() -> {
            recentlyPlayedTracks = songService.getSongs();
            playTrack();
            updateSong();
        });
    }


    // Run song
    private void playTrack() {
        if (recentlyPlayedTracks.size() > 0) {
            String trackID = recentlyPlayedTracks.get(0).getId();
            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("TRACK_ID_KEY", trackID);
            editor.apply();

        }
    }

    private void updateSong() {
        if (recentlyPlayedTracks.size() > 0) {
            songView.setText(recentlyPlayedTracks.get(0).getName());
            songID.setText(recentlyPlayedTracks.get(0).getId());
            song = recentlyPlayedTracks.get(0);
        }
    }
}