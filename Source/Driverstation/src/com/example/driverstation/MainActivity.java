package com.example.driverstation;

import android.os.Bundle;
import android.app.Activity;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends Activity {
	UIManager uiManager;
	PacketSender sender;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.control_layout);
	}

	@Override
	protected void onResume(){
		super.onResume();
		uiManager = new UIManager(this);
		sender = new PacketSender(this, uiManager);
		sender.start();
	}
	
	@Override
	protected void onPause(){
		super.onPause();
		sender.interrupt();
		sender = null;
	}
}


