package com.suli.dynamicload;

import android.app.Application;
import com.suli.libbase.Utils;

/**
 * Created by suli690 on 2017/5/25.
 */

public class App extends Application {

  @Override public void onCreate() {
    super.onCreate();
    Utils.init(this);
    Constant.init(this);
  }
}
