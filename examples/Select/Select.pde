import select.files.*;

SelectLibrary files;

void setup() {
  size(320, 240);
  
  files = new SelectLibrary(this);
  
  files.selectInput("Select a file to process:", "fileSelected");
  // files.selectFolder("Select a folder to process:", "fileSelected");
  // files.selectOutput("Save the file please:", "fileSelected");
}

void fileSelected(File selection) {
  if (selection == null) {
    println("Nothing was selected.");
  } else {
    println("User selected " + selection.getAbsolutePath());
  }
}
