package com.example.andrew.mapprocessor

import android.R.attr.src
import android.graphics.*
import android.graphics.Bitmap
import android.graphics.BlurMaskFilter.Blur
import android.opengl.ETC1.getHeight
import android.opengl.ETC1.getWidth
import android.opengl.ETC1.getWidth
import android.opengl.ETC1.getHeight
import android.graphics.Color.RGBToHSV
import android.R.attr.bitmap
import android.graphics.Color.RGBToHSV
import android.R.attr.bitmap










/**
 * Created by Andrew on 25/01/2018.
 */
fun PrepImage(src: Bitmap):Bitmap {

    var cpy = src.copy(src.config, true)
    var pixels = IntArray(cpy.height *cpy.width)
    cpy.getPixels(pixels, 0, cpy.width, 0, 0, cpy.width, cpy.height)
    //http://bagawerexecinux.cf/1305539/6566619/137c6080a-android-colorrgb-to-hsv-83344
    var coord_x = 0
    var coord_y = 0
    var pos = 0
    while (coord_y < cpy.height) {
        while (coord_x < cpy.width) {
            val touchedRGB = cpy.getPixel(coord_x, coord_y)
            val colorRed = Color.red(touchedRGB)
            val colorGreen = Color.green(touchedRGB)
            val colorBlue = Color.blue(touchedRGB)
            val hsv = FloatArray(3)
            Color.RGBToHSV(colorRed, colorGreen, colorBlue, hsv)
            if (hsv[2] <=0.25){
                pixels[pos] = Color.BLACK
            }
            else{
                pixels[pos] = Color.WHITE
            }
            coord_x++
            pos++
        }
        //pos++
        coord_x = 0
        coord_y++
    }

    /*(0 until pixels.size)
            .filter { it %2 == 0 }
            .forEach { pixels[it] = Color.BLUE }
            */

    cpy.setPixels(pixels, 0, cpy.width, 0, 0, cpy.width, cpy.height)
    return cpy
}