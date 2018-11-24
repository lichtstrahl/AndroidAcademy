package root.iv.androidacademy.activity.listener;

import android.view.View;
import android.widget.Spinner;

import root.iv.androidacademy.retrofit.RetrofitLoader;

public class ButtonUpdateClickListener implements View.OnClickListener, Listener {
    private Spinner spinner;
    private RetrofitLoader loader;

    public ButtonUpdateClickListener(RetrofitLoader loader, Spinner spinner) {
        this.loader = loader;
        this.spinner = spinner;
    }

    @Override
    public void onClick(View v) {
        loader.setSection(spinner.getSelectedItem().toString());
        loader.load();
    }

    public void unsubscribe() {
        loader = null;
        spinner = null;
    }
}
