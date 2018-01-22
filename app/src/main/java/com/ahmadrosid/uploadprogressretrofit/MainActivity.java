package com.ahmadrosid.uploadprogressretrofit;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.features.ReturnMode;
import com.esafirm.imagepicker.model.Image;

import java.io.File;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class MainActivity extends AppCompatActivity implements ProgressRequestBody.Listener {

    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;

    private ImageView image;
    private Button button;
    private TextView progressText;
    private ProgressBar progressBar;
    private RequestBody requestBody;
    private Disposable subscribe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        image = findViewById(R.id.image);
        button = findViewById(R.id.button_pick_image);
        progressText = findViewById(R.id.progressText);
        progressBar = findViewById(R.id.progressBar);

        button.setOnClickListener(view1 -> {
            if (TextUtils.equals(button.getText().toString(), "PICK IMAGE")) {
                pickImage();
            } else {
                upload();
            }
        });

        initProgressNotification();
    }

    private void initProgressNotification() {
        Intent resultIntent = new Intent(this, MainActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(
                this,
                0,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );


        mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(
                getApplicationContext()).setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Upload")
                .setContentText("Progress..")
                .setContentIntent(resultPendingIntent);
    }

    private void upload() {
        ProgressRequestBody body = new ProgressRequestBody(requestBody, this);
        subscribe = Api.build().upload(body)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(__ -> showLoading(true))
                .doOnComplete(() -> showLoading(false))
                .subscribe(res -> {
                    String urlImage = Api.main_url + "/" + res.getPath_file();
                    Glide.with(this).load(urlImage).into(image);
                    Toast.makeText(this, "Upload Succsess.", Toast.LENGTH_SHORT).show();
                }, err -> {
                    Log.e("Response", "upload: ", err);
                    Toast.makeText(this, err.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void showLoading(boolean isLoading) {
        if (isLoading) {
            image.setVisibility(View.GONE);
            button.setVisibility(View.GONE);
            progressText.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressText.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            image.setVisibility(View.VISIBLE);
        }
    }

    private void pickImage() {
        ImagePicker.create(this)
                .returnMode(ReturnMode.ALL)
                .folderMode(true)
                .single()
                .toolbarFolderTitle("Folder")
                .toolbarImageTitle("Tap to select")
                .start(0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (subscribe != null)
            subscribe.dispose();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        List<Image> images = ImagePicker.getImages(data);
        if (images != null && !images.isEmpty()) {
            Image imageData = images.get(0);
            Glide.with(this).load(imageData.getPath()).into(this.image);
            button.setText("UPLOAD IMAGE");
            createRequestBody(imageData);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void createRequestBody(Image imageData) {
        MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
        requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("uploaded_file", imageData.getName(), RequestBody.create(MEDIA_TYPE_PNG, new File(imageData.getPath())))
                .build();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onProgress(int progress) {
        runOnUiThread(() -> {
            progressText.setText("Progress :" + progress + "%");
            setNotifProgress(progress);
        });

    }

    public void setNotifProgress(int progress) {
        if (progress == 100){
            mBuilder.setContentText("Upload complete")
                    .setProgress(0, 0, false);
            mNotifyManager.notify(0, mBuilder.build());
        }else{
            mBuilder.setProgress(100, progress, false);
            mNotifyManager.notify(0, mBuilder.build());
        }
    }
}
