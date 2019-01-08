package root.iv.androidacademy.util.intro;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import root.iv.androidacademy.ui.fragment.IntroFragment;

public class IntroAdapter extends FragmentPagerAdapter {
    private int count;

    public IntroAdapter(FragmentManager fm, int c) {
        super(fm);
        count = c;
    }

    @Override
    public Fragment getItem(int i) {
        return IntroFragment.newInstance(i);
    }

    @Override
    public int getCount() {
        return count;
    }
}
