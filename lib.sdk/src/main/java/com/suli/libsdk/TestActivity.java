package com.suli.libsdk;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

/**
 * Created by suli690 on 2017/6/29.
 */

public class TestActivity extends Activity {
  private final static String TAG = TestActivity.class.getSimpleName();

  private LocationWrapper locationWrapper;

  private boolean mBound = false;
  private Messenger mService;
  private Messenger mGetReplyMessenger = new Messenger(new MessengerHandler());

  private static class MessengerHandler extends Handler {
    @Override public void handleMessage(Message msg) {
      switch (msg.what) {
        case Constant.MSG_FROM_SERVICE:
          Log.i(TAG, "receive msg from Service:" + msg.getData().getString("reply"));
          break;
        default:
          super.handleMessage(msg);
      }
    }
  }

  private ServiceConnection mConnection = new ServiceConnection() {
    public void onServiceConnected(ComponentName className, IBinder service) {
      mService = new Messenger(service);
      mBound = true;
      Log.d(TAG, "bind service");

      sendMessage("Hello, this is client.");
    }

    public void onServiceDisconnected(ComponentName className) {
      mBound = false;
      mService = null;
      Log.d(TAG, "service disconnected");
    }
  };

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.layout_test);
    TextView tvHello = (TextView) findViewById(R.id.tv_hello);
    tvHello.setText(new HelloWrapper().sayHi());

    locationWrapper = new LocationWrapper(this);

    Intent intent = new Intent(this, TargetService.class);
    bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    Log.i(TAG, "onCreate-> bind service");
  }

  @Override protected void onDestroy() {
    unbindService(mConnection);
    super.onDestroy();
    Log.i(TAG, "onDestroy-> unbind service");
  }

  public void onStartLocation(View v) {
    locationWrapper.start();
  }

  public void onSendMessage(View v) {
    sendMessage("on send click!");
  }

  private void sendMessage(String message) {
    if (!mBound) {
      return;
    }
    Message msg = Message.obtain(null, Constant.MSG_FROM_CLIENT);
    Bundle bundle = new Bundle();
    bundle.putString("msg", message);
    msg.setData(bundle);
    msg.replyTo = mGetReplyMessenger;
    try {
      mService.send(msg);
    } catch (RemoteException e) {
      e.printStackTrace();
    }
  }
}
