package com.example.l.mobilefacenet;

/**
 * Created by L on 2018/6/11.
 */

public class Face {

    public native boolean FaceModelInit(String faceDetectionModelPath);

    public native int[] FaceDetect(byte[] imageDate, int imageWidth , int imageHeight, int imageChannel);

    public native boolean FaceModelUnInit();

    public native double FaceRecognize(byte[] faceDate1,int w1,int h1,byte[] faceDate2,int w2,int h2);

    static {
        System.loadLibrary("Face");

    }
}
