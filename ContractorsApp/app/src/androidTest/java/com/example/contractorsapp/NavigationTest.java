package com.example.contractorsapp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.anyOf; // <<< ВАЖЕН IMPORT

import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.PerformException;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.util.HumanReadables;
import androidx.test.espresso.util.TreeIterables;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.example.contractorsapp.ui.MainActivity;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.concurrent.TimeoutException;

@RunWith(AndroidJUnit4.class)
public class NavigationTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void testMainScreenBehavior() {
        // Стъпка 1: Изчакваме мрежовата заявка да приключи.
        // Сигурният знак за това е, когато се появи ИЛИ списъкът, ИЛИ надписът за празен списък.
        onView(isRoot()).perform(waitForView(
                anyOf(withId(R.id.recycler_view_contragents), withId(R.id.text_view_empty)),
                15000 // Чакаме до 15 секунди
        ));

        // Стъпка 2: Проверяваме колко елемента има в списъка.
        if (getRecyclerViewCount() > 0) {
            // СЦЕНАРИЙ 1: Има данни
            // Ако има поне един елемент, тестваме навигацията.
            onView(withId(R.id.recycler_view_contragents))
                    .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

            // Проверяваме дали се е отворил екранът с детайли.
            onView(withId(R.id.button_save)).check(matches(isDisplayed()));
        } else {
            // СЦЕНАРИЙ 2: Няма данни
            // Ако няма елементи, просто проверяваме дали се вижда правилният надпис.
            onView(withId(R.id.text_view_empty)).check(matches(isDisplayed()));
        }
        // И в двата случая тестът ще мине успешно!
    }

    // Помощен метод, който брои елементите в RecyclerView.
    private int getRecyclerViewCount() {
        final int[] count = {0};
        try {
            Matcher<View> matcher = new TypeSafeMatcher<View>() {
                @Override
                protected boolean matchesSafely(View item) {
                    count[0] = ((RecyclerView) item).getAdapter().getItemCount();
                    return true;
                }
                @Override
                public void describeTo(Description description) {}
            };
            onView(withId(R.id.recycler_view_contragents)).check(matches(matcher));
        } catch (Exception e) {
            // Ако RecyclerView не е намерен (например, защото е GONE), броят остава 0.
            return 0;
        }
        return count[0];
    }

    // Помощен метод, който изчаква даден елемент да се появи.
    public static ViewAction waitForView(final Matcher<View> viewMatcher, final long timeout) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isRoot();
            }

            @Override
            public String getDescription() {
                return "wait for a specific view with matcher <" + viewMatcher + "> during " + timeout + " millis.";
            }

            @Override
            public void perform(final UiController uiController, final View view) {
                uiController.loopMainThreadUntilIdle();
                final long startTime = System.currentTimeMillis();
                final long endTime = startTime + timeout;

                do {
                    for (View child : TreeIterables.breadthFirstViewTraversal(view)) {
                        if (viewMatcher.matches(child) && child.isShown()) {
                            return; // Намерихме го!
                        }
                    }
                    uiController.loopMainThreadForAtLeast(50);
                } while (System.currentTimeMillis() < endTime);

                // Времето изтече
                throw new PerformException.Builder()
                        .withActionDescription(this.getDescription())
                        .withViewDescription(HumanReadables.describe(view))
                        .withCause(new TimeoutException())
                        .build();
            }
        };
    }
}