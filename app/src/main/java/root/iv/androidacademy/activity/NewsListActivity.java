package root.iv.androidacademy.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.LinkedList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import root.iv.androidacademy.App;
import root.iv.androidacademy.Section;
import root.iv.androidacademy.ListenerEditText;
import root.iv.androidacademy.NewsAdapter;
import root.iv.androidacademy.R;
import root.iv.androidacademy.retrofit.TopStoriesObserver;

public class NewsListActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = "NewsListActivity";
    private RecyclerView listNews;
    private AlertDialog loadDialog;
    private ILoader loader;
    private ListenerEditText inputListener;

    @BindView(R.id.spinner)
    Spinner spinner;

    @BindView(R.id.input)
    EditText input;

    private void loadSpinner() {
        ArrayAdapter<Section> adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.spinnerListItem));
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        Toast.makeText(NewsListActivity.this, Section.SECTIONS[position].getName(), Toast.LENGTH_SHORT).show();
                        ((NewsAdapter)listNews.getAdapter()).setNewSection(Section.SECTIONS[position].getName());
                        loader.load();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_list);
        ButterKnife.bind(this);
        loadSpinner();

        listNews = findViewById(R.id.listNews);
        listNews.setAdapter(
                NewsAdapter.getBuilderNewsAdapter()
                .buildListNews(new LinkedList<>())
                .buildInflater(LayoutInflater.from(this))
                .buildListener(this)
                .build()
        );
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            listNews.setLayoutManager(new LinearLayoutManager(this));
        }
        else {
            listNews.setLayoutManager(new GridLayoutManager(this, 2));
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setView(R.layout.dialog).setCancelable(false);

        loadDialog = builder.create();
        loadDialog.show();

        inputListener = new ListenerEditText(input);
    }

    @Override
    public void onClick(View v) {
        int pos = listNews.getChildAdapterPosition(v);
        NewsDetailsActivity.start(this, ((NewsAdapter) listNews.getAdapter()).getItem(pos));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.option_menu,menu);
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
    protected void onStart() {
        super.onStart();
        loader = new LoaderRetrofit();
        loader.load();
    }

    @Override
    protected void onStop() {
        super.onStop();
        loader.stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        inputListener.subscribe(((NewsAdapter)listNews.getAdapter())::setFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        inputListener.unsubscribe();
    }

    /**
     * Содержит поле observer, которое хранит адапетр, а также функции, вызывающиеся в случае успеха/неудаче при загрузке
     */
    class LoaderRetrofit implements ILoader {
        private RetrofitProcessor processor = new RetrofitProcessor();
        private TopStoriesObserver observer = TopStoriesObserver.getBuilder()
                .buildAdapter((NewsAdapter)listNews.getAdapter())
                .buildComplete(processor::completeProcess)
                .buildError(processor::errorProcess)
                .build();

        @Override
        public void stop() {
            observer.dispose();
        }

        // TODO Сделать выбор категорий
        @Override
        public void load() {
            App.getApiTopStories().getTopStories(spinner.getSelectedItem().toString())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(observer);
        }

        class RetrofitProcessor {
            private void completeProcess() {
                loadDialog.dismiss();
            }

            private void errorProcess() {
                Toast.makeText(NewsListActivity.this, R.string.errorLoading, Toast.LENGTH_SHORT).show();
                loadDialog.findViewById(R.id.progress).setVisibility(View.GONE);
                TextView textView = loadDialog.findViewById(R.id.text);
                textView.setText(R.string.errorLoading);
                loadDialog.findViewById(R.id.buttonReconnect).setVisibility(View.VISIBLE);
                loadDialog.findViewById(R.id.buttonReconnect).setOnClickListener((view) -> {
                    LoaderRetrofit.this.load();
                });
            }
        }
    }

    interface ILoader {
        void stop();
        void load();
    }
}
