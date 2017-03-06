package music.onestream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by ruspe_000 on 2017-03-05.
 */

public class ImageGetter extends AsyncTask {

    AsyncResponse SAR;

    protected void onPostExecute(Object result) {
        Object[] retObject = new Object[1];
        retObject[0] = result;
        SAR.processFinish(retObject);
    }

    @Override
    protected Object doInBackground(Object[] params) {
        String url = (String) params[0];
        try {
            Bitmap x;
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.connect();
            InputStream input = connection.getInputStream();
            x = BitmapFactory.decodeStream(input);
            return new BitmapDrawable(x);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
