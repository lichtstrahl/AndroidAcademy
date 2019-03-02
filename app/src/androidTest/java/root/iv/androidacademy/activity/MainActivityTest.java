package root.iv.androidacademy.activity;

import android.annotation.TargetApi;
import android.content.res.Configuration;
import android.os.Build;
import android.provider.Settings;
import androidx.test.InstrumentationRegistry;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import androidx.test.runner.lifecycle.Stage;
import androidx.test.uiautomator.UiDevice;
import androidx.recyclerview.widget.RecyclerView;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import root.iv.androidacademy.R;
import root.iv.androidacademy.app.App;
import root.iv.androidacademy.ui.activity.MainActivity;
import root.iv.androidacademy.ui.ivHorizontalScrollView;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {
    @Rule
    public ActivityTestRule<root.iv.androidacademy.ui.activity.MainActivity> mainRule = new ActivityTestRule<>(root.iv.androidacademy.ui.activity.MainActivity.class);
    private MainActivity activity;
    private UiDevice device;

    @Before
    public void onStart() throws Exception {
        activity = mainRule.getActivity();
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        device.setOrientationNatural();
    }

    @Test
    public void testCaseTabletRotate() throws Exception {
        await().atMost(1, TimeUnit.SECONDS).until(activity::hasWindowFocus);
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
        device.setOrientationNatural();
        // Должен быть виден только детальный просмотр
        await().atMost(2, TimeUnit.SECONDS).until(App::listFragmentInvisible);
        await().atMost(2, TimeUnit.SECONDS).until(App::detailsFragmentVisible);


        // Нажатие "back"
        device.pressBack();
        // Должен закрыться детальный просмотр и открыться список новостей
        await().atMost(2, TimeUnit.SECONDS).until(App::listFragmentVisible);
        await().atMost(2, TimeUnit.SECONDS).until(App::detailsFragmentInvisible);

        // Выбираем третью новость
        listNews.perform(RecyclerViewActions.actionOnItemAtPosition(2, click()));
        // Только детальный просмотр третьей новости
        await().atMost(2, TimeUnit.SECONDS).until(App::listFragmentInvisible);
        await().atMost(2, TimeUnit.SECONDS).until(App::detailsFragmentVisible);

        // Переворот экрана
        device.setOrientationLeft();
        // Виден и список новостей и детальный просмотр третьей новости
        await().atMost(2, TimeUnit.SECONDS).until(App::listFragmentVisible);
        await().atMost(2, TimeUnit.SECONDS).until(App::detailsFragmentVisible);

        // Нажатие "Back"
        device.pressBack();
        // Выход из приложения
        await().atMost(3, TimeUnit.SECONDS).until(() -> !activity.hasWindowFocus());

    }

    @Test
    public void testCaseKeyBoard() throws Exception {
        await().atMost(1, TimeUnit.SECONDS).until(activity::hasWindowFocus);
        Assert.assertEquals(Configuration.ORIENTATION_PORTRAIT, activity.getResources().getConfiguration().orientation);

        // Находим inputFilter
        ViewInteraction editFilter = onView(Matchers.allOf(withId(R.id.inputFilter), isDisplayed()));
        // Активируем поле и вводим туда что-нибудь
        editFilter.perform(click(), replaceText("1"));
        // Должна появиться клавиатура и активироваться поле
        await().atMost(2, TimeUnit.SECONDS).until(this::keyBoardIsVisible);
        editFilter.check(ViewAssertions.matches(ViewMatchers.hasFocus()));

        // Нажимаем "Back"
        device.pressBack();
        // Должна спрятаться клавиатура и деактивироваться поле ввода
        await().atMost(2, TimeUnit.SECONDS).until(this::keyBoardIsInvisible);
        editFilter.check(ViewAssertions.matches(not(ViewMatchers.hasFocus())));

        // Активируем поле и удаляем текст
        editFilter.perform(click(), replaceText(""));
        // Должна появиться клавиатура и активироваться поле
        await().atMost(2, TimeUnit.SECONDS).until(this::keyBoardIsVisible);
        editFilter.check(ViewAssertions.matches(ViewMatchers.hasFocus()));

        // Находим список, проматываем его
        RecyclerView recyclerView = activity.findViewById(R.id.listNews);
        int count = recyclerView.getAdapter().getItemCount();
        System.out.println("Count: " + count);
        recyclerView.smoothScrollToPosition(count-1);
        // Должна пропасть клаиатура и деактивироваться поле
        await().atMost(2, TimeUnit.SECONDS).until(this::keyBoardIsInvisible);
        editFilter.check(ViewAssertions.matches(not(ViewMatchers.hasFocus())));

        // Активируем поле и вводим текст
        editFilter.perform(click(), replaceText("2"));
        // Должна появиться клавиатура и активироваться поле
        await().atMost(2, TimeUnit.SECONDS).until(this::keyBoardIsVisible);
        editFilter.check(ViewAssertions.matches(ViewMatchers.hasFocus()));

        // Находим секции, проматываем их
        ivHorizontalScrollView scrollSections = activity.findViewById(R.id.viewSections);
        count = scrollSections.getChildCount();
        scrollSections.smoothScrollBy(100, 0);
        // Должна пропасть клавиатура и деактивироваться поле
        await().atMost(2, TimeUnit.SECONDS).until(this::keyBoardIsInvisible);
        editFilter.check(ViewAssertions.matches(not(ViewMatchers.hasFocus())));

        // Активируем поле и удаляем текст
        editFilter.perform(click(), replaceText(""));
        // Должна появиться клавиатура и активироваться поле
        await().atMost(2, TimeUnit.SECONDS).until(this::keyBoardIsVisible);
        editFilter.check(ViewAssertions.matches(ViewMatchers.hasFocus()));

        // Кликаем по категории
        activity.runOnUiThread(scrollSections.getTouchables().get(0)::performClick);
        // Должнапоявиться загрузка и пропасть клавиатура, деактивируется поле
        await().atMost(2, TimeUnit.SECONDS).until(this::keyBoardIsInvisible);
        editFilter.check(ViewAssertions.matches(not(ViewMatchers.hasFocus())));

        // Активируем поле и вводим текст
        editFilter.perform(click(), replaceText("3"));
        // Должна появиться клавиатура и активироваться поле
        await().atMost(2, TimeUnit.SECONDS).until(this::keyBoardIsVisible);
        editFilter.check(ViewAssertions.matches(ViewMatchers.hasFocus()));

        // Находим кнопку "обновить"
        ViewInteraction buttonUpdate = onView(Matchers.allOf(withId(R.id.buttonUpdate), isDisplayed()));
        // Нажимаем кнопку обновить
        buttonUpdate.perform(click());
        // Должна пропасть клавиатура и деактивироваться поле
        await().atMost(5, TimeUnit.SECONDS).until(this::keyBoardIsInvisible);
        editFilter.check(ViewAssertions.matches(not(ViewMatchers.hasFocus())));

        // Активируем поле и удаляем текст
        editFilter.perform(click(), replaceText(""));
        // Должна появиться клавиатура и активироваться поле
        await().atMost(2, TimeUnit.SECONDS).until(this::keyBoardIsVisible);
        editFilter.check(ViewAssertions.matches(ViewMatchers.hasFocus()));

        // Вызываем OptionsMenu
        Espresso.openActionBarOverflowOrOptionsMenu(activity);
        device.pressBack();
        // Должна пропасть клавиатура и деактивироваться поле
        await().atMost(2, TimeUnit.SECONDS).until(this::keyBoardIsInvisible);
        editFilter.check(ViewAssertions.matches(not(ViewMatchers.hasFocus())));
    }

    @Ignore
    @Test
    public void testDownload() throws Exception {
        await().atMost(2, TimeUnit.SECONDS).until(activity::hasWindowFocus);
        Assert.assertEquals(Configuration.ORIENTATION_PORTRAIT, activity.getResources().getConfiguration().orientation);

        toggleAirplaneMode(false);

        // Кликаем по кнопке обновить
        onView(Matchers.allOf(withId(R.id.buttonUpdate), isDisplayed())).perform(click());
        // Появление диалога загрузки
        onView(withId(R.id.layout)).check(ViewAssertions.matches(isDisplayed()));

        // Поворот экрана, продолжение загрузки
        device.setOrientationLeft();
        // Продолжение попытки загрузиться. Появление кнопки "Перезагрузка"
        onView(withId(R.id.layout)).check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.buttonReconnect))
                .check(ViewAssertions.matches(isDisplayed()))
                .check(ViewAssertions.matches(isEnabled()));

    }

    private boolean keyBoardIsVisible() {
        try {
            return device.executeShellCommand("dumpsys input_method").contains("mInputShown=true");
        } catch (IOException e) {
            Assert.fail();
        }
        return false;
    }

    private boolean keyBoardIsInvisible() {
        return !keyBoardIsVisible();
    }

    private void updateActivity() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(()->{
            Collection resumedActivities = ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(Stage.RESUMED);
            if (!resumedActivities.isEmpty()) {
                activity = (MainActivity) resumedActivities.iterator().next();
            }
        });
    }

    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void toggleAirplaneMode(boolean state) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Settings.System.putInt(activity.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, state ? 0 : 1);
        } else {
            Settings.System.putInt(activity.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, state ? 0 : 1);
        }
    }

    @After
    public void onStop() throws Exception {
        device.unfreezeRotation();
    }
}
