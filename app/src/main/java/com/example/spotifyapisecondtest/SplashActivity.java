package com.example.spotifyapisecondtest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.spotifyapisecondtest.Class.User;
import com.example.spotifyapisecondtest.Connectors.SongService;
import com.example.spotifyapisecondtest.Connectors.UserService;
import com.example.spotifyapisecondtest.databinding.ActivitySplashBinding;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

public class SplashActivity extends AppCompatActivity {

    ActivitySplashBinding binding;
    private static final String CLIENT_ID ="4e5e333774674e6d9b9a34f9f68fe051";
    private static final String REDIRECT_URI = "http://localhost:8888/callback";
    private static final int REQUEST_CODE = 1337;
    private static final String SCOPES = "user-read-recently-played,user-library-modify,user-read-email,user-read-private,streaming";

    private SharedPreferences.Editor editor;
    private SharedPreferences msharedPreferences;

    private RequestQueue queue;

    private SpotifyAppRemote mSpotifyAppRemote;

    private SongService songService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        authenticateSpotify();

        msharedPreferences = this.getSharedPreferences("SPOTIFY", 0);
        queue = Volley.newRequestQueue(this);

        SpotifyAppRemote.connect(this, connectionParams,
                new Connector.ConnectionListener() {
                    @Override
                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        mSpotifyAppRemote = spotifyAppRemote;
                        Log.d("SongPlay", "Connected! Yay!");

                        connected();
                        //spotifyAppRemote.getPlayerApi().play("spotify:playlist:" + songService.getSongs().toString() );

                    }

                    @Override
                    public void onFailure(Throwable throwable) {

                    }
                });


    }

    private void authenticateSpotify() {
        AuthorizationRequest.Builder builder = new AuthorizationRequest.Builder(CLIENT_ID, AuthorizationResponse.Type.TOKEN, REDIRECT_URI);
        builder.setScopes(new String[]{SCOPES});
        AuthorizationRequest request = builder.build();
        AuthorizationClient.openLoginActivity(SplashActivity.this, REQUEST_CODE, request);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, intent);

            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    editor = getSharedPreferences("SPOTIFY", 0).edit();
                    editor.putString("token", response.getAccessToken());
                    Log.d("SpotifyAuth", "GOT AUHT TOKEN" + response.getAccessToken() );
                    editor.apply();

                    waitForUserInfo();

                    break;

                // Auth flow returned an error
                case ERROR:
                    // Handle error response
                    Log.d("SpotifyAuth" , "Lỗi: TOKEN "  + response.getAccessToken());
                    Log.d("SpotifyAuth" , response.getError());
                    Log.d("SpotifyAuth" , response.getState());
                    break;

                // Most likely auth flow was cancelled
                default:
                    // Handle other cases
                    Log.d("SpotifyAuth" , "Cancel "  + response.getAccessToken());
            }
        }
    }

    private void waitForUserInfo() {
        UserService userService = new UserService(queue, msharedPreferences);
        userService.get(() -> {
            User user = userService.getUser();
            editor = getSharedPreferences("SPOTIFY", 0).edit();
            editor.putString("userid", user.id);
            Log.d("SpotifyAuth", "GOT USER INFORMATION");
            // We use commit instead of apply because we need the information stored immediately
            editor.commit();
            startMainActivity();
        });
    }




    private void startMainActivity() {
        Intent intent = new Intent(SplashActivity.this , MainActivity.class);
        startActivity(intent);
    }


    //App remote params
    ConnectionParams connectionParams =
            new ConnectionParams.Builder(CLIENT_ID)
                    .setRedirectUri(REDIRECT_URI)
                    .showAuthView(true)
                    .build();



    @Override
    protected void onStart() {
        super.onStart();
        // We will start writing our code here.
    }

    private void connected() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        String message = sharedPreferences.getString("TRACK_ID_KEY", "");

        mSpotifyAppRemote.getPlayerApi().play("spotify:track:"+message);

        Log.d("SongPlay","spotify:track:"+message);
    }

    @Override
    protected void onStop() {
        super.onStop();
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);
    }
}