package music.onestream;

import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import music.onestream.activity.OneStreamActivity;
import music.onestream.util.Constants;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;

import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;

import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;

import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withChild;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.anything;
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
        onData(anything())
                .inAdapterView(allOf(withId(R.id.ListView1), isCompletelyDisplayed()))
                .atPosition(0).check(ViewAssertions.matches(isDisplayed()));

    }

    //Make sure song view is disabled
    @Test
    public void playLocalSong() {

        //Click Local tab
        onView(allOf(withClassName(endsWith("TabView")),
                withChild(withText(Constants.OneStream_Local)),
                withParent(withParent(withId(R.id.tabs)))
        )).perform(scrollTo()).perform(click());

        onView(withText("Local")).check(ViewAssertions.matches(isDisplayed()));

        onData(anything())
                .inAdapterView(allOf(withId(R.id.ListView1), isCompletelyDisplayed()))
                .atPosition(0).perform(click());

        //Check if play controls are displayed
        onView(withId(R.id.fabIO)).check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.Random)).check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.Rewind)).check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.Prev)).check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.Next)).check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.seekBar)).check(ViewAssertions.matches(isDisplayed()));

    }

    //Make sure song view is disabled
    @Test
    public void playbackControlsTest() {

        //Click Local tab
        onView(allOf(withClassName(endsWith("TabView")),
                withChild(withText(Constants.OneStream_Local)),
                withParent(withParent(withId(R.id.tabs)))
        )).perform(scrollTo()).perform(click());

        onView(withText("Local")).check(ViewAssertions.matches(isDisplayed()));

        onData(anything())
                .inAdapterView(allOf(withId(R.id.ListView1), isCompletelyDisplayed()))
                .atPosition(0).perform(click());

        onView(withId(R.id.fabIO)).perform(click());
        onView(withId(R.id.fabIO)).perform(click());

        onView(withId(R.id.Random)).perform(click());
        onView(withId(R.id.Random)).perform(click());

        onView(withId(R.id.Rewind)).perform(click());
        onView(withId(R.id.Rewind)).perform(click());

        onView(withId(R.id.Next)).perform(click());
        onView(withId(R.id.Prev)).perform(click());

        onView(withId(R.id.seekBar)).perform(click());

    }

    @Test
    public void songViewEnabledPlayLocalSong() {

        //Click options menu and click settings
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText("Settings")).perform(click());

        //Enable song view
        onView(withId(R.id.songViewToggleButton)).perform(click());

        //Go back to Onestream
        pressBack();

        //Click Local tab
        onView(allOf(withClassName(endsWith("TabView")),
                withChild(withText(Constants.OneStream_Local)),
                withParent(withParent(withId(R.id.tabs)))
        )).perform(scrollTo()).perform(click());

        onView(withText("Local")).check(ViewAssertions.matches(isDisplayed()));

        onData(anything())
                .inAdapterView(allOf(withId(R.id.ListView1), isCompletelyDisplayed()))
                .atPosition(0).perform(click());

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

        //hit back button
        pressBack();

        //Click options menu and click settings
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText("Settings")).perform(click());

        //Disable song view
        onView(withId(R.id.songViewToggleButton)).perform(click());

    }
}

