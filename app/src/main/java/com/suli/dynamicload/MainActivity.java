package com.suli.dynamicload;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.suli.libbase.ILocation;
import com.suli.libbase.L;
import com.tbruyelle.rxpermissions.RxPermissions;
import rx.functions.Action1;

public class MainActivity extends Activity {

  private final static String PERMISSIONS_INIT_LIBS[] = new String[] {
      Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
  };

  private final static String PERMISSIONS_LOCATION[] = new String[] {
      Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,
      Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_EXTERNAL_STORAGE,
      Manifest.permission.WRITE_EXTERNAL_STORAGE
  };

  private RxPermissions rxPermissions;

  @BindView(R.id.tv_text) TextView tvText;
  @BindView(R.id.tv_location) TextView tvLocation;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);
    rxPermissions = new RxPermissions(this);

    printClassLoader();
  }

  @OnClick(R.id.btn_init_libs) public void onClickInitLibs() {
    rxPermissions.request(PERMISSIONS_INIT_LIBS).subscribe(new Action1<Boolean>() {
      @Override public void call(Boolean granted) {
        if (granted) {
          // 模拟下载jar，so库, 初始化SDK
          if (SdkFactory.init(MainActivity.this)) {
            printClassLoader();
            Toast.makeText(MainActivity.this, "Init sdk successfully!", Toast.LENGTH_SHORT).show();
          } else {
            Toast.makeText(MainActivity.this, "Init sdk failed!", Toast.LENGTH_SHORT).show();
          }
        } else {
          Toast.makeText(MainActivity.this, "Request read and write external storage permission!",
              Toast.LENGTH_SHORT).show();
        }
      }
    });
  }

  @OnClick(R.id.btn_say_hello) public void onClickSayHello() {
    //ISayHi iSayHi = SdkFactory.createHello(MainActivity.this);
    Hello hello = new Hello();
    tvText.setText(hello.sayHi());
    Toast.makeText(MainActivity.this, hello.sayHi(), Toast.LENGTH_LONG).show();
  }

  @OnClick(R.id.btn_location) public void onClickLocation() {
    rxPermissions.request(PERMISSIONS_LOCATION).subscribe(new Action1<Boolean>() {
      @Override public void call(Boolean granted) {
        if (granted) {
          ILocation iLocation = SdkFactory.createLocation(MainActivity.this);
          iLocation.start();
        } else {
          Toast.makeText(MainActivity.this, "Request permissions for location sdk!",
              Toast.LENGTH_SHORT).show();
        }
      }
    });
  }

  @OnClick(R.id.btn_start_service) public void onClickStartService() {
    Intent intent = new Intent();
    intent.setComponent(
        new ComponentName("com.suli.dynamicload.sdk", "com.suli.libsdk.TargetService"));
    startService(intent);
  }

  private void printClassLoader() {
    ClassLoader classLoader = getClassLoader();
    if (classLoader != null) {
      int i = 0;
      L.i("[onCreate] classLoader " + i++ + " : " + classLoader.toString());
      while (classLoader.getParent() != null) {
        classLoader = classLoader.getParent();
        L.i("[onCreate] classLoader " + i++ + " : " + classLoader.toString());
      }
    }
  }
}
