package com.suli.libsdk;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

/**
 * Created by suli690 on 2017/6/29.
 */

public class TestActivity extends Activity {

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.layout_test);
    TextView tvHello = (TextView) findViewById(R.id.tv_hello);
    tvHello.setText(new HelloWrapper().sayHi());
  }
}
