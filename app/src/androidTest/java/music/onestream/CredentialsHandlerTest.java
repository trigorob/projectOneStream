package music.onestream;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import static music.onestream.util.CredentialsHandler.*;
import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class CredentialsHandlerTest {

    @Test
    public void testToken() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();
        setToken(appContext, "ABC", 999, TimeUnit.SECONDS, "Spotify");
        setToken(appContext, "DEF", 999, TimeUnit.SECONDS, "GoogleMusic");
        CredentialsHandlerTest cH = new CredentialsHandlerTest();
        String token = getToken(appContext, "Spotify");
        assertEquals("ABC", token);
        token = getToken(appContext, "GoogleMusic");
        assertEquals("DEF", token);
    }
}
