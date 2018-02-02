package com.example.andrew.mapprocessor

import android.R.attr.src
import android.graphics.*
import android.graphics.Bitmap
import android.graphics.BlurMaskFilter.Blur
import android.opengl.ETC1.getHeight
import android.opengl.ETC1.getWidth
import android.opengl.ETC1.getWidth
import android.opengl.ETC1.getHeight




/**
 * Created by Andrew on 25/01/2018.
 */
fun PrepImage(src: Bitmap):Bitmap {


    var cpy = src.copy(src.config, true)
    var pixels = IntArray(cpy.height *cpy.width)
    cpy.getPixels(pixels, 0, cpy.getWidth(), 0, 0, cpy.getWidth(), cpy.getHeight())
    for (i in 0 until pixels.size){
        if(i%2 == 0){
            pixels[i] = Color.BLUE
        }
    }

    cpy.setPixels(pixels, 0, cpy.getWidth(), 0, 0, cpy.getWidth(), cpy.getHeight())
    return cpy
}