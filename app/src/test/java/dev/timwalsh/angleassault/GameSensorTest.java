package dev.timwalsh.angleassault;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.shadow.api.Shadow;
import org.robolectric.shadows.ShadowSensorManager;

import static android.os.Looper.getMainLooper;
import static org.junit.Assert.assertEquals;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
public class GameSensorTest {
    private GameActivity testGameActivity;

    @Before
    public void setUp() {
        Intent intent = new Intent();
        intent.putExtra("gameSpeed", 10); // include the expected intent
        testGameActivity = Robolectric.buildActivity(GameActivity.class, intent).create().resume().get();
    }

    @SuppressWarnings("all")
    @Before
    public void init() {
        shadowOf(getMainLooper()).idle();
        SensorManager sensorManager = (SensorManager) RuntimeEnvironment.application.getSystemService(Context.SENSOR_SERVICE);
        ShadowSensorManager sensorManagerShadow = shadowOf(sensorManager);
        Sensor sensor = Shadow.newInstanceOf(Sensor.class);
        sensorManagerShadow.addSensor(sensor);
    }

    // setup storage for sensor testing
    float[] R = new float[9];
    float[] I = new float[9];
    float[] newR = new float[9];
    float[] orientation = new float[3];
    float[] upRightGravity = {0f, 9.81f, 0f};
    float[] upRightMagneto = {0f, 5.9f, -48.40f};
    float[] rightGravity = {9.81f, 0f, 0f};
    float[] rightMagneto = {5.9f, 0f, -48.40f};
    float[] leftGravity = {-9.81f, 0f, 0f};
    float[] leftMagneto = {-5.9f, 0f, -48.40f};
    float roll;
    float sensorAngleUp;
    float sensorAngleRight;
    float sensorAngleLeft;

    @Test
    public void sensorTestUpright() {
        // test rotation
        SensorManager.getRotationMatrix(R, I, upRightGravity, upRightMagneto);
        SensorManager.remapCoordinateSystem(R, SensorManager.AXIS_X, SensorManager.AXIS_Z, newR);
        SensorManager.getOrientation(newR, orientation);
        roll = orientation[2];
        sensorAngleUp = Math.abs((float) (Math.toDegrees(roll) - 180 % 360));
        assertEquals(180f, sensorAngleUp, 0f);
    }

    @Test
    public void sensorTestRight() {
        // Rotated right
        SensorManager.getRotationMatrix(R, I, rightGravity, rightMagneto);
        SensorManager.remapCoordinateSystem(R, SensorManager.AXIS_X, SensorManager.AXIS_Z, newR);
        orientation = new float[3];
        SensorManager.getOrientation(newR, orientation);
        roll = orientation[2];
        sensorAngleRight = Math.abs((float) (Math.toDegrees(roll) - 180 % 360));
        assertEquals(270f, sensorAngleRight, 0f);
    }

    @Test
    public void sensorTestLeft() {
        // Rotated left
        SensorManager.getRotationMatrix(R, I, leftGravity, leftMagneto);
        SensorManager.remapCoordinateSystem(R, SensorManager.AXIS_X, SensorManager.AXIS_Z, newR);
        orientation = new float[3];
        SensorManager.getOrientation(newR, orientation);
        roll = orientation[2];
        sensorAngleLeft = Math.abs((float) (Math.toDegrees(roll) - 180 % 360));
        assertEquals(90f, sensorAngleLeft, 0f);
    }

    @Test
    public void testOnTick() {
        testGameActivity.sensorAngle = 90.0f; // Out emulated sensor angle
        testGameActivity.angleGame.question = 1; // The right Angle question
        testGameActivity.onTick(); // A timer = 0 event
        assertEquals(1, testGameActivity.angleGame.getScore()); // Score should be 1
    }

}