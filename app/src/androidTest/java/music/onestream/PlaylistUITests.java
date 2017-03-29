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
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.matcher.ViewMatchers.hasFocus;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.isSelected;
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
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.core.StringEndsWith.endsWith;

//import android.support.test.espresso.Espresso;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class PlaylistUITests {

    private String playlistName = "TestPlaylist";

    @Rule
    public ActivityTestRule<OneStreamActivity> mActivityRule = new ActivityTestRule<>(
            OneStreamActivity.class);

    @Test
    public void createPlayListTest() {

        createPlaylist("TestPlaylist");

        //Click Playlists tab
        onView(allOf(withClassName(endsWith("TabView")),
                withChild(withText(Constants.OneStream_Playlists)),
                withParent(withParent(withId(R.id.tabs)))
        )).perform(scrollTo()).perform(click());

        //Check if playlist is displayed in list
        onView(withText("Playlists")).check(ViewAssertions.matches(isDisplayed()));
        onView(withText(playlistName)).check(ViewAssertions.matches(isDisplayed()));
        onView(withText("0 Songs")).check(ViewAssertions.matches(isDisplayed()));

        deletePlaylist("TestPlaylist");

    }

    @Test
    public void addSongToPlaylistTest() {

        //Click options menu and click settings
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText("Settings")).perform(click());

        //Go to edit playlist activity
        onView(withId(R.id.playlistPage)).perform(click());

        //Create new playlist and add song
        onView(withId(R.id.addSongsToPlaylist)).perform(click());

        //Check if the song filer, discard changes button, save playlist button, and song list are displayed
        onView(withId(R.id.songFilterAS)).check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.discardNewPlaylistChanges)).check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.saveNewPlaylistChanges)).check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.songList)).check(ViewAssertions.matches(isDisplayed()));

        //Select first song in list
        onData(anything())
                .inAdapterView(allOf(withId(R.id.songList), isCompletelyDisplayed()))
                .atPosition(0).perform(click());


        onView(withId(R.id.saveNewPlaylistChanges)).perform(click());

        //check if song is added to edit playlist activity
        onData(anything())
                .inAdapterView(allOf(withId(R.id.playListSongs), isCompletelyDisplayed()))
                .atPosition(0).check(ViewAssertions.matches(isDisplayed()));

        onView(withId(R.id.playListName)).perform(clearText());
        onView(withId(R.id.playListName)).perform(typeText(playlistName));
        onView(withId(R.id.saveNewPlaylist)).perform(click());

        //Click Playlists tab
        onView(allOf(withClassName(endsWith("TabView")),
                withChild(withText(Constants.OneStream_Playlists)),
                withParent(withParent(withId(R.id.tabs)))
        )).perform(scrollTo()).perform(click());

        //Select playlist in playlist list
        onView(withText(playlistName)).perform(click());

        //Check if song is added to playlist
        onData(anything())
                .inAdapterView(allOf(withId(R.id.ListViewPL), isCompletelyDisplayed()))
                .atPosition(0).check(ViewAssertions.matches(isDisplayed()));

        //Check if playback controls are displayed
        onView(withId(R.id.seekBarPL)).check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.PrevPL)).check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.RewindPL)).check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.fabIOPL)).check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.NextPL)).check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.RandomPL)).check(ViewAssertions.matches(isDisplayed()));

        deletePlaylist(playlistName);

    }

    @Test
    public void playSongFromPlaylistTest() {

        //Click options menu and click settings
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText("Settings")).perform(click());

        //Go to edit playlist activity
        onView(withId(R.id.playlistPage)).perform(click());

        //Create new playlist and add song
        onView(withId(R.id.addSongsToPlaylist)).perform(click());

        //Select first song in list
        onData(anything())
                .inAdapterView(allOf(withId(R.id.songList), isCompletelyDisplayed()))
                .atPosition(0).perform(click());

        onView(withId(R.id.saveNewPlaylistChanges)).perform(click());

        onView(withId(R.id.playListName)).perform(clearText());
        onView(withId(R.id.playListName)).perform(typeText(playlistName));
        onView(withId(R.id.saveNewPlaylist)).perform(click());

        //Click Playlists tab
        onView(allOf(withClassName(endsWith("TabView")),
                withChild(withText(Constants.OneStream_Playlists)),
                withParent(withParent(withId(R.id.tabs)))
        )).perform(scrollTo()).perform(click());

        //Select Playlist
        onView(withText(playlistName)).perform(click());

        SystemClock.sleep(1000);

        //Play song
        onData(anything())
                .inAdapterView(allOf(withId(R.id.ListViewPL), isCompletelyDisplayed()))
                .atPosition(0).perform(click());

        //Check if album art, artist name, and album name are displayed in song view
        onView(withId(R.id.album)).check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.artistOrGenreName)).check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.albumName)).check(ViewAssertions.matches(isDisplayed()));

        pressBack();

        deletePlaylist(playlistName);
    }

    @Test
    public void deletePlayListTest() {

        createPlaylist(playlistName);

        //Click Playlists tab
        onView(allOf(withClassName(endsWith("TabView")),
                withChild(withText(Constants.OneStream_Playlists)),
                withParent(withParent(withId(R.id.tabs)))
        )).perform(scrollTo()).perform(click());

        //Select Playlist and go to edit playlist activity
        onView(withText(playlistName)).perform(click());

        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText("Edit Playlist")).perform(click());


        onView(withId(R.id.discardNewPlaylist)).perform(click());

        SystemClock.sleep(500);

        //Click Playlists tab
        onView(allOf(withClassName(endsWith("TabView")),
                withChild(withText(Constants.OneStream_Playlists)),
                withParent(withParent(withId(R.id.tabs)))
        )).perform(scrollTo()).perform(click());

        //Check if playlist is removed from list
        onView(withText(playlistName)).check(doesNotExist());

    }

    public void createPlaylist(String playlistName) {
        //Click options menu and click settings
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText("Settings")).perform(click());

        //Go to edit playlist activity
        onView(withId(R.id.playlistPage)).perform(click());

        //Create new playlist
        onView(withId(R.id.playListName)).perform(clearText());
        onView(withId(R.id.playListName)).perform(typeText(playlistName));
        onView(withId(R.id.saveNewPlaylist)).perform(click());
    }


    public void deletePlaylist(String playlistName) {
        //Select Playlist and go to edit playlist activity
        onView(withText(playlistName)).perform(click());

        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText("Edit Playlist")).perform(click());

        onView(withId(R.id.discardNewPlaylist)).perform(click());

        SystemClock.sleep(500);

        //Click Playlists tab
        onView(allOf(withClassName(endsWith("TabView")),
                withChild(withText(Constants.OneStream_Playlists)),
                withParent(withParent(withId(R.id.tabs)))
        )).perform(scrollTo()).perform(click());

    }

}

