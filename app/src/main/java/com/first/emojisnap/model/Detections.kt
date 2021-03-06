package com.first.emojisnap.model

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import com.first.emojisnap.SmileyType
import com.google.mlkit.vision.face.Face

class Detections {


    // From here - Start Processing The Image
    public fun processFaceContourDetectionResult(iSmileyType: SmileyType, iFaces : MutableList<Face>, iSmily : Bitmap, iMutableBitmap : Bitmap, iCurBitmap : Bitmap) : Bitmap {

        val facesList = ArrayList<FaceDetails>()

        var curBitmap = iCurBitmap.copy(Bitmap.Config.ARGB_8888, true)

        for (i in iFaces.indices) {
            facesList.add(FaceDetails(iFaces[i]))

            when (iSmileyType) {
                SmileyType.SMILEY -> {
                    var (xMidPoint, yMidPoint, width, hight) = facesList[i].getCenterOfFacePoint()
                    val bitmapForImageView: Bitmap = overlay(
                            iMutableBitmap, iSmily, xMidPoint, yMidPoint,
                            width + width / 3, hight + hight / 2) as Bitmap
                    curBitmap = bitmapForImageView
                }
                SmileyType.EYE -> {
                    var (xMidPoint, yMidPoint, width, hight) = facesList[i].getLeftEyePoint()
                    width = if(width < 10f ) 15f else width
                    hight = if(hight < 10f ) 15f else hight
                    val bitmapForImageView : Bitmap =
                            overlay(iMutableBitmap, iSmily, xMidPoint, yMidPoint, width, hight) as Bitmap


                    var (xMidPoint1, yMidPoint1, width1, hight1) = facesList[i].getRightEyePoint()
                    width1 = if(width1< 10f ) 15f else width1
                    hight1 = if(hight1 < 10f ) 15f else hight1
                    val bitmapForImageView1 : Bitmap = overlay(
                            bitmapForImageView, iSmily, xMidPoint1, yMidPoint1,
                            width1, hight1) as Bitmap
                    curBitmap = bitmapForImageView1
                }
                SmileyType.NOSE -> {
                    var (xMidPoint, yMidPoint, width, hight) = facesList[i].getCenterNosePoint()
                    val bitmapForImageView: Bitmap = overlay(
                            iMutableBitmap, iSmily, xMidPoint, yMidPoint + 10,
                            hight, hight) as Bitmap
                    curBitmap = bitmapForImageView
                }
                SmileyType.MOUTH -> {
                    var (xMidPoint, yMidPoint, width, hight) = facesList[i].getCenterLipPoint()
                    val bitmapForImageView: Bitmap =
                            overlay(iMutableBitmap, iSmily, xMidPoint, yMidPoint, width, hight) as Bitmap
                    curBitmap = bitmapForImageView
                }
                SmileyType.MUSTACHE -> {
                    var (xMidPoint, yMidPoint, width, hight) = facesList[i].getMustachePoint()
                    val bitmapForImageView: Bitmap =
                            overlay(iMutableBitmap, iSmily, xMidPoint, yMidPoint+30f, width+10, hight) as Bitmap
                    curBitmap = bitmapForImageView
                }
            }
        }
        return curBitmap
    }

    private fun overlay(faceBitmap: Bitmap, smileyBitmap: Bitmap, xMidPoint: Float,
                        yMidPoint: Float, width: Float, height: Float): Bitmap? {
        return try {
            val bmOverlay = Bitmap.createBitmap(faceBitmap.width, faceBitmap.height, faceBitmap.config)
            val canvas = Canvas(bmOverlay)
            canvas.drawBitmap(faceBitmap, Matrix(), null)
            val scaledBitmap: Bitmap = Bitmap.createScaledBitmap(smileyBitmap, width.toInt(), height.toInt(), false)
            canvas.drawBitmap(scaledBitmap, xMidPoint - scaledBitmap.width / 2, yMidPoint - scaledBitmap.height / 2, null)
            return bmOverlay
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}