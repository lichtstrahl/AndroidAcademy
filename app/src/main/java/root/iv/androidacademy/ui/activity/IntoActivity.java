package root.iv.androidacademy.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Completable;
import io.reactivex.disposables.Disposable;
import me.relex.circleindicator.CircleIndicator;
import root.iv.androidacademy.R;
import root.iv.androidacademy.util.intro.IntroAdapter;
import root.iv.androidacademy.util.intro.IntroOnPageChangeListener;

public class IntoActivity extends FragmentActivity {
    private static final String INTENT_INTRO_SHOW = "intent:intro-flag";
    private static final int COUNT_PAGE = 3;
    @BindView(R.id.viewPager)
    ViewPager pager;
    @BindView(R.id.indicator)
    CircleIndicator indicator;
    private PagerAdapter pagerAdapter;
    private IntroOnPageChangeListener introListener;
    @Nullable
    private Disposable disposable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean showIntro = getPreferences(MODE_PRIVATE).getBoolean(INTENT_INTRO_SHOW, true);
        getPreferences(MODE_PRIVATE).edit().putBoolean(INTENT_INTRO_SHOW, !showIntro).apply();
        introListener = new IntroOnPageChangeListener();

        if (showIntro) {
            setContentView(R.layout.activity_into);
            ButterKnife.bind(this);

            pagerAdapter = new IntroAdapter(getSupportFragmentManager(), COUNT_PAGE);
            pager.setAdapter(pagerAdapter);
            pager.addOnPageChangeListener(introListener);
            indicator.setViewPager(pager);
            return;
        }

        startMainActivity();

    }

    @Override
    protected void onStart() {
        super.onStart();
        introListener.subscribe(page -> {
            if (page == (COUNT_PAGE-1)) {
                disposable = Completable.complete()
                    .delay(1500, TimeUnit.MILLISECONDS)
                    .subscribe(this::startMainActivity);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        introListener.unsubscribe();
        if (disposable != null) disposable.dispose();
    }

    private void startMainActivity() {
        MainActivity.start(this);
        finish();
    }
}

