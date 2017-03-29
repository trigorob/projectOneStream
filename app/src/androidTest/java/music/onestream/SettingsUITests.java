package music.onestream;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import music.onestream.activity.SettingsActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class SettingsUITests {

    @Rule
    public ActivityTestRule<SettingsActivity> mActivityRule = new ActivityTestRule<>(
            SettingsActivity.class);

    @Test
    public void checkButtonsTest() {

        Espresso.onView(ViewMatchers.withId(R.id.playlistPage)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Espresso.onView(ViewMatchers.withId(R.id.playlistPage)).check(ViewAssertions.matches(ViewMatchers.withText("Create Playlist")));

        Espresso.onView(ViewMatchers.withId(R.id.playlistRecommendationButton)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Espresso.onView(ViewMatchers.withId(R.id.playlistRecommendationButton)).check(ViewAssertions.matches(ViewMatchers.withText("Music Suggestions")));

        Espresso.onView(ViewMatchers.withId(R.id.back)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Espresso.onView(ViewMatchers.withId(R.id.back)).check(ViewAssertions.matches(ViewMatchers.withText("Return to OneStream")));

        Espresso.onView(ViewMatchers.withId(R.id.resetDir)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Espresso.onView(ViewMatchers.withId(R.id.resetDir)).check(ViewAssertions.matches(ViewMatchers.withText("Reset directory")));

        Espresso.onView(ViewMatchers.withId(R.id.accountsPage)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Espresso.onView(ViewMatchers.withId(R.id.accountsPage)).check(ViewAssertions.matches(ViewMatchers.withText("User accounts")));

        Espresso.onView(ViewMatchers.withId(R.id.sortPage)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Espresso.onView(ViewMatchers.withId(R.id.sortPage)).check(ViewAssertions.matches(ViewMatchers.withText("Organize Songs")));

        Espresso.onView(ViewMatchers.withId(R.id.cachePlaylists)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Espresso.onView(ViewMatchers.withId(R.id.cachePlaylists)).check(ViewAssertions.matches(ViewMatchers.withText("Playlists on cloud")));

        Espresso.onView(ViewMatchers.withId(R.id.songViewToggleButton)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Espresso.onView(ViewMatchers.withId(R.id.songViewToggleButton)).check(ViewAssertions.matches(ViewMatchers.withText("Display SongView")));

        Espresso.onView(ViewMatchers.withId(R.id.cacheSongs)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Espresso.onView(ViewMatchers.withId(R.id.cacheSongs)).check(ViewAssertions.matches(ViewMatchers.withText("Song Caching Off")));

        Espresso.onView(ViewMatchers.withId(R.id.change_dir)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Espresso.onView(ViewMatchers.withId(R.id.change_dir)).check(ViewAssertions.matches(ViewMatchers.withText("Change Local Music Folder")));

    }
}

