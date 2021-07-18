package com.first.emojisnap

import android.graphics.*
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.first.emojisnap.Fragments.*
import com.first.emojisnap.databinding.ActivityMainBinding
import com.first.emojisnap.model.Detections
import com.first.emojisnap.model.ICommunicator
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
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

    private var mMainFragment = MainFragment()
    private lateinit var mEditFragment : FrameLayout
    private lateinit var mEditImageFragment : EditImageFragment
    private lateinit var mFeaturesFragment : FaceFeaturesFragment
    private var mFaceDetection = Detections()

    private lateinit var mFacesList : MutableList<Face>
    private var mSmileyOnFaceBoolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val binding = ActivityMainBinding.inflate(layoutInflater)

        mMainFragment.setMainActivity(this)
        supportFragmentManager.beginTransaction().replace(R.id.mainFrame, mMainFragment).commit()

        mFeaturesFragment = FaceFeaturesFragment()
        supportFragmentManager.beginTransaction().replace(R.id.editFragment, mFeaturesFragment).commit()
        mEditFragment = binding.editFragment

        BottomSheetBehavior.from(mEditFragment).apply {
            peekHeight=65
            this.state= BottomSheetBehavior.STATE_COLLAPSED
        }
        mEditFragment.visibility = View.GONE

        setContentView(binding.root)
    }

    override fun getEyeFromFragment(imageResource: Int) {
        val smiley = BitmapFactory.decodeResource(resources, imageResource)
        mCurBitmap = mFaceDetection.processFaceContourDetectionResult(SmileyType.EYE, mFacesList,smiley,returnMutableBitmap(), mCurBitmap)
        mEditImageFragment.changeBitmap(mCurBitmap)
    }

    override fun getNoseFromFragment(imageResource: Int) {
        val smiley = BitmapFactory.decodeResource(resources, imageResource)
        mCurBitmap = mFaceDetection.processFaceContourDetectionResult(SmileyType.NOSE, mFacesList,smiley,returnMutableBitmap(), mCurBitmap)
        mEditImageFragment.changeBitmap(mCurBitmap)
    }

    override fun getMouthFromFragment(imageResource: Int) {
        val smiley = BitmapFactory.decodeResource(resources, imageResource)
        mCurBitmap = mFaceDetection.processFaceContourDetectionResult(SmileyType.MOUTH, mFacesList,smiley,returnMutableBitmap(), mCurBitmap)
        mEditImageFragment.changeBitmap(mCurBitmap)
    }

    override fun getMustacheFromFragment(imageResource: Int) {
        val smiley = BitmapFactory.decodeResource(resources, imageResource)
        mCurBitmap = mFaceDetection.processFaceContourDetectionResult(SmileyType.MUSTACHE, mFacesList,smiley,returnMutableBitmap(), mCurBitmap)
        mEditImageFragment.changeBitmap(mCurBitmap)
    }

    override fun getFaceFromFragment(imageResource: Int) {
        val smiley = BitmapFactory.decodeResource(resources, imageResource)
        mCurBitmap = mFaceDetection.processFaceContourDetectionResult(SmileyType.SMILEY, mFacesList,smiley,returnMutableBitmap(), mCurBitmap)
        mEditImageFragment.changeBitmap(mCurBitmap)
    }

    fun buttonContinueEdit(bitmap: Bitmap) {
        mOriginalBitmap = bitmap
        mWorkingBitmap = mOriginalBitmap
        mCurBitmap = mOriginalBitmap
        mEditImageFragment = EditImageFragment()
        mEditImageFragment.setBitmap(bitmap)
        mEditImageFragment.setMainActivity(this)
        this.supportFragmentManager.beginTransaction().replace(R.id.mainFrame, mEditImageFragment)
            .commit()
        mFeaturesFragment.showFaces()
        mEditFragment.visibility = View.VISIBLE
        mSmileyOnFaceBoolean = true
    }

    fun changeEditFrameToFaces() {
        mWorkingBitmap = mCurBitmap
        mSmileyOnFaceBoolean = true
        mFeaturesFragment.showFaces()
        BottomSheetBehavior.from(mEditFragment).apply {
            this.state= BottomSheetBehavior.STATE_EXPANDED
        }
    }

    fun changeEditFrameToEyes() {
        changeImageIfFaceBoolean(false)
        mFeaturesFragment.showEyes()
    }

    fun changeEditFrameToNoses() {
        changeImageIfFaceBoolean(false)
        mFeaturesFragment.showNoses()
    }

    fun changeEditFrameToMoustaches() {
        changeImageIfFaceBoolean(false)
        mFeaturesFragment.showMoustaches()
    }

    fun changeEditFrameToMouth() {
        changeImageIfFaceBoolean(false)
        mFeaturesFragment.showMouth()
    }

    private fun changeImageIfFaceBoolean(iTurnTo : Boolean)
    {
        if(mSmileyOnFaceBoolean) {
            mWorkingBitmap = mOriginalBitmap
            mCurBitmap = mOriginalBitmap
        } else {
            mWorkingBitmap = mCurBitmap
        }
        mSmileyOnFaceBoolean = iTurnTo

        BottomSheetBehavior.from(mEditFragment).apply {
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
                        mMainFragment.setDisableContinueButton()
                        showToast("No face found")
                    } else {
                        mFacesList = faces
                        mMainFragment.setEableContinueButton()
                    }
                }
                .addOnFailureListener { e -> // Task failed with an exception
                    mMainFragment.setDisableContinueButton()
                    e.printStackTrace()
                }
    }

    private fun returnMutableBitmap(): Bitmap {
        var mutableBitmap : Bitmap

        if(mSmileyOnFaceBoolean) {
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


