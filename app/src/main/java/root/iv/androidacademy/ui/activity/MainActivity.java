package root.iv.androidacademy.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import root.iv.androidacademy.R;
import root.iv.androidacademy.app.App;
import root.iv.androidacademy.ui.fragment.NewsDetailsFragment;
import root.iv.androidacademy.ui.fragment.NewsListFragment;

public class MainActivity extends AppCompatActivity implements NewsListFragment.Listener, NewsDetailsFragment.Listener {
    private static final String TRANSACTION_INIT = "transaction:init";
    private static final String TAG_LIST_FRAGMENT = "fragment:list";
    private static final String TAG_DETAILS_FRAGMENT = "fragment:details";
    private static final String TAG_REPLACE_DETAILS_FRAGMENT = "fragment:details-replace";
    private boolean isLandTabletOrientation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isLandTabletOrientation = findViewById(R.id.frame_detail) != null;

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_list, NewsListFragment.newInstance(isLandTabletOrientation), TAG_LIST_FRAGMENT)
                    .addToBackStack(TRANSACTION_INIT)
                    .commit();
            App.logI("Count fragments: " + getSupportFragmentManager().getFragments().size());
        } else {
            Fragment detailsFragment = getSupportFragmentManager().findFragmentByTag(TAG_DETAILS_FRAGMENT);
            if (detailsFragment != null) {
                App.logI("Поворот экрана и отображение details");
                int frameID = isLandTabletOrientation ? R.id.frame_detail : R.id.frame_list;
                getSupportFragmentManager().popBackStackImmediate();    // Чтобы в колонке справа не накладывать Details на List

                getSupportFragmentManager()
                        .beginTransaction()
                        .add(frameID, detailsFragment, TAG_DETAILS_FRAGMENT)
                        .addToBackStack(null)
                        .commit();
            }

//            detailsFragment = getSupportFragmentManager().findFragmentByTag(TAG_REPLACE_DETAILS_FRAGMENT);
//            if (detailsFragment != null) {
//                // Сейчас 100% портрет
//                App.logI("Поворот экрана, после details в Land-scape");
//                getSupportFragmentManager().popBackStackImmediate();
//
//                getSupportFragmentManager()
//                        .beginTransaction()
//                        .add(R.id.frame_list, detailsFragment, TAG_DETAILS_FRAGMENT)
//                        .addToBackStack(null)
//                        .commit();
//            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (getSupportFragmentManager().getBackStackEntryCount() == 0 || isLandTabletOrientation) {
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * @param item - выбранный пункт меню. В данном случае он НЕВАЖЕН!
     * @return - всегда false, потому что обработка пунктов меню происходит внутри фрагментов
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }

    @Override
    public void clickItemNews(int id) {
        int frameID = isLandTabletOrientation ? R.id.frame_detail : R.id.frame_list;

        getSupportFragmentManager()
                .beginTransaction()
                .add(frameID, NewsDetailsFragment.newInstance(id), TAG_DETAILS_FRAGMENT)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void menuItemAboutSelected() {
        AboutActivity.start(this);
    }

    @Override
    public void menuItemExitSelected() {
        finish();
    }

    @Override
    public void menuItemDeleteSelected(int itemID) {
        App.getDatabase().getNewsDAO().delete(itemID);
        getSupportFragmentManager().popBackStackImmediate();
        NewsListFragment fragment = (NewsListFragment)getSupportFragmentManager().findFragmentByTag(TAG_LIST_FRAGMENT);
        if (fragment != null) fragment.onStart();
    }

    public static void start(Activity activity) {
        Intent intent = new Intent(activity, MainActivity.class);
        activity.startActivity(intent);
    }
}
