package com.example.touchpic;


import com.example.touchpic.widget.MyImageView;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

public class MainActivity extends Activity {
	
	private static final int NONE = 0;
	private static final int DRAG = 1;
	private static final int ZOOM = 2;
	
	private int bitmapW = 0;
	private int bitmapH = 0;
	
	private int bitmapChangeW = 0;
	private int bitmapChangeToW = 0;
	private boolean stateIsLock = false;
	
	private int mode = NONE;
	private MyImageView view;
	private float oldDist;
	private Matrix matrix = new Matrix();
	private Matrix savedMatrix = new Matrix();
	private PointF start = new PointF();
	private PointF mid = new PointF();
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        view = (MyImageView) findViewById(R.id.img);
        initImg();
        
        view.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				final ImageView view = (ImageView) v;
				switch (event.getAction() & MotionEvent.ACTION_MASK) {
				case MotionEvent.ACTION_DOWN:
					savedMatrix.set(matrix);
					start.set(event.getX(), event.getY());
					mode = DRAG;
					break;
				case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_POINTER_UP:
					mode = NONE;
					break;
				case MotionEvent.ACTION_POINTER_DOWN:
					oldDist = spacing(event);
					stateIsLock = false;
					bitmapChangeW = bitmapChangeToW;
					if (oldDist > 10f) {
						savedMatrix.set(matrix);
						midPoint(mid, event);
						mode = ZOOM;
					}
					break;
				case MotionEvent.ACTION_MOVE:
					
					if (mode == DRAG) {
						matrix.set(savedMatrix);
						matrix.postTranslate(event.getX() - start.x, event.getY()
								- start.y);
					} else if (mode == ZOOM) {
							
							float newDist = spacing(event);
							if (newDist > 10f) {
								float scale = newDist / oldDist;
								if(bitmapChangeToW > 300 ){
									if(scale < 1){
										
									}else
										return true;
								}
								if(bitmapChangeToW <48){
									if(scale >1){
										
									}else
										return true;
								}
								matrix.set(savedMatrix);
								bitmapChangeToW = (int) (bitmapChangeW*scale);
								
								matrix.postScale(scale, scale, mid.x, mid.y);
							}
						}
				
					break;
				}
				view.setImageMatrix(matrix);
				return true;
			}
			
			private float spacing(MotionEvent event) {
				float x = event.getX(0) - event.getX(1);
				float y = event.getY(0) - event.getY(1);
				return FloatMath.sqrt(x * x + y * y);
			}

			private void midPoint(PointF point, MotionEvent event) {
				float x = event.getX(0) + event.getX(1);
				float y = event.getY(0) + event.getY(1);
				point.set(x / 2, y / 2);
			}});
	
    }
    
    public void initImg(){
    	bitmapW = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher).getWidth();
    	bitmapH = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher).getHeight();
    	bitmapChangeW = bitmapW;
    	bitmapChangeToW = bitmapH;
    	DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels;
		int screenH = dm.heightPixels;
		
		//∂‡”‡
		int w = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED); 
		int h = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED); 
		view.measure(w, h);
		
    	Log.d("image w h:", "w="+bitmapChangeW+"  h="+bitmapChangeToW+"  ---t"+view.getMeasuredWidth()+"==="+view.getPaddingLeft()+"  r--"+view.getPaddingRight());
    	
    	matrix.postTranslate(screenW/2, screenH/2);
    	view.setImageMatrix(matrix);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
}
