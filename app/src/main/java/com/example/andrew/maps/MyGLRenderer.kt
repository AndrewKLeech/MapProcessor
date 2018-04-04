package com.example.andrew.maps


import android.opengl.GLES20
import javax.microedition.khronos.opengles.GL10
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import javax.microedition.khronos.egl.EGLConfig
import android.opengl.Matrix.frustumM
import android.os.SystemClock


/**
 * Created by Andrew on 15/03/2018.
 */
class MyGLRenderer : GLSurfaceView.Renderer {

    private var mMap: Map? = null

    // mMVPMatrix is an abbreviation for "Model View Projection Matrix"
    private val mMVPMatrix = FloatArray(16)
    private val mProjectionMatrix = FloatArray(16)
    private val mViewMatrix = FloatArray(16)

    // For rotation
    private var mRotationMatrix: FloatArray = FloatArray(16)


    @Volatile
    var mAngle: Float = 0.toFloat()

    fun getAngle(): Float {
        return mAngle
    }

    fun setAngle(angle: Float) {
        mAngle = angle
    }

    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
        // Set the background frame color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)

        // initialize a triangle
        mMap = Map()

    }

    fun loadShader(type: Int, shaderCode: String): Int {

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        val shader = GLES20.glCreateShader(type)

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode)
        GLES20.glCompileShader(shader)

        return shader
    }

    fun trackball(va: FloatArray, vb: FloatArray){


    }
    override fun onDrawFrame(unused: GL10) {
        // for rotation
        var scratch: FloatArray =  FloatArray(16)

        // Redraw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        // Set the camera position (View matrix)
        Matrix.setLookAtM(mViewMatrix, 0, 0F, 0f, -5f, 0f, 0f, 0f, 0f, 1.0f, 0.0f)

        // Calculate the projection and view transformation
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0)

        // Create a rotation transformation for the triangle
        // var time: Long = SystemClock.uptimeMillis() % 4000L
        // var angle: Float = 0.090f * (time.toInt())

        //mAngle mult by -1 to spin same way as touch input
        Matrix.setRotateM(mRotationMatrix, 0, mAngle*-1, 1f, 1f, 1.0f)

        // Combine the rotation matrix with the projection and camera view
        // Note that the mMVPMatrix factor *must be first* in order
        // for the matrix multiplication product to be correct.
        Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mRotationMatrix, 0)

        // Draw triangle
        mMap!!.draw(scratch)
    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)

        val ratio = width.toFloat() / height.toFloat()
        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 7f)
    }
}