package root.iv.androidacademy.activity;

import android.content.res.Configuration;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ScrollView;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import root.iv.androidacademy.R;
import root.iv.androidacademy.app.App;
import root.iv.androidacademy.ui.activity.MainActivity;
import root.iv.androidacademy.ui.fragment.NewsDetailsFragment;
import root.iv.androidacademy.ui.fragment.NewsListFragment;
import root.iv.androidacademy.ui.ivHorizontalScrollView;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.not;

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
    }

    /*
        Запустили активти
        Вводим в EditText что-то
        Нажимаем "back"
        Изменяем EditText
        Нажимаем обновить
        Изменяем EditText
     */

    @Test
    public void testCaseKeyBoard() throws Exception {
        await().atMost(1, TimeUnit.SECONDS).until(activity::hasWindowFocus);
        Assert.assertEquals(Configuration.ORIENTATION_PORTRAIT, activity.getResources().getConfiguration().orientation);

        // Находим inputFilter
        ViewInteraction editFilter = onView(Matchers.allOf(withId(R.id.inputFilter), isDisplayed()));
        // Активируем поле и вводим туда что-нибудь
        editFilter.perform(click(), replaceText("i"));
        // Должна появиться клавиатура и активироваться поле
        await().atMost(1, TimeUnit.SECONDS).until(this::keyBoardIsVisible);
        editFilter.check(ViewAssertions.matches(ViewMatchers.hasFocus()));


        // Нажимаем "Back"
        device.pressBack();
        // Должна спрятаться клавиатура и деактивироваться поле ввода
        await().atMost(1, TimeUnit.SECONDS).until(this::keyBoardIsInvisible);
        editFilter.check(ViewAssertions.matches(not(ViewMatchers.hasFocus())));


        // Активируем поле и стираем оттуда все
        editFilter.perform(click(), replaceText(""));
        // Должна снова появиться клавиатура и активироваться поле
        await().atMost(1, TimeUnit.SECONDS).until(this::keyBoardIsVisible);
        editFilter.check(ViewAssertions.matches(ViewMatchers.hasFocus()));

        // Находим кнопку "обновить"
        ViewInteraction buttonUpdate = onView(Matchers.allOf(withId(R.id.buttonUpdate), isDisplayed()));
        // Нажимаем кнопку обновить
        buttonUpdate.perform(click());
        // Должна пропасть клавиатура и деактивироваться поле
        await().atMost(5, TimeUnit.SECONDS).until(this::keyBoardIsInvisible);
        editFilter.check(ViewAssertions.matches(not(ViewMatchers.hasFocus())));


        // Активируем поле и добавляем текст
        editFilter.perform(click(), replaceText("1"));
        // Должна появиться клавиатура и активироваться поле
        await().atMost(1, TimeUnit.SECONDS).until(this::keyBoardIsVisible);
        editFilter.check(ViewAssertions.matches(ViewMatchers.hasFocus()));

        // Находим список, проматываем его
        RecyclerView recyclerView = activity.findViewById(R.id.listNews);
        int count = recyclerView.getAdapter().getItemCount();
        System.out.println("Count: " + count);
        recyclerView.smoothScrollToPosition(count-1);
        // Должна пропасть клаиатура и деактивироваться поле
        await().atMost(2, TimeUnit.SECONDS).until(this::keyBoardIsInvisible);
        editFilter.check(ViewAssertions.matches(not(ViewMatchers.hasFocus())));

        // Активируем поле и удаляем текст
        editFilter.perform(click(), replaceText(""));
        // Должна появиться клавиатура и активироваться поле
        await().atMost(1, TimeUnit.SECONDS).until(this::keyBoardIsVisible);
        editFilter.check(ViewAssertions.matches(ViewMatchers.hasFocus()));

        // Находим секции, проматываем их
        ivHorizontalScrollView scrollSections = activity.findViewById(R.id.viewSections);
        count = scrollSections.getChildCount();
        scrollSections.smoothScrollBy(100, 0);
        // Должна пропастьклавиатура и деактивироваться поле
        await().atMost(2, TimeUnit.SECONDS).until(this::keyBoardIsInvisible);
        editFilter.check(ViewAssertions.matches(not(ViewMatchers.hasFocus())));

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

    @After
    public void onStop() throws Exception {
        device.unfreezeRotation();
    }
}
