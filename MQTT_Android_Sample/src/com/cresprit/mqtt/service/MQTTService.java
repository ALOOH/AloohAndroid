package com.cresprit.mqtt.service;

import java.util.ArrayList;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.cresprit.mqtt.manager.DeviceListManager;
import com.cresprit.mqtt.manager.MessageManager;
import com.cresprit.mqtt_sdk.ISubscribeListener;
import com.cresprit.mqtt_sdk.MQTTSDK.DeviceInfo;
import com.cresprit.mqtt_sdk.MQTTSDK.KeyInfo;
import com.cresprit.mqtt_sdk.MQTTSDK;

public class MQTTService extends Service {


    /* In a real application, you should get an Unique Client ID of the device and use this, see
    http://android-developers.blogspot.de/2011/03/identifying-app-installations.html */
    public static final String clientId = "android-client";
    
    private int mSelectedDevice=0;
    private DeviceInfo deviceInfo=null;
    ArrayList<KeyInfo> KeyList = null;
        
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onStart(Intent intent, int startId) {
		mSelectedDevice = intent.getIntExtra(DeviceListManager.SELECT_DEVICE, 0);
        deviceInfo = DeviceListManager.getInstance(this).getDeviceList().get(mSelectedDevice);    		
        
        KeyList = MQTTSDK.AloohCloud_GetKeyInfos(deviceInfo.getAuid());
        Log.i("","feedId:"+deviceInfo.getTopicId()+" AccessKey : "+KeyList.get(0).getAccessKey());
        MQTTSDK.AloohMessage_Subscribe(deviceInfo.getTopicId(), KeyList.get(0).getAccessKey(), listener);

        super.onStart(intent, startId);
    }

    @Override
    public void onDestroy() {
    	Log.i("","Service Destory");
       MQTTSDK.AloohMessage_Disconnect();
    }
    
    ISubscribeListener listener = new ISubscribeListener()
    {

		@Override
		public void update(String _message) {
			// TODO Auto-generated method stub
			Log.i("", "reviced Message");

			Intent intentM = new Intent(MessageManager.MSG_RECEIVE);
			intentM.putExtra("message", _message);
			sendBroadcast(intentM);
		}
    	
    };
}
