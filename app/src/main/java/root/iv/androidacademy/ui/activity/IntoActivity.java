package root.iv.androidacademy.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.relex.circleindicator.CircleIndicator;
import root.iv.androidacademy.R;
import root.iv.androidacademy.ui.fragment.IntroFragment;

public class IntoActivity extends FragmentActivity {
    private static final int COUNT_PAGE = 3;
    @BindView(R.id.viewPager)
    ViewPager pager;
    @BindView(R.id.indicator)
    CircleIndicator indicator;
    private PagerAdapter pagerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    }
}
