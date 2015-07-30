package com.jerry.compressionimage;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;


public class MainActivity extends ActionBarActivity {
    private static final String TAG = "MainActivity";
    private String path;//图片全路径

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        new Thread(){
            @Override
            public void run() {

              /*  File f = new File(Environment.getExternalStorageDirectory() + "/DCIM/","aa.png");
                if (f.exists()) {
                    // f.delete();
                    f = new File(Environment.getExternalStorageDirectory() + "/DCIM/","bb.png");*/
                String aa = Environment.getExternalStorageDirectory() + "/DCIM/aa.png";
                String aamd5 = Util.fileMD5(aa);
                String bb = Environment.getExternalStorageDirectory() + "/DCIM/bb.png";
                String bbmd5 = Util.fileMD5(bb);
                Log.d(TAG,"aa:"+aamd5);
                Log.d(TAG,"bb:"+bbmd5);
                Log.d(TAG,"eq:"+aamd5.equals(bbmd5));
            }
        }.start();

    }


    public void getPath(View v) {
        /*Intent intent = new Intent();
                *//* 开启Pictures画面Type设定为image *//*
        intent.setType("image*//*");
                *//* 使用Intent.ACTION_GET_CONTENT这个Action *//*
        intent.setAction(Intent.ACTION_GET_CONTENT);
                *//* 取得相片后返回本画面 *//*
        startActivityForResult(intent, 1);*/

        //String srcPath = "//mnt//sdcard//DCIM//Camera//IMG_20130101_000120634.jpg";


        String path = Environment.getExternalStorageDirectory() + "/DCIM//Camera//w3.jpg";
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
       // newOpts.inPreferredConfig = Bitmap.Config.ARGB_4444;
        //newOpts.inPurgeable = true;
        //newOpts.inInputShareable = true;

        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(path, newOpts);//此时返回bm为空,为了防止oom

       // int height = newOpts.outHeight * 200 / newOpts.outWidth;

        //newOpts.outWidth = 200;
       // newOpts.outHeight = height;
/* 这样才能真正的返回一个Bitmap给你 */
        newOpts.inJustDecodeBounds = false;
        Bitmap bmp = BitmapFactory.decodeFile(path, newOpts);
        //image.setImageBitmap(bmp);
       // saveImage(bmp);
        imageZoom(bmp,path);
        //newOpts.inJustDecodeBounds = false;
       /* Log.d(TAG, "path = " + path);

        if (bmp != null) {
            // Log.d(TAG, "原始 = " +  bitmap.getRowBytes() * bitmap.getHeight());
            Bitmap bitmap1 = compressImage(bmp);
            Log.d(TAG, "压缩后 = " + bitmap1.getByteCount());
        } else {
            Log.d(TAG, "bitmap为空");
        }*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Uri uri = data.getData();
            Log.e("uri", uri.toString());
            ContentResolver cr = this.getContentResolver();
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));


                String[] proj = {MediaStore.Images.Media.DATA};
                Cursor cursor = managedQuery(uri, proj, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                path = cursor.getString(column_index);// 图片在的路径
                Log.d(TAG, "path = " + path);

                Log.d(TAG, "原始 = " + bitmap.getByteCount());
                Bitmap bitmap1 = compressImage(bitmap);


                ImageView imageView = (ImageView) findViewById(R.id.iv_01);
                /* 将Bitmap设定到ImageView */
                imageView.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                Log.e("Exception", e.getMessage(), e);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);

    }


    private Bitmap compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) {    //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
           /* if (options < 10)
                break;*//*

        }*/
        }
        //saveBitmap(image);
        Log.d(TAG, "压缩后 = " + baos.toByteArray().length);
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片

        //保存图片
        return bitmap;
    }



    /**
     * 保存方法
     */
    public void saveBitmap(Bitmap bm) {
        Log.e(TAG, "保存图片");
        File f = new File(Environment.getExternalStorageDirectory() + "/DCIM/","aa.png");
        if (f.exists()) {
           // f.delete();
          f = new File(Environment.getExternalStorageDirectory() + "/DCIM/","bb.png");
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.PNG, 0, out);
            out.flush();
            out.close();
            Log.i(TAG, "已经保存");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /**
     * 图像压缩并保存到本地
     * 返回处理过的图片
     *
     */


    public Bitmap saveImage( Bitmap bit) {
        File file = new File(Environment.getExternalStorageDirectory() + "/DCIM/", new Date().getTime()+".png");
       // File file = new File(fileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            ByteArrayOutputStream stream = new  ByteArrayOutputStream();
            stream.reset();
            bit.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            Log.d(TAG, "压缩前 = " + stream.toByteArray().length);
            int temp = 20;
            while (stream.toByteArray().length > 102400) {
                stream.reset();
                bit.compress(Bitmap.CompressFormat.JPEG, temp, stream);
                temp--;
                if (temp == 0){
                    Log.d(TAG, "已压缩到最小");
                    break;
                }
            }

            Log.d(TAG, "压缩后 = " + stream.toByteArray().length);
            // 70 是压缩率，表示压缩30%; 如果不压缩是100，表示压缩率为0

            ByteArrayInputStream isBm = new ByteArrayInputStream(stream.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
            Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片

            Bitmap bitmap1 = zoomImg(bitmap, (100 - temp) / 100, (100 - temp) / 100);

            FileOutputStream os = new FileOutputStream(file);
           // bitmap1.
            os.write(stream.toByteArray());
            os.close();
            return bit;
        } catch (Exception e) {
            file = null;
            return null;
        }
    }

    /**
     *  处理图片
     * @param bm 所要转换的bitmap
     * @param newWidth 新的宽
     * @param newHeight 新的高
     * @return 指定宽高的bitmap
     */
    public static Bitmap zoomImg(Bitmap bm, int newWidth ,int newHeight){
        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(newWidth, newHeight);
        // 得到新的图片
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return newbm;
    }




    //不多说直接上代码，代码中在做仔细解释：
    private void imageZoom(Bitmap bitMap,String filePath) {
        File file = new File(filePath);
        long mid = file.length()/1024;
        Log.d(TAG, "mid = " + mid);

        //图片允许最大空间   单位：KB
        double maxSize =100.00;
        //将bitmap放至数组中，意在bitmap的大小（与实际读取的原文件要大）
       // ByteArrayOutputStream baos = new ByteArrayOutputStream();
       // bitMap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        //byte[] b = baos.toByteArray();
        //将字节换成KB
        //double mid = b.length/1024;
       // Log.d(TAG, "mid = " + mid);
        //判断bitmap占用空间是否大于允许最大空间  如果大于则压缩 小于则不压缩
        if (mid > maxSize) {
            //获取bitmap大小 是允许最大大小的多少倍
            double i = mid / maxSize;
            Log.d(TAG, "i = " + i);
            //开始压缩  此处用到平方根 将宽带和高度压缩掉对应的平方根倍 （1.保持刻度和高度和原bitmap比率一致，压缩后也达到了最大大小占用空间的大小）
            Bitmap bit = zoomImage(bitMap, Math.sqrt(mid)/Math.sqrt(maxSize),Math.sqrt(mid)/Math.sqrt(maxSize));
            saveBitmap(bit);
        }
    }



    /***
     * 图片的缩放方法
     *
     * @param bgimage
     *            ：源图片资源
     * @param newWidth
     *            ：缩放后宽度
     * @param newHeight
     *            ：缩放后高度
     * @return
     */
    public static Bitmap zoomImage(Bitmap bgimage, double newWidth,double newHeight) {
        // 获取这个图片的宽和高
        float width = bgimage.getWidth();
        float height = bgimage.getHeight();
        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();
        // 计算宽高缩放率
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 缩放图片动作
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width,
                (int) height, matrix, true);
        return bitmap;
    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
