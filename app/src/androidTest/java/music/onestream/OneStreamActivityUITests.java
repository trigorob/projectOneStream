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

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isSelected;
import static android.support.test.espresso.matcher.ViewMatchers.withChild;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.core.StringEndsWith.endsWith;
//import android.support.test.espresso.Espresso;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class OneStreamActivityUITests {

    @Rule
    public ActivityTestRule<OneStreamActivity> mActivityRule = new ActivityTestRule<>(
            OneStreamActivity.class);

    @Test
    public void verifyPlaybackControls() {

        onView(withText("OneStream")).check(ViewAssertions.matches(isDisplayed()));

        //Check if play controls are displayed
        onView(withId(R.id.fabIO)).check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.Random)).check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.Rewind)).check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.Prev)).check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.Next)).check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.seekBar)).check(ViewAssertions.matches(isDisplayed()));

    }

    @Test
    public void navigateToLocalTabTest() {

        //Click Local tab
        onView(allOf(withClassName(endsWith("TabView")),
                withChild(withText(Constants.OneStream_Local)),
                withParent(withParent(withId(R.id.tabs)))
        )).perform(scrollTo()).perform(click());

        //Check if Local tab is selected
        onView(withText(Constants.OneStream_Local)).check(ViewAssertions.matches(isDisplayed()));
        onView(withText(Constants.OneStream_Local)).check(ViewAssertions.matches(isSelected()));

    }

    @Test
    public void navigateToSpotifyTabTest() {

        //Click Local tab
        onView(allOf(withClassName(endsWith("TabView")),
                withChild(withText(Constants.OneStream_Spotify)),
                withParent(withParent(withId(R.id.tabs)))
        )).perform(scrollTo()).perform(click());

        //Check if Local tab is selected
        onView(withText(Constants.OneStream_Spotify)).check(ViewAssertions.matches(isDisplayed()));
        onView(withText(Constants.OneStream_Spotify)).check(ViewAssertions.matches(isSelected()));

    }

    @Test
    public void navigateToSoundCloudTabTest() {

        //Click Local tab
        onView(allOf(withClassName(endsWith("TabView")),
                withChild(withText(Constants.OneStream_SoundCloud)),
                withParent(withParent(withId(R.id.tabs)))
        )).perform(scrollTo()).perform(click());

        //Check if Local tab is selected
        onView(withText(Constants.OneStream_SoundCloud)).check(ViewAssertions.matches(isDisplayed()));
        onView(withText(Constants.OneStream_SoundCloud)).check(ViewAssertions.matches(isSelected()));

    }

    @Test
    public void navigateToPlaylistsTabTest() {

        //Click Local tab
        onView(allOf(withClassName(endsWith("TabView")),
                withChild(withText(Constants.OneStream_Playlists)),
                withParent(withParent(withId(R.id.tabs)))
        )).perform(scrollTo()).perform(click());

        //Check if Local tab is selected
        onView(withText(Constants.OneStream_Playlists)).check(ViewAssertions.matches(isDisplayed()));
        onView(withText(Constants.OneStream_Playlists)).check(ViewAssertions.matches(isSelected()));

    }

    @Test
    public void navigateToArtistsTabTest() {

        //Click Local tab
        onView(allOf(withClassName(endsWith("TabView")),
                withChild(withText(Constants.OneStream_Artists)),
                withParent(withParent(withId(R.id.tabs)))
        )).perform(scrollTo()).perform(click());

        //Check if Local tab is selected
        onView(withText(Constants.OneStream_Artists)).check(ViewAssertions.matches(isDisplayed()));
        onView(withText(Constants.OneStream_Artists)).check(ViewAssertions.matches(isSelected()));

    }

    @Test
    public void navigateToAlbumsTabTest() {

        //Click Local tab
        onView(allOf(withClassName(endsWith("TabView")),
                withChild(withText(Constants.OneStream_Albums)),
                withParent(withParent(withId(R.id.tabs)))
        )).perform(scrollTo()).perform(click());

        //Check if Local tab is selected
        onView(withText(Constants.OneStream_Albums)).check(ViewAssertions.matches(isDisplayed()));
        onView(withText(Constants.OneStream_Albums)).check(ViewAssertions.matches(isSelected()));

    }

    @Test
    public void navigateToGenresTabTest() {

        //Click Local tab
        onView(allOf(withClassName(endsWith("TabView")),
                withChild(withText(Constants.OneStream_Genre)),
                withParent(withParent(withId(R.id.tabs)))
        )).perform(scrollTo()).perform(click());

        //Check if Local tab is selected
        onView(withText(Constants.OneStream_Genre)).check(ViewAssertions.matches(isDisplayed()));
        onView(withText(Constants.OneStream_Genre)).check(ViewAssertions.matches(isSelected()));

    }
}

