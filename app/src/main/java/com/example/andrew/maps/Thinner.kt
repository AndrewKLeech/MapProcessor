package com.example.andrew.maps

import org.opencv.core.Mat
import java.util.*


/**
 * Created by Andrew on 24/03/2018.
 */
class Thinner {

    // Addapted from https://web.archive.org/web/20160322113207/http://opencv-code.com/quick-tips/implementation-of-thinning-algorithm-in-opencv/


    // Adapted from https://nayefreza.wordpress.com/2013/05/11/zhang-suen-thinning-algorithm-java-implementation/
    fun thin(mat: Mat): Mat{

        var a: Int
        var b: Int
        val pointsToChange = LinkedList<Point>()
        var hasChange: Boolean
        val rows = mat.rows()
        val cols = mat.cols()
        do {
            hasChange = false
            run {
                var y = 1
                while (y + 1 < rows) {
                    var x = 1
                    while (x + 1 < cols) {
                        a = getA(mat, y, x)
                        b = getB(mat, y, x)
                        if (mat.get(y,x)[0] == on && on*2 <= b && b <= on*6 && a == 1
                                && mat.get(y-1, x)[0] * mat.get(y,x + 1)[0] * mat.get(y + 1, x)[0] == off
                                && mat.get(y, x + 1)[0] * mat.get(y + 1, x)[0] * mat.get(y, x - 1)[0] == off) {
                            pointsToChange.add(Point(x, y))
                            hasChange = true
                        }
                        x++
                    }
                    y++
                }
            }
            for (point in pointsToChange) {
                System.out.println("turning off")
                mat.put(point.y, point.x, off)
            }
            pointsToChange.clear()
            var y = 1
            while (y + 1 < rows) {
                var x = 1
                while (x + 1 < cols) {
                    a = getA(mat, y, x)
                    b = getB(mat, y, x)
                    if (mat.get(y,x)[0] == on && on*2 <= b && b <= on*6 && a == 1
                            && mat.get(y - 1, x)[0] * mat.get(y, x + 1)[0] * mat.get(y, x - 1)[0] == off
                            && mat.get(y - 1, x)[0] * mat.get(y + 1, x)[0] * mat.get(y, x - 1)[0] == off) {
                        pointsToChange.add(Point(x, y))
                        hasChange = true
                    }
                    x++
                }
                y++
            }
            for (point in pointsToChange) {
                System.out.println("turning off")
                mat.put(point.y, point.x, off)
            }
            pointsToChange.clear()
        } while (hasChange)
        return mat
    }

    // Value for off pixel
    private val off = 0.toDouble()
    // Value for on pixel
    private val on = 255.toDouble()

    private fun getA(mat: Mat, y: Int, x: Int): Int {
        val rows = mat.rows()
        val cols = mat.cols()
        var count = 0
        //p2 p3
        if (y - 1 >= 0 && x + 1 < cols && mat.get(y - 1, x)[0] == off && mat.get(y - 1, x + 1)[0] == on) {
            count++
        }
        //p4 p5
        if (y + 1 < rows && x + 1 < cols && mat.get(y,x+1)[0] == off && mat.get(y + 1, x + 1)[0] == on) {
            count++
        }
        //p5 p6
        if (y + 1 < rows && x + 1 < cols && mat.get(y + 1, x + 1)[0] == off && mat.get(y + 1, x)[0] == on) {
            count++
        }
        //p6 p7
        if (y + 1 < rows && x - 1 >= 0 && mat.get(y + 1, x)[0] == off && mat.get(y + 1, x - 1)[0] == on) {
            count++
        }
        //p7 p8
        if (y + 1 < rows && x - 1 >= 0 && mat.get(y + 1, x - 1)[0] == off && mat.get(y, x - 1)[0] == on) {
            count++
        }
        //p8 p9
        if (y - 1 >= 0 && x - 1 >= 0 && mat.get(y, x - 1)[0] == off && mat.get(y - 1, x - 1)[0] == on) {
            count++
        }
        //p9 p2
        if (y - 1 >= 0 && x - 1 >= 0 && mat.get(y - 1, x - 1)[0] == off && mat.get(y - 1, x)[0] == on) {
            count++
        }
        return count
    }

    private fun getB(mat: Mat, y: Int, x: Int): Int {
        return ((mat.get(y - 1, x)[0] + mat.get(y - 1, x + 1)[0] + mat.get(y, x + 1)[0]
                + mat.get(y + 1, x + 1)[0] + mat.get(y + 1, x)[0] + mat.get(y + 1, x - 1)[0]
                + mat.get(y, x - 1)[0] + mat.get(y - 1, x - 1)[0]).toInt())
    }

    // Object to store x and y coordinates
    inner class Point(var x: Int, var y: Int)
}