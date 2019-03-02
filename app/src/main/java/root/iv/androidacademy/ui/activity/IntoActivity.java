package root.iv.androidacademy.ui.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.relex.circleindicator.CircleIndicator;
import root.iv.androidacademy.R;
import root.iv.androidacademy.app.App;
import root.iv.androidacademy.util.intro.IntroAdapter;

public class IntoActivity extends FragmentActivity {
    private static final String INTENT_INTRO_SHOW = "intent:intro-flag";
    private static final int COUNT_PAGE = 3;
    @BindView(R.id.viewPager)
    ViewPager pager;
    @BindView(R.id.indicator)
    CircleIndicator indicator;
    @OnClick(R.id.viewWelcome)
    public void startUpdate() {
        startMainActivity();
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean showIntro = getPreferences(MODE_PRIVATE).getBoolean(INTENT_INTRO_SHOW, true);
        if (!App.isEspressoTest()) {
            getPreferences(MODE_PRIVATE).edit().putBoolean(INTENT_INTRO_SHOW, !showIntro).apply();
        } else {
            showIntro = true;
        }

        if (showIntro) {
            setContentView(R.layout.activity_into);
            ButterKnife.bind(this);

            IntroAdapter pagerAdapter = new IntroAdapter(getSupportFragmentManager(), COUNT_PAGE);
            pager.setAdapter(pagerAdapter);
            indicator.setViewPager(pager);
            return;
        }

        startMainActivity();

    }

    private void startMainActivity() {
        MainActivity.start(this);
        finish();
    }
}

