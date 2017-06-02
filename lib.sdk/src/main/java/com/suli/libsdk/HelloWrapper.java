package com.suli.libsdk;

import com.suli.libbase.ISayHi;

/**
 * Created by suli690 on 2017/5/25.
 */

public class HelloWrapper implements ISayHi {

  private Hello hello;

  public HelloWrapper() {
    this.hello = new Hello();
  }

  @Override public String sayHi() {
    return "From Hi Class, " + hello.sayHi();
  }
}
