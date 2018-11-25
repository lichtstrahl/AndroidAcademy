package root.iv.androidacademy.activity.listener;

import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Spinner;

import root.iv.androidacademy.App;
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
        Object item = spinner.getSelectedItem();
        if (item == null) {
            App.logI("Spinner item is NULL");
        } else {
            App.logI("Spinner item not NULL");
        }
        loader.setSection(item.toString());
        loader.load();
    }

    public void unsubscribe() {
        loader = null;
        spinner = null;
    }
}
