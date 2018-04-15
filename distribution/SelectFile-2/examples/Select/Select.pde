/*
 * This minimalistic sketch shows how to use `selectFile()` on Android.
 * 
 * This sketch requires the READ_EXTERNAL_STORAGE permission to be selected
 * through the Sketch Permissions list in the PDE, and then to use the requestPermission()
 * function in the code, since READ_EXTERNAL_STORAGE is a dangerous permission that must
 * be requested during run-time in Android 23 and newer:
 * https://developer.android.com/guide/topics/permissions/requesting.html#normal-dangerous 
 */

import select.files.*;

SelectLibrary files;

void setup() {
  size(displayWidth, displayHeight);

  files = new SelectLibrary(this);
  if (!hasPermission("android.permission.READ_EXTERNAL_STORAGE")) {
    requestPermission("android.permission.READ_EXTERNAL_STORAGE", "handleRequest");
  }

  // files.selectFolder("Select a folder to process:", "fileSelected");
  // files.selectOutput("Save the file please:", "fileSelected");
}

void handleRequest(boolean granted) {
  if (granted) {
    files.selectInput("Select a file to process:", "fileSelected");
  } else {
    println("Does not have permission to read external storage.");
  }
}

void fileSelected(File selection) {
  if (selection == null) {
    println("Nothing was selected.");
  } else {
    println("User selected " + selection.getAbsolutePath());
  }
}
