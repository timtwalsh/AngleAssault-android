package dev.timwalsh.angleassault;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import android.widget.ToggleButton;

public class SettingsActivity extends Activity {
    public static final int SETTINGS_REQUEST = 1;
    DatabaseHelper database;
    private int gameSpeed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Bundle extras = getIntent().getExtras();
        gameSpeed = extras.getInt("gameSpeed");
        if (gameSpeed >= 0) {
            ToggleButton gameSpeedToggle = findViewById(R.id.gameMode);
            gameSpeedToggle.setChecked(gameSpeed == 10);
        }
    }

    public void gameModeClicked(View view) {
        if (gameSpeed == 10)
            gameSpeed = 5;
        else
            gameSpeed = 10;
        Log.e("GameMode", String.valueOf(gameSpeed));
    }

    public void resetDataButton(View view) {
        database = new DatabaseHelper(this);
        database.resetScoresData();
        Toast.makeText(this, "All Scores have been cleared.", Toast.LENGTH_SHORT).show();
    }

    public void saveClicked(View view) {
        Intent intent = new Intent();
        intent.putExtra("gameSpeed", gameSpeed);
        setResult(RESULT_OK, intent); // Set the response
        finish();
    }
}