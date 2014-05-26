package com.example.driverstation;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import com.example.driverstation.Joystick.OnChangeListener;

import android.R.color;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnGenericMotionListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;
import android.widget.ToggleButton;

public class UIManager 
{
	public boolean enabled = false;
	public boolean auto = false;
	public byte joy1X = 0;
	public byte joy1Y = 0;
	public byte joy2X = 0;
	public byte joy2Y = 0;
	public boolean[] buttons = new boolean[8];
	
	private Joystick joystick1;
	private Joystick joystick2;
	private ToggleButton enableBttn;
	private RadioButton enableAuto;
	private ViewGroup buttonsGroup;
	private View mainView;
	private Thread test = new Thread(){
		public void run(){
			URL url;
			try {
				 url = new URL("http://10.26.57.11/jpg/image.jpg");
				 InputStream in = url.openStream();
				 Drawable drw = Drawable.createFromStream(in, "image.jpg");
				 mainView.setBackgroundDrawable(drw);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};
	
	
	public UIManager(Activity activity){
		
		//Initialize UI components.
		joystick1 = (Joystick)activity.findViewById(R.id.joystick1);
		joystick2 = (Joystick)activity.findViewById(R.id.joystick2);
		enableBttn = (ToggleButton)activity.findViewById(R.id.enable_button);
		enableAuto = (RadioButton)activity.findViewById(R.id.run_autonomous);
		mainView = (View)activity.findViewById(R.id.control_layout);
		
		
		//Set event listeners
		joystick1.setOnChangeListener(joyListener1);
		joystick2.setOnChangeListener(joyListener2);
		enableBttn.setOnCheckedChangeListener(enableListener);
		enableAuto.setOnCheckedChangeListener(autoListener);
		mainView.setOnGenericMotionListener(phyJoystickListener);
		mainView.setOnKeyListener(phyButtonListener);
			//Don't need tele listener because auto listener takes off auto
				//mode and puts in tele.
		
		//Set up the buttons with a loop.
		buttonsGroup = (ViewGroup)activity.findViewById(R.id.buttons);
		for(int i = 0; i < buttonsGroup.getChildCount(); i++)
		{
			final int x = i;
			//Add listeners to the buttons.
			final Button bttn = (Button)buttonsGroup.getChildAt(i);
			
			bttn.setOnTouchListener(new OnTouchListener(){

				@Override
				public boolean onTouch(View view, MotionEvent event) {
					switch(event.getAction())
					{
					case MotionEvent.ACTION_DOWN:
						buttons[x] = true;
						bttn.setBackgroundColor(Color.argb(0x50, 0xe6, 0xe6, 0xe6));
						break;
					case MotionEvent.ACTION_UP:
						buttons[x] = false;
						bttn.setBackgroundColor(Color.argb(0x50, 0x60, 0x60, 0x60));
						break;
					}
					return false;
				}
			});
		}
	}
	//-----------------------Event Listeners------------------------------------------
	
	//Joystick1
	OnChangeListener joyListener1 = new OnChangeListener()
	{

		@Override
		public boolean onChange(byte xAxis, byte yAxis) {
			joy1X = xAxis;
			joy1Y = yAxis;
			return false;
		}
	};
	
	//Joystick2
	OnChangeListener joyListener2 = new OnChangeListener()
	{

		@Override
		public boolean onChange(byte xAxis, byte yAxis) {
			joy2X = xAxis;
			joy2Y = yAxis;
			return false;
		}
	};
	
	//Enable button
	OnCheckedChangeListener enableListener = new OnCheckedChangeListener(){

		@Override
		public void onCheckedChanged(CompoundButton button, boolean value) {
			if(value)
			{
				enabled = true;
				button.setTextColor(Color.GREEN);
			}else{
				enabled = false;
				button.setTextColor(Color.RED);
			}
		}
	};
	
	//Autonimous radio
	OnCheckedChangeListener autoListener = new OnCheckedChangeListener(){

		@Override
		public void onCheckedChanged(CompoundButton button, boolean value) {
			//Disable robot when changing modes.
			enabled = false;
			enableBttn.setChecked(false);
			if(value)
				auto = true;
			else
				auto = false;
		}

	};
	
	//Listener for physical gamepad or controller without buttons.
	OnGenericMotionListener phyJoystickListener = 
			new OnGenericMotionListener(){

				@Override
				public boolean onGenericMotion(View view, MotionEvent event) {
					if(event.getSource() == InputDevice.SOURCE_JOYSTICK &&
							event.getAction() == MotionEvent.ACTION_MOVE){
						
						//Joystick 1 on the controller
						joy1X = (byte) (127*event.getAxisValue(MotionEvent.AXIS_X));
						joy1Y = (byte) (127*event.getAxisValue(MotionEvent.AXIS_Y));
						
						//Joystick 2
						joy2X = (byte) (127*event.getAxisValue(MotionEvent.AXIS_Z));
						joy2Y = (byte) (127*event.getAxisValue(MotionEvent.AXIS_RZ));
					}
					return true;
				}
		
	};
	
	//Physical button listener.
	OnKeyListener phyButtonListener = new OnKeyListener(){

		@Override
		public boolean onKey(View view, int keyCode, KeyEvent event) {
			if(event.getAction() == MotionEvent.ACTION_DOWN)
			{
				switch(keyCode)
				{
				case KeyEvent.KEYCODE_BUTTON_1:
					buttons[0] = true;
					break;
				case KeyEvent.KEYCODE_BUTTON_2:
					buttons[1] = true;
					break;
				case KeyEvent.KEYCODE_BUTTON_3:
					buttons[2] = true;
					break;
				case KeyEvent.KEYCODE_BUTTON_4:
					buttons[3] = true;
					break;
				case KeyEvent.KEYCODE_BUTTON_5:
					buttons[4] = true;
					break;
				case KeyEvent.KEYCODE_BUTTON_6:
					buttons[5] = true;
					break;
				case KeyEvent.KEYCODE_BUTTON_7:
					buttons[6] = true;
					break;
				case KeyEvent.KEYCODE_BUTTON_8:
					buttons[7] = true;
				}
			}
			else if(event.getAction() == MotionEvent.ACTION_UP)
			{
				switch(keyCode)
				{
				case KeyEvent.KEYCODE_BUTTON_1:
					buttons[0] = false;
					break;
				case KeyEvent.KEYCODE_BUTTON_2:
					buttons[1] = false;
					break;
				case KeyEvent.KEYCODE_BUTTON_3:
					buttons[2] = false;
					break;
				case KeyEvent.KEYCODE_BUTTON_4:
					buttons[3] = false;
					break;
				case KeyEvent.KEYCODE_BUTTON_5:
					buttons[4] = false;
					break;
				case KeyEvent.KEYCODE_BUTTON_6:
					buttons[5] = false;
					break;
				case KeyEvent.KEYCODE_BUTTON_7:
					buttons[6] = false;
					break;
				case KeyEvent.KEYCODE_BUTTON_8:
					buttons[7] = false;
				}
			}
			return true;
		}
	};
}

