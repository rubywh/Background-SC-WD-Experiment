package ruby.backgroundwearproject;

import android.app.ActivityManager;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.List;

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
            // DetectedActivity detectedActivity = result.getMostProbableActivity();

            handleDetectedActivities(result);
        }
    }

    /* Check the type of activity and act accordingly. While the user is detected to be walking
    * with a confidence of over 75, start the service WearableService to initiate writing to file
    */

    private void handleDetectedActivities(ActivityRecognitionResult result) {
        boolean serviceRunning;
        DetectedActivity detectedActivity = result.getMostProbableActivity();
        int typeOfActivity = detectedActivity.getType();

        Log.d(TAG, "handleDetectedActivities");
        Intent intent = new Intent(this, WearableService.class);
        // for (DetectedActivity activity : probableActivities) {
        switch (detectedActivity.getType()) {
            case DetectedActivity.WALKING: {

                Log.e(TAG, "Walking: " + detectedActivity.getConfidence());
                if (detectedActivity.getConfidence() >= 75) {
                    serviceRunning = isMyServiceRunning(WearableService.class);
                    if (serviceRunning) {
                        Log.d(TAG, "Service already running");
                    } else {
                        startService(intent);
                        Log.d(TAG, "Wear service Started");
                    }
                } else {
                    stopService(intent);
                    break;
                }
            }
            case DetectedActivity.STILL: {
                Log.d(TAG, "Still: " + detectedActivity.getConfidence());
                serviceRunning = isMyServiceRunning(WearableService.class);
                if (serviceRunning) {
                    Log.d(TAG, "Stopping service.");
                    stopService(intent);
                }
                break;
            }
            case DetectedActivity.UNKNOWN: {
                Log.e(TAG, "Unknown: " + detectedActivity.getConfidence());
                serviceRunning = isMyServiceRunning(WearableService.class);
                if (serviceRunning) {
                    Log.d(TAG, "Stopping service.");
                    stopService(intent);
                }
                break;
            }
            case DetectedActivity.IN_VEHICLE: {
                Log.e(TAG, "In Vehicle: " + detectedActivity.getConfidence());
                serviceRunning = isMyServiceRunning(WearableService.class);
                if (serviceRunning) {
                    Log.d(TAG, "Stopping service.");
                    stopService(intent);
                }
                break;
            }
            case DetectedActivity.ON_BICYCLE: {
                Log.e(TAG, "On Bike: " + detectedActivity.getConfidence());
                serviceRunning = isMyServiceRunning(WearableService.class);
                if (serviceRunning) {
                    Log.d(TAG, "Stopping service.");
                    stopService(intent);
                }
                break;
            }
            case DetectedActivity.RUNNING: {
                Log.e(TAG, "Running: " + detectedActivity.getConfidence());
                serviceRunning = isMyServiceRunning(WearableService.class);
                if (serviceRunning) {
                    Log.d(TAG, "Stopping service.");
                    stopService(intent);
                }
                break;
            }
            case DetectedActivity.TILTING: {
                Log.e(TAG, "Tilting: " + detectedActivity.getConfidence());
                serviceRunning = isMyServiceRunning(WearableService.class);

                if (serviceRunning) {
                    Log.d(TAG, "Stopping service.");
                    stopService(intent);
                }
                break;
            }
            case DetectedActivity.ON_FOOT: {
                Log.e(TAG, "On Foot: " + detectedActivity.getConfidence());
                DetectedActivity moreSpecific = getMoreSpecific(result.getProbableActivities());
                if (moreSpecific != null) {
                    detectedActivity = moreSpecific;
                }
                if (detectedActivity.getType() == DetectedActivity.WALKING) {
                    Log.d(TAG, "On foot and walking");
                    if (detectedActivity.getConfidence() >= 75) {
                        serviceRunning = isMyServiceRunning(WearableService.class);
                        if (serviceRunning) {
                            Log.i(TAG, "Service already running!");
                        } else {
                            startService(intent);
                            Log.d(TAG, "Wear service Started.");
                        }
                    } else {
                        serviceRunning = isMyServiceRunning(WearableService.class);
                        if (serviceRunning) {
                            Log.d(TAG, "Stopping service.");
                            stopService(intent);
                        }
                        break;
                    }
                } else if (detectedActivity.getType() == DetectedActivity.RUNNING) {
                    serviceRunning = isMyServiceRunning(WearableService.class);
                    if (serviceRunning) {
                        Log.i(TAG, "Stopping service.");
                    } else {
                        serviceRunning = isMyServiceRunning(WearableService.class);
                        if (serviceRunning) {
                            Log.i(TAG, "Stopping service.");
                        }
                    }
                }
            }
            default: {
                break;
            }
        }
    }

    private DetectedActivity getMoreSpecific(List<DetectedActivity> probableActivities) {
        DetectedActivity myActivity = null;
        int confidence = 0;
        for (DetectedActivity activity : probableActivities) {
            if (activity.getType() != DetectedActivity.RUNNING && activity.getType() != DetectedActivity.WALKING)
                continue;

            if (activity.getConfidence() > confidence)
                myActivity = activity;
        }

        return myActivity;
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}

