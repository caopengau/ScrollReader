package com.example.caope.scrollreader;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.example.caope.scrollreader.R.mipmap.ic_launcher;

public class Step1 extends AppCompatActivity {
    private String filename;
    private ImageView imageView;
    private  String SAVE_PIC_PATH  = Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED) ? Environment.getExternalStorageDirectory().getAbsolutePath() : "/mnt/sdcard";//保存到SD卡
    private static final int CAMERA_REQUEST = 1888;
    private static final int GALLERY_REQUEST = 1889;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.step1);
        setTitle(R.string.Step1);
        imageView = (ImageView)findViewById(R.id.imageView1);
    }

    public void takePhoto(View view){
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_REQUEST);
    }
    public void selectPhoto(View view){
        Intent intent = new Intent("android.intent.action.PICK");
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data==null||resultCode==RESULT_CANCELED) return;
        Bitmap bitmap = null;
        if(requestCode==GALLERY_REQUEST){
            bitmap = data.getParcelableExtra("data");
            if(bitmap==null){
                try {
                    //通过URI得到输入流
                    InputStream inputStream = getContentResolver().openInputStream(data.getData());
                    //通过输入流得到bitmap对象
                    bitmap = BitmapFactory.decodeStream(inputStream);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }else if(requestCode==CAMERA_REQUEST){
            bitmap = (Bitmap) data.getExtras().get("data");
            try {
                saveToSDCard(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        imageView.setImageBitmap(bitmap);
    }

    public void refreshGallery(File file){
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(file);
        intent.setData(uri);
        this.sendBroadcast(intent);//这个广播的目的就是更新图库，发了这个广播进入相册就可以找到你保存的图片了！，记得要传你更新的file哦
    }

    public void saveToSDCard(Bitmap bitmap) throws IOException {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File file = new File(SAVE_PIC_PATH);
            if (!file.exists()) {
                file.mkdirs();
            }

            FileOutputStream outputStream;
            createPhotoName();
            File myCaptureFile = new File(SAVE_PIC_PATH, filename);
            outputStream = new FileOutputStream(myCaptureFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);//把图片数据写入文件
            outputStream.flush(); outputStream.close();
            refreshGallery(myCaptureFile);
        }else {
            Toast.makeText(this,"no SD-card",Toast.LENGTH_SHORT).show();
        }
    }

    public void createPhotoName(){
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        filename = format.format(date)+".jpg";

    }
    public void clearPhoto(View view){
        imageView.setImageResource(ic_launcher);
    }

    public void next(View view){
        if (imageView.getDrawable().getConstantState().equals(getDrawable(R.mipmap.ic_launcher).getConstantState())){
            Toast.makeText(this, R.string.toast1, Toast.LENGTH_LONG).show(); return;
        }

        sendToServer();

        Intent intent = new Intent();
        intent.putExtra("filename", SAVE_PIC_PATH + "/" + filename);
        intent.setClass(this, Step2.class);
        this.startActivityForResult(intent, 1);

    }

    private void sendToServer(){
    }
}
