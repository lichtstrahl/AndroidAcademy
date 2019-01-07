package root.iv.androidacademy.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import root.iv.androidacademy.R;
import root.iv.androidacademy.ui.fragment.NewsDetailsFragment;
import root.iv.androidacademy.ui.fragment.NewsListFragment;

public class MainActivity extends AppCompatActivity implements NewsListFragment.Listener {
    private static final String TRANSACTION_START_DETAILS_FRAGMENT = "transaction:start-details-fragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NewsListFragment listFragment = new NewsListFragment();

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.frame_list, listFragment)
                .commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.option_menu_list,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itemAbout:
                Intent intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                return true;
            case R.id.itemExit:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void clickItemNews(int id) {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.frame_list, NewsDetailsFragment.newInstance(id))
                .addToBackStack(TRANSACTION_START_DETAILS_FRAGMENT)
                .commit();
    }

    public static void start(Activity activity) {
        Intent intent = new Intent(activity, MainActivity.class);
        activity.startActivity(intent);
    }
}
