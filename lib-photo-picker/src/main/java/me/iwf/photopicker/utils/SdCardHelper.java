package me.iwf.photopicker.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

/**
 * Created by Administrator on 2017/2/14.
 * Sd卡工具类
 */

public class SdCardHelper {


  /**
   * 创建文件夹
   */
  public static boolean createFolder(String forder_path) {
    try {
      File destDir_portrait = new File(forder_path);
      if (!destDir_portrait.exists()) {
        destDir_portrait.mkdirs();
      }
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * 创建文件
   */
  public static File createFile(String file_path) {
    try {
      File destDir_portrait = new File(file_path);
      File parent = destDir_portrait.getParentFile();
      if (!parent.exists()) {
        parent.mkdirs();
      }
      if (!destDir_portrait.exists()) {
        destDir_portrait.createNewFile();
      }
      return destDir_portrait;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }


  /**
   * 返回SD卡跟目录
   */
  public static String getSdCardPath() {
    String sdDir = null;
    boolean sdCardExist = Environment.getExternalStorageState().equals(
        Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
    if (sdCardExist) {
      sdDir = getStorage();// 获取根目录
      return sdDir.toString();
    } else {
      return null;
    }
  }

  /**
   * 获取sdkardFile
   * @return null 获取失败
   */
  public static File getSdKardFile() {
    return new File(getExternalSdCardPath());
  }

  /**
   * 获取扩展SD卡存储目录
   * 如果有外接的SD卡，并且已挂载，则返回这个外置SD卡目录 否则：返回内置SD卡目录
   * @return
   */
  public static String getExternalSdCardPath() {
    if (isHavdSdKard()) {
      File sdCardFile = new File(Environment
          .getExternalStorageDirectory().getAbsolutePath());
      return sdCardFile.getAbsolutePath();
    }
    String path = null;
    File sdCardFile = null;
    ArrayList<String> devMountList = getDevMountList();
    for (String devMount : devMountList) {
      File file = new File(devMount);
      if (file.isDirectory() && file.canWrite()) {
        path = file.getAbsolutePath();
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss")
            .format(new Date());
        File testWritable = new File(path, "test_" + timeStamp);
        if (testWritable.mkdirs()) {
          testWritable.delete();
        } else {
          path = null;
        }
      }
    }
    if (path != null) {
      sdCardFile = new File(path);
      return sdCardFile.getAbsolutePath();
    }
    return null;
  }

  /**
   * 遍历 "system/etc/vold.fstab” 文件，获取全部的Android的挂载点信息
   * @return
   */
  private static ArrayList<String> getDevMountList() {
    String[] toSearch = readFile("/etc/vold.fstab").split(" ");
    ArrayList<String> out = new ArrayList<>();
    for (int i = 0; i < toSearch.length; i++) {
      if (toSearch[i].contains("dev_mount")) {
        if (new File(toSearch[i + 2]).exists()) {
          out.add(toSearch[i + 2]);
        }
      }
    }
    return out;
  }

  public static String readFile(String fileName) {
    String output = "";
    File file = new File(fileName);
    if (file.exists()) {
      if (file.isFile()) {
        try {
          BufferedReader input = new BufferedReader(new FileReader(
              file));
          StringBuffer buffer = new StringBuffer();
          String text;
          while ((text = input.readLine()) != null) {
            buffer.append(text + "");
          }
          output = buffer.toString();
        } catch (IOException ioException) {
          ioException.printStackTrace();
        }
      } else if (file.isDirectory()) {
        String[] dir = file.list();
        output += "Directory contents: ";
        for (int i = 0; i < dir.length; i++) {
          output += dir[i] + "";
        }
      }
    }
    return output;
  }


  public static String getFromAssets(Context context, String fileName) {
    try {
      InputStreamReader inputReader = new InputStreamReader(context.getResources().getAssets().open(fileName));
      BufferedReader bufReader = new BufferedReader(inputReader);
      String line = "";
      String Result = "";
      while ((line = bufReader.readLine()) != null) {
        Result += line;
      }
      return Result;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * 判断SD卡是否存在
   */
  public static boolean isHavdSdKard() {
    return Environment.getExternalStorageState().equals(
        Environment.MEDIA_MOUNTED);
  }


  /**
   * 获取根目录
   */
  public static String getStorage() {
    String PATH_BASE = null;
    if (PATH_BASE == null) {
      PATH_BASE = Environment.getExternalStorageDirectory() + "/";
      File file = new File(PATH_BASE);
      if (!file.canWrite()) {
        PATH_BASE = Environment.getExternalStorageDirectory() + "0/";
        file = new File(PATH_BASE);
      }
      if (!file.canWrite()) {
        PATH_BASE = Environment.getExternalStorageDirectory() + "1/";
        file = new File(PATH_BASE);
      }
      if (!file.canWrite()) {
        PATH_BASE = Environment.getExternalStorageDirectory() + "2/";
        file = new File(PATH_BASE);
      }
      if (!file.canWrite()) {
        PATH_BASE = "/sdcard/";
        file = new File(PATH_BASE);
      }
      if (!file.canWrite()) {
        PATH_BASE = "/sdcard0/";
        file = new File(PATH_BASE);
      }
      if (!file.canWrite()) {
        PATH_BASE = "/sdcard1/";
        file = new File(PATH_BASE);
      }
      if (!file.canWrite()) {
        PATH_BASE = "/sdcard2/";
        file = new File(PATH_BASE);
      }
    }

    return PATH_BASE;
  }

  /**
   * 读取文件内容
   *
   */

  public static String readBookContents(File bookFilePath) {
    String all_read_contents = "";
    try {
      FileInputStream fis = new FileInputStream(bookFilePath);
      BufferedReader br = new BufferedReader(new InputStreamReader(fis,
          "utf-8"));
      String read = null;
      while ((read = br.readLine()) != null) {
        all_read_contents += read;
      }
      br.close();
      fis.close();
    } catch (Exception e) {
      // TODO: handle exception
      e.printStackTrace();
    }
    return all_read_contents;
  }


  /**
   * 删除文件夹
   *
   * @return boolean
   */
  public static void delFolder(String folderPath) {
    try {
      delAllFile(folderPath); // 删除完里面所有内容
      String filePath = folderPath;
      filePath = filePath.toString();
      File myFilePath = new File(filePath);
      myFilePath.delete(); // 删除空文件夹
    } catch (Exception e) {
      System.out.println("删除文件夹操作出错");
      e.printStackTrace();
    }
  }

  /**
   * 删除文件夹里面的所有文件
   *
   * @param path String 文件夹路径 如 c:/fqf
   */
  public static void delAllFile(String path) {
    File file = new File(path);
    if (!file.exists()) {
      return;
    }
    if (!file.isDirectory()) {
      return;
    }
    String[] tempList = file.list();
    File temp = null;
    for (int i = 0; i < tempList.length; i++) {
      if (path.endsWith(File.separator)) {
        temp = new File(path + tempList[i]);
      } else {
        temp = new File(path + File.separator + tempList[i]);
      }
      if (temp.isFile()) {
        temp.delete();
      }
      if (temp.isDirectory()) {
        delAllFile(path + "/" + tempList[i]);// 先删除文件夹里面的文件
        delFolder(path + "/" + tempList[i]);// 再删除空文件夹
      }
    }
  }

  /**
   * 根据Uri获取图片绝对路径，解决Android4.4以上版本Uri转换
   */
  @TargetApi(19)
  public static String getImageAbsolutePath(Activity context, Uri imageUri) {
    if (context == null || imageUri == null) {
      return null;
    }
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT && DocumentsContract
        .isDocumentUri(context, imageUri)) {
      if (isExternalStorageDocument(imageUri)) {
        String docId = DocumentsContract.getDocumentId(imageUri);
        String[] split = docId.split(":");
        String type = split[0];
        if ("primary".equalsIgnoreCase(type)) {
          return Environment.getExternalStorageDirectory() + "/" + split[1];
        }
      } else if (isDownloadsDocument(imageUri)) {
        String id = DocumentsContract.getDocumentId(imageUri);
        Uri contentUri = ContentUris
            .withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
        return getDataColumn(context, contentUri, null, null);
      } else if (isMediaDocument(imageUri)) {
        String docId = DocumentsContract.getDocumentId(imageUri);
        String[] split = docId.split(":");
        String type = split[0];
        Uri contentUri = null;
        if ("image".equals(type)) {
          contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        } else if ("video".equals(type)) {
          contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        } else if ("audio".equals(type)) {
          contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        }
        String selection = MediaStore.Images.Media._ID + "=?";
        String[] selectionArgs = new String[] {split[1]};
        return getDataColumn(context, contentUri, selection, selectionArgs);
      }
    } // MediaStore (and general)
    else if ("content".equalsIgnoreCase(imageUri.getScheme())) {
      // Return the remote address
      if (isGooglePhotosUri(imageUri)) {
        return imageUri.getLastPathSegment();
      }
      return getDataColumn(context, imageUri, null, null);
    }
    // File
    else if ("file".equalsIgnoreCase(imageUri.getScheme())) {
      return imageUri.getPath();
    }
    return null;
  }

  public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
    Cursor cursor = null;
    String column = MediaStore.Images.Media.DATA;
    String[] projection = {column};
    try {
      cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
      if (cursor != null && cursor.moveToFirst()) {
        int index = cursor.getColumnIndexOrThrow(column);
        return cursor.getString(index);
      }
    } finally {
      if (cursor != null) {
        cursor.close();
      }
    }
    return null;
  }

  /**
   * @param uri The Uri to check.
   * @return Whether the Uri authority is ExternalStorageProvider.
   */
  public static boolean isExternalStorageDocument(Uri uri) {
    return "com.android.externalstorage.documents".equals(uri.getAuthority());
  }

  /**
   * @param uri The Uri to check.
   * @return Whether the Uri authority is DownloadsProvider.
   */
  public static boolean isDownloadsDocument(Uri uri) {
    return "com.android.providers.downloads.documents".equals(uri.getAuthority());
  }

  /**
   * @param uri The Uri to check.
   * @return Whether the Uri authority is MediaProvider.
   */
  public static boolean isMediaDocument(Uri uri) {
    return "com.android.providers.media.documents".equals(uri.getAuthority());
  }

  /**
   * @param uri The Uri to check.
   * @return Whether the Uri authority is Google Photos.
   */
  public static boolean isGooglePhotosUri(Uri uri) {
    return "com.google.android.apps.photos.content".equals(uri.getAuthority());
  }

  /**
   * 判断文件夹是否存在
   */
  public static boolean hasDirInSDCard(String dirPath, boolean isCreate) {

    File dir = new File(Environment.getExternalStorageDirectory(), dirPath);

    if (!isCreate) {
      // 不用创建
      return dir.exists();
    } else {
      // 已经存在
      if (dir.exists()) {
        return true;
      }

      // 不存在
      String[] dirs = dirPath.split("/");
      for (int i = 0; i < dirs.length; i++) {
        String path = Environment.getExternalStorageDirectory()
            .toString();

        for (int j = 0; j <= i; j++) {
          path += "/" + dirs[j];
        }

        dir = new File(path);
        if (!dir.exists()) {
          dir.mkdir();
        }
      }
      return true;
    }
  }

  /**
   * 判断文件是否存在
   *
   */
  public static boolean hasFile(String filePath) {

    File file = new File(filePath);
    return file.exists();
  }
}
