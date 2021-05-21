package dev.timwalsh.angleassault;


import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.PerformException;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.util.HumanReadables;
import androidx.test.espresso.util.TreeIterables;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeoutException;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class SmokeTests {
    @SuppressWarnings("deprecation")
    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class, false);
    // App Test Elements
    ViewInteraction startGameBtn = onView(
            allOf(withId(R.id.start_btn), withText("Start"),
                    childAtPosition(
                            childAtPosition(
                                    withId(android.R.id.content),
                                    0),
                            3),
                    isDisplayed()));

    ViewInteraction mainActivityHeader = onView(
            allOf(withId(R.id.textView), withText("AngleAssault"),
                    withParent(withParent(withId(android.R.id.content))),
                    isDisplayed()));

    ViewInteraction playAgainBtn = onView(
            allOf(withId(R.id.newGameButton), withText("Play again"),
                    childAtPosition(
                            childAtPosition(
                                    withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                    0),
                            5),
                    isDisplayed()));

    ViewInteraction settingsBtn = onView(
            allOf(withId(R.id.settings_btn), withText("Settings"),
                    childAtPosition(
                            childAtPosition(
                                    withId(android.R.id.content),
                                    0),
                            7),
                    isDisplayed()));

    ViewInteraction gameModeTglBtnSlow = onView(
            allOf(withId(R.id.gameMode), withText("SWITCH TO SLOW"),
                    withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout.class))),
                    isDisplayed()));

    ViewInteraction settingsSaveBtn = onView(
            allOf(withId(R.id.saveButton), withText("Save"),
                    childAtPosition(
                            childAtPosition(
                                    withClassName(is("android.widget.LinearLayout")),
                                    3),
                            4),
                    isDisplayed()));

    ViewInteraction mainMenuHighScoreBtn = onView(
            allOf(withId(R.id.scores_btn), withText("High Scores"),
                    childAtPosition(
                            childAtPosition(
                                    withId(android.R.id.content),
                                    0),
                            5),
                    isDisplayed()));

    ViewInteraction highScoreHeader = onView(
            allOf(withId(R.id.score_header), withText("High Scores"),
                    withParent(withParent(withId(android.R.id.content))),
                    isDisplayed()));

    ViewInteraction highScoresMainMenuBtn = onView(
            allOf(withId(R.id.exitScores), withText("Main Menu"),
                    childAtPosition(
                            childAtPosition(
                                    withId(android.R.id.content),
                                    0),
                            0),
                    isDisplayed()));

    // App test performance
    @Test
    public void gameActivityTest() {
        // IT: Starts a game and can press play again.
        mainActivityHeader.check(matches(withText("AngleAssault")));
        startGameBtn.perform(click());
        // Max correct answers at 180deg is 2, so 3 * wait-time
        long maxWaitTime = 30001;
        onView(isRoot()).perform(waitId(R.id.newGameButton, maxWaitTime));
        playAgainBtn.perform(click());
    }


    @Test
    public void highScoreActivityTest() {
        // IT: Can open the high scores activity
        mainActivityHeader.check(matches(withText("AngleAssault")));
        mainMenuHighScoreBtn.perform(click());
        highScoreHeader.check(matches(withText("High Scores")));
        highScoresMainMenuBtn.perform(click());
    }

    @Test
    public void settingActivityTest() {
        // IT: Sees the expected Slow Button Toggle when returning to settings.
        mainActivityHeader.check(matches(withText("AngleAssault")));
        settingsBtn.perform(click());
        gameModeTglBtnSlow.check(matches(isDisplayed()));
        settingsSaveBtn.perform(click());
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }

    /**
     * Perform action of waiting for a specific view id.
     *
     * @param viewId The id of the view to wait for.
     * @param millis The timeout of until when to wait for.
     */
    public static ViewAction waitId(final int viewId, final long millis) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isRoot();
            }

            @Override
            public String getDescription() {
                return "wait for a specific view with id <" + viewId + "> during " + millis + " millis.";
            }

            @Override
            public void perform(final UiController uiController, final View view) {
                uiController.loopMainThreadUntilIdle();
                final long startTime = System.currentTimeMillis();
                final long endTime = startTime + millis;
                final Matcher<View> viewMatcher = withId(viewId);

                do {
                    for (View child : TreeIterables.breadthFirstViewTraversal(view)) {
                        // found view with required ID
                        if (viewMatcher.matches(child)) {
                            return;
                        }
                    }

                    uiController.loopMainThreadForAtLeast(50);
                }
                while (System.currentTimeMillis() < endTime);

                // timeout happens
                throw new PerformException.Builder()
                        .withActionDescription(this.getDescription())
                        .withViewDescription(HumanReadables.describe(view))
                        .withCause(new TimeoutException())
                        .build();
            }
        };
    }
}
