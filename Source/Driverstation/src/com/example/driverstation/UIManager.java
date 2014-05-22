package com.example.driverstation;

import com.example.driverstation.Joystick.OnChangeListener;

import android.R.color;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.InputDevice;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnGenericMotionListener;
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
						bttn.setBackgroundColor(Color.argb(0x88, 0xe6, 0xe6, 0xe6));
						break;
					case MotionEvent.ACTION_UP:
						buttons[x] = false;
						bttn.setBackgroundColor(Color.argb(0x88, 0x60, 0x60, 0x60));
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
	OnGenericMotionListener phyControllerListener = 
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
}

