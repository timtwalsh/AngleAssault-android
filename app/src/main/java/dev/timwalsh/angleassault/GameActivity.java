package dev.timwalsh.angleassault;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

import java.util.Locale;

public class GameActivity extends Activity implements SensorEventListener {
    public static final int GAME_REQUEST = 0;
    TextView questionText;
    TextView angleText;
    TextView titleText;
    private SensorManager sensorManager;
    private int sensorUpdateRate = SensorManager.SENSOR_DELAY_GAME;
    private AngleView angleView;
    Sensor gravity;
    double sensorAngle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        questionText = findViewById(R.id.questionText);
        titleText = findViewById(R.id.titleText);
        angleText = findViewById(R.id.currentAngle);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        gravity = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        angleView = findViewById(R.id.angleView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, gravity, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = (float) (event.values[0] / 9.81);
        float y = (float) (event.values[1] / 9.81);
        if (x > 0) { // Right side of display
            // inverse sin + offset with rollover for > 360
            sensorAngle = Math.abs((-60*Math.asin(y)) + 270 + 2.7 * y) % 360;
        } else { // (x < 0) Left Side
            sensorAngle = Math.abs((-60*Math.asin(y)) - 90 + 2.7 * y) % 360;
        }

        String angleString = String.format(Locale.getDefault(), "%.1fº", sensorAngle);
        angleText.setText(angleString);
        angleText.setRotation((float)sensorAngle+180%360);
        titleText.setText(String.format(Locale.getDefault(), "%3.2f, %.2f", Math.abs(x),Math.abs(y))); // Temp x,y
        angleView.setAngle(-x, y);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}