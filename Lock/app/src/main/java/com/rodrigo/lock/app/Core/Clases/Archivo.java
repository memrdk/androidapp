package com.rodrigo.lock.app.Core.Clases;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import com.rodrigo.lock.app.Core.Utils.Utils;

import java.io.File;

/**
 * Created by Rodrigo on 28/09/2014.
 */
public class Archivo {

    File file;
    FileType tipo;

    public Archivo(Context ctx, Uri uri) {
        String path = getPath(ctx,uri );
        this.file = new File(path);

        String extension = Utils.getExtensionFile(file.getName());
        if(isEncExtension(extension)){
            tipo = FileType.OpenPBX;
        } else if (isExtensionImage(extension)){
            tipo = FileType.Imagen;
        } else if (isExtensionVideo(extension)){
            tipo = FileType.Video;
        } else {
            tipo = FileType.Otro;
        }

    }

    public Archivo(File f) {
        this.file = f;

        String extension = Utils.getExtensionFile(file.getName());
        if(isEncExtension(extension)){
            tipo = FileType.OpenPBX;
        } else if (isExtensionImage(extension)){
            tipo = FileType.Imagen;
        } else if (isExtensionVideo(extension)){
            tipo = FileType.Video;
        } else {
            tipo = FileType.Otro;
        }

    }

    public File getFile() {
        return file;
    }

    public FileType getTipo() {
        return tipo;
    }


    public boolean soyMultimedia(){
        return  ( tipo ==FileType.Imagen || tipo == FileType.Video);
    }


    public static boolean isExtensionImage(String extension) {
        return (extension.equals("jpg") || extension.equals("jpeg") || extension.equals("jpe") || extension.equals("bmp") || extension.equals("webp")
                || extension.equals("png")
                || extension.equals("gif"));
    }

    public static boolean isExtensionVideo(String extension) {
        return (extension.equals("3gp") || extension.equals("mp4") || extension.equals("ts") || extension.equals("webm") || extension.equals("mkv") );
    }


    public  static boolean isEncExtension(String extension) {
        return  extension.equals("pbx");
    }

    /////////////////////////////////

    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // LocalStorageProvider
            if (isLocalStorageDocument(uri)) {
                // The path is the id
                return DocumentsContract.getDocumentId(uri);
            }
            // ExternalStorageProvider
            else if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }


    public static boolean isLocalStorageDocument(Uri uri) {
        //return LocalStorageProvider.AUTHORITY.equals(uri.getAuthority());
        return false;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }


    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


}
