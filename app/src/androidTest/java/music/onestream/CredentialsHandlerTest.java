package music.onestream;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static music.onestream.util.CredentialsHandler.getToken;
import static music.onestream.util.CredentialsHandler.setToken;
import static org.junit.Assert.assertEquals;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class CredentialsHandlerTest {

    @Test
    public void testToken() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();
        setToken(appContext, "ABC", 999, TimeUnit.SECONDS, "Spotify");
        setToken(appContext, "DEF", 999, TimeUnit.SECONDS, "SoundCloud");
        CredentialsHandlerTest cH = new CredentialsHandlerTest();
        String token = getToken(appContext, "Spotify");
        assertEquals("ABC", token);
        token = getToken(appContext, "SoundCloud");
        assertEquals("DEF", token);
    }
}
