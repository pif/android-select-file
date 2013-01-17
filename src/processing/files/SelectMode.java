package processing.files;

import java.io.File;
import java.io.FileFilter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public abstract class SelectMode implements FileFilter {

  public static final int SELECT_FILE = 1;
  public static final int SELECT_FOLDER = 2;
  public static final int SAVE_FILE = 4;

  /**
   * Initialises custom UI elements for the selector.
   */
  abstract void updateUI();

  /**
   * Checks, if the proposed file can be selected.
   * @param pathname file to check.
   * @return ACCEPTABLE, if file is ok.
   * DONT_NOTIFY if no action should be performed,
   * or any other R.string.fs_* resource id, in case of problems.
   */
  abstract int isOk(File pathname);

  /**
   * this method get's called when ListItem from the file list is clicked. 
   * @param pathname
   */
  abstract void onItemClickedImpl(File pathname);

  SelectActivity activity;

  /**
   * Create an instance of {@link SelectMode} for the specific activity.
   * @param type
   * @param activity
   * @return selectMode
   */
  static SelectMode createSelectMode(int type, SelectActivity activity) {
    switch (type) {
    case SELECT_FILE:
      return new OPEN_FILE(activity);
    case SELECT_FOLDER:
      return new OPEN_FOLDER(activity);
    case SAVE_FILE:
      return new SAVE_FILE(activity);
    default:
      throw new IllegalArgumentException("Only OPEN_FILE, OPEN_FOLDER, SAVE_FILE allowed");
    }
  }

  private static final int ACCEPTABLE = 0;
  private static final int DONT_NOTIFY = R.string.fs_do_nothing;

  /**
   * This method is called from the bound activity, when result should be selected.
   * @param f
   */
  public void selectResult(File f) {
    int isOkMessage = isOk(f);
    if (isOkMessage == DONT_NOTIFY) {
      // do nothing
    } else if (isOkMessage == ACCEPTABLE) {
      sendResult(f);
    } else {
      sayToUser(R.string.fs_warning, isOkMessage, f.getName());
    }
  }

  public void onItemClicked(File pathname) {
    if (!pathname.canRead()) {
      sayToUser(R.string.fs_warning, R.string.fs_cant_read, pathname.getName());
    } else {
      onItemClickedImpl(pathname);
    }
  }

  void sendResult(File f) {
    Intent result = new Intent();
    result.putExtra(SelectActivity.EX_PATH_RESULT, f.getAbsolutePath());
    activity.setResult(SelectActivity.RESULT_OK, result);
    //Toast.makeText(activity, "Selected: " + f.getAbsolutePath(), Toast.LENGTH_LONG).show();
    activity.finish();
  }

  void sayToUser(int title, int message, Object... params) {
    AlertDialog dialog = new AlertDialog.Builder(activity)
      .setTitle(activity.getString(title))
      .setMessage(activity.getString(message, params))
      .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int id) {
        }
      }).create();
    dialog.show();
  }

  private static class OPEN_FILE extends SelectMode {
    public OPEN_FILE(SelectActivity activity) {
      this.activity = activity;
    }

    @Override
    public int isOk(File file) {
      return (file.canRead() && file.isFile()) ? ACCEPTABLE : R.string.fs_unacceptable;
    }

    @Override
    public boolean accept(File pathname) {
      // show all files
      return true;
    }

    @Override
    void onItemClickedImpl(File f) {
      if (f.isDirectory()) {
        activity.updateCurrentList(f);
      } else {
        selectResult(f);
      }
    }

    @Override
    void updateUI() {
    }
  }

  private static class OPEN_FOLDER extends SelectMode {
    public OPEN_FOLDER(SelectActivity activity) {
      this.activity = activity;
    }

    @Override
    public int isOk(File file) {
      return file.isDirectory() ? ACCEPTABLE : R.string.fs_unacceptable;
    }

    @Override
    public boolean accept(File pathname) {
      // accept folders only
      return pathname.isDirectory();
    }

    @Override
    void onItemClickedImpl(File f) {
      activity.updateCurrentList(f);
      // result is selected with the help of "Select Current Folder" button
    }

    @Override
    void updateUI() {
      Button selectFolder = (Button) activity.findViewById(R.id.select_folder);
      selectFolder.setOnClickListener(new View.OnClickListener() {
        public void onClick(View v) {
          OPEN_FOLDER.this.selectResult(new File(activity.getCurrentPath()));
        }
      });

      View controls = activity.findViewById(R.id.controls);
      controls.setVisibility(View.VISIBLE);
      View additionalControls = activity.findViewById(R.id.select_folder);
      additionalControls.setVisibility(View.VISIBLE);
    }
  }

  private static class SAVE_FILE extends SelectMode {
    public SAVE_FILE(SelectActivity activity) {
      this.activity = activity;
    }

    @Override
    public boolean accept(File pathname) {
      // accept files and folders... everything
      return true;
    }

    @Override
    public int isOk(final File file) {
      if (!file.getParentFile().canWrite()) {
        return R.string.fs_cant_write_parent_dir;
      }
      if (!file.exists()) {
        return ACCEPTABLE;
      } else {
        DialogInterface.OnClickListener yesNoListener = new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
              sendResult(file);
              break;
            case DialogInterface.BUTTON_NEGATIVE:
              break;
            }
          }
        };
        AlertDialog dialog = new AlertDialog.Builder(activity).setTitle(activity.getString(R.string.fs_warning))
            .setMessage(activity.getString(R.string.fs_save_file_overwrite, file.getName()))
            .setPositiveButton(android.R.string.yes, yesNoListener)
            .setNegativeButton(android.R.string.no, yesNoListener).create();
        dialog.show();
        return DONT_NOTIFY;
      }
    }

    @Override
    void onItemClickedImpl(File f) {
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
      final EditText fileName = (EditText) activity.findViewById(R.id.save_file_name);
      Button createFile = (Button) activity.findViewById(R.id.save_file);
      createFile.setOnClickListener(new View.OnClickListener() {
        public void onClick(View v) {
          File path = new File(activity.getCurrentPath());
          File newFile = new File(path, fileName.getText().toString());
          SAVE_FILE.this.selectResult(newFile);
        }
      });

      View controls = activity.findViewById(R.id.controls);
      controls.setVisibility(View.VISIBLE);
      View additionalControls = activity.findViewById(R.id.controls_save);
      additionalControls.setVisibility(View.VISIBLE);
    }
  }
}
