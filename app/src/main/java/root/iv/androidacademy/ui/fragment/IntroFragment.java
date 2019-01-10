package root.iv.androidacademy.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import root.iv.androidacademy.R;
import root.iv.androidacademy.util.GlideApp;

public class IntroFragment extends Fragment {
    private static final String ARG_PAGE_NUM = "args:page-num";
    private static final int[] INTRO_SCREENS = new int[] {R.drawable.intro_list_news, R.drawable.intro_news_details, R.drawable.intro_about};
    private ImageView image;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_intro, container, false);

        image = view.findViewById(R.id.image);

        Bundle bundle = getArguments();
        if (bundle != null) {
            int drawRes = INTRO_SCREENS[bundle.getInt(ARG_PAGE_NUM, 0)];
            GlideApp.with(view).load(drawRes).into(image);
        }

        return view;
    }

    public static IntroFragment newInstance(int pos) {
        IntroFragment fragment = new IntroFragment();
        Bundle bundle = new Bundle();

        bundle.putInt(ARG_PAGE_NUM, pos);
        fragment.setArguments(bundle);

        return fragment;
    }
}