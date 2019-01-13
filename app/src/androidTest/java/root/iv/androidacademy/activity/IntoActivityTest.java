package root.iv.androidacademy.activity;

import android.support.test.espresso.ViewAction;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.TimeUtils;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.awaitility.Awaitility.await;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import root.iv.androidacademy.R;
import root.iv.androidacademy.ui.activity.IntoActivity;


@RunWith(AndroidJUnit4.class)
public class IntoActivityTest {
    @Rule
    public ActivityTestRule<IntoActivity> introRule = new ActivityTestRule<>(IntoActivity.class);

    // Запуск Intro-Screen
    // Свайп влево
    // Свайп влево
    // Ждём 2 секунды и появляется активити с новостями
    @Test
    public void welcomeIsDisplayed() {
        // Ждём секунду, пока Activity загрузится
        await().atMost(1, TimeUnit.SECONDS).until(introRule.getActivity()::hasWindowFocus);
        // Находим ViewPager
        ViewInteraction viewPager = onView(Matchers.allOf(withId(R.id.viewPager), isDisplayed()));
        // Листаем влево
        viewPager.perform(ViewActions.swipeLeft());
        // Листаем вправо
        viewPager.perform(ViewActions.swipeLeft());
        // Ждём 2 секунды, пока запустится MainActivity
        await().atMost(3, TimeUnit.SECONDS).until(introRule.getActivity()::isFinishing);
    }


    private void isDisplayedById(int id) {
        onView(ViewMatchers.withId(id)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }
}

// Возможное оформление паузы на 5 секунд. Но потом выбросится исключение
// Awaitility.await().atMost(5, TimeUnit.SECONDS).until(() -> {
//        return false;
//        });