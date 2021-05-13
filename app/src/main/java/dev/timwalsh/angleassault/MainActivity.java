package dev.timwalsh.angleassault;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void settingsButton(View view) {
        // Open Settings with the appropriate intent.
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivityForResult(intent, SettingsActivity.SETTINGS_REQUEST);
    }
    public void highScoreButton(View view) {
        // Open Settings with the appropriate intent.
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivityForResult(intent, HighScoreActivity.HIGHSCORE_REQUEST);
    }
}