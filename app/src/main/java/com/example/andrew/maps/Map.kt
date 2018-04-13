package com.example.andrew.maps

import android.opengl.GLES20
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

/**
* Created by Andrew on 16/03/2018.
 * Class that will hold x, y and z coordinates of the scanned map
*/
class Map {
    private val vertexShaderCode =
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "void main() {" +
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "}"

    private val fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}"

    // Use to access and set the view transformation
    private var mMVPMatrixHandle:Int = 0
    private var mPositionHandle: Int = 0
    private var mColorHandle: Int = 0
    private var mProgram: Int = 0

    //Buffer objects
    private val vbo = IntArray(1)
    private val ibo = IntArray(1)

    // Buffers
    private val vertexBuffer: FloatBuffer
    private val indexBuffer: ShortBuffer

    // Set color with red, green, blue and alpha (opacity) values
    internal var color = floatArrayOf(0.63671875f, 0.76953125f, 0.22265625f, 5.0f)

    //private val vertexCount = triangleCoords.size / COORDS_PER_VERTEX

    // Stride for reading through buffers (number of coordinate values multiplied
    // by the size of the data type
    private val vertexStride = COORDS_PER_VERTEX * FLOAT_SIZE // 4 bytes per vertex

    // For testing
    val useGenExample = true

    init {

        // For testing
        if(useGenExample){

            val width = 5
            val height = 6
            var pos = 0
            var dem = DigitalElevationModel().exampleModel(width,height,5)

            for(i in 0 until height){
                for(j in 0 until width){
                    val x = (j - width/2) * (0.1).toFloat()
                    val y = (i - height/2) * (0.1).toFloat()
                    val z = dem[i][j][2] * (-0.1).toFloat()
                    triangleCoords[pos] = x
                    triangleCoords[++pos] = y
                    triangleCoords[++pos] = z
                    System.out.println("X: $x   Y: $y   Z: $z")
                    pos++
                }
            }
            ROWS = height
            COLS = width
        }

        setUpIndices()

        // initialize vertex byte buffer for shape coordinates
        val bb = ByteBuffer.allocateDirect(
                // (number of coordinate values * 4 bytes per float)
                triangleCoords.size * FLOAT_SIZE)

        // use the device hardware's native byte order
        bb.order(ByteOrder.nativeOrder())

        // create a floating point buffer from the ByteBuffer
        vertexBuffer = bb.asFloatBuffer()

        // add the coordinates to the FloatBuffer
        vertexBuffer.put(triangleCoords)

        // set the buffer to read the first coordinate
        vertexBuffer.position(0)

        // initialize index byte buffer for coordinate order
        val ib = ByteBuffer.allocateDirect(
                // (number of indices * 2 bytes per short)
                indices.size * SHORT_SIZE)

        // use the device hardware's native byte order
        ib.order(ByteOrder.nativeOrder())

        // create a short point buffer from the ByteBuffer
        indexBuffer = ib.asShortBuffer()

        // add the indices to the ShortBuffer
        indexBuffer.put(indices)

        // set the buffer to read the first index
        indexBuffer.position(0)

        GLES20.glGenBuffers(1, vbo, 0)
        GLES20.glGenBuffers(1, ibo, 0)

        // VBO
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[0])
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertexBuffer.capacity()
                * 4, vertexBuffer, GLES20.GL_STATIC_DRAW)

        // IBO
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, ibo[0])
        GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, indexBuffer.capacity()
                * SHORT_SIZE, indexBuffer, GLES20.GL_STATIC_DRAW)

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0)
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0)

        val vertexShader = MyGLRenderer().loadShader(GLES20.GL_VERTEX_SHADER,
                vertexShaderCode)
        val fragmentShader = MyGLRenderer().loadShader(GLES20.GL_FRAGMENT_SHADER,
                fragmentShaderCode)

        // create empty OpenGL ES Program
        mProgram = GLES20.glCreateProgram()

        // add the vertex shader to program
        GLES20.glAttachShader(mProgram, vertexShader)

        // add the fragment shader to program
        GLES20.glAttachShader(mProgram, fragmentShader)

        // creates OpenGL ES program executables
        GLES20.glLinkProgram(mProgram)
    }

    fun draw(mvpMatrix: FloatArray) {
        // Add program to OpenGL ES environment
        GLES20.glUseProgram(mProgram)

        // get handle to vertex shader's vPosition member
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition")

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle)

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer)

        // get handle to fragment shader's vColor member
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor")

        // Set color for drawing the triangle
        GLES20.glUniform4fv(mColorHandle, 1, color, 0)

        // get handle to shape's transformation matrix
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix")

        // Pass the projection and view transformation to the shader
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0)

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[0])

        // Draw
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, ibo[0]);
        GLES20.glDrawElements(GLES20.GL_LINE_STRIP, indices.size, GLES20.GL_UNSIGNED_SHORT, 0)

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0)
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0)

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle)
    }

    companion object {
        // The number of bytes in a short
        val SHORT_SIZE = 2

        // The number of bytes in a float
        val FLOAT_SIZE = 4

        // Number of coordinates per vertex in this array
        internal const val COORDS_PER_VERTEX = 3

        // Number of rows of coordinates to be put into the triangle strip
        private var ROWS = 6

        // Number of columns of coordinates to be put into the triangle strip
        internal var COLS = 11

        // ROWS * COLS will equal the total number of coordinates
        internal var triangleCoords = floatArrayOf(
                // in counterclockwise order:
                -0.5f, 0.5f, -0.5f, // 1
                -0.4f, 0.5f, -0.5f, // 2
                -0.3f, 0.5f, -0.5f, // 3
                -0.2f, 0.5f, -0.5f, // 4
                -0.1f, 0.5f, -0.5f, // 5
                0.0f, 0.5f, -0.5f, // 6
                0.1f, 0.5f, -0.5f, // 7
                0.2f, 0.5f, -0.5f, // 8
                0.3f, 0.5f, -0.5f, // 9
                0.4f, 0.5f, -0.5f, // 10
                0.5f, 0.5f, -0.5f, // 11

                -0.5f, 0.4f, -0.5f, // 12
                -0.4f, 0.4f, -0.5f, // 13
                -0.3f, 0.4f, -0.5f, // 14
                -0.2f, 0.4f, -0.5f, // 15
                -0.1f, 0.4f, -0.5f, // 16
                0.0f, 0.4f, -0.5f, // 17
                0.1f, 0.4f, -0.5f, // 18
                0.2f, 0.4f, -0.5f, // 19
                0.3f, 0.4f, -0.5f, // 20
                0.4f, 0.4f, -0.5f, // 21
                0.5f, 0.4f, -0.5f, // 22

                -0.5f, 0.3f, -0.5f, // 23
                -0.4f, 0.3f, -0.5f, // 24
                -0.3f, 0.3f, -0.5f, // 25
                -0.2f, 0.3f, -0.5f, // 26
                -0.1f, 0.3f, -0.3f, // 27
                0.0f, 0.3f, -0.3f, // 28
                0.1f, 0.3f, -0.3f, // 29
                0.2f, 0.3f, -0.5f, // 30
                0.3f, 0.3f, -0.5f, // 31
                0.4f, 0.3f, -0.5f, // 32
                0.5f, 0.3f, -0.5f, // 33

                -0.5f, 0.2f, -0.5f, // 34
                -0.4f, 0.2f, -0.5f, // 35
                -0.3f, 0.2f, -0.5f, // 36
                -0.2f, 0.2f, -0.5f, // 37
                -0.1f, 0.2f, -0.2f, // 38
                0.0f, 0.2f, -0.2f, // 39
                0.1f, 0.2f, -0.1f, // 40
                0.2f, 0.2f, -0.5f, // 41
                0.3f, 0.2f, -0.5f, // 42
                0.4f, 0.2f, -0.5f, // 43
                0.5f, 0.2f, -0.5f, // 44

                -0.5f, 0.1f, -0.5f, // 45
                -0.4f, 0.1f, -0.5f, // 46
                -0.3f, 0.1f, -0.5f, // 47
                -0.2f, 0.1f, -0.5f, // 48
                -0.1f, 0.1f, -0.5f, // 49
                0.0f, 0.1f, -0.5f, // 50
                0.1f, 0.1f, -0.5f, // 51
                0.2f, 0.1f, -0.5f, // 52
                0.3f, 0.1f, -0.5f, // 53
                0.4f, 0.1f, -0.5f, // 54
                0.5f, 0.1f, -0.5f, // 55

                -0.5f, 0.0f, -0.5f, // 56
                -0.4f, 0.0f, -0.5f, // 57
                -0.3f, 0.0f, -0.5f, // 58
                -0.2f, 0.0f, -0.5f, // 59
                -0.1f, 0.0f, -0.5f, // 60
                0.0f, 0.0f, -0.5f, // 61
                0.1f, 0.0f, -0.5f, // 62
                0.2f, 0.0f, -0.5f, // 63
                0.3f, 0.0f, -0.5f, // 64
                0.4f, 0.0f, -0.5f, // 65
                0.5f, 0.0f, -0.5f //66
        )

        // Set array size to be the number of points + the number of points excluding the first
        // last row + 2 for each of these rows
        internal val indices = ShortArray((ROWS * COLS) + ((COLS + 2) * (ROWS - 2)))
    }

    private fun setUpIndices(){
        /* Setting up indices
           This code dynamically sets up an indices array to use when
            drawing the map. It will have to use COLS and ROWS from the map.
            The COLS being the number of vertical rows of points
            The ROWS being the number of horizontal rows of points.

            index:      holds the current position where a new value should
                        be added to the indices array

            posNumber:  holds the current position number in relation
                        the the map coordinates

        */

        // Create array of arrays to hold position numbers for each row
        val posArray = arrayListOf<ShortArray>()

        // Start position number at one
        var posNumber:Short = 0

        // Set array values to position numbers
        for( i in 0 until ROWS){

            // For each row add a new row
            posArray.add(ShortArray(COLS))
            for (j in 0 until COLS){
                posArray[i][j] = posNumber
                posNumber++
            }
        }

        var index = 0

        // For each row excluding last row (exclude last row as the code below uses i + 1 to reference the next row
        // and an array out of bounds error will occur if last row is included)
        for(i in 0 until ROWS-1){

            // For each item in the row
            for(j in 0 until COLS){

                // Set current index to the value of the current row and column
                indices[index] = posArray[i][j]

                // Set next index to the value of the same column on the next row
                indices[index+1] = posArray[i+1][j]
                index += 2

                // If loop has reached the end of a column and it is not the second last row
                if (j == COLS - 1 && i != ROWS - 2){

                    // Set current index to the same as the last value set
                    indices[index] = posArray[i+1][j]

                    // Set the next index to the beginning value of the next row
                    indices[index+1] = posArray[i+1][0]
                    index += 2
                }
            }
        }

        /*System.out.println("\n\n\n\n LIST INDICES: \n\n")
        for (i in 0 until indices.size){
            System.out.println("indices[" + i.toString() + "]: " + indices[i].toString())
        }*/
    }
}