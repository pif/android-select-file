package processing.files;

import java.io.File;

import processing.core.PApplet;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;

public class FileLibrary {
  private Object parent;
  private Context Cparent;
  
  public FileLibrary(Context c, Object parent) {
    this.parent = parent;
    this.Cparent = c;
  }

  /**
   * Open file chooser activity to select a file for input.
   * After the selection is made, the selected File will be passed to the
   * 'callback' function. If the dialog is closed or canceled, null will be
   * sent to the function, so that the program is not waiting for additional
   * input. The callback is necessary because of how android threading works.
   *
   * <pre>
   * void setup() {
   *   selectInput("Select a file to process:", "fileSelected");
   * }
   *
   * void fileSelected(File selection) {
   *   if (selection == null) {
   *     println("Window was closed or the user hit cancel.");
   *   } else {
   *     println("User selected " + fileSeleted.getAbsolutePath());
   *   }
   * }
   * </pre>
   *
   * For advanced users, the method must be 'public', which is true for all
   * methods inside a sketch when run from the PDE, but must explicitly be
   * set when using Eclipse or other development environments.
   *
   * @webref input:files
   * @param prompt message to the user
   * @param callback name of the method to be called when the selection is made
   */
  public void selectInput(String prompt, String callback) {
    selectImpl(prompt, callback, null, SelectMode.SELECT_FILE);
  }

  /**
   * See selectInput() for details.
   *
   * @webref output:files
   * @param prompt message to the user
   * @param callback name of the method to be called when the selection is made
   */
  public void selectOutput(String prompt, String callback) {
    selectImpl(prompt, callback, null, SelectMode.SAVE_FILE);
  }
  
  /**
   * See selectInput() for details.
   *
   * @webref input:files
   * @param prompt message to the user
   * @param callback name of the method to be called when the selection is made
   */
  public void selectFolder(String prompt, String callback) {
    selectImpl(prompt, callback, null, SelectMode.SELECT_FOLDER);
  }

  /**
   * Starts open/save dialog.
   *  
   * @param prompt
   * @param callbackMethod
   * @param defaultSelection
   * @param mode
   */
  protected void selectImpl(final String prompt,
                                   final String callbackMethod,
                                   File defaultSelection,
                                   final int mode) {
    if (defaultSelection == null) {
      defaultSelection = Environment.getExternalStorageDirectory();
    }

    Intent i = new Intent();
    i.putExtra(SelectActivity.EX_PATH, defaultSelection.getAbsolutePath());
    i.putExtra(SelectActivity.EX_STYLE, mode);
    i.putExtra(SelectActivity.EX_CALLBACK, callbackMethod);
    i.putExtra(SelectActivity.EX_TITLE, prompt);
    
    // TODO Amend THIS
    Dialog dlg = new SelectActivity(Cparent, parent, i);
    dlg.show();//startActivityForResult(i, RESULT_SELECT);
  }
}
