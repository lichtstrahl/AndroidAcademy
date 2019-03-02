package root.iv.androidacademy.util.intro;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import root.iv.androidacademy.util.Action1;

public class IntroOnPageChangeListener implements ViewPager.OnPageChangeListener {
    @Nullable
    private Action1<Integer> action;


    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {
         if (action != null) action.run(i);
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    public void subscribe(Action1<Integer> a) {
        action = a;
    }

    public void unsubscribe() {
        action = null;
    }
}
