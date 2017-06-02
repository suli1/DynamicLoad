package com.suli.dynamicload;

import android.content.Context;
import com.suli.libbase.ILocation;
import com.suli.libbase.ISayHi;
import com.suli.libsdk.HelloWrapper;
import com.suli.libsdk.LocationWrapper;

/**
 * Created by suli690 on 2017/5/25.
 */

public class SdkFactory {

  public static boolean init() {
    return true;
  }

  public static ISayHi createHello(Context context) {
    return new HelloWrapper();
  }

  public static ILocation createLocation(Context context) {
    return new LocationWrapper(context);
  }
}
