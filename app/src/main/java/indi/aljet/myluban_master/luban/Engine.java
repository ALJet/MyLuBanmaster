package indi.aljet.myluban_master.luban;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Responsible for starting compress and managing active and cached resources
 * 负责开始压缩和管理活动和缓存资源
 */
public class Engine {

    private InputStreamProvider srcImg;
    private File tagImg;
    private int srcWidth;
    private int srcHeight;
    private boolean focusAlpha;


    Engine(InputStreamProvider srcImg,File tagImg,
           boolean focusAlpha)throws IOException{
        this.srcImg = srcImg;
        this.tagImg = tagImg;
        this.focusAlpha = focusAlpha;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inSampleSize = 1;
        BitmapFactory.decodeStream(srcImg.open(),null,
                options);
        this.srcWidth = options.outWidth;
        this.srcHeight = options.outHeight;
    }

    /**
     * 压缩图片大小算法 自动压缩算法 缩放多少倍
     * @return
     */
    private int computeSize(){
        srcWidth = srcWidth % 2 == 1 ? srcWidth + 1 :
                 srcWidth;
        srcHeight = srcHeight % 2 == 1? srcHeight + 1 :
                srcHeight;
        int longSide = Math.max(srcWidth,srcHeight);
        int shortSide = Math.min(srcWidth,srcHeight);

        float scale = ((float) shortSide / longSide);
        if(scale <= 1 && scale > 0.5625){
            if(longSide < 1664){
                return 1;
            }else if(longSide < 4990){
                return 2;
            }else if(longSide > 4990 && longSide < 10240){
                return 4;
            }else {
                return longSide / 1280 == 0 ?
                        1 : longSide / 1280;
            }
        }else if(scale <= 0.5625 && scale > 0.5){
            return longSide / 1280 == 0 ?
                    1 : longSide / 1280;
        }else{
            return (int) Math.ceil(longSide /
                    (1280.0 / scale));
        }
    }


    private Bitmap rotatingImage(Bitmap bitmap,
                                 int angle){
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(bitmap,
                0,0,bitmap
        .getWidth(),bitmap.getHeight(),matrix
        ,true);
    }


    /**
     * 压缩图片的方法  重点
     * @return
     * @throws IOException
     */
    File compress() throws IOException{
        BitmapFactory.Options  options= new
                BitmapFactory.Options();
        options.inSampleSize = computeSize();
        Bitmap tagBitmap = BitmapFactory.decodeStream
                (srcImg.open(),null,options);
        ByteArrayOutputStream stream = new
                ByteArrayOutputStream();
        if(Checker.SINGLE.isJPG(srcImg.open())){
            tagBitmap = rotatingImage(tagBitmap,
                    Checker.SINGLE.getOrientation(srcImg
                    .open()));
        }
        /**保留原图像60%的品质，压缩40% **/
        tagBitmap.compress(focusAlpha ? Bitmap.CompressFormat
        .PNG : Bitmap.CompressFormat.JPEG,
                60,stream);
        tagBitmap.recycle();

        FileOutputStream fos = new FileOutputStream(tagImg);
        fos.write(stream.toByteArray());
        fos.flush();
        fos.close();
        stream.close();
        return tagImg;
    }

}
