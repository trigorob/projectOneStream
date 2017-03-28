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

import music.onestream.activity.OneStreamActivity;
import music.onestream.util.Constants;


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

@RunWith(AndroidJUnit4.class)
@LargeTest
public class LocalPlaybackTests {

    //Make sure that at least one song is stored on phone/VM before running test

    @Rule
    public ActivityTestRule<OneStreamActivity> mActivityRule = new ActivityTestRule<>(
            OneStreamActivity.class);

    @Test
    public void verifySongList() {

        //Click Local tab
        onView(allOf(withClassName(endsWith("TabView")),
                withChild(withText(Constants.OneStream_Local)),
                withParent(withParent(withId(R.id.tabs)))
        )).perform(scrollTo()).perform(click());

        onView(withText("Local")).check(ViewAssertions.matches(isDisplayed()));

        //Should just check if it exists do not need to check for exact name
        onView(withId(R.id.listSongName)).check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.listSongArtistAlbum)).check(ViewAssertions.matches(isDisplayed()));

    }

    //Make sure song view is enabled
    @Test
    public void playLocalSong() {

        //Click Local tab
        onView(allOf(withClassName(endsWith("TabView")),
                withChild(withText(Constants.OneStream_Local)),
                withParent(withParent(withId(R.id.tabs)))
        )).perform(scrollTo()).perform(click());

        onView(withText("Local")).check(ViewAssertions.matches(isDisplayed()));

        onView(withId(R.id.listSongName)).perform(click());

        //Check if album art, artist name, and album name are displayed in song view
        onView(withId(R.id.album)).check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.artistOrGenreName)).check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.albumName)).check(ViewAssertions.matches(isDisplayed()));

        //Check if seek bar and playback controls are displayed
        onView(withId(R.id.seekBarSV)).check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.RewindSV)).check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.PrevSV)).check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.fabIOSV)).check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.NextSV)).check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.RandomSV)).check(ViewAssertions.matches(isDisplayed()));

    }
}

