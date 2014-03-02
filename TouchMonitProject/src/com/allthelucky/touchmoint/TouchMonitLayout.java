package com.allthelucky.touchmoint;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.FrameLayout;

public class TouchMonitLayout extends FrameLayout {
	private ViewTouchMoniter viewTouchMoniter = ViewTouchMoniter.getInstance();
	private final static int CHECK_TOUCH_INTERVAL = 1 * 2 * 1000;
	private final static int NOT_TOUCH_INTERVAL = 1 * 10 * 1000;
	private boolean enable =false;
	
	public TouchMonitLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public TouchMonitLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public TouchMonitLayout(Context context) {
		super(context);
	}
	
	public void resume() {
		enable=true;
	}
	
	public void pause() {
		enable=false;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		Log.d("helllo", "onInterceptTouchEvent");
		try {
			if(enable) {
				viewTouchMoniter.update();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return super.onInterceptTouchEvent(ev);
	}

	private static class ViewTouchMoniter implements Runnable {
		private static ViewTouchMoniter INSTANCE = null;
		private long lastTouchTime = 0;
		private Thread timeTask;

		private ViewTouchMoniter() {
			this.lastTouchTime = System.currentTimeMillis();
			this.timeTask = new Thread(this);
			this.timeTask.start();
		}

		public static ViewTouchMoniter getInstance() {
			if (INSTANCE == null) {
				INSTANCE = new ViewTouchMoniter();

			}
			return INSTANCE;
		}

		public void update() {
			synchronized (TouchMonitLayout.class) {
				lastTouchTime = System.currentTimeMillis();
			}
		}

		@Override
		public void run() {
			while (true) {
				try {
					Thread.sleep(CHECK_TOUCH_INTERVAL);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				synchronized (TouchMonitLayout.class) {
					long time = System.currentTimeMillis();
					if ((time - lastTouchTime) > NOT_TOUCH_INTERVAL) {
						Log.d("tag", "time out!!!!!!!!!!!!!!!!!!!");
					}
				}
			}
		}

	}

}
