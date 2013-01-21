Android Select File
===================

This is Android library project, which provides
 * select file,
 * select folder,
 * save file
functionality.


Usage
-----

Well, here's a bit of source code, which shows, how one can use the library:

#MainActivity.java

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

# activity_main.xml
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
