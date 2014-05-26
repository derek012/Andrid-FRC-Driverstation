package com.example.driverstation;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import android.graphics.drawable.Drawable;
import android.view.View;

public class VideoManager extends Thread{
	private URL url;
	private Drawable draw;
	private InputStream is;
	private MainActivity activity;
	private View mainView;
	private int fps = 30;
	
	public VideoManager(MainActivity activity, int fps){
		this.activity = activity;
		mainView = this.activity.findViewById(R.id.control_layout);
		this.fps = fps;
	}
	
	public void run(){;
		while(true){
			
			try {
				 url = new URL("http://10.26.57.11/jpg/image.jpg");
				 is = url.openStream();
				 draw = Drawable.createFromStream(is, "image.jpg");
				 
				 activity.runOnUiThread(new Runnable(){
					@Override
					public void run() {
						mainView.setBackgroundDrawable(draw);	//Only UI thread can access
					} 											//layout views.
				 });
				 
				 is.close();
				 Thread.sleep(1000/fps);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
