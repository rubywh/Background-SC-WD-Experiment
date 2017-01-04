package ruby.backgroundwearproject;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;

/**
 * Connects to API Client, periodically requests updates from ActivityRecognition and starts the
 * ActivityRecognised service
 */

public class testService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "testService";
    private GoogleApiClient mApiClient;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        super.onCreate();
        buildGoogleApiClient();
        mApiClient.connect();
    }


    protected synchronized void buildGoogleApiClient() {
        mApiClient = new GoogleApiClient.Builder(this)
                .addApi(ActivityRecognition.API)
                .addConnectionCallbacks(testService.this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    /*START_STICKY tells the OS to recreate the service after
     * it has enough memory and calls onStartCommand() again with a null intent
      */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "start");
        mApiClient.connect();
        return Service.START_STICKY;

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "connected");
        //Pass pending intent to the api
        Intent intent = new Intent(this, ActivityRecognisedService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //Check activity every 2 seconds and send to ActivityRecognisedService
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(mApiClient, 1, pendingIntent);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "suspended");
        mApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "Connection Failed");
    }
}
