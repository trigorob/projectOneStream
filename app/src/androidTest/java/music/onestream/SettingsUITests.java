package music.onestream;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import music.onestream.util.Constants;

import music.onestream.activity.SettingsActivity;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class SettingsUITests {

    @Rule
    public ActivityTestRule<SettingsActivity> mActivityRule = new ActivityTestRule<>(
            SettingsActivity.class);

    @Test
    public void checkButtonsTest() {

        onView(withId(R.id.playlistPage)).check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.playlistPage)).check(ViewAssertions.matches(withText("Create Playlist")));

        onView(withId(R.id.playlistRecommendationButton)).check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.playlistRecommendationButton)).check(ViewAssertions.matches(withText("Music Suggestions")));

        onView(withId(R.id.back)).check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.back)).check(ViewAssertions.matches(withText("Return to OneStream")));

        onView(withId(R.id.resetDir)).check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.resetDir)).check(ViewAssertions.matches(withText("Reset directory")));

        onView(withId(R.id.accountsPage)).check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.accountsPage)).check(ViewAssertions.matches(withText("User accounts")));

        onView(withId(R.id.sortPage)).check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.sortPage)).check(ViewAssertions.matches(withText("Organize Songs")));

        onView(withId(R.id.cachePlaylists)).check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.cachePlaylists)).check(ViewAssertions.matches(withText("Playlists on cloud")));

        onView(withId(R.id.songViewToggleButton)).check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.songViewToggleButton)).check(ViewAssertions.matches(withText("Toggle SongView On")));

        onView(withId(R.id.cacheSongs)).check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.cacheSongs)).check(ViewAssertions.matches(withText("Song Cache Off")));

        onView(withId(R.id.change_dir)).check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.change_dir)).check(ViewAssertions.matches(withText("Change Local Music Folder")));

    }
}

