package root.iv.androidacademy.ui.activity.listener;

import android.view.View;

import javax.annotation.Nullable;

import io.reactivex.functions.Action;
import root.iv.androidacademy.app.App;

public class ButtonUpdateClickListener implements ClickListener<Action> {
    @Nullable
    private Action action;

    @Override
    public void onClick(View v) {
        try {
            if (action != null) action.run();
        } catch (Exception e) {
            App.logE(e.getMessage());
        }
    }

    public void unsubscribe() {
        action = null;
    }

    @Override
    public void subscribe(Action a) {
        action = a;
    }
}
