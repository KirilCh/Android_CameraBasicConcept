package com.first.emojisnap

import android.graphics.*
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
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

    private lateinit var mainFragment: Fragment
    private lateinit var editFragment : FrameLayout
    private lateinit var editImageFragment : EditImageFragment

    private lateinit var featuersFragment : FaceFeatuersFragment

    private var mFaceBoolean = false


    companion object {
        val IMAGE_REQUEST_CODE = 100
    }

    // save image

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val binding = ActivityMainBinding.inflate(layoutInflater)

        mainFragment = MainFragment()
        (mainFragment as MainFragment).setMainActivity(this)
        supportFragmentManager.beginTransaction().replace(R.id.mainFrame, mainFragment).commit()


        var designFragment = DesignFragment()
        //supportFragmentManager.beginTransaction().replace(R.id.editFragment, designFragment).commit()

        featuersFragment = FaceFeatuersFragment()
        supportFragmentManager.beginTransaction().replace(R.id.editFragment, featuersFragment).commit()
        editFragment = binding.editFragment


        BottomSheetBehavior.from(editFragment).apply {
            peekHeight=65
            this.state= BottomSheetBehavior.STATE_COLLAPSED
        }
        editFragment.visibility = View.GONE

        mSmily = BitmapFactory.decodeResource(resources, R.drawable.smiley1)

        setContentView(binding.root)
    }


    // From here - Start Processing The Image

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
                processFaceContourDetectionResult(faces, smileyType)
            }
            .addOnFailureListener { e -> // Task failed with an exception
                //button.setEnabled(true)
                e.printStackTrace()
            }
    }

    private fun showToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
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
                        mutableBitmap,
                        mSmily,
                        xMidPoint,
                        yMidPoint,
                        width + width / 3,
                        hight + hight / 2
                    ) as Bitmap
                    mCurBitmap = bitmapForImageView
                }
                SmileyType.EYE -> {
                    var (xMidPoint, yMidPoint, width, hight) = facesList[i].getLeftEyePoint()
                    width = if(width< 10f ) 30f else width
                    hight = if(hight < 10f ) 30f else hight
                    val bitmapForImageView : Bitmap =
                        overlay(mutableBitmap, mSmily, xMidPoint, yMidPoint, width, hight) as Bitmap


                    var (xMidPoint1, yMidPoint1, width1, hight1) = facesList[i].getRightEyePoint()
                    width1 = if(width1< 10f ) 30f else width1
                    hight1 = if(hight1 < 10f ) 30f else hight1
                    val bitmapForImageView1 : Bitmap = overlay(
                        bitmapForImageView,
                        mSmily,
                        xMidPoint1,
                        yMidPoint1,
                        width1,
                        hight1
                    ) as Bitmap
                    mCurBitmap = bitmapForImageView1
                }
                SmileyType.NOSE -> {
                    var (xMidPoint, yMidPoint, width, hight) = facesList[i].getCenterNosePoint()
                    val bitmapForImageView: Bitmap = overlay(
                        mutableBitmap,
                        mSmily,
                        xMidPoint,
                        yMidPoint + 10,
                        hight,
                        hight
                    ) as Bitmap
                    mCurBitmap = bitmapForImageView
                }
                SmileyType.MOUTH -> {
                    var (xMidPoint, yMidPoint, width, hight) = facesList[i].getCenterLipPoint()
                    val bitmapForImageView: Bitmap =
                        overlay(mutableBitmap, mSmily, xMidPoint, yMidPoint, width, hight) as Bitmap
                    //imageView.setImageBitmap(bitmapForImageView)
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

    fun overlay(
        faceBitmap: Bitmap,
        smileyBitmap: Bitmap,
        xMidPoint: Float,
        yMidPoint: Float,
        width: Float,
        height: Float
    ): Bitmap? {
        return try {
            val bmOverlay =
                Bitmap.createBitmap(faceBitmap.width, faceBitmap.height, faceBitmap.config)
            val canvas = Canvas(bmOverlay)
            canvas.drawBitmap(faceBitmap, Matrix(), null)
            //canvas.drawBitmap(smileyBitmap, Matrix(), null)
            val biti: Bitmap =
                Bitmap.createScaledBitmap(smileyBitmap, width.toInt(), height.toInt(), false)
            canvas.drawBitmap(biti, xMidPoint - biti.width / 2, yMidPoint - biti.height / 2, null)
            return bmOverlay
        } catch (e: Exception) {
            // TODO: handle exception
            e.printStackTrace()
            return null
        }
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
        featuersFragment.showFaces()
        editFragment.visibility = View.VISIBLE
        mFaceBoolean = true
    }

    fun changeEditFrameToFaces() {
        mWorkingBitmap = mCurBitmap
        mFaceBoolean = true
        featuersFragment.showFaces()
        BottomSheetBehavior.from(editFragment).apply {
            this.state= BottomSheetBehavior.STATE_EXPANDED
        }
    }

    fun changeEditFrameToEyes() {
        changeImageIfFaceBoolean(false)
        featuersFragment.showEyes()

    }

    fun changeEditFrameToNoses() {
        changeImageIfFaceBoolean(false)
        featuersFragment.showNoses()
    }

    fun changeEditFrameToMoustaches() {
        changeImageIfFaceBoolean(false)
        featuersFragment.showMoustaches()
    }

    fun changeEditFrameToMouth() {
        changeImageIfFaceBoolean(false)
        featuersFragment.showMouth()
    }

    fun changeImageIfFaceBoolean(iTurnTo : Boolean)
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
}


