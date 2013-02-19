package processing.files;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import processing.core.PApplet;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Dialog, which provides
 *  <ul>
 *    <li>select file,</li>
 *    <li>select folder,</li>
 *    <li>save file</li>
 * functionality.
 * <br>
 * <br>
 * Usage:
 * {@see FileLibrary}
 *  
 * @author ostap.andrusiv
 *
 */
public class SelectDialog extends Dialog {

  private static final String CURRENT_PATH = "currentPath";

  public static final String EX_PATH = "extraPath";
  public static final String EX_STYLE = "selectStyle";
  public static final String EX_PATH_RESULT = "pathResult";
  public static final String EX_CALLBACK = "selectCallback";
  public static final String EX_TITLE = "selectTitle";


  private String currentPath = "";
  private ArrayAdapter<FileItem> simpleAdapter = null;

  private SelectMode selectMode = null;
  private final Intent intent;

  private ListView listView = null;

  public SelectDialog(PApplet context, Intent intent) {
    super(context);
    this.intent = intent;
  }
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(SelectConstants.generateMainActivityViews(getContext()));
    listView = (ListView) findViewById(android.R.id.list);
    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView parent, View v, int position, long id) {
        onListItemClick((ListView) parent, v, position, id);
      }
      
    });

    setTitle(getIntent().getStringExtra(EX_TITLE));
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

    selectMode = SelectMode.createSelectMode(getIntent().getIntExtra(EX_STYLE, SelectMode.SELECT_FILE), this);
    selectMode.updateUI();

    File f = new File(currentPath);

    simpleAdapter = new ArrayAdapter<FileItem>(getContext(), android.R.layout.simple_list_item_2, android.R.id.text1) {
      @Override
      public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        
        FileItem fItem = this.getItem(position);

        view.setBackgroundColor(fItem.getType().getColor());
        TextView tv1 = (TextView) view.findViewById(android.R.id.text1);
        TextView tv2 = (TextView) view.findViewById(android.R.id.text2);
        tv2.setText(fItem.getFullPath());
        
        tv1.setTextColor(Color.BLACK);
        tv2.setTextColor(Color.BLACK);
        

        return view;
      }
    };

    updateCurrentList(f);

    setListAdapter(simpleAdapter);
  }

  private void setListAdapter(ArrayAdapter<FileItem> simpleAdapter) {
    listView.setAdapter(simpleAdapter);
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

  /**
   * 1. directories first
   * 2. dirs/files are sorted ignoring case
   */
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
      result.add(new FileItem(SelectConstants.fs_up_item, FileType.Up, parentFolder));
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

  protected void onListItemClick(ListView l, View v, int position, long id) {
    FileItem item = simpleAdapter.getItem(position);

    selectMode.onItemClicked(item.getFile());
  }

  /**
   * TODO: Probably, should be moved inside the {@link SelectLibrary#selectImpl(String, String, File, int)} method.
   * @param file
   */
  protected void onFileSelected(File file, Intent intent) {
    if (file != null) {
      String callbackMethod = intent.getStringExtra(SelectDialog.EX_CALLBACK);
      selectCallback(file, callbackMethod, getContext());
    }
  }

  static private void selectCallback(File selectedFile, String callbackMethod, Object callbackObject) {
    try {
      Class<?> callbackClass = callbackObject.getClass();
      Method selectMethod = callbackClass.getMethod(callbackMethod, new Class[] { File.class });
      selectMethod.invoke(callbackObject, new Object[] { selectedFile });

    } catch (IllegalAccessException iae) {
      System.err.println(callbackMethod + "() must be public");

    } catch (InvocationTargetException ite) {
      ite.printStackTrace();

    } catch (NoSuchMethodException nsme) {
      System.err.println(callbackMethod + "() could not be found");
    }
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if ((keyCode == KeyEvent.KEYCODE_BACK)) {
      File parentFile = new File(currentPath).getParentFile();
      if (parentFile == null) {
        // finita la comedia: returning to the calling activity
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
  public Bundle onSaveInstanceState() {
    Bundle outState = super.onSaveInstanceState();
    outState.putString(CURRENT_PATH, currentPath);
    return outState;
  }
  
  public Intent getIntent() {
    return intent;
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
    File(SelectConstants.COLOR_FILE), 
    Folder(SelectConstants.COLOR_FOLDER),
    Up(SelectConstants.COLOR_UP);
    
    private final int color;

    FileType(int color) {
      this.color = color;
    }
    
    public int getColor() {
      return color;
    }
    
  }
}
