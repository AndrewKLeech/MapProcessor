package com.example.andrew.maps


import kotlin.math.sqrt


class DigitalElevationModel {

    // 2d array that holds array of x,y,z values
    private var model: ArrayList<ArrayList<FloatArray>>? = null
    private var rows = 0
    private var cols = 0


    fun setDEM(dem :ArrayList<ArrayList<FloatArray>>){
        model = dem
        this.rows = dem.size
        this.cols = dem[0].size
    }

    fun getDEM():ArrayList<ArrayList<FloatArray>>{
        return model!!
    }

    fun rows(): Int{
        return rows
    }

    fun cols(): Int{
        return cols
    }

    /*
        Get the inverse distance weight squared for a point given 3 points with a known
        height.

        point: the point to interpolate a height
        ref: an array of float arrays that hold x and y coordinates as well as a height.
     */
    private fun inverseDisWeight(point: FloatArray, ref: Array<FloatArray>): Double{

        var heightDivDist = 0.0
        var oneDivDist = 0.0

        for(i in 0 until ref.size){

            // euclidean(point x, point y, ref x, ref y)
            val dist = euclidean(point[0].toDouble(), point[1].toDouble(), ref[i][0].toDouble(), ref[i][1].toDouble())

            // height divided by distance
            heightDivDist += ref[i][2] / dist

            // one divided by distance
            oneDivDist += 1 / dist
        }
        return heightDivDist/oneDivDist
    }

    // get the euclidean distance between two points
    private fun euclidean(x1: Double, y1: Double, x2: Double, y2: Double): Double{
        return sqrt(Math.pow(x2 - x1, 2.0) + Math.pow(y2 - y1, 2.0))
    }


    // Make example model
    fun exampleModel(width: Int, height: Int, maxHeight: Int): ArrayList<ArrayList<FloatArray>>{

        model = arrayListOf(
                arrayListOf(FloatArray(3, {i -> 0.toFloat()}))
        )

        // find middle value on width and height
        val wMid = (width-1)/2
        val hMid= (height-1)/2

        var firstIttr = true

        for(i in 0 until height){

            // For all but the first row add new array list
            if(!firstIttr){
                model!!.add(ArrayList())
            }

            for(j in 0 until width){

                // For all but the first col in the first row add Float array
                if(!firstIttr){
                    model!![i].add(FloatArray(3))
                }

                // Get height value, making points nearer to the center higher
                var value = maxHeight-1 / (Math.abs(wMid - i)).toFloat() / (Math.abs(hMid - j)).toFloat()

                // Some of the values from the above process will result in infinity, change this to a
                // max height
                if (value == Float.POSITIVE_INFINITY){
                    value = 1 + maxHeight.toFloat()
                }

                // Set point and height values to DEM
                model!![i][j] = floatArrayOf(i.toFloat(), j.toFloat(), value)

                firstIttr = false
                //System.out.println("[$i][$j]: $value")
            }
        }
        return model!!
    }
}