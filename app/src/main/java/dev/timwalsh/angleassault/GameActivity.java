package dev.timwalsh.angleassault;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;

public class GameActivity extends Activity implements SensorEventListener {
    public static final int GAME_REQUEST = 0;
    static final float DEFAULT_GAME_TIMER = 10;

    private SensorManager sensorManager;
    private Sensor gravitySensor;
    private Sensor magneticSensor;
    private float[] gravityValues;
    private float[] geomagneticValues;
    double sensorAngle;

    // UI elements
    private AngleView angleView;
    TextView questionText;
    TextView angleText;
    TextView timerText;
    TextView scoreText;

    // Activity Data
    int gameSpeed = (int) DEFAULT_GAME_TIMER;
    public float fullRoundTime = DEFAULT_GAME_TIMER;
    public float currentRemainingTime = DEFAULT_GAME_TIMER;
    DatabaseHelper database;
    boolean gameRunning = false;
    private String playerName = "";
    AngleGame angleGame;
    long startTime = 0;
    Handler timerHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Bundle extras = getIntent().getExtras();
        if (!extras.isEmpty()) {
            gameSpeed = extras.getInt("gameSpeed");
        }
        if (gameSpeed >= 0) {
            currentRemainingTime = gameSpeed;
            fullRoundTime = gameSpeed;
        }
        loadData();
        // Setup for runtime
        angleGame = new AngleGame(this);
        questionText = findViewById(R.id.questionText);
        timerText = findViewById(R.id.timerText);
        angleText = findViewById(R.id.currentAngleText);
        scoreText = findViewById(R.id.scoreText);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        angleView = findViewById(R.id.angleView);
        scoreText.setText(String.format(Locale.getDefault(), "%s%d", getResources().getString(R.string.score_text), angleGame.getScore())); // show the initial score
        startGame();
    }

    public void startGame() {
        angleGame.resetGame();
        angleGame.newQuestion();
        fullRoundTime = gameSpeed;
        currentRemainingTime = fullRoundTime;
        gameRunning = true;
        startTimer();
    }

    public void startTimer() {
        startTime = System.currentTimeMillis();
        timerHandler.postDelayed(timerRunnable, 0);
    }

    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            long interval = 10;
            long millis = System.currentTimeMillis() - startTime;
            currentRemainingTime = (fullRoundTime * 10f - millis / 100f) / interval;
            updateUI();
            if (currentRemainingTime <= 0) {
                onTick();
            } else {
                timerHandler.postDelayed(this, interval);
            }
        }
    };


    public void onTick() {
        timerHandler.removeCallbacks(timerRunnable);
        if (angleGame.checkAnswer(sensorAngle)) {
            angleGame.winRound(fullRoundTime, DEFAULT_GAME_TIMER); // Score bonus as game speeds up
            if (fullRoundTime > 2)
                fullRoundTime -= 0.5; // Min timer of 2 second
            angleGame.newQuestion();
            updateUI();
            startTimer();
        } else {
            gameRunning = false;
            // Inflate the layout of the game over window
            popupGameOverWindow();
        }
    }

    private void updateUI() {
        scoreText.setText(String.format("%s%s", getResources().getString(R.string.score_text), angleGame.getScore()));
        timerText.setText(getString(R.string.seconds_short, currentRemainingTime));
        questionText.setText(angleGame.getQuestion());
    }

    private void popupGameOverWindow() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View gameOverView = inflater.inflate(R.layout.fragment_game_over, null);
        // Create popup window
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;
        PopupWindow gameOverWindow = new PopupWindow(gameOverView, width, height, true);
        EditText editText = gameOverWindow.getContentView().findViewById(R.id.playerNameInput);
        editText.setText(playerName);
        // Show popup window
        gameOverWindow.showAtLocation(gameOverView, Gravity.CENTER, 0, 0);
        TextView popupScore = gameOverView.findViewById(R.id.scoreNumber);
        popupScore.setText(String.valueOf(angleGame.getScore()));
        EditText playerNameInput = gameOverView.findViewById(R.id.playerNameInput);
        gameOverWindow.setOnDismissListener(() -> {
            completeRound(playerNameInput);
            gameOverWindow.dismiss();
        });
        // Close the popup window and start a new game.
        Button newGameButton = gameOverView.findViewById(R.id.newGameButton);
        newGameButton.setOnClickListener(v -> gameOverWindow.dismiss());
    }

    private void completeRound(EditText playerNameInput) {
        playerName = playerNameInput.getText().toString();
        Log.d("GameActivity: Adding Score ", playerName + " @ " + angleGame.getScore());
        if (!playerName.equals(getString(R.string.enter_name)) && playerName != null) {
            database.addScore(new HighScoreItem(playerName, angleGame.getScore()));
        }
        startGame();
    }


    @Override
    protected void onResume() {
        super.onResume();
        startGame();
        // Sensor vars
        int sensorUpdateRate = SensorManager.SENSOR_DELAY_GAME;
        sensorManager.registerListener(this, gravitySensor, sensorUpdateRate);
        sensorManager.registerListener(this, magneticSensor, sensorUpdateRate);
    }

    @Override
    protected void onPause() {
        super.onPause();
        timerHandler.removeCallbacks(timerRunnable);
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (gameRunning) {
            if (event.sensor.getType() == Sensor.TYPE_GRAVITY)
                gravityValues = event.values;
            if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
                geomagneticValues = event.values;
            if (gravityValues != null && geomagneticValues != null) {
                float[] R = new float[9];
                float[] I = new float[9];
                boolean success = SensorManager.getRotationMatrix(R, I, gravityValues, geomagneticValues);
                float[] newR = new float[9];
                SensorManager.remapCoordinateSystem(R, SensorManager.AXIS_X, SensorManager.AXIS_Z, newR);

                if (success) {
                    float[] orientation = new float[3];
                    SensorManager.getOrientation(newR, orientation);
                    float roll = orientation[2];
                    sensorAngle = Math.abs((float) (Math.toDegrees(roll) - 180 % 360));
                }
            }
            String angleString = String.format(Locale.getDefault(), "%.1fº", sensorAngle);
            angleText.setText(angleString);
            angleText.setRotation((float) sensorAngle + 180 % 360); // flip the text relative to the sensor
            angleView.setAngle((float) sensorAngle);
        }
    }

    public void shareButton(View view) {
        Bitmap screenshotBitmap = takeScreenshot();
        Uri fileProviderUri = createContentUri(screenshotBitmap);
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareIntent.putExtra(Intent.EXTRA_STREAM, fileProviderUri);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My AngleAssault Score!");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out my AngleAssault high score! #angleassault");
        shareIntent.setType("image/*");
        startActivity(Intent.createChooser(shareIntent, "Share on..."));
    }


    @SuppressWarnings("deprecation")
    public Bitmap takeScreenshot() {
        View rootView = findViewById(android.R.id.content).getRootView();
        rootView.setDrawingCacheEnabled(true);
        return rootView.getDrawingCache();
    }

    public Uri createContentUri(Bitmap bitmap) {
        Context ctx = this.getApplicationContext();
        File file = new File(ctx.getExternalCacheDir(), "angle_assault_score.jpeg");
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fileOutputStream);
        try {
            assert fileOutputStream != null;
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException e) {
            Log.e("GameActivity_e", e.getMessage(), e);
        }
        return FileProvider.getUriForFile(ctx, ctx.getApplicationContext().getPackageName()
                + ".provider", file);
    }

    public void exitGameButton(View v) {
        finish();
    }

    public void loadData() {
        database = new DatabaseHelper(this);
    }
}