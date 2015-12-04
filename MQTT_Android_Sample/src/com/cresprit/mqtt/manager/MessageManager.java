package com.cresprit.mqtt.manager;

import android.content.Context;

import java.lang.String;
import java.util.ArrayList;



import com.cresprit.mqtt.ui.MessageAdapter;
import com.cresprit.mqtt_sdk.ISubscribeListener;

public class MessageManager{

	private static ArrayList<String> list;
	private static ArrayList<String> date;
	public static String MSG_RECEIVE = "com.cresprit.mqtt.ConnectionMgr.receive_message";
	public static String MSG_TEMPERATURE = "com.cresprit.mqtt.MessageManager.Temp";
	private static MessageAdapter adapter;	private Context mContext= null;
	ISubscribeListener _listener = null;
	
	public  MessageManager(ISubscribeListener _listener)
	{
		if(list == null)
			list = new ArrayList();
		if(date == null)
			date = new ArrayList();
		
		_listener = _listener;
	}

	public  MessageManager()
	{
		if(list == null)
			list = new ArrayList();
		if(date == null)
			date = new ArrayList();
		

	}
	public static ArrayList<String> getMessages()
	{
		return list;
	}
	
	public static ArrayList<String> getDates()
	{
		return date;
	}
	
	public static MessageAdapter getAdapter()
	{
		return adapter;
	}
	
	public static void setAdapter(MessageAdapter _adapter)
	{
		adapter = _adapter;
	}
	
	public ISubscribeListener getListener()
	{
		return _listener;
	}
}