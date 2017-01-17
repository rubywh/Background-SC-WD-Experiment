package ruby.backgroundwearproject;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;

/**
 * Unused at the moment, replaced by testService
 */

public class WearActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "WearActivity";
    public GoogleApiClient mApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "created");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        //Initialise client, connect to Google Play Services
        //Request ActivityRecognition API, associate listeners
        buildGoogleApiClient();
        mApiClient.connect();

    }

    protected synchronized void buildGoogleApiClient() {
        mApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(ActivityRecognition.API)
                .build();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "connected");
        //Pass pending intent to the api
        Intent intent = new Intent(this, ActivityRecognisedService.class);
        //FLAG_UPDATE_CURRENT means get the same pending intent back when requesting updates
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //Check activity every 2 seconds and send to ActivityRecognisedService
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(mApiClient, 0, pendingIntent);
    }


    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "start");
        mApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "stop");
        if (mApiClient.isConnected()) {
            mApiClient.disconnect();
        }
    }


    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "suspended");
        mApiClient.connect();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "failed");
    }


}

