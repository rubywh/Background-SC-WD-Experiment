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

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

/**
 * The service that receives onSensorChanged updates and writes data to file
 */

public class WearableService extends Service implements SensorEventListener {
    private static final String TAG = "SenseService";
    Sensor senAccelerometer;
    Sensor senGyro;
    private SensorManager senSensorManager;
    private PrintStream ps;
    private PrintStream ps_gyro;
    private String androidpath;
    private String fileAccelerometer;
    private String fileGyro;

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

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            new AccelerometerEventLoggerTask().execute(event);

        }
        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {

            new GyroEventLoggerTask().execute(event);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

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
        Log.d(TAG, "makeFile");

        androidpath = Environment.getExternalStorageDirectory().toString();
        File fAccelerometer = new File(androidpath + "/testaccelerometer.dat");
        File fGyroscope = new File(androidpath + "/testgyroscope.dat");

        if (fAccelerometer.length() == 0) {

            try {
                ps = new PrintStream(new FileOutputStream(fAccelerometer));
                ps_gyro = new PrintStream(new FileOutputStream(fGyroscope));
            } catch (Exception e) {
                e.printStackTrace();
            }
            // fileAccelerometer = androidpath + "/testaccelerometer.dat";
            // System.out.println(fileAccelerometer);
            //fileGyro = androidpath + "/testgyroscope.dat";
        } else {
            try {
                ps = new PrintStream(new FileOutputStream(fAccelerometer, true));
                ps_gyro = new PrintStream(new FileOutputStream(fGyroscope, true));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    //Stop service if battery low
    public class broadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("Service stopping as battery low.");
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
            Log.d(TAG, "writing accelerometer data to file");
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
            Log.d(TAG, "writing gyro data to file");
            ps_gyro.println(line);
            return null;
        }
    }
}
