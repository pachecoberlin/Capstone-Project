package com.udacity.gradle.builditbigger;

import android.view.View;

import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertFalse;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class JokeTest {
    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(
            MainActivity.class);
    private IdlingResource idlingResource;

    @Test
    public void jokeTest() {
        MainActivity activity = mActivityTestRule.getActivity();
        View view = activity.findViewById(R.id.joker);
        activity.tellJoke(view);
        assertFalse(MainActivity.jokeTester.isEmpty());
        activity.tellJokeForFriend(view);
        assertFalse(MainActivity.jokeTester.isEmpty());
    }

    @Test
    public void jokeTest2() {
        ViewInteraction recyclerView = onView(
                allOf(withId(R.id.joker)));
        recyclerView.perform(actionOnItemAtPosition(0, click()));
        assertFalse(MainActivity.jokeTester.isEmpty());
    }
}