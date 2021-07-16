package com.first.emojisnap.model

import android.graphics.Bitmap

interface ICommunicator {
    fun getEyeFromFragment(imageResource: Int)

    fun getNoseFromFragment(imageResource: Int)

    fun getMouthFromFragment(imageResource: Int)

    fun getMustacheFromFragment(imageResource: Int)

    fun getFaceFromFragment(imageResource: Int)
}