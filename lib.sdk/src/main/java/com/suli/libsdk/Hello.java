package com.suli.libsdk;

/**
 * Created by suli690 on 2017/5/25.
 * SDK原始类
 */

public class Hello {

  static {
    System.loadLibrary("sdk-lib");
  }

  public String sayHi() {
    return "From Hello Class, " + sayHello();
  }

  private native String sayHello();
}
