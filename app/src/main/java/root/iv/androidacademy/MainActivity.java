package root.iv.androidacademy;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends AppCompatActivity {
    @BindView(R.id.editMessage)
    EditText editMessage;

    @BindView(R.id.buttonSend)
    Button buttonSend;
    @OnClick(R.id.buttonSend)
    public void sendClick() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:cool.rainbow2012@yandex.ru"));
        intent.putExtra(Intent.EXTRA_TEXT, editMessage.getText().toString());
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.feedback));
        if (intent.resolveActivity(getPackageManager()) != null)
            startActivity(Intent.createChooser(intent, "Email"));
        else
            Toast.makeText(this, getString(R.string.notFoundEmailActiviy), Toast.LENGTH_LONG).show();
    }

    @OnClick(R.id.buttonGoogle)
    public void googleClick() {
        openBrowser(getString(R.string.urlGooglePlusAccount));
    }

    @OnClick(R.id.buttonVK)
    public void vkClick() {
        openBrowser(getString(R.string.urlVKAccount));
    }

    private void openBrowser(String url) {
        Intent intentBrowser = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intentBrowser);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(R.string.name);
        ButterKnife.bind(this);
        new ListenerEditText(editMessage, buttonSend);
    }
}
