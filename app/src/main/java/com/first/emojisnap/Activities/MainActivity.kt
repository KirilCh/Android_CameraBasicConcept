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
        runFaceContourDetection(SmileyType.EYE)
        editImageFragment.changeBitmap(mCurBitmap)
    }

    override fun getNoseFromFragment(imageResource: Int) {
        mSmily = BitmapFactory.decodeResource(resources, imageResource)
        runFaceContourDetection(SmileyType.NOSE)
        editImageFragment.changeBitmap(mCurBitmap)
    }

    override fun getMouthFromFragment(imageResource: Int) {
        mSmily = BitmapFactory.decodeResource(resources, imageResource)
        runFaceContourDetection(SmileyType.MOUTH)
        editImageFragment.changeBitmap(mCurBitmap)
    }

    override fun getMustacheFromFragment(imageResource: Int) {
        mSmily = BitmapFactory.decodeResource(resources, imageResource)
        runFaceContourDetection(SmileyType.MUSTACHE)
        editImageFragment.changeBitmap(mCurBitmap)
    }

    override fun getFaceFromFragment(imageResource: Int) {
        mSmily = BitmapFactory.decodeResource(resources, imageResource)
        runFaceContourDetection(SmileyType.SMILEY)
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
        if(mFaceBoolean)
        {
            mWorkingBitmap = mOriginalBitmap
            mCurBitmap = mOriginalBitmap
        }
        else {
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
                .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
                .build()
        val detector = FaceDetection.getClient(options)
        detector.process(image)
                .addOnSuccessListener { faces ->
                    if (faces.size==0)
                    {
                        mainFragment.setDisableContinueButton()
                    }
                    else
                    {
                        mFaces = faces
                        mainFragment.setEableContinueButton()
                    }

                }
                .addOnFailureListener { e -> // Task failed with an exception
                    //button.setEnabled(true)
                    e.printStackTrace()
                }
    }

    private fun runFaceContourDetection(smileyType: SmileyType) {
        val image = InputImage.fromBitmap(mOriginalBitmap, 0)
        val options = FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
                .build()
        //button.setEnabled(false)
        val detector = FaceDetection.getClient(options)
        detector.process(image)
                .addOnSuccessListener { faces ->
                    //button.setEnabled(true)
                    if (faces.size==0)
                    {
                        //mainFragment.setDisableContinueButton()
                    }
                    else
                    {
                        processFaceContourDetectionResult(faces, smileyType)
                        //mainFragment.setEableContinueButton()
                    }

                }
                .addOnFailureListener { e -> // Task failed with an exception
                    //button.setEnabled(true)
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


    // From here - Start Processing The Image
    private fun processFaceContourDetectionResult(faces: List<Face>, smileyType: SmileyType) {
        // Task completed successfully
        if (faces.isEmpty()) {
            showToast("No face found")
            return
        }
        val facesList = ArrayList<FaceDetails>()

        for (i in faces.indices) {
            facesList.add(FaceDetails(faces[i]))

            var mutableBitmap : Bitmap = returnMutableBitmap()

            when (smileyType) {
                SmileyType.SMILEY -> {
                    var (xMidPoint, yMidPoint, width, hight) = facesList[i].getCenterOfFacePoint()
                    val bitmapForImageView: Bitmap = overlay(
                            mutableBitmap, mSmily, xMidPoint, yMidPoint,
                            width + width / 3, hight + hight / 2) as Bitmap
                    mCurBitmap = bitmapForImageView
                }
                SmileyType.EYE -> {
                    var (xMidPoint, yMidPoint, width, hight) = facesList[i].getLeftEyePoint()
                    width = if(width < 10f ) 20f else width
                    hight = if(hight < 10f ) 20f else hight
                    val bitmapForImageView : Bitmap =
                            overlay(mutableBitmap, mSmily, xMidPoint, yMidPoint, width, hight) as Bitmap


                    var (xMidPoint1, yMidPoint1, width1, hight1) = facesList[i].getRightEyePoint()
                    width1 = if(width1< 10f ) 20f else width1
                    hight1 = if(hight1 < 10f ) 20f else hight1
                    val bitmapForImageView1 : Bitmap = overlay(
                            bitmapForImageView, mSmily, xMidPoint1, yMidPoint1,
                            width1, hight1) as Bitmap
                    mCurBitmap = bitmapForImageView1
                }
                SmileyType.NOSE -> {
                    var (xMidPoint, yMidPoint, width, hight) = facesList[i].getCenterNosePoint()
                    val bitmapForImageView: Bitmap = overlay(
                            mutableBitmap, mSmily, xMidPoint, yMidPoint + 10,
                            hight, hight) as Bitmap
                    mCurBitmap = bitmapForImageView
                }
                SmileyType.MOUTH -> {
                    var (xMidPoint, yMidPoint, width, hight) = facesList[i].getCenterLipPoint()
                    val bitmapForImageView: Bitmap =
                            overlay(mutableBitmap, mSmily, xMidPoint, yMidPoint, width, hight) as Bitmap
                    mCurBitmap = bitmapForImageView
                }
                SmileyType.MUSTACHE -> {
                    var (xMidPoint, yMidPoint, width, hight) = facesList[i].getMustachePoint()
                    val bitmapForImageView: Bitmap =
                            overlay(mutableBitmap, mSmily, xMidPoint, yMidPoint, width, hight) as Bitmap
                    mCurBitmap = bitmapForImageView
                }
            }
        }
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

    private fun showToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }
}


