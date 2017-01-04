package ruby.backgroundwearproject;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

/*A Service to recognise the retrieve the detected activity*/

public class ActivityRecognisedService extends IntentService {
    private static final String TAG = "ARService";

    public ActivityRecognisedService() {
        super("ActivityRecognisedService");
    }

    public ActivityRecognisedService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "handleIntent");
        if (ActivityRecognitionResult.hasResult(intent)) {
            //Get the update from the intent
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            //Find the most probably activity
            DetectedActivity detectedActivity = result.getMostProbableActivity();
            handleDetectedActivities(detectedActivity);
        }
    }

    /* Check the type of activity and act accordingly. While the user is detected to be walking
    * with a confidence of over 75, start the service WearableService to initiate writing to file
    */

    private void handleDetectedActivities(DetectedActivity detectedActivity) {

        Log.d(TAG, "handleDetectedActivities");
        // for (DetectedActivity activity : probableActivities) {
        switch (detectedActivity.getType()) {
            case DetectedActivity.WALKING: {
                Intent intent = new Intent(this, WearableService.class);
                Log.e(TAG, "Walking: " + detectedActivity.getConfidence());
                while (detectedActivity.getConfidence() >= 75) {
                    Log.d(TAG, "Wear service Started");
                    startService(intent);
                }
                stopService(intent);
                break;
            }
            case DetectedActivity.STILL: {
                Log.d(TAG, "Still: " + detectedActivity.getConfidence());
                break;
            }
            case DetectedActivity.UNKNOWN: {
                Log.e(TAG, "Unknown: " + detectedActivity.getConfidence());
                break;
            }
            case DetectedActivity.IN_VEHICLE: {
                Log.e(TAG, "In Vehicle: " + detectedActivity.getConfidence());
                break;
            }
            case DetectedActivity.ON_BICYCLE: {
                Log.e(TAG, "On Bike: " + detectedActivity.getConfidence());
                break;
            }
            case DetectedActivity.RUNNING: {
                Log.e(TAG, "Running: " + detectedActivity.getConfidence());
                break;
            }
            case DetectedActivity.TILTING: {
                Log.e(TAG, "Tilting: " + detectedActivity.getConfidence());
                break;
            }
            case DetectedActivity.ON_FOOT: {
                Log.e(TAG, "On Foot: " + detectedActivity.getConfidence());
                break;
            }
            default: {
                break;
            }
        }
    }
}

