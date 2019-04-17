package me.iwf.photopicker.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import android.widget.Button;
import me.iwf.photopicker.PhotoPickerActivity;
import me.iwf.photopicker.R;
import me.iwf.photopicker.adapter.PhotoPagerAdapter;
import me.iwf.photopicker.utils.SdCardHelper;

/**
 * Image Pager Fragment
 * Created by donglua on 15/6/21.
 */
public class ImagePagerFragment extends Fragment {

  public final static String ARG_PATH = "PATHS";

  public final static String ARG_CURRENT_ITEM = "ARG_CURRENT_ITEM";
  public final static String SHOW_CROP_IMAGE_BUTTON = "SHOW_CROP_IMAGE_BUTTON";//显示裁减按钮

  public static final int PHOTO_REQUEST_CUT = 2;//裁剪结束

  private ArrayList<String> paths;

  private ViewPager mViewPager;

  private PhotoPagerAdapter mPagerAdapter;

  private Button bt_crop;

  private int currentItem = 0;


  public static String FILE_SAVEPATH = SdCardHelper.getExternalSdCardPath() + "/ImagePicker/Portrait/";

  public Uri uritempFile;

  private String protraitPath;//裁剪后的绝对地址 （有文件目录）

  private boolean showCropImage=false;

  public static ImagePagerFragment newInstance(List<String> paths, int currentItem,boolean showCropImage) {
    ImagePagerFragment f = new ImagePagerFragment();

    Bundle args = new Bundle();
    args.putStringArray(ARG_PATH, paths.toArray(new String[paths.size()]));
    args.putInt(ARG_CURRENT_ITEM, currentItem);
    args.putBoolean(SHOW_CROP_IMAGE_BUTTON, showCropImage);

    f.setArguments(args);

    return f;
  }
  public static ImagePagerFragment newInstance(List<String> paths, int currentItem) {
    return newInstance(paths,currentItem,false);
  }

  @Override
  public void onResume() {
    super.onResume();
    if (getActivity() instanceof PhotoPickerActivity) {
      PhotoPickerActivity photoPickerActivity = (PhotoPickerActivity) getActivity();
      photoPickerActivity.updateTitleDoneItem();
    }
  }

  public void setPhotos(List<String> paths, int currentItem) {
    this.paths.clear();
    this.paths.addAll(paths);
    this.currentItem = currentItem;

    mViewPager.setCurrentItem(currentItem);
    mViewPager.getAdapter().notifyDataSetChanged();
  }


  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    paths = new ArrayList<>();

    Bundle bundle = getArguments();

    if (bundle != null) {
      String[] pathArr = bundle.getStringArray(ARG_PATH);
      paths.clear();
      if (pathArr != null) {

        paths = new ArrayList<>(Arrays.asList(pathArr));
      }

      currentItem = bundle.getInt(ARG_CURRENT_ITEM);
      showCropImage=bundle.getBoolean(SHOW_CROP_IMAGE_BUTTON,false);
    }

    mPagerAdapter = new PhotoPagerAdapter(Glide.with(this), paths);
  }


  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    View rootView = inflater.inflate(R.layout.__picker_picker_fragment_image_pager, container, false);

    mViewPager = rootView.findViewById(R.id.vp_photos);
    mViewPager.setAdapter(mPagerAdapter);
    mViewPager.setCurrentItem(currentItem);
    mViewPager.setOffscreenPageLimit(5);

    bt_crop = rootView.findViewById(R.id.bt_image_crop);
    bt_crop.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {

        String currentPath = paths.get(getCurrentItem());
        createUriTempFile();
        crop(createUri(currentPath));
        System.currentTimeMillis();
      }
    });
    if(!showCropImage){
      bt_crop.setVisibility(View.GONE);
    }

    return rootView;
  }


  public ViewPager getViewPager() {
    return mViewPager;
  }


  public ArrayList<String> getPaths() {
    return paths;
  }


  public ArrayList<String> getCurrentPath() {
    ArrayList<String> list = new ArrayList<>();
    int position = mViewPager.getCurrentItem();
    if (paths != null && paths.size() > position) {
      list.add(paths.get(position));
    }
    return list;
  }


  public int getCurrentItem() {
    return mViewPager.getCurrentItem();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();

    paths.clear();
    paths = null;

    if (mViewPager != null) {
      mViewPager.setAdapter(null);
    }
  }

  /**
   * 初始化要裁剪保存的file
   */
  public void createUriTempFile() {
    String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
    String cropFileName = "picker_crop_" + timeStamp + ".jpg"; //裁剪的图片地址
    protraitPath = FILE_SAVEPATH + cropFileName;
    File photoOutputFile = SdCardHelper.createFile(protraitPath);
    uritempFile = Uri.fromFile(photoOutputFile);
  }

  private Uri createUri(String path){
    Uri uri;
    File photoOutputFile = SdCardHelper.createFile(path);
    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      String authority = getContext().getApplicationInfo().packageName + ".provider";
      uri = FileProvider.getUriForFile(getContext().getApplicationContext(), authority, photoOutputFile);
    } else {
      uri = Uri.fromFile(photoOutputFile);
    }
    return uri;
  }

  /**
   * @param uri
   * 裁剪
   */
  public void crop(Uri uri) {

    // 裁剪图片意图
    Intent intent = new Intent("com.android.camera.action.CROP");
    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//7.0权限
    intent.setDataAndType(uri, "image/*");
    intent.putExtra("crop", "true");
//    // 裁剪框的比例，1：1  //不设置 可以自由选择裁减框大小
//    intent.putExtra("aspectX", 1);
//    intent.putExtra("aspectY", 1);
//    // 裁剪后输出图片的尺寸大小
//    intent.putExtra("outputX", 250);
//    intent.putExtra("outputY", 250);

    intent.putExtra("outputFormat", "JPEG");// 图片格式
    intent.putExtra("noFaceDetection", true);// 取消人脸识别
    intent.putExtra("return-data", true);
    // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_CUT
    intent.putExtra(MediaStore.EXTRA_OUTPUT, uritempFile);
    intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
    getActivity().startActivityForResult(intent, PHOTO_REQUEST_CUT);
  }
}
