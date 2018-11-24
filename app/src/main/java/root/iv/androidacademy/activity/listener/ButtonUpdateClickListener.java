package root.iv.androidacademy.activity.listener;

import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Spinner;

import root.iv.androidacademy.retrofit.RetrofitLoader;

public class ButtonUpdateClickListener implements View.OnClickListener, Listener {
    private Spinner spinner;
    private RetrofitLoader loader;
    private AlertDialog loadDialog;

    public ButtonUpdateClickListener(RetrofitLoader loader, Spinner spinner, AlertDialog dialog) {
        this.loader = loader;
        this.spinner = spinner;
        this.loadDialog = dialog;
    }

    @Override
    public void onClick(View v) {
        loadDialog.show();

        loader.setSection(spinner.getSelectedItem().toString());
        loader.load();
    }

    public void unsubscribe() {
        loader = null;
        spinner = null;
    }
}
