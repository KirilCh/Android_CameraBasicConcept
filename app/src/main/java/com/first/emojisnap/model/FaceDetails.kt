package com.first.emojisnap.model

import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceContour
import kotlin.math.abs
import kotlin.math.max

class FaceDetails ( face : Face ) {

    val mFace : Face

    init {
        this.mFace = face
    }

    private fun returnMidPoint(x1Point : Float, x2Point : Float) : Float
    {
        return (x1Point+x2Point)/2
    }

    private fun getSizeImage(x1Point : Float, x2Point : Float) : Float
    {
        return abs(x1Point-x2Point)
    }

    fun getCenterOfFacePoint() : Array<Float>
    {
        val faceContour = mFace.getContour(FaceContour.FACE)
        return getContourByType(faceContour)
    }


    fun getLeftEyePoint() : Array<Float>
    {
        val leftEyeContour = mFace.getContour(FaceContour.LEFT_EYE)
        return getContourByType(leftEyeContour)
    }

    fun getRightEyePoint() : Array<Float>
    {
        val rightEyeContour = mFace.getContour(FaceContour.RIGHT_EYE)
        return getContourByType(rightEyeContour)
    }

    fun getCenterNosePoint() : Array<Float>
    {
        val noseContour = mFace.getContour(FaceContour.NOSE_BRIDGE)
//        val xMidPoint = returnMidPoint(lipContour.points[0].x, lipContour.points[lipContour.points.size/2].x)
//        val yMidPoint = returnMidPoint(lipContour.points[0].y, lipContour.points[lipContour.points.size/2].y)

        return getContourByType(noseContour)
    }

    fun getCenterLipPoint() : Array<Float>
    {
        val upperLipsContour = mFace.getContour(FaceContour.UPPER_LIP_TOP)
        val bottomLipsContour = mFace.getContour(FaceContour.LOWER_LIP_BOTTOM)

        val (xMidPoint,yMidPoint, widthBitmap, heightBitmap) = getContourByType(upperLipsContour)
        val (xMidPoint1,yMidPoint1, widthBitmap1, heightBitmap1) = getContourByType(bottomLipsContour)

        val x = returnMidPoint(xMidPoint1,xMidPoint)
        val y = returnMidPoint(yMidPoint1,yMidPoint)
        val width = (widthBitmap1+widthBitmap)
        val height = max(heightBitmap1,heightBitmap)

        return arrayOf(x,y,width,height+10)
    }

    fun getMustachePoint() : Array<Float>
    {
        val noseBottomContour = mFace.getContour(FaceContour.NOSE_BOTTOM)
        val bottomLipsContour = mFace.getContour(FaceContour.LOWER_LIP_BOTTOM)
        val upperLipsContour = mFace.getContour(FaceContour.UPPER_LIP_TOP)

        val (xMidPoint,yMidPoint, widthBitmap, heightBitmap) = getContourByType(noseBottomContour)
        val (xMidPoint1,yMidPoint1, widthBitmap1, heightBitmap1) = getContourByType(bottomLipsContour)
        val (xMidPoint2,yMidPoint2, widthBitmap2, heightBitmap2) = getContourByType(upperLipsContour)

        val x = returnMidPoint(xMidPoint1,xMidPoint)
        val y = returnMidPoint(yMidPoint1,yMidPoint)
        val width = (widthBitmap1+widthBitmap2+20)
        val height = max(heightBitmap1,heightBitmap2)

        return arrayOf(x,y,width,height)
    }

    private fun getContourByType(faceContour: FaceContour) : Array<Float>
    {
        val xMidPoint = returnMidPoint(faceContour.points[0].x, faceContour.points[faceContour.points.size/2].x)
        val yMidPoint = returnMidPoint(faceContour.points[0].y, faceContour.points[faceContour.points.size/2].y)
        val widthBitmap : Float = getSizeImage(faceContour.points[faceContour.points.size/4].x, faceContour.points[faceContour.points.size/4*3].x)
        val heightBitmap : Float =  getSizeImage(faceContour.points[0].y, faceContour.points[faceContour.points.size/2].y)

        return arrayOf(xMidPoint,yMidPoint, widthBitmap, heightBitmap)
    }
}