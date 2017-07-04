package com.suli.libsdk;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by suli690 on 2017/6/30.
 */

public class TargetService extends Service {
  private final static String TAG = TargetService.class.getSimpleName();



  private static class MessengerHandler extends Handler {
    @Override public void handleMessage(Message msg) {
      super.handleMessage(msg);
      switch (msg.what) {
        case Constant.MSG_FROM_CLIENT:
          Log.i(TAG, "receive msg from client:" + msg.getData().getString("msg"));
          Messenger client = msg.replyTo;
          Message replyMessage =  Message.obtain(null, Constant.MSG_FROM_SERVICE);
          Bundle bundle = new Bundle();
          bundle.putString("reply", "我已经收到您的消息了,稍后回复!");
          replyMessage.setData(bundle);
          try {
            client.send(msg);
          } catch (RemoteException e) {
            e.printStackTrace();
          }
          break;
        default:
          break;
      }
    }
  }

  private final Messenger mMessenger = new Messenger(new MessengerHandler());

  @Override public void onCreate() {
    super.onCreate();
    Log.i(TAG, "onCreate");
  }

  @Override public void onStart(Intent intent, int startId) {
    super.onStart(intent, startId);
    Log.i(TAG, "onStart");
  }

  @Override public int onStartCommand(Intent intent, int flags, int startId) {
    Log.i(TAG, "onStartCommand");
    return super.onStartCommand(intent, flags, startId);
  }

  @Override public void onDestroy() {
    super.onDestroy();
    Log.i(TAG, "onDestroy");
  }

  @Nullable @Override public IBinder onBind(Intent intent) {
    Log.i(TAG, "onBind");
    return mMessenger.getBinder();
  }
}
