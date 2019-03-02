package root.iv.androidacademy.util.intro;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

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
