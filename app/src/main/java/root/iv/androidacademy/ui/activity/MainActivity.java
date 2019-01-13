package root.iv.androidacademy.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import root.iv.androidacademy.R;
import root.iv.androidacademy.app.App;
import root.iv.androidacademy.ui.fragment.NewsDetailsFragment;
import root.iv.androidacademy.ui.fragment.NewsListFragment;

public class MainActivity extends AppCompatActivity implements NewsListFragment.Listener, NewsDetailsFragment.Listener {
    private static final String TRANSACTION_INIT = "transaction:init";
    private boolean isLandTabletOrientation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isLandTabletOrientation = findViewById(R.id.frame_detail) != null;

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_list, NewsListFragment.newInstance(), NewsListFragment.TAG)
                    .addToBackStack(TRANSACTION_INIT)
                    .commit();
            App.logI("Count fragments: " + getSupportFragmentManager().getFragments().size());
        } else {
            Fragment detailsFragment = getSupportFragmentManager().findFragmentByTag(NewsDetailsFragment.TAG);
            if (detailsFragment != null) {
                App.logI("Поворот экрана и отображение details");
                int frameID = isLandTabletOrientation ? R.id.frame_detail : R.id.frame_list;
                getSupportFragmentManager().popBackStackImmediate();    // Чтобы в колонке справа не накладывать Details на List

                if (isLandTabletOrientation) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .add(frameID, detailsFragment, NewsDetailsFragment.TAG)
                            .addToBackStack(null)
                            .commit();
                } else {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(frameID, detailsFragment, NewsDetailsFragment.TAG)
                            .addToBackStack(null)
                            .commit();
                }
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
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
        if (isLandTabletOrientation) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                    .add(frameID, NewsDetailsFragment.newInstance(id), NewsDetailsFragment.TAG)
                    .addToBackStack(null)
                    .commit();
        } else {
            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                    .replace(frameID, NewsDetailsFragment.newInstance(id), NewsDetailsFragment.TAG)
                    .addToBackStack(null)
                    .commit();
        }
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
        getSupportFragmentManager().popBackStackImmediate();
        NewsListFragment fragment = (NewsListFragment)getSupportFragmentManager().findFragmentByTag(NewsListFragment.TAG);
        if (fragment != null) fragment.onStart();
    }

    public static void start(Activity activity) {
        Intent intent = new Intent(activity, MainActivity.class);
        activity.startActivity(intent);
    }
}
