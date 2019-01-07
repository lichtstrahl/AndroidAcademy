package root.iv.androidacademy.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.relex.circleindicator.CircleIndicator;
import root.iv.androidacademy.R;
import root.iv.androidacademy.ui.fragment.IntroFragment;

public class IntoActivity extends FragmentActivity {
    private static final String INTENT_INTRO_FLAG = "intent:intro-flag";
    private static final int COUNT_PAGE = 3;
    @BindView(R.id.viewPager)
    ViewPager pager;
    @BindView(R.id.indicator)
    CircleIndicator indicator;
    private PagerAdapter pagerAdapter;
    @BindView(R.id.viewWelcome)
    TextView viewWelcome;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean flag = getPreferences(MODE_PRIVATE).getBoolean(INTENT_INTRO_FLAG, true);
        if (flag) {
            setContentView(R.layout.activity_into);
            ButterKnife.bind(this);

            pagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
                @Override
                public Fragment getItem(int i) {
                    return IntroFragment.newInstance(i);
                }

                @Override
                public int getCount() {
                    return COUNT_PAGE;
                }
            };

            pager.setAdapter(pagerAdapter);
            indicator.setViewPager(pager);

            viewWelcome.setOnClickListener((v) -> {
                MainActivity.start(this);
                finish();
            });
        } else {
            MainActivity.start(this);
            finish();
        }

        getPreferences(MODE_PRIVATE).edit().putBoolean(INTENT_INTRO_FLAG, !flag).apply();
    }
}
