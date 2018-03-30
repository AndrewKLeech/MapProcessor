package com.example.andrew.maps

import android.content.Context
import android.graphics.Rect
import android.graphics.RectF
import android.hardware.Camera
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import java.io.IOException


@Suppress("NAME_SHADOWING")
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
        mHolder = holder
        mHolder!!.addCallback(this)
        mHolder!!.setType(SurfaceHolder.SURFACE_TYPE_NORMAL)
    }
    // help from https://stackoverflow.com/questions/18460647/android-setfocusarea-and-auto-focus
    override fun onTouchEvent(motionEvent: MotionEvent): Boolean {
        if (mCamera != null) {
            mCamera!!.cancelAutoFocus()
            var focusRect: Rect = calculateTapArea(motionEvent.getX(), motionEvent.getY(), 1f);

            var parameters: Camera.Parameters = mCamera!!.getParameters();
            if (parameters.focusMode.equals(Camera.Parameters.FOCUS_MODE_AUTO)) {
                parameters.focusMode = Camera.Parameters.FOCUS_MODE_AUTO;
            }

            if (parameters.getMaxNumFocusAreas() > 0) {
                var mylist: ArrayList<Camera.Area> = ArrayList()
                mylist.add(Camera.Area(focusRect, 1000))
                parameters.focusAreas = mylist
            }

            try {
                mCamera!!.cancelAutoFocus();
                mCamera!!.parameters = parameters;
                mCamera!!.startPreview()
                mCamera!!.autoFocus({ b: Boolean, camera: Camera ->
                    @Override
                    fun onAutoFocus(success: Boolean, camera: Camera) {
                        if (camera.parameters.focusMode == Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE) {
                            val parameters: Camera.Parameters = camera.parameters;
                            parameters.focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE;
                            if (parameters.maxNumFocusAreas > 0) {
                                parameters.focusAreas = null
                            }
                            camera.parameters = parameters
                            camera.startPreview()
                        }
                    }
                })
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return true
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

    fun clamp(x: Int, min: Int, max: Int): Int {
        if (x > max) {
            return max
        }
        return if (x < min) {
            min
        } else x
    }
    fun calculateTapArea(x: Float, y: Float, coefficient: Float): Rect {
        val areaSize = java.lang.Float.valueOf(210 * coefficient).toInt()

        val left = clamp(x.toInt() - areaSize / 2, 0, this.width - areaSize)
        val top = clamp(y.toInt() - areaSize / 2, 0, this.height - areaSize)

        val rectF = RectF(left.toFloat(), top.toFloat(), left.toFloat() + areaSize, top.toFloat() + areaSize)
        matrix.mapRect(rectF)

        return Rect(Math.round(rectF.left), Math.round(rectF.top), Math.round(rectF.right), Math.round(rectF.bottom))
    }


}