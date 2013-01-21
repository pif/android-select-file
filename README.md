Android Select File
===================

This is Android library project, which provides
 * select file,
 * select folder,
 * save file
functionality.


Usage
-----
```java
// define somewhere a constant for the activity result.
protected static final int PATH_RESULT = 123;

// ...
// Create an Intent:

  Intent i = new Intent(TestActivity.this, SelectActivity.class);
  i.putExtra(SelectActivity.EX_PATH, Environment.getExternalStorageDirectory().getAbsolutePath());
  i.putExtra(SelectActivity.EX_STYLE, SelectMode.OPEN_FILE);
  startActivityForResult(i, PATH_RESULT);
```
@author ostap.andrusiv
