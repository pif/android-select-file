# SelectFile Library for Processing Android Mode

Android has no default open&save file dialogs. 
`SelectFile` [Processing](https://processing.org/) library provides UI dialogs for these methods in Android mode:
* `selectInput()` - select any file
* `selectFolder()` - select any folder
* `selectOutput()` - save file

Tested with Processing 3.0. 

![Select File UI](web/android-select-file.gif?raw=true)

## Installation

1. Go to `Sketches > Import Library > Add Library...`
2. Search for `SelectFile`
3. Click install.

## Usage

1. After you installed the library, check out an example `File > Examples > Contributed Libraries > SelectFile`.
2. Don't forget to add correct READ/WRITE permissions to your sketch. Go to `Android > Sketch Permissions` and choose `READ_EXTERNAL_STORAGE` and `WRITE_EXTERNAL_STORAGE`.

## Building From Source

1. Install `ant`. On Mac OS X: `brew install ant`
2. `cd resources`
3. `ant`
4. `SelectFile` will appear in your Sketchbooks folder. On Mac OS X the default location is `~/Documents/Processing`.

## Using in Pure Android

The code responsible for selecting files does not depend on Processing and can be reused in other Android projects.
Look into `SelectLibrary` class. 

Here's a bit of source code, which shows, how one can use the library from Android:

### MainActivity.java

Activity with three buttons and a text field. When someone accomplishes 
selected action (select file, folder or save file) text field changes 
its text to the selected path.

```java
public class MainActivity extends Activity {

  // define a constant for activity result
  protected static final int PATH_RESULT = 123;

  private TextView tv = null;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    tv = (TextView) findViewById(R.id.textView1);

    Button b1 = (Button) findViewById(R.id.button1);
    Button b2 = (Button) findViewById(R.id.button2);
    Button b3 = (Button) findViewById(R.id.button3);

    b1.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        Intent i = new Intent(MainActivity.this, SelectActivity.class);
        i.putExtra(SelectActivity.EX_PATH, Environment.getExternalStorageDirectory().getAbsolutePath());
        i.putExtra(SelectActivity.EX_STYLE, SelectMode.SELECT_FILE);
        startActivityForResult(i, PATH_RESULT);
      }
    });

    b2.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        Intent i = new Intent(MainActivity.this, SelectActivity.class);
        i.putExtra(SelectActivity.EX_PATH, Environment.getExternalStorageDirectory().getAbsolutePath());
        i.putExtra(SelectActivity.EX_STYLE, SelectMode.SELECT_FOLDER);
        startActivityForResult(i, PATH_RESULT);
      }
    });

    b3.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        Intent i = new Intent(MainActivity.this, SelectActivity.class);
        i.putExtra(SelectActivity.EX_PATH, Environment.getExternalStorageDirectory().getAbsolutePath());
        i.putExtra(SelectActivity.EX_STYLE, SelectMode.SAVE_FILE);
        startActivityForResult(i, PATH_RESULT);
      }
    });

  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == PATH_RESULT && resultCode == RESULT_OK) {
      tv.setText(data.getStringExtra(SelectActivity.EX_PATH_RESULT));
    } else {
      super.onActivityResult(requestCode, resultCode, data);
    }
  }
}
```

### activity_main.xml
Layout for the activity:

```xml
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical" >

    <TextView
        android:id="@+id/textView1"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Selected file path"
        tools:context=".TestActivity" />

    <Button
        android:id="@+id/button1"
        android:layout_width="fill_parent"
        android:layout_height="wrap_content"
        android:text="Open File" />

    <Button
        android:id="@+id/button2"
        android:layout_width="fill_parent"
        android:layout_height="wrap_content"
        android:text="Open Folder" />

    <Button
        android:id="@+id/button3"
        android:layout_width="fill_parent"
        android:layout_height="wrap_content"
        android:text="Save File" />

</LinearLayout>
```


@author ostap.andrusiv

