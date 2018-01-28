package com.example.andrew.mapprocessor

import android.R.attr.src
import android.graphics.*
import android.graphics.BlurMaskFilter.Blur
import android.opengl.ETC1.getHeight
import android.opengl.ETC1.getWidth
import android.opengl.ETC1.getWidth
import android.opengl.ETC1.getHeight





/**
 * Created by Andrew on 25/01/2018.
 */
//from https://xjaphx.wordpress.com/2011/06/20/image-processing-highlight-image-on-the-fly/
fun PrepImage(src: Bitmap?):Bitmap {
    // create new bitmap, which will be painted and becomes result image
    val bmOut = Bitmap.createBitmap(src!!.width + 96, src!!.height + 96, Bitmap.Config.ARGB_8888)
    // setup canvas for painting
    val canvas = Canvas(bmOut)
    // setup default color
    canvas.drawColor(0, PorterDuff.Mode.CLEAR)
    val height = bmOut.getHeight()
    val width = bmOut.getWidth()
    for ( i in 1..height){
        for ( j in 1..width){
            if(j%2 == 0){
                bmOut.setPixel(height,width, 10)
            }

        }
    }

    // paint the image source
    //canvas.drawBitmap(src, 0, 0, null)

    // return out final image
    return bmOut
}