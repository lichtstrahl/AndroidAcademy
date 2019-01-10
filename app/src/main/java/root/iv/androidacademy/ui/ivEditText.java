package root.iv.androidacademy.ui;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

import io.reactivex.functions.Action;
import root.iv.androidacademy.app.App;
import root.iv.androidacademy.util.listener.Subscribed;

public class ivEditText extends EditText implements Subscribed<Action> {
    @Nullable
    private Action action;

    public ivEditText(Context context, AttributeSet set) {
        super(context, set);
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            try {
                if (action != null) {
                    action.run();
                }
            } catch (Exception e) {
                App.logE(e.getMessage());
            }
        }
        return false;
    }

    @Override
    public void subscribe(Action a) {
        action = a;
    }

    @Override
    public void unsubscribe() {
        action = null;
    }
}
