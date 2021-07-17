package com.first.emojisnap

import android.graphics.*
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.first.emojisnap.Fragments.*
import com.first.emojisnap.databinding.ActivityMainBinding
import com.first.emojisnap.model.Detections
import com.first.emojisnap.model.FaceDetails
import com.first.emojisnap.model.ICommunicator
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import kotlinx.android.synthetic.main.fragment_main.*
import java.io.File

private const val FILE_NAME = "myEmoji.jpg" //Can be later changed to dynamic naming
private const val REQUEST_CODE = 42
private lateinit var photoFile: File

enum class SmileyType {
    SMILEY,
    EYE,
    MOUTH,
    NOSE,
    MUSTACHE
}

class MainActivity : AppCompatActivity(), ICommunicator {

    private lateinit var mOriginalBitmap: Bitmap
    private lateinit var mWorkingBitmap: Bitmap
    private lateinit var mCurBitmap: Bitmap
    private lateinit var mSmily: Bitmap

    private var mainFragment = MainFragment()
    private lateinit var editFragment : FrameLayout
    private lateinit var editImageFragment : EditImageFragment
    private lateinit var featuresFragment : FaceFeaturesFragment
    private var mFaceDetection = Detections()

    private lateinit var mFaces : MutableList<Face>

    private var mFaceBoolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val binding = ActivityMainBinding.inflate(layoutInflater)

        mainFragment.setMainActivity(this)
        supportFragmentManager.beginTransaction().replace(R.id.mainFrame, mainFragment).commit()

        featuresFragment = FaceFeaturesFragment()
        supportFragmentManager.beginTransaction().replace(R.id.editFragment, featuresFragment).commit()
        editFragment = binding.editFragment

        BottomSheetBehavior.from(editFragment).apply {
            peekHeight=65
            this.state= BottomSheetBehavior.STATE_COLLAPSED
        }
        editFragment.visibility = View.GONE

        setContentView(binding.root)
    }

    override fun getEyeFromFragment(imageResource: Int) {
        mSmily = BitmapFactory.decodeResource(resources, imageResource)
        mCurBitmap = mFaceDetection.processFaceContourDetectionResult(SmileyType.EYE,mFaces,mSmily,returnMutableBitmap(), mCurBitmap)
        editImageFragment.changeBitmap(mCurBitmap)
    }

    override fun getNoseFromFragment(imageResource: Int) {
        mSmily = BitmapFactory.decodeResource(resources, imageResource)
        mCurBitmap = mFaceDetection.processFaceContourDetectionResult(SmileyType.NOSE,mFaces,mSmily,returnMutableBitmap(), mCurBitmap)
        editImageFragment.changeBitmap(mCurBitmap)
    }

    override fun getMouthFromFragment(imageResource: Int) {
        mSmily = BitmapFactory.decodeResource(resources, imageResource)
        mCurBitmap = mFaceDetection.processFaceContourDetectionResult(SmileyType.MOUTH,mFaces,mSmily,returnMutableBitmap(), mCurBitmap)
        editImageFragment.changeBitmap(mCurBitmap)
    }

    override fun getMustacheFromFragment(imageResource: Int) {
        mSmily = BitmapFactory.decodeResource(resources, imageResource)
        mCurBitmap = mFaceDetection.processFaceContourDetectionResult(SmileyType.MUSTACHE,mFaces,mSmily,returnMutableBitmap(), mCurBitmap)
        editImageFragment.changeBitmap(mCurBitmap)
    }

    override fun getFaceFromFragment(imageResource: Int) {
        mSmily = BitmapFactory.decodeResource(resources, imageResource)
        mCurBitmap = mFaceDetection.processFaceContourDetectionResult(SmileyType.SMILEY,mFaces,mSmily,returnMutableBitmap(), mCurBitmap)
        editImageFragment.changeBitmap(mCurBitmap)
    }

    fun buttonContinueEdit(bitmap: Bitmap) {
        mOriginalBitmap = bitmap
        mWorkingBitmap = mOriginalBitmap
        mCurBitmap = mOriginalBitmap
        editImageFragment = EditImageFragment()
        editImageFragment.setBitmap(bitmap)
        editImageFragment.setMainActivity(this)
        this.supportFragmentManager.beginTransaction().replace(R.id.mainFrame, editImageFragment)
            .commit()
        featuresFragment.showFaces()
        editFragment.visibility = View.VISIBLE
        mFaceBoolean = true
    }

    fun changeEditFrameToFaces() {
        mWorkingBitmap = mCurBitmap
        mFaceBoolean = true
        featuresFragment.showFaces()
        BottomSheetBehavior.from(editFragment).apply {
            this.state= BottomSheetBehavior.STATE_EXPANDED
        }
    }

    fun changeEditFrameToEyes() {
        changeImageIfFaceBoolean(false)
        featuresFragment.showEyes()
    }

    fun changeEditFrameToNoses() {
        changeImageIfFaceBoolean(false)
        featuresFragment.showNoses()
    }

    fun changeEditFrameToMoustaches() {
        changeImageIfFaceBoolean(false)
        featuresFragment.showMoustaches()
    }

    fun changeEditFrameToMouth() {
        changeImageIfFaceBoolean(false)
        featuresFragment.showMouth()
    }

    private fun changeImageIfFaceBoolean(iTurnTo : Boolean)
    {
        if(mFaceBoolean) {
            mWorkingBitmap = mOriginalBitmap
            mCurBitmap = mOriginalBitmap
        } else {
            mWorkingBitmap = mCurBitmap
        }
        mFaceBoolean = iTurnTo

        BottomSheetBehavior.from(editFragment).apply {
            this.state= BottomSheetBehavior.STATE_EXPANDED
        }
    }

    fun getOriginalBitmap(): Bitmap {
        mWorkingBitmap = mOriginalBitmap
        mCurBitmap = mOriginalBitmap
        return mOriginalBitmap
    }

    fun checkIfExist(bitmap: Bitmap) {
        val image = InputImage.fromBitmap(bitmap, 0)
        val options = FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                .setContourMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                .build()
        val detector = FaceDetection.getClient(options)
        detector.process(image).addOnSuccessListener { faces ->
                    if (faces.size==0) {
                        mainFragment.setDisableContinueButton()
                        showToast("No face found")
                    } else {
                        mFaces = faces
                        mainFragment.setEableContinueButton()
                    }
                }
                .addOnFailureListener { e -> // Task failed with an exception
                    mainFragment.setDisableContinueButton()
                    e.printStackTrace()
                }
    }

    private fun returnMutableBitmap(): Bitmap {
        var mutableBitmap : Bitmap

        if(mFaceBoolean) {
            mutableBitmap = mOriginalBitmap.copy(Bitmap.Config.ARGB_8888, true)
        }
        else {
            mutableBitmap = mWorkingBitmap.copy(Bitmap.Config.ARGB_8888, true)
        }
        return mutableBitmap
    }

    private fun showToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }
}


