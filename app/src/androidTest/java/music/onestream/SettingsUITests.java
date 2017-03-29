package music.onestream;

import android.os.SystemClock;
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

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
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

        onView(withId(R.id.playlistPage)).check(matches(isDisplayed()));
        onView(withId(R.id.playlistPage)).check(matches(withText("Create Playlist")));

        onView(withId(R.id.playlistRecommendationButton)).check(matches(isDisplayed()));
        onView(withId(R.id.playlistRecommendationButton)).check(matches(withText("Music Suggestions")));

        onView(withId(R.id.back)).check(matches(isDisplayed()));
        onView(withId(R.id.back)).check(matches(withText("Return to OneStream")));

        onView(withId(R.id.resetDir)).check(matches(isDisplayed()));
        onView(withId(R.id.resetDir)).check(matches(withText("Reset directory")));

        onView(withId(R.id.accountsPage)).check(matches(isDisplayed()));
        onView(withId(R.id.accountsPage)).check(matches(withText("User accounts")));

        onView(withId(R.id.sortPage)).check(matches(isDisplayed()));
        onView(withId(R.id.sortPage)).check(matches(withText("Organize Songs")));

        onView(withId(R.id.cachePlaylists)).check(matches(isDisplayed()));
        onView(withId(R.id.cachePlaylists)).check(matches(withText("Playlists on cloud")));

        onView(withId(R.id.songViewToggleButton)).check(matches(isDisplayed()));
        onView(withId(R.id.songViewToggleButton)).check(matches(withText("Display SongView")));

        onView(withId(R.id.cacheSongs)).check(matches(isDisplayed()));
        onView(withId(R.id.cacheSongs)).check(matches(withText("Song Caching Off")));

        onView(withId(R.id.change_dir)).check(matches(isDisplayed()));
        onView(withId(R.id.change_dir)).check(matches(withText("Change Local Music Folder")));

    }

    @Test
    public void selectChangeLocalDirectoryButton() {

        onView(withId(R.id.change_dir)).perform(click());

        onView(withText("Select Music Directory")).check(matches(isDisplayed()));
        onView(withText("Select")).check(matches(isDisplayed()));
        onView(withText("Cancel")).check(matches(isDisplayed()));

    }

    @Test
    public void selectMusicSuggestionsButton() {

        onView(withId(R.id.playlistRecommendationButton)).perform(click());

        onView(withId(R.id.appbarPR)).check(matches(isDisplayed()));

    }

    @Test
    public void selectReturnToOneStreamButton() {

        onView(withId(R.id.back)).perform(click());

        onView(withText("OneStream")).check(matches(isDisplayed()));

    }

    @Test
    public void selectUserAccountsButton() {

        onView(withId(R.id.accountsPage)).perform(click());

        onView(withId(R.id.soundCloudMusicLoginLauncherButton)).check(matches(isDisplayed()));
        onView(withId(R.id.oneStreamDomainLauncherButton)).check(matches(isDisplayed()));
        onView(withId(R.id.spotifyLoginLauncherButton)).check(matches(isDisplayed()));

    }

    @Test
    public void selectOrganizeSongsButton() {

        onView(withId(R.id.sortPage)).perform(click());

        onView(withId(R.id.sortAlphDescend)).check(matches(isDisplayed()));
        onView(withId(R.id.sortAlphAscend)).check(matches(isDisplayed()));
        onView(withId(R.id.sortAlphAscendGenre)).check(matches(isDisplayed()));
        onView(withId(R.id.sortAlphDescendArtist)).check(matches(isDisplayed()));
        onView(withId(R.id.sortAlphAscendArtist)).check(matches(isDisplayed()));
        onView(withId(R.id.resetSortingType)).check(matches(isDisplayed()));
        onView(withId(R.id.sortAlphDescendAlbum)).check(matches(isDisplayed()));
        onView(withId(R.id.sortAlphAscendAlbum)).check(matches(isDisplayed()));
        onView(withId(R.id.sortAlphDescendGenre)).check(matches(isDisplayed()));

    }

}

