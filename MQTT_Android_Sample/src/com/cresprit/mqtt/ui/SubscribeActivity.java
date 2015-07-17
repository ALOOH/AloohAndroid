package com.cresprit.mqtt.ui;
import java.util.ArrayList;

import org.eclipse.paho.client.mqttv3.MqttException;

import com.cresprit.mqtt.R;
import com.cresprit.mqtt.service.MQTTService;
import com.cresprit.mqtt.manager.DeviceListManager;
import com.cresprit.mqtt.manager.MessageManager;
import com.cresprit.mqtt_sdk.ISubscribeListener;
import com.cresprit.mqtt_sdk.MQTTSDK;
import com.cresprit.mqtt_sdk.MQTTSDK.DeviceInfo;
import com.cresprit.mqtt_sdk.MQTTSDK.KeyInfo;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.DataSetObserver;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class SubscribeActivity extends Activity {
	EditText edtFeed;
	Button btnSubscribe;
	String feed;
	MessageAdapter adapter;
	ListView tvMessages;
	int mSelectedDevice;
	Intent intent = null;
	MessageManager mgr = null;
	private DeviceInfo deviceInfo=null;
    ArrayList<KeyInfo> KeyList = null;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.subscribe);
		
		tvMessages = (ListView)findViewById(R.id.listMessages);
		adapter = new MessageAdapter(SubscribeActivity.this);
		tvMessages.setAdapter(adapter);
		//adapter.registerDataSetObserver(observer);
			
		registerReceiver(mBroadcastReceiver, new IntentFilter(MessageManager.MSG_RECEIVE));
		
		intent = new Intent(SubscribeActivity.this, MQTTService.class);
		intent.putExtra(DeviceListManager.SELECT_DEVICE, mSelectedDevice);
        startService(intent);
	}
	
	final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			Log.i("", "received Broadcast");
			String message = (String)intent.getCharSequenceExtra("message");
			mgr.getMessages().add(message);
			adapter.notifyDataSetChanged();
			tvMessages.setSelection(mgr.getMessages().size());
		}
	};
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		//MQTTSDK.Disconnect();
		stopService(intent);
		unregisterReceiver(mBroadcastReceiver);

		super.onBackPressed();
	}


	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		

		super.onDestroy();
	}

			
	DataSetObserver observer = new DataSetObserver()
	{

		@Override
		public void onChanged() {
			// TODO Auto-generated method stub
			Log.i("","onChanged");
			super.onChanged();
		}

		@Override
		public void onInvalidated() {
			// TODO Auto-generated method stub
			Log.i("","onInvalidated");
			super.onInvalidated();
		}
		
	};

}