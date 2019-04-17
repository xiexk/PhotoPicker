package me.iwf.photopicker.adapter;

import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import me.iwf.photopicker.entity.Photo;
import me.iwf.photopicker.entity.PhotoDirectory;
import me.iwf.photopicker.event.Selectable;

public abstract class SelectableAdapter<VH extends RecyclerView.ViewHolder>
    extends RecyclerView.Adapter<VH> implements Selectable {

  private static final String TAG = SelectableAdapter.class.getSimpleName();

  protected List<PhotoDirectory> photoDirectories;
  protected List<String> selectedPhotos;

  public int currentDirectoryIndex = 0;


  public SelectableAdapter() {
    photoDirectories = new ArrayList<>();
    selectedPhotos = new ArrayList<>();
  }

  /**
   * 修改某个下标的图片地址
   * @param path 新图片地址
   * @param index 下标
   */
  public void changeData(String path,int index){
    Photo photo= getCurrentPhotos().get(index);
    if (selectedPhotos.contains(photo.getPath())) {
      selectedPhotos.remove(photo.getPath());
    }
   photo.setPath(path);
   selectedPhotos.add(path);
    notifyDataSetChanged();
  }


  /**
   * Indicates if the item at position where is selected
   *
   * @param photo Photo of the item to check
   * @return true if the item is selected, false otherwise
   */
  @Override public boolean isSelected(Photo photo) {
    return getSelectedPhotos().contains(photo.getPath());
  }

  /**
   * Toggle the selection status of the item at a given position
   *
   * @param photo Photo of the item to toggle the selection status for
   */
  @Override public void toggleSelection(Photo photo) {
    if (selectedPhotos.contains(photo.getPath())) {
      selectedPhotos.remove(photo.getPath());
    } else {
      selectedPhotos.add(photo.getPath());
    }
  }


  /**
   * Clear the selection status for all items
   */
  @Override public void clearSelection() {
    selectedPhotos.clear();
  }


  /**
   * Count the selected items
   *
   * @return Selected items count
   */
  @Override public int getSelectedItemCount() {
    return selectedPhotos.size();
  }


  public void setCurrentDirectoryIndex(int currentDirectoryIndex) {
    this.currentDirectoryIndex = currentDirectoryIndex;
  }


  public List<Photo> getCurrentPhotos() {
    if (photoDirectories.size() <= currentDirectoryIndex) {
      currentDirectoryIndex = photoDirectories.size() - 1;
    }
    return photoDirectories.get(currentDirectoryIndex).getPhotos();
  }


  public List<String> getCurrentPhotoPaths() {
    List<String> currentPhotoPaths = new ArrayList<>(getCurrentPhotos().size());
    for (Photo photo : getCurrentPhotos()) {
      currentPhotoPaths.add(photo.getPath());
    }
    return currentPhotoPaths;
  }


  public List<String> getSelectedPhotos() {
    return selectedPhotos;
  }

}