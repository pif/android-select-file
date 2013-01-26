package processing.files;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.ListActivity;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

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

  /*private static final String I_FULL_PATH = "fullPath";
  private static final String I_FILENAME = "fileName";
  private static final String I_TYPE = "fileType";
  private static final int I_TYPE_FOLDER = R.drawable.type_folder;
  private static final int I_TYPE_FILE = R.drawable.type_file;
  private static final int I_TYPE_UP = R.drawable.type_up;*/

  private static final String CURRENT_PATH = "currentPath";

  public static final String EX_PATH = "extraPath";
  public static final String EX_STYLE = "selectStyle";
  public static final String EX_PATH_RESULT = "pathResult";

  //private List<Map<String, Object>> currentFileList = new ArrayList<Map<String, Object>>();
  private String currentPath = "";
  private ArrayAdapter<FileItem> simpleAdapter = null;

  private SelectMode selectMode = null;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(SelectConstants.generateMainActivityViews(this));
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

    selectMode = SelectMode.createSelectMode(getIntent().getIntExtra(EX_STYLE, SelectConstants.SELECT_FILE), this);
    selectMode.updateUI();

    File f = new File(currentPath);
//    simpleAdapter = new SimpleAdapter(this, currentFileList, R.layout.select_file_item, new String[] { I_FILENAME,
//        I_FULL_PATH, I_TYPE }, new int[] { R.id.fileName, R.id.fullPath, R.id.fileType });

    simpleAdapter = new ArrayAdapter<FileItem>(this, android.R.layout.simple_list_item_2, android.R.id.text1) {
      @Override
      public View getView(int position, View convertView, ViewGroup parent) {
        View item = super.getView(position, convertView, parent);
        TextView tv2 = (TextView) item.findViewById(android.R.id.text2);
        FileItem fItem = this.getItem(position);
        tv2.setText(fItem.getFullPath());

        return item;
      }
    };

    updateCurrentList(f);

    setListAdapter(simpleAdapter);
  }

  void updateCurrentList(File f) {
    List<FileItem> newData = getData(f);
    currentPath = f.getAbsolutePath();
    simpleAdapter.clear();
    for (FileItem item : newData) {
      simpleAdapter.add(item);
    }
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

  private List<FileItem> getData(File folder) {
    if (!folder.isDirectory()) {
      return Collections.emptyList();
    }

    // selectMode specifies file-filtering rules
    File[] listFiles = folder.listFiles(selectMode);
    sortData(listFiles);

    List<FileItem> result = new ArrayList<FileItem>();

    // add "Up one level" item
    File parentFolder = folder.getParentFile();
    if (parentFolder != null) {
      result.add(new FileItem("Up", FileType.Up, parentFolder));
    }

    for (int i = 0; i < listFiles.length; i++) {
      File f = listFiles[i];
      FileItem item = new FileItem(
        f.getName(),
        f.isDirectory() ? FileType.Folder : FileType.File,
        f);

      result.add(item);
    }
    return result;
  }

  @Override
  protected void onListItemClick(ListView l, View v, int position, long id) {
    FileItem item = simpleAdapter.getItem(position);

    selectMode.onItemClicked(item.getFile());

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

  private static class FileItem {
    private final String name;
    private final FileType type;
    private final File file;

    public FileItem(String name, FileType type, File file) {
      this.name = name;
      this.type = type;
      this.file = file;
    }

    public String getName() {
      return name;
    }

    public FileType getType() {
      return type;
    }

    public File getFile() {
      return file;
    }

    public String getFullPath() {
      return file.getAbsolutePath();
    }

    @Override
    public String toString() {
      return getName();
    }
  }

  private enum FileType {
    File, Folder, Up
  }
}
