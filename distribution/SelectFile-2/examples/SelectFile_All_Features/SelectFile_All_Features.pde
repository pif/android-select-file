/* 
 * Reading/Writing files on Android requires runtime permissions. Therefore:
 * 
 * 1. Don't forget to add permissions in `Android > Sketch Permissions` menu.
 * SelectFile library requires:
 * READ_EXTERNAL_STORAGE  - if you want to read files
 * WRITE_EXTERNAL_STORAGE - if you also want to write files
 * 
 * 2. Don't forget to call requestPermission() in your sketch.
 * 
 * You can find more details about permissions here:
 * https://developer.android.com/guide/topics/permissions/requesting.html#normal-dangerous 
 */
 
import select.files.*;

SelectLibrary files;

PImage saveFile;
PImage selectFile;
PImage selectFolder;

boolean grantedRead = false;
boolean grantedWrite = false;

String f = "";
int type = 0;

void setup() {
  size(displayWidth, displayHeight, P2D);

  files = new SelectLibrary(this);

  imageMode(CENTER);
  textAlign(CENTER, TOP);
  textSize(36);
  noStroke();

  saveFile = loadImage("ic_save_black_24dp.png");
  selectFile = loadImage("ic_insert_drive_file_black_24dp.png");
  selectFolder = loadImage("ic_folder_open_black_24dp.png");

  requestPermissions();
}

void requestPermissions() {
  // if you need to read files, request this
  if (!hasPermission("android.permission.READ_EXTERNAL_STORAGE")) {
    requestPermission("android.permission.READ_EXTERNAL_STORAGE", "handleRead");
  }
  // if you need to save files, request this as well 
  if (!hasPermission("android.permission.WRITE_EXTERNAL_STORAGE")) {
    requestPermission("android.permission.WRITE_EXTERNAL_STORAGE", "handleWrite");
  }
}

void handleRead(boolean granted) {
  if (granted) {
    grantedRead = granted;
    println("Granted read permissions.");
  } else {
    println("Does not have permission to read external storage.");
  }
}

void handleWrite(boolean granted) {
  if (granted) {
    grantedWrite = granted;
    println("Granted write permissions.");
  } else {
    println("Does not have permission to write external storage.");
  }
}

void draw() {
  fill(255);
  rect(0, 0, displayWidth, displayHeight);

  int baseLine = displayHeight*3/7;
  image(selectFile, displayWidth/6, baseLine);
  image(selectFolder, displayWidth/2, baseLine);
  image(saveFile, displayWidth*5/6, baseLine);

  fill(0);
  text("select\nfile", displayWidth/6, baseLine + 96);
  text("select\nfolder", displayWidth/2, baseLine + 96);
  text("save\nfile", displayWidth*5/6, baseLine + 96);

  fill(0);
  text(f, displayWidth/2, displayHeight*5/6);
}

void mousePressed() {
  openDialog();
}

void openDialog() { 
  if (!grantedRead || !grantedWrite) {
    requestPermissions();
  }

  float click = mouseX*1./displayWidth;

  if (click < 0.33) {
    // clicked left
    files.selectInput("Select a file to process:", "fileSelected"); 
    type = 0;
  } else if (click < 0.66) {
    // clicked middle
    files.selectFolder("Select a folder to process:", "fileSelected");
    type = 1;
  } else {
    // clicked right
    files.selectOutput("Save the file please:", "fileSelected");
    type = 2;
  }
}

void fileSelected(File selection) {
  if (selection == null) {
    println("Nothing was selected.");
    f = "Selected\nnothing.";
  } else {
    println("User selected: " + selection.getAbsolutePath());
    switch (type) {
    case 0: 
      f = "Selected file:\n"; 
      break;
    case 1: 
      f = "Selected folder:\n"; 
      break;
    case 2: 
      f = "Saved file:\n"; 
      break;
    }

    f = f + selection.getName();
  }
}
