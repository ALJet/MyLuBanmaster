package indi.aljet.myluban_master.example;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;

import java.io.File;

public class PathUtils {
    public final static String SDCARD_MNT = "/mnt/sdcard";
    public final static String SDCARD = Environment
            .getExternalStorageDirectory().getPath();

    public static String getSDCardPath(){
        return Environment.getExternalStorageDirectory()
                .getPath();
    }

    public static String getAbsolutePathFromNoStandardUri
            (Uri mUri){
        String filePath = null;
        String mUriString = mUri.toString();
        mUriString = Uri.decode(mUriString);

        String pre1 = "file://" + SDCARD + File.separator;
        String pre2 = "file://" + SDCARD_MNT + File.separator;

        if(mUriString.startsWith(pre1)){
            filePath = Environment.getExternalStorageDirectory()
                    .getPath() + File.separator + mUriString
                    .substring(pre1.length());
        }else if(mUriString.startsWith(pre2)){
            filePath = Environment.getExternalStorageDirectory()
                    .getPath() + File.separator +
                    mUriString.substring(pre2.length());
        }
        return filePath;
    }


    public static String getAbsoluteUriPath(Context c,
                                            Uri uri){
        String imgPath = "";
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = new CursorLoader(c,uri,proj,null,
                null,null).loadInBackground();
        if(cursor != null){
            int column_index = cursor.getColumnIndexOrThrow
                    (MediaStore.Images.Media.DATA);
            if(cursor.getCount() > 0 && cursor.moveToFirst()){
                imgPath = cursor.getString(column_index);
            }
        }
        return imgPath;
    }


    public static File getExternalCacheDir(Context context){
        if(hasExternalCacheDir()){
            return context.getExternalCacheDir();
        }
        final String chacheDir = "/Android/data/" + context
                .getPackageName() + "/cache/";
        return new File(Environment.getExternalStorageDirectory()
        .getPath()+ chacheDir);
    }


    public static File findOrCreateDir(File parent,String dirName){
        File directory = new File(parent,dirName);
        if(!directory.exists()){
            directory.mkdirs();
        }
        return directory;
    }


    private static boolean hasExternalCacheDir(){
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES
                .FROYO;
    }


    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getPath(final Context context,
                                 final Uri uri){
        final boolean isKitKat = Build.VERSION.SDK_INT >=
                Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if(isKitKat && DocumentsContract.isDocumentUri(context,
                uri)){
            if(isExternalStorageDocument(uri)){
                final String docId = DocumentsContract
                        .getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {

                    return Environment.getExternalStorageDirectory()
                            + "/"+ split[1];
                }
            }else if(isDownloadsDocument(uri)){
                final String id = DocumentsContract
                        .getDocumentId(uri);
                final Uri contentUri = ContentUris
                        .withAppendedId(Uri.parse
                                ("content://downloads/public_downloads"),
                                Long.valueOf(id));
                return getDataColumn(context,contentUri,null,
                        null);
            }else if(isMediaDocument(uri)){
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri = null;
                switch (type){
                    case "image":
                        contentUri = MediaStore.Images.Media
                                .EXTERNAL_CONTENT_URI;
                        break;
                    case "video":
                        contentUri = MediaStore.Video.Media
                                .EXTERNAL_CONTENT_URI;
                        break;
                    case "audio":
                        contentUri = MediaStore.Audio.Media
                                .EXTERNAL_CONTENT_URI;
                        break;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[]
                        {split[1]};
                return getDataColumn(context,contentUri,
                        selection,selectionArgs);
            }
        }else if("content".equalsIgnoreCase(uri.getScheme())){
            if(isGooglePhotosUri(uri)){
                return uri.getLastPathSegment();
            }
            return getDataColumn(context,uri,null,null);
        }else if("file".equalsIgnoreCase(uri.getScheme())){
            return uri.getPath();
        }
        return null;
    }


    public static String getDataColumn(Context context,
                                      Uri uri,String selection,
                                      String[] selectionArgs){
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try{
            cursor = context.getContentResolver()
                    .query(uri,projection,selection,
                            selectionArgs,null);
            if(cursor != null && cursor.moveToFirst()){
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(cursor != null)
                cursor.close();
        }
        return null;
    }


    public static boolean isExternalStorageDocument(Uri uri){
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri){
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri){
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }


    public static boolean isGooglePhotosUri(Uri uri){
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }
}
