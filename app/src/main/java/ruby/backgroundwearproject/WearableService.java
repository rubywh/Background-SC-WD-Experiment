package ruby.backgroundwearproject;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.PrintStream;

/**
 * Created by ruby__000 on 22/12/2016.
 */

public class WearableService extends Service implements SensorEventListener {
    private static final String TAG = "SenseService";
    Sensor senAccelerometer;
    Sensor senGyro;
    private SensorManager senSensorManager;
    private PrintStream ps;
    private PrintStream ps_gyro;
    private String androidpath;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "startSensorService");
        this.makeFile();
        //fetch the system's SensorManager instance. get a reference to a service of the system by passing the name of the service
        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        //get accelerometer
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senGyro = senSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        //register the sensor, use context, name and rate at which sensor events are delivered to us.
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        senSensorManager.registerListener(this, senGyro, SensorManager.SENSOR_DELAY_FASTEST);

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.i(TAG, "onSensorChanged");
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            new AccelerometerEventLoggerTask().execute(event);
        }
        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            new GyroEventLoggerTask().execute(event);
        }
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();

        this.makeFile();

        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (senSensorManager != null) {
            senSensorManager.unregisterListener(this);
        }
        ps.flush();
        ps_gyro.flush();
        ps.close();
        ps_gyro.close();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void makeFile() {
        androidpath = Environment.getExternalStorageDirectory().toString();

        try {
            ps = new PrintStream(new FileOutputStream(androidpath + "/accelerometer.dat"));
            ps_gyro = new PrintStream(new FileOutputStream(androidpath + "/gyroscope.dat"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class broadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ps.flush();
            ps_gyro.flush();
            ps.close();
            ps_gyro.close();
            stopSelf();
        }
    }

    private class AccelerometerEventLoggerTask extends AsyncTask<SensorEvent, Void, Void> {
        @Override
        protected Void doInBackground(SensorEvent... events) {
            SensorEvent event = events[0];
            String line = String.valueOf(System.currentTimeMillis()) + ";" +
                    String.valueOf(event.values[0]) + ";" +
                    String.valueOf(event.values[1]) + ";" +
                    String.valueOf(event.values[2]);
            Log.i(TAG, line);
            ps.println(line);
            return null;
        }
    }

    private class GyroEventLoggerTask extends AsyncTask<SensorEvent, Void, Void> {
        @Override
        protected Void doInBackground(SensorEvent... events) {
            //Getting the event and values

            SensorEvent event = events[0];
            String line = String.valueOf(System.currentTimeMillis()) + ";" +
                    String.valueOf(event.values[0]) + ";" +
                    String.valueOf(event.values[1]) + ";" +
                    String.valueOf(event.values[2]);
            Log.i(TAG, line);
            ps_gyro.println(line);
            return null;
        }
    }
}
