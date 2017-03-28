package music.onestream;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.os.SystemClock;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import music.onestream.activity.EditPlaylistActivity;
import music.onestream.activity.OneStreamActivity;
import music.onestream.util.Constants;

import music.onestream.activity.SettingsActivity;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withChild;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.core.StringEndsWith.endsWith;

//import android.support.test.espresso.Espresso;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class PlaylistUITests {

    @Rule
    public ActivityTestRule<OneStreamActivity> mActivityRule = new ActivityTestRule<>(
            OneStreamActivity.class);

    @Test
    public void createPlayListTest() {

        //Click options menu and click settings
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText("Settings")).perform(click());

        //Go to edit playlist activity
        onView(withId(R.id.playlistPage)).perform(click());

        //Create new playlist
        onView(withId(R.id.playListName)).perform(clearText());
        onView(withId(R.id.playListName)).perform(typeText("TestPlaylist"));
        onView(withId(R.id.saveNewPlaylist)).perform(click());

        SystemClock.sleep(500);

        //Click Playlists tab
        onView(allOf(withClassName(endsWith("TabView")),
                withChild(withText(Constants.OneStream_Playlists)),
                withParent(withParent(withId(R.id.tabs)))
        )).perform(scrollTo()).perform(click());

        //Check if playlist is displayed in list
        onView(withText("Playlists")).check(ViewAssertions.matches(isDisplayed()));
        onView(withText("TestPlaylist")).check(ViewAssertions.matches(isDisplayed()));
        onView(withText("0 Songs")).check(ViewAssertions.matches(isDisplayed()));

    }
}

