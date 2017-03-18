package music.onestream.util;


import android.app.Service;
import android.content.Context;
import android.content.SharedPreferences;
import java.util.concurrent.TimeUnit;

public class CredentialsHandler {

    public static void setToken(Context context, String token, long expiresIn, TimeUnit unit, String service) {
        Context appContext = context.getApplicationContext();

        long now = System.currentTimeMillis();
        long expiresAt = now + unit.toMillis(expiresIn);

        SharedPreferences sharedPref;
        if (service.equals("Spotify")) {
           sharedPref = getSpotifySharedPreferences(appContext);
        }
        else {
            sharedPref = getGoogleMusicSharedPreferences(appContext);
        }
        SharedPreferences.Editor editor = sharedPref.edit();
        if (service.equals("Spotify")) {
            editor.putString(Constants.SPOTIFY_ACCESS_TOKEN, token);
            editor.putLong(Constants.EXPIRES_AT, expiresAt);
        }
        else if (service.equals("GoogleMusic"))
        {
            editor.putString(Constants.GoogleMusic_ACCESS_TOKEN, token);
            editor.putLong(Constants.EXPIRES_AT, expiresAt);
        }
        editor.apply();
    }

    private static SharedPreferences getSpotifySharedPreferences(Context appContext) {
        return appContext.getSharedPreferences(Constants.SPOTIFY_TOKEN_NAME, Context.MODE_PRIVATE);
    }

    private static SharedPreferences getGoogleMusicSharedPreferences(Context appContext) {
        return appContext.getSharedPreferences(Constants.GoogleMusic_TOKEN_NAME, Context.MODE_PRIVATE);
    }

    public static String getToken(Context context, String Service) {
        Context appContext = context.getApplicationContext();
        SharedPreferences sharedPref = null;
        if (Service.equals("GoogleMusic")) {
            sharedPref = getGoogleMusicSharedPreferences(appContext);
        }
        else if (Service.equals("Spotify"))
        {
            sharedPref = getSpotifySharedPreferences(appContext);
        }

        String token = null;
        long expiresAt = 0;
        if (Service.equals("Spotify")) {
            token = sharedPref.getString(Constants.SPOTIFY_ACCESS_TOKEN, null);
            expiresAt = sharedPref.getLong(Constants.EXPIRES_AT, 0L);
        }
        else if (Service.equals("GoogleMusic")) {
            token = sharedPref.getString(Constants.GoogleMusic_ACCESS_TOKEN, null);
            expiresAt = sharedPref.getLong(Constants.EXPIRES_AT, 0L);
        }

        if (token == null || expiresAt < System.currentTimeMillis()) {
            return null;
        }

        return token;
    }
}