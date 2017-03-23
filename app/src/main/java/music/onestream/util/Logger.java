package music.onestream.util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by ruspe_000 on 2017-03-22.
 */

public class Logger {

    private Context context;
    private String TAG;

    public Logger(Context context, String TAG) {
        this.context = context;
        this.TAG = TAG;
    }

    public void logError(String msg) {
        Toast.makeText(context, "Error: " + msg, Toast.LENGTH_SHORT).show();
        Log.e(TAG, msg);
    }

    public void logMessage(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        Log.d(TAG, msg);
    }
}
