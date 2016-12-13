package com.example.caope.scrollreader;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.example.caope.scrollreader.R.mipmap.ic_launcher;

public class Step1 extends AppCompatActivity {
    private  String SAVE_PIC_PATH  = Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED) ? Environment.getExternalStorageDirectory().getAbsolutePath() : "/mnt/sdcard";
    private static final int CAMERA_REQUEST = 1888;
    private static final int GALLERY_REQUEST = 1889;
    /**************************************************************************************/
    private static final String SERVER_ADDRESS = "192.168.0.102";
    private static final int SERVER_PORT = 2020;
    private boolean IS_CONNECTED = false;   // set to true when not testing file transfer
    /**************************************************************************************/
    private String filename;
    private ImageView imageView;
    private Socket socket;
    private Thread ClientThread = null;
    private boolean isRunning = false;
    private Bitmap bitmap = null;


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
        if(requestCode==GALLERY_REQUEST){
            bitmap = data.getParcelableExtra("data");
            if(bitmap==null){
                try {
                    InputStream inputStream = getContentResolver().openInputStream(data.getData());
                    bitmap = BitmapFactory.decodeStream(inputStream);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }else if(requestCode==CAMERA_REQUEST){
            bitmap = (Bitmap) data.getExtras().get("data");
            try {
                saveToLocal(bitmap);
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
        this.sendBroadcast(intent);// update gallery for new file
    }

    public void createPhotoName(){
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        filename = format.format(date)+".jpg";
    }

    public void saveToLocal(Bitmap bitmap) throws IOException {
        createPhotoName();
        File myCaptureFile = new File(SAVE_PIC_PATH, filename);

        FileOutputStream outputStream = new FileOutputStream(myCaptureFile);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);//把图片数据写入文件
        outputStream.flush(); outputStream.close();

        refreshGallery(myCaptureFile);
    }

    public void clearPhoto(View view){
        imageView.setImageResource(ic_launcher);
    }

    public void next(View view){
        if (imageView.getDrawable().getConstantState().equals(getDrawable(R.mipmap.ic_launcher).getConstantState())){
            Toast.makeText(this, R.string.toast1, Toast.LENGTH_LONG).show(); return;
        }

        Intent intent = new Intent();
        intent.putExtra("filename", SAVE_PIC_PATH + "/" + filename);
        intent.setClass(this, Step2.class);

        isRunning = true;
        this.ClientThread = new Thread(new ClientThread());
        this.ClientThread.start();

        if(IS_CONNECTED) this.startActivityForResult(intent, 1);
        else Toast.makeText(Step1.this, "Failed to connect to server", Toast.LENGTH_LONG).show();
    }

    public String GetIpAddress() {
        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int i = wifiInfo.getIpAddress();
        return (i & 0xFF) + "." +
                ((i >> 8 ) & 0xFF) + "." +
                ((i >> 16 ) & 0xFF)+ "." +
                ((i >> 24 ) & 0xFF );
    }

    public static byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream bos =new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        // customize height and width of the image
        // Bitmap map = Bitmap.createScaledBitmap(bitmap, 400, 400, true);
        return bos.toByteArray();
    }

    public class ClientThread implements Runnable{
        public void run(){
            try{
                InetAddress serverAddress = InetAddress.getByName(SERVER_ADDRESS);
                socket = new Socket(serverAddress,SERVER_PORT);

                byte[] imageByte = Bitmap2Bytes(bitmap);
                OutputStream outputStream = socket.getOutputStream();
                outputStream.write(imageByte);
                outputStream.flush(); outputStream.close(); socket.close();
                IS_CONNECTED = true;
            }catch(IOException e) {
                e.printStackTrace();
            }
        }
    }
}
