package com.unclekong.ebookdemo;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.view.View;

@SuppressLint("ViewConstructor")
public class Splash extends View implements Runnable {
    boolean isLoop = false;
    int NumCount = 0;
    StarActivey activiy;
    Bitmap backBitmap = null;
    Bitmap newBitmap = null;
    Paint p = new Paint();
    float sw, sh;
    Matrix matrix = new Matrix();
    Bitmap newBitmap2;
    //�����Ǵ������ͼƬ��,���硰������Щ�¶����������֣��������6
    Bitmap frames[] = new Bitmap[3];
    Bitmap newFrames[] = new Bitmap[3];
    int[] alf = new int[4];
    int btween;
    int scrWidth, scrHeight;

    //www.javaapk.com
    public Splash(StarActivey activey) {
        super(activey);
        this.activiy = activey;
        scrWidth = activiy.scrWidth;
        scrHeight = activiy.scrHeight;
        init();

    }

    public void init() {
        backBitmap = getBitmap(R.drawable.splash);
        frames[0] = getBitmap(R.drawable.t1);
        frames[1] = getBitmap(R.drawable.t2);
        frames[2] = getBitmap(R.drawable.t3);
        sw = ((float) scrWidth) / backBitmap.getWidth();
        sh = ((float) scrHeight) / backBitmap.getHeight();
        matrix.postScale(sw, sh);
        newBitmap = Bitmap.createBitmap(backBitmap, 0, 0,
                backBitmap.getWidth(), backBitmap.getHeight(), matrix, true);

        isLoop = true;
        start();

    }

    public void logic() {

        if (NumCount <= 10) {
            newBitmap2 = null;
            newBitmap2 = alfImage(newBitmap, NumCount);
        }

        if (NumCount <= 20 && NumCount > 10) {
            newFrames[0] = null;
            newFrames[0] = alfImage(frames[0], NumCount - 10);
        }
        if (NumCount <= 30 && NumCount > 20) {
            newFrames[1] = null;
            newFrames[1] = alfImage(frames[1], NumCount - 20);
        }
        if (NumCount <= 40 && NumCount > 30) {
            newFrames[2] = null;
            newFrames[2] = alfImage(frames[2], NumCount - 30);
        }
        if (NumCount >= 50) {
            activiy.myHandler.sendEmptyMessage(1);
        }

    }

    public void exit() {
        isLoop = false;
        backBitmap = null;
        newBitmap = null;
        newBitmap2 = null;
        newFrames = null;
        frames = null;
    }

    public void start() {
        Thread thread = new Thread(this);
        thread.start();
    }

    public Bitmap getBitmap(int BitmapId) {// ��ȡͼƬ��Դ
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), BitmapId);
        return bitmap;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);
        if (newBitmap2 != null) {
            canvas.drawBitmap(newBitmap2, 0, 0, p);

        }
        int positiony = activiy.scrHeight >> 1 - 100;
        if (newFrames[0] != null) {
            canvas.drawBitmap(newFrames[0], activiy.scrWidth - 100, positiony,
                    p);
        }
        if (newFrames[1] != null) {
            canvas.drawBitmap(newFrames[1], activiy.scrWidth - 100,
                    positiony + 100, p);
        }
        if (newFrames[2] != null) {
            canvas.drawBitmap(newFrames[2], activiy.scrWidth - 100,
                    positiony + 200, p);
        }
    }


    public void run() {
        while (isLoop) {
            logic();
            this.postInvalidate();
            NumCount = NumCount + 1;
            try {
                Thread.sleep(80);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    // ��ð�͸��ͼƬ��͸���ȴ�0��10����Ϊ11���ȼ�
    public static final Bitmap alfImage(Bitmap img, int alf) {
        if (img == null) {
            System.out.println("alfImage");
            return null;
        }
        if (alf < 0)
            alf = 0;
        else if (alf > 10)
            alf = 10;
        int imgW = img.getWidth();
        int imgH = img.getHeight();
        int[] RGBData = new int[imgW * imgH];
        img.getPixels(RGBData, 0, imgW, 0, 0, imgW, imgH);
        int tmp = ((alf * 255 / 10) << 24) | 0x00ffffff;
        for (int i = 0; i < RGBData.length; i++)
            RGBData[i] &= tmp;
        Bitmap bitmap = Bitmap.createBitmap(RGBData, imgW, imgH,
                Bitmap.Config.ARGB_8888);
        return bitmap;
    }

}
