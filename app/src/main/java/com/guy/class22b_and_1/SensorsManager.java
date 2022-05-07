package com.guy.class22b_and_1;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class SensorsManager implements SensorEventListener {
    private Sensor sensor;
    private float[] values;

    public SensorsManager() {

    }

    public void getMovementDir() {
        if (values != null && values.length == 3) {
            float y = values[1];
            float z = values[2];

            if (z >= 30)
                Activity_Game.instance.changeMoveDirection(DIRECTION.LEFT);
            else if (z <= -30)
                Activity_Game.instance.changeMoveDirection(DIRECTION.RIGHT);

            if (y >= 20)
                Activity_Game.instance.changeMoveDirection(DIRECTION.UP);
            else if (y <= -30)
                Activity_Game.instance.changeMoveDirection(DIRECTION.DOWN);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent arg0) {
        values = arg0.values;
        getMovementDir();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public void init() {
        SensorManager sensorManager = ((SensorManager) Activity_Game.instance.getSystemService(Context.SENSOR_SERVICE));

        if (sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION) != null) {
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        }

        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }
}
