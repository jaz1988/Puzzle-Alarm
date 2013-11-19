package com.example.jyalarm;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


import android.os.Bundle;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.Drawable;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;
import android.view.Display;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.DragShadowBuilder;
import android.view.View.OnDragListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	View draggedView;
	List<View> viewList;
	Point screenSize;
	List<Integer> listOrder,randListOrder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);		
		
		try 
		{
			ImageView iv = (ImageView) findViewById(R.id.imageView1);
			//iv.setImageBitmap(region);
			
			Display display = getWindowManager().getDefaultDisplay(); 
			screenSize = new Point(display.getWidth(),display.getHeight());
			
			Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.edited);
			bitmap = Global.bitmap;
			Bitmap resized = Bitmap.createScaledBitmap(bitmap, display.getWidth(), display.getHeight(), true);
			
			ByteArrayOutputStream bos = new ByteArrayOutputStream(); 
			resized.compress(CompressFormat.PNG, 0 /*ignored for PNG*/, bos); 
			byte[] bitmapdata = bos.toByteArray();
			InputStream is = new ByteArrayInputStream(bitmapdata);
			
			BitmapRegionDecoder decoder = BitmapRegionDecoder.newInstance(is, false);
			
			iv.getLayoutParams().width = display.getWidth();
			iv.getLayoutParams().height = display.getHeight();
			
			//3 x 3
			int left_right = display.getWidth() / 3;
			int top_bot = display.getHeight() / 3;
			int top,bot,left,right;
			List<List<Integer>> list = new ArrayList<List<Integer>>();		
			List<Integer> innerList;
			listOrder = new ArrayList<Integer>();
			randListOrder = new ArrayList<Integer>();
			int count = 0;
			ImageView newIV;
			Bitmap region;
			viewList = new ArrayList<View>();
			
			for(int i = 0, ii = 0; ii < 3; i += top_bot, ii++)
			{
				top = i;
				bot = i + top_bot;
				
				for(int j = 0, jj = 0; jj < 3; j += left_right, jj++)
				{
					left = j;
					right = j + left_right;
					
					innerList = new ArrayList<Integer>();
					innerList.add(left);
					innerList.add(top);
					innerList.add(right);
					innerList.add(bot);
					
					listOrder.add(count);
					
					newIV = new ImageView(MainActivity.this);
					newIV.setTag(count);
					region = decoder.decodeRegion(new Rect((Integer) innerList.get(0), (Integer) innerList.get(1), (Integer) innerList.get(2), (Integer) innerList.get(3)), null);
					newIV.setImageBitmap(region);
					newIV.setOnTouchListener(new MyTouchListener());
					newIV.setOnDragListener(new MyDragListener());
					viewList.add(newIV);
					
					count++;
					
					list.add(innerList);
				}				
			}
			
			for(int i = 0; i < list.size(); i++)
			{
				innerList = list.get(i);
				
				Log.d("List", "(" + innerList.get(0) + "," + innerList.get(1) + "," + innerList.get(2) + "," + innerList.get(3) + ")");
				
			}
			
			Random rand;
			int x;
			View getX;
			List<View> newList = new ArrayList<View>();
			List<View> copyList = new ArrayList<View>();
			
			for(int i = 0; i < viewList.size(); i++)
	    	{
	    		copyList.add(viewList.get(i));
	    	}
			
			for(int i = 0; i < list.size(); i++)
	    	{
	    		rand = new Random();
	    		
	    		x = rand.nextInt(copyList.size());  	 		
	    		
	    		getX = copyList.get(x);
	    		  		
	    		newList.add(getX);
	    		copyList.remove(getX);   		   		
	    	} 	
			
			viewList = newList;
			
			for(int i = 0; i < list.size(); i++)
			{
				innerList = list.get(i);
				
				Log.d("new List", "(" + innerList.get(0) + "," + innerList.get(1) + "," + innerList.get(2) + "," + innerList.get(3) + ")");
				
			}
			
			for(int i = 0; i < listOrder.size(); i++)
			{		
				Log.d("listOrder", "(" + listOrder.get(i) + ")");			
			}
			for(int i = 0; i < randListOrder.size(); i++)
			{		
				Log.d("randListOrder", "(" + randListOrder.get(i) + ")");			
			}
					
			
			
			//Create number of image views according to choice
			//3 * 3 = 9		
			
			LinearLayout ll_hori, ll_verti;
			RelativeLayout rl_main = (RelativeLayout) findViewById(R.id.rl_main);
			rl_main.removeAllViews();
			int index = 0;
			
			ll_verti = new LinearLayout(MainActivity.this);
			ll_verti.setOrientation(LinearLayout.VERTICAL);
			ll_verti.setLayoutParams(new LayoutParams(screenSize.x, screenSize.y));
			
			
			count = 0;
			for(int i = 0; i < 3; i++)
			{				
				ll_hori = new LinearLayout(MainActivity.this);
				ll_hori.setOrientation(LinearLayout.HORIZONTAL);
				ll_hori.setLayoutParams(new LayoutParams(screenSize.x, screenSize.y/3));
				
				for(int j = 0; j < 3; j++)
				{
					ll_hori.addView(viewList.get(count));	
					count++;
				}
				ll_verti.addView(ll_hori);		
			}
			rl_main.addView(ll_verti);
	
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		
	}


	 private final class MyTouchListener implements OnTouchListener {
		    public boolean onTouch(View view, MotionEvent motionEvent) {
		      if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
		        ClipData data = ClipData.newPlainText("", "");
		        DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
		        view.startDrag(data, shadowBuilder, view, 0);
		        view.setVisibility(View.INVISIBLE);
		        return true;
		      } else {
		        return false;
		      }
		    }
		  }
		
		  class MyDragListener implements OnDragListener {
		  
		    @Override
		    public boolean onDrag(View v, DragEvent event) {
		      int action = event.getAction();
		      View dropView = v;
		      switch (event.getAction()) {
		      case DragEvent.ACTION_DRAG_STARTED:
		        // Do nothing
		        break;
		      case DragEvent.ACTION_DRAG_ENTERED:
		        //v.setBackgroundDrawable(enterShape);
		        break;
		      case DragEvent.ACTION_DRAG_EXITED:
		        //v.setBackgroundDrawable(normalShape);
		        break;
		      case DragEvent.ACTION_DROP:
		        // Dropped, reassign View to ViewGroup
		        draggedView = (View) event.getLocalState();
		        
		        List<View> innerViewList;
		        int draggedIndex = 0;
		        //Check which ImageView is being dragged
		        for(int i = 0; i < viewList.size(); i ++)
		        {
		        
		        	if(viewList.get(i) == draggedView)
	        		{
		        		draggedIndex = i;
	        			break;
	        		}
		        	
		        }
		        
		        int droppedIndex = 0;
		        //Check which view is being dropped
		        for(int i = 0; i < viewList.size(); i ++)
		        {
		        	
	        		if(viewList.get(i) == dropView)
	        		{
	        			droppedIndex = i;
	        			break;
	        		}
		        	
		        }
		        
		        
		        //Remove all views
            	ViewGroup owner;
               // owner.removeAllViews();
                
                viewList.set(draggedIndex, dropView);
                viewList.set(droppedIndex, draggedView);
                
                ImageView newIV;
    			LinearLayout ll_hori, ll_verti = null;
    			RelativeLayout rl_main = (RelativeLayout) findViewById(R.id.rl_main);
    			
    			ll_verti = (LinearLayout) rl_main.getChildAt(0);
    			 for(int i = 0; i < 3; i++)
     			{
    				//Log.d("parent?", "" + rl_main.getChildCount());
    				owner = (ViewGroup) ll_verti.getChildAt(i);
    				owner.removeAllViews();
     			}
    			    			
    			rl_main.removeAllViews();
    			int index = 0;
    			
    			ll_verti = new LinearLayout(MainActivity.this);
    			ll_verti.setOrientation(LinearLayout.VERTICAL);		
    			ll_verti.setLayoutParams(new LayoutParams(screenSize.x, screenSize.y));
    			
    			int count = 0;
                for(int i = 0; i < 3; i++)
    			{				
    				ll_hori = new LinearLayout(MainActivity.this);
    				ll_hori.setOrientation(LinearLayout.HORIZONTAL);
    				ll_hori.setLayoutParams(new LayoutParams(screenSize.x, screenSize.y/3));
    				
    				
    				for(int j = 0; j < 3; j++)
    				{
//    					ViewGroup owner = (ViewGroup) innerViewList.get(j).getParent();
//    		            owner.removeAllViews();
    					//Log.d("parent?", "" + innerViewList.get(j).getParent().toString());
    					ll_hori.addView(viewList.get(count));	
    					count++;
    				}
    				
    				ll_verti.addView(ll_hori);		
    			}
                
                rl_main.addView(ll_verti);
                draggedView.setVisibility(View.VISIBLE);
                
                //Check if it is of correct order
                count = 0;
                for(int i = 0; i < viewList.size(); i++)
    			{
                	if(((Integer)viewList.get(i).getTag() == i))
                	{
                		count++;
                	}
    			}
                if(count == 9)
                {
                	Toast.makeText(MainActivity.this, "Done!", Toast.LENGTH_SHORT).show();
                	 Global.mMediaPlayer.stop();
                }
		       
		       
		        break;
		      case DragEvent.ACTION_DRAG_ENDED:
					if (dropEventNotHandled(event)) {
						
						draggedView = (View) event.getLocalState();
						draggedView.post(new Runnable(){
		
							@Override
							public void run() {
								// TODO Auto-generated method stub
								draggedView.setVisibility(View.VISIBLE);
							}
					       
					    });
		          }
		      default:
		        break;
		      }
		      return true;
		    }
		  }
		  private boolean dropEventNotHandled(DragEvent dragEvent) {
		      return !dragEvent.getResult();
			}

}
