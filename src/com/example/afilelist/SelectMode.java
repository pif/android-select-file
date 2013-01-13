package com.example.afilelist;

import java.io.File;
import java.io.FileFilter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public abstract class SelectMode implements FileFilter {
	
	public static final int OPEN_FILE = 1;
	public static final int OPEN_FOLDER = 2;
	public static final int SAVE_FILE = 4;

	public abstract void onItemClicked(File pathname);
	public abstract boolean accept(File pathname);
	
	abstract boolean isOk(File pathname);
	
	MainActivity activity;
	
	public static SelectMode createSelectMode(int type, MainActivity activity) {
		switch (type) {
		case OPEN_FILE:
			return new OPEN_FILE(activity);
		case OPEN_FOLDER:
			return new OPEN_FOLDER(activity);			
		case SAVE_FILE:
			return new SAVE_FILE(activity);
		default:
			throw new IllegalArgumentException("Only OPEN_FILE, OPEN_FOLDER, SAVE_FILE allowed");			
		}
	}
	
	public void select(File f) {
		if (isOk(f)) {
			sendResult(f);
		} else {
			activity.sayToUser(activity.getString(R.string.warning), activity.getString(R.string.unacceptable, f.getName()));
		}
	}
	
	void sendResult(File f) {
		Intent result = new Intent();
		result.putExtra(MainActivity.EX_PATH_RESULT, f.getAbsolutePath());
		activity.setResult(MainActivity.RESULT_OK, result);
		Toast.makeText(activity, "Selected: " + f.getAbsolutePath(),
				Toast.LENGTH_LONG).show();
		activity.finish();
	}
	
	private static class OPEN_FILE extends SelectMode {
		public OPEN_FILE(MainActivity activity) {
			this.activity = activity;
		}

		@Override
		public boolean isOk(File file) {
			return file.canRead() && file.isFile();
		}

		@Override
		public boolean accept(File pathname) {
			// show all files
			return true;
		}

		@Override
		public void onItemClicked(File f) {
	    	if (f.isDirectory()) {
	    		activity.updateCurrentList(f);
	    	} else {
	    		select(f);
	    	}
		}
		
		@Override
		void updateUI() {}
	}
	
	private static class OPEN_FOLDER extends SelectMode {
		public OPEN_FOLDER(MainActivity activity) {
			this.activity = activity;
		}
		@Override
		public boolean isOk(File file) {
			return file.isDirectory();
		}

		@Override
		public boolean accept(File pathname) {
			// accept folders only
			return pathname.isDirectory();
		}

		@Override
		public void onItemClicked(File f) {
	    	activity.updateCurrentList(f);
	    	// result is selected with the help of "Select Current Folder" button
    	}
		
		@Override
		void updateUI() {
			View controls = activity.findViewById(R.id.controls);
			controls.setVisibility(View.VISIBLE);
			View additionalControls = activity.findViewById(R.id.select_folder);
			additionalControls.setVisibility(View.VISIBLE);
		}
	}
	
	private static class SAVE_FILE extends SelectMode {
		public SAVE_FILE(MainActivity activity) {
			this.activity = activity;
		}
		@Override
		public boolean accept(File pathname) {
			// accept files and folders... everything
			return true;
		}

		@Override
		public boolean isOk(final File file) {
			if (!file.canWrite()) {
				return false;
			}
			if (file.exists()) {
				DialogInterface.OnClickListener yesNoListener = new DialogInterface.OnClickListener() {
				    @Override
				    public void onClick(DialogInterface dialog, int which) {
				        switch (which){
				        case DialogInterface.BUTTON_POSITIVE:
				        	sendResult(file);
				            break;
				        case DialogInterface.BUTTON_NEGATIVE:
				            break;
				        }
				    }
				};
				AlertDialog.Builder builder = new AlertDialog.Builder(activity);
				builder.setMessage(activity.getString(R.string.save_file_overwrite, file.getName()))
						.setPositiveButton(android.R.string.yes, yesNoListener)
						.setNegativeButton(android.R.string.no, yesNoListener).show();
				// true action (sendResult) will be performed in dialog callback on "Yes" click
				return false; 
			}
			
			return true;
		}

		@Override
		public void onItemClicked(File f) {
	    	if (f.isDirectory()) {
	    		activity.updateCurrentList(f);
	    	} else {
	    		EditText editText = (EditText) activity.findViewById(R.id.save_file_name);
	    		editText.setText(f.getName());
	    	}
	    	// result is returned with the help of "Save file" button 
		}
		
		@Override
		void updateUI() {
			View controls = activity.findViewById(R.id.controls);
			controls.setVisibility(View.VISIBLE);
			View additionalControls = activity.findViewById(R.id.controls_save);
			additionalControls.setVisibility(View.VISIBLE);
		}
	}

	abstract void updateUI();

}
