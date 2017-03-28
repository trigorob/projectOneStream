package music.onestream;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;



import music.onestream.activity.OneStreamActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
//import android.support.test.espresso.Espresso;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class OneStreamActivityUITests {

    @Rule
    public ActivityTestRule<OneStreamActivity> mActivityRule = new ActivityTestRule<>(
            OneStreamActivity.class);

    @Test
    public void checkUI() {

        onView(withText("OneStream")).check(ViewAssertions.matches(isDisplayed()));

        //Check if play controls are displayed
        onView(withId(R.id.fabIO)).check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.Random)).check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.Rewind)).check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.Prev)).check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.Next)).check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.seekBar)).check(ViewAssertions.matches(isDisplayed()));

    }
}

