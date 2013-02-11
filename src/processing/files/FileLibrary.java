package processing.files;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.content.Intent;
import android.os.Environment;
import processing.core.PApplet;

public class FileLibrary {
  private PApplet parent;
  
  public FileLibrary(PApplet parent) {
    this.parent = parent;
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

    Intent i = new Intent(PApplet.this, SelectActivity.class);
    i.putExtra(SelectActivity.EX_PATH, defaultSelection.getAbsolutePath());
    i.putExtra(SelectActivity.EX_STYLE, mode);
    i.putExtra(SelectActivity.EX_CALLBACK, callbackMethod);
    
    // TODO Amend THIS
    startActivityForResult(i, RESULT_SELECT);
    crap(ololo);
  }

  /**
   * TODO: Probably, should be moved inside the {@link FileLibrary#selectImpl(String, String, File, int)} method.
   * @param file
   */
  private void onFileSelected(File file) {
    if (file != null) {
      String callbackMethod = data.getStringExtra(SelectActivity.EX_CALLBACK);
      selectCallback(file, callbackMethod, parent);
    }
  }
  

  static private void selectCallback(File selectedFile,
                                     String callbackMethod,
                                     Object callbackObject) {
    try {
      Class<?> callbackClass = callbackObject.getClass();
      Method selectMethod =
        callbackClass.getMethod(callbackMethod, new Class[] { File.class });
      selectMethod.invoke(callbackObject, new Object[] { selectedFile });

    } catch (IllegalAccessException iae) {
      System.err.println(callbackMethod + "() must be public");

    } catch (InvocationTargetException ite) {
      ite.printStackTrace();

    } catch (NoSuchMethodException nsme) {
      System.err.println(callbackMethod + "() could not be found");
    }
  }
  
  
  
}
