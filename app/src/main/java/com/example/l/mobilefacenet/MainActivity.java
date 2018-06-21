package com.example.l.mobilefacenet;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {
    private static final int SELECT_IMAGE1 = 1,SELECT_IMAGE2 = 2;
    private ImageView imageView1,imageView2;
    private Bitmap yourSelectedImage1 = null,yourSelectedImage2 = null;
    private Bitmap faceImage1 = null,faceImage2 = null;
    TextView faceInfo1,faceInfo2,cmpResult;
    private Face mFace = new Face();
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE" };

    public static void verifyStoragePermissions(Activity activity) {

        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        verifyStoragePermissions(this);

        try {
            copyBigDataToSD("det1.bin");
            copyBigDataToSD("det2.bin");
            copyBigDataToSD("det3.bin");
            copyBigDataToSD("det1.param");
            copyBigDataToSD("det2.param");
            copyBigDataToSD("det3.param");
            copyBigDataToSD("recognition.bin");
            copyBigDataToSD("recognition.param");
        } catch (IOException e) {
            e.printStackTrace();
        }
        //model init
        File sdDir = Environment.getExternalStorageDirectory();//get directory
        String sdPath = sdDir.toString() + "/facem/";
        mFace.FaceModelInit(sdPath);

        //LEFT IMAGE
        imageView1 = (ImageView) findViewById(R.id.imageView1);
        faceInfo1=(TextView)findViewById(R.id.faceInfo1);
        Button buttonImage1 = (Button) findViewById(R.id.select1);
        buttonImage1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(Intent.ACTION_PICK);
                i.setType("image/*");
                startActivityForResult(i, SELECT_IMAGE1);
            }
        });

        Button buttonDetect1 = (Button) findViewById(R.id.detect1);
        buttonDetect1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (yourSelectedImage1 == null)
                    return;
                faceImage1=null;
                //detect
                int width = yourSelectedImage1.getWidth();
                int height = yourSelectedImage1.getHeight();
                byte[] imageDate = getPixelsRGBA(yourSelectedImage1);

                long timeDetectFace = System.currentTimeMillis();
                int faceInfo[]=mFace.FaceDetect(imageDate,width,height,4);
                timeDetectFace = System.currentTimeMillis() - timeDetectFace;

                if(faceInfo.length>1){
                    faceInfo1.setText("pic1 detect time:"+timeDetectFace);
                    int faceNum = faceInfo[0];
                    Log.i(TAG, "pic width："+width+"height："+height+" face num：" + faceNum );
                    Bitmap drawBitmap = yourSelectedImage1.copy(Bitmap.Config.ARGB_8888, true);
                    for(int i=0;i<faceInfo[0];i++) {
                        int left, top, right, bottom;
                        Canvas canvas = new Canvas(drawBitmap);
                        Paint paint = new Paint();
                        left = faceInfo[1+14*i];
                        top = faceInfo[2+14*i];
                        right = faceInfo[3+14*i];
                        bottom = faceInfo[4+14*i];
                        paint.setColor(Color.BLUE);
                        paint.setStyle(Paint.Style.STROKE);
                        paint.setStrokeWidth(5);
                        canvas.drawRect(left, top, right, bottom, paint);
                    }
                    imageView1.setImageBitmap(drawBitmap);
                    faceImage1 = Bitmap.createBitmap(yourSelectedImage1,faceInfo[1],faceInfo[2],faceInfo[3]-faceInfo[1],faceInfo[4]-faceInfo[2]);
                }else{
                    faceInfo1.setText("no face");
                }
            }
        });

        //RIGHT IMAGE
        imageView2 = (ImageView) findViewById(R.id.imageView2);
        faceInfo2=(TextView)findViewById(R.id.faceInfo2);
        Button buttonImage2 = (Button) findViewById(R.id.select2);
        buttonImage2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(Intent.ACTION_PICK);
                i.setType("image/*");
                startActivityForResult(i, SELECT_IMAGE2);
            }
        });

        Button buttonDetect2 = (Button) findViewById(R.id.detect2);
        buttonDetect2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (yourSelectedImage2 == null)
                    return;
                //detect
                faceImage2=null;
                int width = yourSelectedImage2.getWidth();
                int height = yourSelectedImage2.getHeight();
                byte[] imageDate = getPixelsRGBA(yourSelectedImage2);

                long timeDetectFace = System.currentTimeMillis();
                int faceInfo[]=mFace.FaceDetect(imageDate,width,height,4);
                timeDetectFace = System.currentTimeMillis() - timeDetectFace;

                if(faceInfo.length>1){
                    faceInfo2.setText("pic2 detect time:"+timeDetectFace);
                    int faceNum = faceInfo[0];
                    Log.i(TAG, "pic width："+width+"height："+height+" face num：" + faceNum );
                    Bitmap drawBitmap = yourSelectedImage2.copy(Bitmap.Config.ARGB_8888, true);
                    for(int i=0;i<faceInfo[0];i++) {
                        int left, top, right, bottom;
                        Canvas canvas = new Canvas(drawBitmap);
                        Paint paint = new Paint();
                        left = faceInfo[1+14*i];
                        top = faceInfo[2+14*i];
                        right = faceInfo[3+14*i];
                        bottom = faceInfo[4+14*i];
                        paint.setColor(Color.GREEN);
                        paint.setStyle(Paint.Style.STROKE);
                        paint.setStrokeWidth(5);
                        canvas.drawRect(left, top, right, bottom, paint);
                    }
                    imageView2.setImageBitmap(drawBitmap);
                    faceImage2 = Bitmap.createBitmap(yourSelectedImage2,faceInfo[1],faceInfo[2],faceInfo[3]-faceInfo[1],faceInfo[4]-faceInfo[2]);
                }else{
                    faceInfo2.setText("no face");
                }

            }
        });

        //cmp
        cmpResult=(TextView)findViewById(R.id.textView1);
        Button cmpImage = (Button) findViewById(R.id.facecmp);
        cmpImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (faceImage1 == null||faceImage2 == null){
                    cmpResult.setText("no enough face,return");
                    return;
                }
                byte[] faceDate1 = getPixelsRGBA(faceImage1);
                byte[] faceDate2 = getPixelsRGBA(faceImage2);
                long timeRecognizeFace = System.currentTimeMillis();
                double similar=mFace.FaceRecognize(faceDate1,faceImage1.getWidth(),faceImage1.getHeight(),
                        faceDate2,faceImage2.getWidth(),faceImage2.getHeight());
                timeRecognizeFace = System.currentTimeMillis() - timeRecognizeFace;
                cmpResult.setText("cosin:"+similar+"\n"+"cmp time:"+timeRecognizeFace);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();

            try {
                if (requestCode == SELECT_IMAGE1) {
                    Bitmap bitmap = decodeUri(selectedImage);
                    Bitmap rgba = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                    yourSelectedImage1 = rgba;
                    imageView1.setImageBitmap(yourSelectedImage1);
                }
                else if (requestCode == SELECT_IMAGE2) {
                    Bitmap bitmap = decodeUri(selectedImage);
                    Bitmap rgba = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                    yourSelectedImage2 = rgba;
                    imageView2.setImageBitmap(yourSelectedImage2);
                }
            } catch (FileNotFoundException e) {
                Log.e("MainActivity", "FileNotFoundException");
                return;
            }
        }
    }


    private Bitmap decodeUri(Uri selectedImage) throws FileNotFoundException {
        // Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o);

        // The new size we want to scale to
        final int REQUIRED_SIZE = 400;

        // Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp / 2 < REQUIRED_SIZE
                    || height_tmp / 2 < REQUIRED_SIZE) {
                break;
            }
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o2);
    }

    //get pixels
    private byte[] getPixelsRGBA(Bitmap image) {
        // calculate how many bytes our image consists of
        int bytes = image.getByteCount();
        ByteBuffer buffer = ByteBuffer.allocate(bytes); // Create a new buffer
        image.copyPixelsToBuffer(buffer); // Move the byte data to the buffer
        byte[] temp = buffer.array(); // Get the underlying array containing the

        return temp;
    }

    private void copyBigDataToSD(String strOutFileName) throws IOException {
        Log.i(TAG, "start copy file " + strOutFileName);
        File sdDir = Environment.getExternalStorageDirectory();//get directory
        File file = new File(sdDir.toString()+"/facem/");
        if (!file.exists()) {
            file.mkdir();
        }

        String tmpFile = sdDir.toString()+"/facem/" + strOutFileName;
        File f = new File(tmpFile);
        if (f.exists()) {
            Log.i(TAG, "file exists " + strOutFileName);
            return;
        }
        InputStream myInput;
        java.io.OutputStream myOutput = new FileOutputStream(sdDir.toString()+"/facem/"+ strOutFileName);
        myInput = this.getAssets().open(strOutFileName);
        byte[] buffer = new byte[1024];
        int length = myInput.read(buffer);
        while (length > 0) {
            myOutput.write(buffer, 0, length);
            length = myInput.read(buffer);
        }
        myOutput.flush();
        myInput.close();
        myOutput.close();
        Log.i(TAG, "end copy file " + strOutFileName);

    }

}
