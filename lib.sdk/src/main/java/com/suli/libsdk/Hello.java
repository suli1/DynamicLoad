package com.suli.libsdk;

/**
 * Created by suli690 on 2017/5/25.
 * SDK原始类
 */

public class Hello {

  static {
    try {
      System.loadLibrary("sdk-lib");
    } catch (UnsatisfiedLinkError e) {
      e.printStackTrace();
    }
  }

  public String sayHi() {
    String hello;
    try {
      hello = sayHello();
    } catch (UnsatisfiedLinkError e) {
      // ignore
      hello = "";
    }
    return "From Hello Class, " + hello;
  }

  private native String sayHello();
}
