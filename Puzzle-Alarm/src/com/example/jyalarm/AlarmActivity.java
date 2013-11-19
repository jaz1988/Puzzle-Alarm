package com.example.jyalarm;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;


public class AlarmActivity extends Activity implements OnClickListener{

	AlarmManager AlmMgr;
	PendingIntent Sender;
	TimePicker timepicker;
	private Uri outputFileUri;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alarm);
		
		
		    
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_alarm, menu);
		
		//Initialize timepicker
		timepicker = (TimePicker) findViewById(R.id.timepicker);
		
		return true;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId())
		{
			case R.id.start:
				
				//Get hour
				int hour = timepicker.getCurrentHour();
				
				//Get minute
				int min = timepicker.getCurrentMinute();
				
				Calendar cal = Calendar.getInstance();
		        
		        cal.set(Calendar.HOUR_OF_DAY, hour);
		        cal.set(Calendar.MINUTE, min);
		        cal.set(Calendar.SECOND, 0);
		        cal.set(Calendar.MILLISECOND, 0);
		        
		        
		        //Create a new PendingIntent and add it to the AlarmManager
		        Intent intent = new Intent(this, AlarmReceiverActivity.class);
		        Sender = PendingIntent.getActivity(this,
		            12345, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		        AlmMgr = (AlarmManager)getSystemService(Activity.ALARM_SERVICE);
		        AlmMgr.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
		      		  Sender);
		        
		        Toast.makeText(AlarmActivity.this, "Alarm started!!", Toast.LENGTH_SHORT).show();
		        break;
		        
			case R.id.ringtone:
				
				//Choose ringtone	
				openIntent(0); //type = 0 
				break;
				
			case R.id.image:
			
				//Choose image
				Intent myIntent = new Intent(this, FileChooser.class);
		        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		        this.startActivity(myIntent);
				break;
				
		}
	}
	
	private void openIntent(int type) 
	{

		// Determine Uri of camera image to save.
		final File root = new File(Environment.getExternalStorageDirectory() + File.separator + "MyDir" + File.separator);
		root.mkdirs();
		final String fname = "Test";
		final File sdImageMainDirectory = new File(root, fname);
		outputFileUri = Uri.fromFile(sdImageMainDirectory);

		    // Camera.
		    final List<Intent> cameraIntents = new ArrayList<Intent>();
		    final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		    final PackageManager packageManager = getPackageManager();
		    final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
		    for(ResolveInfo res : listCam) 
		    {
		        final String packageName = res.activityInfo.packageName;
		        final Intent intent = new Intent(captureIntent);
		        intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
		        intent.setPackage(packageName);
		        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
		        cameraIntents.add(intent);
		    }

		    // Filesystem.
		    final Intent galleryIntent = new Intent();
		    galleryIntent.setType("image/*");
		    galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

		    // Chooser of filesystem options.
		    final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");

		    // Add the camera options.
		    if(type == 0) 
		    	chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[]{}));
		    
		    startActivityForResult(chooserIntent, 1);
		}

		@Override
		protected void onActivityResult(int requestCode, int resultCode, Intent data)
		{
		    if(resultCode == RESULT_OK)
		    {
		    	Log.d("Gallery Chooser", "RESULT_OK");
		        if(requestCode == 1)
		        {
		        	Log.d("Gallery Chooser", "request code = 1");
		            final boolean isCamera;
		            if(data == null)
		            {
		            	Log.d("Gallery Chooser", "data == null");
		                isCamera = true;
		            }
		            else
		            {
		            	Log.d("Gallery Chooser", "data != null");
		                final String action = data.getAction();
		                if(action == null)
		                {
		                	Log.d("Gallery Chooser", "action == null");
		                    isCamera = false;
		                }
		                else
		                {
		                	Log.d("Gallery Chooser", "action != null");
		                    isCamera = action.equals(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		                }
		            }

		            Uri selectedImageUri;
		            if(isCamera)
		            {
		            	Log.d("Gallery Chooser", "isCamera == true");
		                selectedImageUri = outputFileUri;
		                Log.d("Gallery Chooser", "captured uri = " + selectedImageUri);
		                try {
							InputStream input = this.getContentResolver().openInputStream(selectedImageUri);
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							Log.e("Gallery Chooser", e.getMessage());
						}
		                
		            }
		            else
		            {
		            	Log.d("Gallery Chooser", "isCamera != true");
		                selectedImageUri = data == null ? null : data.getData();
		                Log.d("Gallery Chooser", selectedImageUri.toString());
		                
		            }
		            
		            try  
	                {
						//Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
						//Log.d("Gallery Chooser", "bitmap size: " + bitmap.getHeight() + "," + bitmap.getWidth());
	                	Bitmap bitmap = getThumbnail(selectedImageUri);
	                	Global.bitmap = bitmap;
						Log.d("Gallery Chooser", "bitmap size: " + bitmap.getHeight() + "," + bitmap.getWidth());
						
						//Getting the Filename of the chosen image
						File image = new File(selectedImageUri.toString());
						String filename = image.getName();
						Log.d("Gallery Chooser", "Filename: " + filename);
					} 
	                catch (FileNotFoundException e) 
	                {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
	                catch (IOException e) 
	                {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		        }
		    }
		}
	
		public Bitmap getThumbnail(Uri uri) throws FileNotFoundException, IOException{
			int THUMBNAIL_SIZE = 500;
	        InputStream input = this.getContentResolver().openInputStream(uri);

	        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
	        onlyBoundsOptions.inJustDecodeBounds = true;
	        onlyBoundsOptions.inDither=true;//optional
	        onlyBoundsOptions.inPreferredConfig=Bitmap.Config.ARGB_8888;//optional
	        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
	        input.close();
	        if ((onlyBoundsOptions.outWidth == -1) || (onlyBoundsOptions.outHeight == -1))
	            return null;

	        int originalSize = (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) ? onlyBoundsOptions.outHeight : onlyBoundsOptions.outWidth;

	        double ratio = (originalSize > THUMBNAIL_SIZE) ? (originalSize / THUMBNAIL_SIZE) : 1.0;

	        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
	        bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
	        bitmapOptions.inDither=true;//optional
	        bitmapOptions.inPreferredConfig=Bitmap.Config.ARGB_8888;//optional
	        input = this.getContentResolver().openInputStream(uri);
	        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
	        input.close();
	        return bitmap;
	    }

	    private static int getPowerOfTwoForSampleRatio(double ratio){
	    	Log.d("Gallery Chooser", "ratio = " + ratio);
	    	Log.d("Gallery Chooser", "ratio (floored) = " + (int)Math.floor(ratio));
	    	Log.d("Gallery Chooser", "ratio (floored) HighestOneBit = " + Integer.highestOneBit((int)Math.floor(ratio)));
	        int k = Integer.highestOneBit((int)Math.floor(ratio));
	        if(k==0) return 1;
	        else return k;
	    }


}
