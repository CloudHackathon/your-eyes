package com.youreye.tts.activity;

import com.youreye.texttospeech.R;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.Toast;
import android.view.MotionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class CameraScanActivity extends Activity{

	
   private Camera mCamera;
   private ShowCamera showCamera;
   
   private long preScreenClickTime = 0;
   private long currentScreenClickTime = 0;
   
   private String picTakenStoragePath;
   
   
   public static Camera isCameraAvailiable(){
      Camera object = null;
      try {
         object = Camera.open(); 
      }
      catch (Exception e){
      }
      return object; 
   }


   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      requestWindowFeature(Window.FEATURE_NO_TITLE);

      setContentView(R.layout.camera_scan_layout);
      
      mCamera = isCameraAvailiable();
      showCamera = new ShowCamera(this, mCamera);
      FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
      preview.addView(showCamera);
      String sdCardState = Environment.getExternalStorageState();
      if(sdCardState.equals(Environment.MEDIA_MOUNTED)){    	  
    	  picTakenStoragePath = Environment.getExternalStorageDirectory().getPath() + "/youreyes";
    	  File picTakenStorageDir = new File(picTakenStoragePath);
    	  if(!picTakenStorageDir.exists()){
    		  picTakenStorageDir.mkdirs();    		  
    	  }
      }
      
   }
   
   @Override
   public boolean onTouchEvent(MotionEvent event){
	   
	   switch(event.getAction()){
	   
	   case MotionEvent.ACTION_DOWN:
		   currentScreenClickTime = System.currentTimeMillis();
		   if((currentScreenClickTime - preScreenClickTime) < 1000){			   
			   snapIt();	  
		   }
		   preScreenClickTime = currentScreenClickTime;
		   break;	   
	   }
	   
	   return true;
	   
   } 
   
   private PictureCallback capturedIt = new PictureCallback() {

	      @Override
	      public void onPictureTaken(byte[] data, Camera camera) {

	      Bitmap bitmap = BitmapFactory.decodeByteArray(data , 0, data .length);
	      if(bitmap==null){
	         Toast.makeText(getApplicationContext(), "not taken", Toast.LENGTH_SHORT).show();
	         return;
	      }
	      
	      try{
	    	  Date d = new Date(System.currentTimeMillis());
	    	  String picName = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(d);

	    	  FileOutputStream outStream = new FileOutputStream(picTakenStoragePath+"/"+picName+".png");
	    	  
	    	  if(bitmap.compress(Bitmap.CompressFormat.PNG, 20, outStream)){
	    		  outStream.flush();
	    		  outStream.close();    		  
	    		  
	    	  }
	    	  
	      }catch(FileNotFoundException e){
	    	  e.printStackTrace();	    	  
	    	  
	      }catch(IOException e){
	    	  e.printStackTrace();
	      }
	      Toast.makeText(getApplicationContext(), "taken"+picTakenStoragePath, Toast.LENGTH_SHORT).show();  
	         
	         
	      
	      //mCamera.release();
	   }
	};

   
   public void snapIt(){
      mCamera.takePicture(null, null, capturedIt);
   }

   
   @Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mCamera.release();
	
	}


}
