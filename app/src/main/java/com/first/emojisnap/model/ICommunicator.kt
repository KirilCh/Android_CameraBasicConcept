package com.first.emojisnap.model

import android.graphics.Bitmap

interface ICommunicator {
    fun getBitmapFromFragment(imageResource : Int)

    fun getEyeFromFragment(imageResource: Int)

    fun getNoseFromFragment(imageResource: Int)

    fun getMouthFromFragment(imageResource: Int)

    fun getMustacheFromFragment(imageResource: Int)
}