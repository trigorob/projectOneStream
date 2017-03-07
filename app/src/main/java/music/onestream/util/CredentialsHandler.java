package music.onestream.util;


import android.content.Context;
import android.content.SharedPreferences;
import java.util.concurrent.TimeUnit;

public class CredentialsHandler {

    private static final String SPOTIFY_TOKEN_NAME = "webapi.credentials.access_token";
    private static final String GoogleMusic_TOKEN_NAME = "webapi.credentials.access_token";
    private static final String SPOTIFY_ACCESS_TOKEN = "spotify_access_token";
    private static final String GoogleMusic_ACCESS_TOKEN = "googlemusic_access_token";
    private static final String EXPIRES_AT = "expires_at";

    public static void setToken(Context context, String token, long expiresIn, TimeUnit unit, String service) {
        Context appContext = context.getApplicationContext();

        long now = System.currentTimeMillis();
        long expiresAt = now + unit.toMillis(expiresIn);

        SharedPreferences sharedPref = getSpotifySharedPreferences(appContext);
        SharedPreferences.Editor editor = sharedPref.edit();
        if (service.equals("Spotify")) {
            editor.putString(SPOTIFY_ACCESS_TOKEN, token);
            editor.putLong(EXPIRES_AT, expiresAt);
        }
        else if (service.equals("GoogleMusic"))
        {
            editor.putString(GoogleMusic_ACCESS_TOKEN, token);
            editor.putLong(EXPIRES_AT, expiresAt);
        }
        editor.apply();
    }

    private static SharedPreferences getSpotifySharedPreferences(Context appContext) {
        return appContext.getSharedPreferences(SPOTIFY_TOKEN_NAME, Context.MODE_PRIVATE);
    }

    private static SharedPreferences getGoogleMusicSharedPreferences(Context appContext) {
        return appContext.getSharedPreferences(SPOTIFY_TOKEN_NAME, Context.MODE_PRIVATE);
    }

    public static String getToken(Context context, String Service) {
        Context appContext = context.getApplicationContext();
        SharedPreferences sharedPref = getGoogleMusicSharedPreferences(appContext);

        String token = null;
        long expiresAt = 0;
        if (Service.equals("Spotify")) {
            token = sharedPref.getString(SPOTIFY_ACCESS_TOKEN, null);
            expiresAt = sharedPref.getLong(EXPIRES_AT, 0L);
        }
        else if (Service.equals("GoogleMusic")) {
            token = sharedPref.getString(GoogleMusic_ACCESS_TOKEN, null);
            expiresAt = sharedPref.getLong(EXPIRES_AT, 0L);
        }

        if (token == null || expiresAt < System.currentTimeMillis()) {
            return null;
        }

        return token;
    }
}