package com.bytedance.camera.demo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import com.bytedance.camera.demo.utils.Utils;

import java.io.File;

public class TakePictureActivity extends AppCompatActivity {

    private ImageView imageView;
    public static final int REQUEST_IMAGE_CAPTURE = 1;
    File file;
    private static final int REQUEST_EXTERNAL_STORAGE = 101;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_picture);
        imageView = findViewById(R.id.img);
        findViewById(R.id.btn_picture).setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(TakePictureActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(TakePictureActivity.this,
                    Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                //todo 在这里申请相机、存储的权限
                if(ContextCompat.checkSelfPermission(getApplication(),Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED
                        ||ContextCompat.checkSelfPermission(getApplication(),Manifest.permission.CAMERA)!=PackageManager.PERMISSION_GRANTED)
                    ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO}, 1);


            } else {
                takePicture();
            }
        });

    }

    private void takePicture() {
        //todo 打开相机

        Intent intent_camera_image = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        file = Utils.getOutputMediaFile(Utils.MEDIA_TYPE_IMAGE);
        if(file != null) {
            Uri fileuri = FileProvider.getUriForFile(this,"com.bytedance.camera.demo",file);
            intent_camera_image.putExtra(MediaStore.EXTRA_OUTPUT,fileuri);
            startActivityForResult(intent_camera_image, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            setPic();
        }
    }
    private void setPic() {
        //todo 根据imageView裁剪
        Log.d("TEXT","deal picture");

        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();
        BitmapFactory.Options bit_Options = new BitmapFactory.Options();
        bit_Options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getAbsolutePath(),bit_Options);
        int photoW = bit_Options.outWidth;
        int photoH = bit_Options.outHeight;
        int scale = Math.min(photoW/targetW,photoH/targetH);
        bit_Options.inJustDecodeBounds =false;
        bit_Options.inSampleSize = scale;
        bit_Options.inPurgeable = true;
        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(),bit_Options);
        Bitmap rotate_bitmap = Utils.rotateImage(bitmap,file.getAbsolutePath());
        imageView.setImageBitmap(rotate_bitmap);
        //todo 根据缩放比例读取文件，生成Bitmap

        //todo 如果存在预览方向改变，进行图片旋转

        //todo 如果存在预览方向改变，进行图片旋转
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        int cnt = 0;
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE: {
                //todo 判断权限是否已经授予
                break;
            }
        }
    }
}
