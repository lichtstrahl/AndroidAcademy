package root.iv.androidacademy;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import io.reactivex.subjects.PublishSubject;

public class ListenerEditText implements TextWatcher {
    private static final String TAG = "ListenerEditText";
    private PublishSubject<String> subject;

    public ListenerEditText(EditText ed, View view) {
        subject = PublishSubject.create();
        ed.addTextChangedListener(this);
        subject.subscribe(
                x -> view.setVisibility(x.isEmpty() ? View.INVISIBLE : View.VISIBLE),
                e -> Log.e(TAG, e.getMessage())
        );
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // Не используется
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        subject.onNext(s.toString());
    }

    @Override
    public void afterTextChanged(Editable s) {
        // Не используется
    }
}
