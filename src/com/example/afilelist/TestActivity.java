package com.example.afilelist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class TestActivity extends Activity {

  protected static final int PATH_RESULT = 123;

  private TextView tv = null;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_test);

    tv = (TextView) findViewById(R.id.textView1);

    Button b1 = (Button) findViewById(R.id.button1);
    Button b2 = (Button) findViewById(R.id.button2);
    Button b3 = (Button) findViewById(R.id.button3);

    b1.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        Intent i = new Intent(TestActivity.this, SelectActivity.class);
        i.putExtra(SelectActivity.EX_PATH, Environment.getExternalStorageDirectory().getAbsolutePath());
        i.putExtra(SelectActivity.EX_STYLE, SelectMode.OPEN_FILE);
        startActivityForResult(i, PATH_RESULT);
      }
    });

    b2.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        Intent i = new Intent(TestActivity.this, SelectActivity.class);
        i.putExtra(SelectActivity.EX_PATH, Environment.getExternalStorageDirectory().getAbsolutePath());
        i.putExtra(SelectActivity.EX_STYLE, SelectMode.OPEN_FOLDER);
        startActivityForResult(i, PATH_RESULT);
      }
    });

    b3.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        Intent i = new Intent(TestActivity.this, SelectActivity.class);
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
