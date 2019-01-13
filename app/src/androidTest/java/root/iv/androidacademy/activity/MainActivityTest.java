package root.iv.androidacademy.activity;

import android.app.Instrumentation;
import android.content.res.Configuration;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;
import android.support.v4.app.FragmentManager;

import org.awaitility.Awaitility;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.sql.Time;
import java.util.concurrent.TimeUnit;

import root.iv.androidacademy.R;
import root.iv.androidacademy.app.App;
import root.iv.androidacademy.ui.activity.MainActivity;
import root.iv.androidacademy.ui.fragment.NewsDetailsFragment;
import root.iv.androidacademy.ui.fragment.NewsListFragment;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.action.ViewActions.click;
import static org.awaitility.Awaitility.await;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {
    @Rule
    public ActivityTestRule<root.iv.androidacademy.ui.activity.MainActivity> mainRule = new ActivityTestRule<>(root.iv.androidacademy.ui.activity.MainActivity.class);
    private MainActivity activity;
    private FragmentManager fragmentManager;
    private UiDevice device;
    private static final String DETAILS_TAG = NewsDetailsFragment.TAG;
    private static final String LIST_TAG = NewsListFragment.TAG;

    @Before
    public void onStart() throws Exception {
        activity = mainRule.getActivity();
        fragmentManager = activity.getSupportFragmentManager();
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        device.setOrientationNatural();
    }

    // Запуск MainActivity с ListFragment
    // Проверяем, что мы в портретной ориентации
    @Test
    public void testCase1() throws Exception {
        Awaitility.await().atMost(1, TimeUnit.SECONDS).until(activity::hasWindowFocus);
        Assert.assertEquals(activity.getResources().getConfiguration().orientation, Configuration.ORIENTATION_PORTRAIT);

        // Наодим RecyclerView
        ViewInteraction listNews = onView(Matchers.allOf(withId(R.id.listNews), isDisplayed()));
        // Виден только список, без детального просмора
        await().atMost(2, TimeUnit.SECONDS).until(App::listFragmentVisible);
        await().atMost(2, TimeUnit.SECONDS).until(App::detailsFragmentInvisible);


        // Нажимаем на первую новость
        listNews.perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        // Должен появиться детальный просмотр первой новости
        await().atMost(2, TimeUnit.SECONDS).until(App::listFragmentInvisible);
        await().atMost(2, TimeUnit.SECONDS).until(App::detailsFragmentVisible);

        // Переворот экрана
        device.setOrientationLeft();
        // Должен быть виден и детальный просмотр и список
        await().atMost(2, TimeUnit.SECONDS).until(App::listFragmentVisible);
        await().atMost(2, TimeUnit.SECONDS).until(App::detailsFragmentVisible);

        // Выбор второй новости
        listNews.perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));
        // Должен обновиться детальный просмотр
        await().atMost(2, TimeUnit.SECONDS).until(App::listFragmentVisible);
        await().atMost(2, TimeUnit.SECONDS).until(App::detailsFragmentVisible);

        // Переворот экрана.
        activity.setRequestedOrientation(Configuration.ORIENTATION_PORTRAIT);
        // Должен быть виден только детальный просмотр
        await().atMost(2, TimeUnit.SECONDS).until(App::listFragmentInvisible);
        await().atMost(2, TimeUnit.SECONDS).until(App::detailsFragmentVisible);


        // Нажатие "back"
        device.pressBack();
        // Должен закрыться детальный просмотр и открыться список новостей
        await().atMost(1, TimeUnit.SECONDS).until(App::listFragmentVisible);
        await().atMost(1, TimeUnit.SECONDS).until(App::detailsFragmentInvisible);
        device.unfreezeRotation();
    }
}
