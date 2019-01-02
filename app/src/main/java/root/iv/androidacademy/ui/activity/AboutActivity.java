package root.iv.androidacademy.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import root.iv.androidacademy.ui.activity.listener.ListenerEditText;
import root.iv.androidacademy.R;


public class AboutActivity extends AppCompatActivity {
    @BindView(R.id.editMessage)
    EditText editMessage;
    @BindView(R.id.imageAvatar)
    ImageView imageAvatar;
    @BindView(R.id.imageBMSTU)
    ImageView imageBMSTU;
    @BindView(R.id.imageAcademy)
    ImageView imageAcademy;
    @BindView(R.id.imageAtlant)
    ImageView imageAtlant;
    @BindView(R.id.buttonVK)
    ImageView imageVK;
    @BindView(R.id.buttonGoogle)
    ImageView imageGoogle;
    @BindView(R.id.buttonSend)
    Button buttonSend;
    private ListenerEditText listenerEditText;

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
        setContentView(R.layout.activity_about);
        setTitle(R.string.name);
        ButterKnife.bind(this);
        Glide.with(this).load(R.drawable.igor_smirnov).into(imageAvatar);
        Glide.with(this).load(R.drawable.ic_bmstu).into(imageBMSTU);
        Glide.with(this).load(R.drawable.ic_android_academy).into(imageAcademy);
        Glide.with(this).load(R.drawable.ic_atlant).into(imageAtlant);
        Glide.with(this).load(R.drawable.ic_vk).into(imageVK);
        Glide.with(this).load(R.drawable.ic_googleplus).into(imageGoogle);
        listenerEditText = new ListenerEditText(editMessage);

    }

    // TODO Что происходит? Почему вызывается onTextChanged???
    @Override
    protected void onResume() {
        super.onResume();
        listenerEditText.subscribe(str -> buttonSend.setVisibility(str.isEmpty() ? View.INVISIBLE : View.VISIBLE));
        listenerEditText.onTextChanged(editMessage.getText().toString(),0,0,0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        listenerEditText.unsubscribe();
    }
}
