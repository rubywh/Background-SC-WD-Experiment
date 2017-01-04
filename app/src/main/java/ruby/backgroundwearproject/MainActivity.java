package ruby.backgroundwearproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;

/*
* Main Activity has transparent theme, simply ensures service running and then finishes.
 */
public class MainActivity extends WearableActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, testService.class);
        startService(intent);
        Log.d(TAG, "service started");
        finish();
    }
}
