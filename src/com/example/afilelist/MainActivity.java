package com.example.afilelist;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class MainActivity extends ListActivity {

	private static final String I_FULL_PATH = "fullPath";
	private static final String I_FILENAME = "fileName";
	private static final String I_TYPE = "fileType";
	private static final int I_TYPE_FOLDER = R.drawable.type_folder;
	private static final int I_TYPE_FILE = R.drawable.type_file;
	private static final int I_TYPE_UP = R.drawable.type_up;
	
	public  static final String EX_PATH = "extraPath";
	public  static final String EX_STYLE = "selectStyle";
	public  static final String EX_PATH_RESULT = "pathResult";
	
	private List<Map<String, Object>> currentFileList = new ArrayList<Map<String,Object>>();
	private String currentPath = "";
	private SimpleAdapter simpleAdapter = null;

	private SelectMode selectMode = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setResult(RESULT_CANCELED);
        
        // TODO get that from extras
//        currentPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        currentPath = getIntent().getStringExtra(EX_PATH);
        selectMode = (SelectMode) getIntent().getSerializableExtra(EX_STYLE);
        
        File f = new File(currentPath);
        simpleAdapter = new SimpleAdapter(getBaseContext(), currentFileList, 
        		R.layout.item, 
        		new String[]{I_FILENAME, I_FULL_PATH, I_TYPE}, 
        		new int[]{R.id.fileName, R.id.fullPath, R.id.fileType});
        
        updateCurrentList(f);
        
        setListAdapter(simpleAdapter);
    }
    
    private void updateCurrentList(File f) {
		List<Map<String, Object>> newData = getData(f);
		currentPath = f.getAbsolutePath();
		currentFileList.clear();
		currentFileList.addAll(newData);
		simpleAdapter.notifyDataSetChanged();
	}

	private void sortData(File[] files) {
		Arrays.sort(files, new Comparator<File>() {
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
			
		});
	}

	private List<Map<String,Object>> getData(File folder) {
    	if (!folder.isDirectory()) {
    		return Collections.emptyList();
    	}
    	
    	File[] listFiles = folder.listFiles(selectMode.getFileFilter()); 
    	// TODO filter files: filterData(listFiles);
    	sortData(listFiles);
    	
    	List<Map<String, Object>> result = new ArrayList<Map<String,Object>>();

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
			item.put(I_TYPE, f.isDirectory()? I_TYPE_FOLDER : I_TYPE_FILE);
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
    	if (!f.canRead()) {
    		sayToUser(getString(R.string.warning), getString(R.string.cant_read, f.getName()));
    	} else {
    		// readeable
	    	if (f.isDirectory()) {
	    		updateCurrentList(f);
	    	} else {
	    		// select smth?
	    		Intent result = new Intent();
	    		result.putExtra(EX_PATH_RESULT, f.getAbsolutePath());
	    		setResult(RESULT_OK, result);
	    		Toast.makeText(this, "Selected: "+f.getAbsolutePath(), Toast.LENGTH_LONG).show();
	    		finish();
	    	}
    	}
    	
    	super.onListItemClick(l, v, position, id);
    }
    
	private void sayToUser(String title, String message) {
		AlertDialog dialog = new AlertDialog.Builder(this)
				.setTitle(title)
				.setMessage(message)
				.setPositiveButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
							}
						}).create();
		dialog.show();
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

}
