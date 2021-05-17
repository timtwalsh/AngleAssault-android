package dev.timwalsh.angleassault;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;

import java.util.Locale;
import java.util.Random;

public class GameActivity extends Activity implements SensorEventListener {
    public static final int GAME_REQUEST = 0;
    static final int DEFAULT_GAME_TIMER = 5;
    // Sensor vars
    private final int sensorUpdateRate = SensorManager.SENSOR_DELAY_UI;
    private SensorManager sensorManager;
    private Sensor accelerometerSensor;
    private Sensor magneticSensor;
    private float[] accelerometerValues;
    private float[] geomagneticValues;
    private float roll;
    double sensorAngle;
    // UI
    public float gameTimer = 5.0f; // in seconds
    private AngleView angleView;
    TextView questionText;
    TextView angleText;
    TextView timerText;
    TextView scoreText;
    // Game
    int question;
    int score = 0;
    String[] questions;
    double[][] answers = {{0.00, 90.00}, {88.00, 92.00}, {90.00, 180.00}, {178.00, 182.00}, {180.00, 360.00}};
    Random rand = new Random();
    private CountDownTimer questionTimer;
    private final int difficulty = 5; // difficulty / 10 = speed increase per round.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        questionText = findViewById(R.id.questionText);
        timerText = findViewById(R.id.timerText);
        angleText = findViewById(R.id.currentAngle);
        scoreText = findViewById(R.id.scoreText);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        angleView = findViewById(R.id.angleView);
        scoreText.setText(String.format(Locale.getDefault(),"%s%d", getResources().getString(R.string.score_text), score)); // show the initial score
        questions = new String[]{ // localised question strings
                getResources().getString(R.string.question_acute),
                getResources().getString(R.string.question_right),
                getResources().getString(R.string.question_obtuse),
                getResources().getString(R.string.question_straight),
                getResources().getString(R.string.question_reflex)
        };
        questionText.setText(getQuestion());
        startTimer(gameTimer);
    }

    private void startTimer(float timeout) {
        float interval = 1000 * timeout;
        questionTimer = new CountDownTimer((long) interval, 10) {

            public void onTick(long remainingMs) {
                timerText.setText(String.format(Locale.getDefault(), "%.1fs", (float) remainingMs / 1000));
            }

            public void onFinish() {
                checkAnswer(sensorAngle);
            }
        }.start();

    }

    private String getQuestion() {
        question = rand.nextInt(questions.length);
        return questions[question];
    }

    private void checkAnswer(double sensorAngle) {
        double answer_min = answers[question][0];
        double answer_max = answers[question][1];
        if (sensorAngle >= answer_min && sensorAngle <= answer_max) {
            score = score + 1;
            if (gameTimer > 1.5)
                gameTimer -= 0.5; // Min timer of 1 second
            scoreText.setText(String.format("%s%s", getResources().getString(R.string.score_text), score));
            questionText.setText(getQuestion());
            questionTimer.cancel();
            startTimer(gameTimer);
        } else {
            // game over, #TODO:  inflate game over screen
            // #TODO: implement tweet sharing
            // #TODO: implement high score save
            // #TODO: implement
            // #TODO: 'play again' (deflate, reset vars, startTimer)
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometerSensor, sensorUpdateRate);
        sensorManager.registerListener(this, magneticSensor, sensorUpdateRate);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_GRAVITY)
            accelerometerValues = event.values;
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            geomagneticValues = event.values;
        if (accelerometerValues != null && geomagneticValues != null) {
            float[] R = new float[9];
            float[] I = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, accelerometerValues, geomagneticValues);
            float[] newR = new float[9];
            SensorManager.remapCoordinateSystem(R, SensorManager.AXIS_X, SensorManager.AXIS_Z, newR);

            if (success) {
                float[] orientation = new float[3];
                SensorManager.getOrientation(newR, orientation);
                roll = orientation[2];
                sensorAngle = Math.abs((float) (Math.toDegrees(roll) - 180 % 360));
            }
        }
        String angleString = String.format(Locale.getDefault(), "%.1fº", sensorAngle);
        angleText.setText(angleString);
        angleText.setRotation((float) sensorAngle + 180 % 360); // flip the text relative to the sensor
        angleView.setAngle((float) sensorAngle);
    }
}