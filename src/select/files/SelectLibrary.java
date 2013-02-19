/**
 * ##library.name##
 * ##library.sentence##
 * ##library.url##
 *
 * Copyright ##copyright## ##author##
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General
 * Public License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA  02111-1307  USA
 * 
 * @author      ##author##
 * @modified    ##date##
 * @version     ##library.prettyVersion## (##library.version##)
 */
package select.files;

import java.io.File;

import processing.core.PApplet;
import android.app.Dialog;
import android.content.Intent;
import android.os.Environment;

/**
 * ##library.name## for android allow everyone to port existing sketches with
 * selectInput(), selectOutput(), selectFolder() calls.
 * 
 * @example Select
 * 
 * Library open an android dialog, which, if successful selection was made, calls
 * the appropriate callback method.
 *
 */
public class SelectLibrary {
  private PApplet parent;
  
  public SelectLibrary(PApplet parent) {
    this.parent = parent;
  }

  /**
   * Open file chooser dialog to select a file for input.
   * After the selection is made, the selected File will be passed to the
   * 'callback' function. If the dialog is closed or canceled, null will be
   * sent to the function, so that the program is not waiting for additional
   * input. The callback is necessary because of how android threading works.
   *
   * @example Select
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

    final Intent i = new Intent();
    i.putExtra(SelectDialog.EX_PATH, defaultSelection.getAbsolutePath());
    i.putExtra(SelectDialog.EX_STYLE, mode);
    i.putExtra(SelectDialog.EX_CALLBACK, callbackMethod);
    i.putExtra(SelectDialog.EX_TITLE, prompt);
    
    parent.runOnUiThread(new Runnable() {
        public void run() {
          Dialog dlg = new SelectDialog(parent, i);
          dlg.show();//startActivityForResult(i, RESULT_SELECT);
        }
    });
  }
}
