package com.example.andrew.mapprocessor

import android.content.Context
import android.hardware.Camera
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import java.io.IOException

/**
 * Created by Andrew on 05/03/2018.
 * whit help form http://blog.rhesoft.com/2015/04/02/tutorial-how-to-use-camera-with-android-and-android-studio/
 * and https://developer.android.com/training/camera/cameradirect.html#TaskSettings
 */

class Preview(context: Context, camera:Camera) : SurfaceView(context), SurfaceHolder.Callback {

    var mHolder:SurfaceHolder? = null
    var mCamera:Camera? = null

    init{
        mCamera = camera
        Log.d("SUCCESS", "mCamera is " + mCamera)
        mCamera!!.setDisplayOrientation(90)
        mHolder = getHolder()
        mHolder!!.addCallback(this)
        mHolder!!.setType(SurfaceHolder.SURFACE_TYPE_NORMAL)
    }

    override fun surfaceCreated(surfaceHolder: SurfaceHolder?) {
       try{
           //when the surface is created, set the camera to draw images here
           mCamera!!.setPreviewDisplay(surfaceHolder)
           mCamera!!.startPreview()
       } catch (e:IOException){
           Log.d("ERROR", "Camera error on surfaceCreated " + e.message)
       }

    }

    override fun surfaceDestroyed(p0: SurfaceHolder?) {
        mCamera!!.stopPreview()
        mCamera!!.release()
    }

    override fun surfaceChanged(p0: SurfaceHolder?, p1: Int, p2: Int, p3: Int) {
        //before changing the application orientation, preview needs to be stopped
        if(mHolder!!.surface == null)/*check if surface is ready to recieve*/{
            return
        }
        try{
            mCamera!!.stopPreview()
        } catch (e:Exception){
            Log.d("ERROR", "Camera error on surfaceDestroyed while trying to stop Preview " + e.message)
        }

        //recreate the camera preview
        try{
            mCamera!!.setPreviewDisplay(mHolder)
            mCamera!!.startPreview()
        } catch (e:IOException){
            Log.d("ERROR", "Camera error on surfaceCreated while trying to set or start preview " + e.message)
        }
    }


}