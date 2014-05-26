package com.example.driverstation;

import android.os.Bundle;
import android.app.Activity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class MainActivity extends Activity {
	UIManager uiManager;
	PacketSender sender;
	VideoManager video;
	Switch videoSwitch;
	MainActivity thisActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.control_layout);
		thisActivity = this;
	}

	@Override
	protected void onResume(){
		super.onResume();
		uiManager = new UIManager(this);
		sender = new PacketSender(this, uiManager);
		sender.start();
		video = new VideoManager(this,60);
		video.start();
	}
	
	@Override
	protected void onPause(){
		super.onPause();
		sender.interrupt();
		sender = null;
		video.interrupt();
		video = null;
	}
	
}


