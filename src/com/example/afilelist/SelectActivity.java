package com.example.afilelist;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

/**
 * Activity, which provides 
 *  <ul>
 *    <li>select file,</li>
 *    <li>select folder,</li>
 *    <li>save file</li>
 * functionality.
 * <br>
 * <br>
 * Usage:
 * <pre>
 *   // define somewhere
 *   protected static final int PATH_RESULT = 123;
 *   
 *   // ...
 *   // call this:
 *   Intent i = new Intent(TestActivity.this, SelectActivity.class);
 *   i.putExtra(SelectActivity.EX_PATH, Environment.getExternalStorageDirectory().getAbsolutePath());
 *   i.putExtra(SelectActivity.EX_STYLE, SelectMode.OPEN_FILE);
 *   startActivityForResult(i, PATH_RESULT);
 * </pre>
 * @author ostap.andrusiv
 *
 */
public class SelectActivity extends ListActivity {

  private static final String I_FULL_PATH = "fullPath";
  private static final String I_FILENAME = "fileName";
  private static final String I_TYPE = "fileType";
  private static final int I_TYPE_FOLDER = R.drawable.type_folder;
  private static final int I_TYPE_FILE = R.drawable.type_file;
  private static final int I_TYPE_UP = R.drawable.type_up;

  private static final String CURRENT_PATH = "currentPath";

  public static final String EX_PATH = "extraPath";
  public static final String EX_STYLE = "selectStyle";
  public static final String EX_PATH_RESULT = "pathResult";

  private List<Map<String, Object>> currentFileList = new ArrayList<Map<String, Object>>();
  private String currentPath = "";
  private SimpleAdapter simpleAdapter = null;

  private SelectMode selectMode = null;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    setResult(RESULT_CANCELED);

    currentPath = getIntent().getStringExtra(EX_PATH);
    if (currentPath == null) {
      currentPath = Environment.getExternalStorageDirectory().getAbsolutePath();
    }
    if (savedInstanceState != null) {
      String savedPath = savedInstanceState.getString(CURRENT_PATH);
      if (savedPath != null) {
        currentPath = savedPath;
      }
    }
    
    selectMode = SelectMode.createSelectMode(getIntent().getIntExtra(EX_STYLE, SelectMode.OPEN_FILE), this);
    selectMode.updateUI();

    File f = new File(currentPath);
    simpleAdapter = new SimpleAdapter(this, currentFileList, R.layout.item, new String[] { I_FILENAME,
        I_FULL_PATH, I_TYPE }, new int[] { R.id.fileName, R.id.fullPath, R.id.fileType });

    updateCurrentList(f);

    setListAdapter(simpleAdapter);
  }

  void updateCurrentList(File f) {
    List<Map<String, Object>> newData = getData(f);
    currentPath = f.getAbsolutePath();
    currentFileList.clear();
    currentFileList.addAll(newData);
    simpleAdapter.notifyDataSetChanged();
  }

  private static final Comparator<File> sorter = new Comparator<File>() {
    @Override
    public int compare(File lhs, File rhs) {
      // file or folder
      int lhsType = lhs.isDirectory() ? 0 : 1;
      int rhsType = rhs.isDirectory() ? 0 : 1;
      if (lhsType != rhsType) {
        return lhsType - rhsType;
      }
      return lhs.getName().compareToIgnoreCase(rhs.getName());
    }
  };

  private void sortData(File[] files) {
    Arrays.sort(files, sorter);
  }

  private List<Map<String, Object>> getData(File folder) {
    if (!folder.isDirectory()) {
      return Collections.emptyList();
    }

    // selectMode specifies file-filtering rules
    File[] listFiles = folder.listFiles(selectMode);
    sortData(listFiles);

    List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

    // add "Up one level" item
    File parentFolder = folder.getParentFile();
    if (parentFolder != null) {
      Map<String, Object> up = new HashMap<String, Object>();
      up.put(I_FILENAME, "Up");
      up.put(I_TYPE, I_TYPE_UP);
      up.put(I_FULL_PATH, parentFolder.getAbsolutePath());

      result.add(up);
    }

    for (int i = 0; i < listFiles.length; i++) {
      File f = listFiles[i];
      Map<String, Object> item = new HashMap<String, Object>();
      item.put(I_FILENAME, f.getName());
      item.put(I_TYPE, f.isDirectory() ? I_TYPE_FOLDER : I_TYPE_FILE);
      item.put(I_FULL_PATH, f.getAbsolutePath());

      result.add(item);
    }
    return result;
  }

  @Override
  protected void onListItemClick(ListView l, View v, int position, long id) {
    Map<String, Object> item = (Map<String, Object>) simpleAdapter.getItem(position);

    String path = (String) item.get(I_FULL_PATH);
    File f = new File(path);
    selectMode.onItemClicked(f);

    super.onListItemClick(l, v, position, id);
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if ((keyCode == KeyEvent.KEYCODE_BACK)) {
      File parentFile = new File(currentPath).getParentFile();
      if (parentFile == null) {
        // finita la comedia
        return super.onKeyDown(keyCode, event);
      } else {
        updateCurrentList(parentFile);
      }
      return true;

    } else {
      return super.onKeyDown(keyCode, event);
    }
  }

  public String getCurrentPath() {
    return currentPath;
  }
  
  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putString(CURRENT_PATH, currentPath);
  }
}
