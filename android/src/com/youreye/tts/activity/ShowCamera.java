package com.youreye.tts.activity;

import java.io.IOException;

import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


public class ShowCamera extends SurfaceView implements SurfaceHolder.Callback {

   private SurfaceHolder mholder;
   private Camera mCamera;

   public ShowCamera(Context context,Camera camera) {
      super(context);
      mCamera = camera;
      mholder = getHolder();
      mholder.addCallback(this);
   }

   
   @Override
   public void surfaceCreated(SurfaceHolder holder) {
      try   {
         mCamera.setPreviewDisplay(holder);
         mCamera.startPreview(); 
         mCamera.setDisplayOrientation(90);
      } catch (IOException e) {
      }
   }

   @Override
   public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
   }


   @Override
   public void surfaceDestroyed(SurfaceHolder arg0) {
   }

}

