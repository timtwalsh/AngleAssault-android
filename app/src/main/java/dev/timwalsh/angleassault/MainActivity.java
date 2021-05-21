package dev.timwalsh.angleassault;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

public class MainActivity extends Activity {
    public int gameSpeed = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadSharedPrefs();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // Updates when SettingsActivity made a change.
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SettingsActivity.SETTINGS_REQUEST) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    int newGameSpeed;
                    newGameSpeed = data.getIntExtra("gameSpeed", gameSpeed);
                    // If gameSpeed was updated save in shared prefs
                    SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
                    final SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
                    gameSpeed = newGameSpeed;
                    prefsEditor.putInt("gameSpeed", gameSpeed);
                    prefsEditor.apply();
                }
            }
        }
    }

    private void loadSharedPrefs() {
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        gameSpeed = sharedPreferences.getInt("gameSpeed", gameSpeed);
    }

    public void gameButton(View view) {
        // Open Game with the appropriate intent.
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("gameSpeed", gameSpeed);
        startActivityForResult(intent, GameActivity.GAME_REQUEST);
    }

    public void settingsButton(View view) {
        // Open Settings with the appropriate intent.
        Intent intent = new Intent(this, SettingsActivity.class);
        intent.putExtra("gameSpeed", gameSpeed);
        startActivityForResult(intent, SettingsActivity.SETTINGS_REQUEST);
    }

    public void highScoreButton(View view) {
        // Open Scores with the appropriate intent.
        Intent intent = new Intent(this, HighScoreActivity.class);
        startActivityForResult(intent, HighScoreActivity.HIGHSCORE_REQUEST);
    }

}