package com.first.emojisnap

import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceContour

class FaceDetails ( face : Face ) {

    val mFace : Face
//    val faceContour : FaceContour
//    val leftEyeContour : FaceContour
//    val rightEyeContour : FaceContour
//    val lipsContour : FaceContour

    init {
//        this.faceContour = face.getContour(FaceContour.FACE)
//        this.leftEyeContour = face.getContour(FaceContour.LEFT_EYE)
//        this.rightEyeContour = face.getContour(FaceContour.RIGHT_EYE)
//        this.lipsContour = face.getContour(FaceContour.UPPER_LIP_BOTTOM)

        this.mFace = face

    }

    private fun returnMidPoint(x1Point : Float, x2Point : Float) : Float
    {
        return (x1Point+x2Point)/2
    }

    fun getSizeImage(x1Point : Float, x2Point : Float) : Float
    {
        return Math.abs(x1Point-x2Point)
    }

    fun getCenterOfFacePoint() : Array<Float>
    {
        val faceContour = mFace.getContour(FaceContour.FACE)
        val xMidPoint = returnMidPoint(faceContour.points[0].x, faceContour.points[faceContour.points.size/2].x)
        val yMidPoint = returnMidPoint(faceContour.points[0].y, faceContour.points[faceContour.points.size/2].y)
        val widthBitmap : Float = getSizeImage(faceContour.points[faceContour.points.size/4].x, faceContour.points[faceContour.points.size/4*3].x)
        val heightBitmap : Float =  getSizeImage(faceContour.points[0].y, faceContour.points[faceContour.points.size/2].y)

        return arrayOf(xMidPoint,yMidPoint, widthBitmap, heightBitmap)
    }


    fun getLeftEyePoint() : Pair<Float, Float>
    {
        val leftEyeContour = mFace.getContour(FaceContour.LEFT_EYE)
        val xMidPoint = returnMidPoint(leftEyeContour.points[0].x, leftEyeContour.points[leftEyeContour.points.size/2].x)
        val yMidPoint = returnMidPoint(leftEyeContour.points[0].y, leftEyeContour.points[leftEyeContour.points.size/2].y)

        return Pair(xMidPoint,yMidPoint)
    }

    fun getRightEyePoint() : Pair<Float, Float>
    {
        val rightEyeContour = mFace.getContour(FaceContour.RIGHT_EYE)
        val xMidPoint = returnMidPoint(rightEyeContour.points[0].x, rightEyeContour.points[rightEyeContour.points.size/2].x)
        val yMidPoint = returnMidPoint(rightEyeContour.points[0].y, rightEyeContour.points[rightEyeContour.points.size/2].y)

        return Pair(xMidPoint,yMidPoint)
    }

    fun getCenterLipPoint() : Pair<Float, Float>
    {
        val lipContour = mFace.getContour(FaceContour.UPPER_LIP_BOTTOM)
        val xMidPoint = returnMidPoint(lipContour.points[0].x, lipContour.points[lipContour.points.size/2].x)
        val yMidPoint = returnMidPoint(lipContour.points[0].y, lipContour.points[lipContour.points.size/2].y)

        return Pair(xMidPoint,yMidPoint)
    }

}