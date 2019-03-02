package root.iv.androidacademy.activity;

import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import root.iv.androidacademy.R;
import root.iv.androidacademy.ui.activity.IntoActivity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.awaitility.Awaitility.await;


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
}
