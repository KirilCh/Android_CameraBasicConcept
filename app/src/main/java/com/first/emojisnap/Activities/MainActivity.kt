package com.first.emojisnap

import android.app.Activity
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
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
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.fragment_half_show_edit.view.*
import java.io.File
import java.io.InputStream

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

    private lateinit var imageView: ImageView
    private lateinit var button: Button
    private lateinit var mBitmap: Bitmap
    private lateinit var mSelectedImage: Bitmap
    private lateinit var mSmily: Bitmap

    private lateinit var mFragment: Fragment

    private var mImageMaxWidth: Int? = null
    private var mImageMaxHeight: Int? = null

    private lateinit var mBtnTakePicture: Button
    private lateinit var mBtnChooseFromLibrary: Button

    private lateinit var mainFragment: Fragment


    companion object {
        val IMAGE_REQUEST_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val binding = ActivityMainBinding.inflate(layoutInflater)

        mainFragment = MainFragment()
        (mainFragment as MainFragment).setMainActivity(this)
        supportFragmentManager.beginTransaction().replace(R.id.mainFrame, mainFragment).commit()


        var designFragment = DesignFragment()
        //supportFragmentManager.beginTransaction().replace(R.id.editFragment, designFragment).commit()

        var featuersFragment = FaceFeatuersFragment()
        //supportFragmentManager.beginTransaction().replace(R.id.editFrame, featuersFragment).commit()




        var editFragment : View = view.findViewById(R.id.editFrame)
        BottomSheetBehavior.from(editFragment).apply {
            peekHeight=65
            this.state= BottomSheetBehavior.STATE_COLLAPSED
        }

        mSmily = BitmapFactory.decodeResource(resources, R.drawable.smiley1)

        setContentView(binding.root)
    }


    // From here - Start Processing The Image

    private fun runFaceContourDetection(iSelectedImage: Bitmap, smileyType: SmileyType) {
        val image = InputImage.fromBitmap(iSelectedImage, 0)
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
        //imageView.setImageBitmap()
    }

    private fun showToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    private fun processFaceContourDetectionResult(faces: List<Face>, smileyType: SmileyType) {
        // Task completed successfully
        if (faces.size == 0) {
            showToast("No face found")
            return
        }
        val facesList = ArrayList<FaceDetails>()

        for (i in faces.indices) {
            facesList.add(FaceDetails(faces[i]))

            val mutableBitmap =
                (imageView.drawable as BitmapDrawable).bitmap.copy(
                    Bitmap.Config.ARGB_8888, true
                )

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
                    imageView.setImageBitmap(bitmapForImageView)
                }
                SmileyType.EYE -> {
                    var (xMidPoint, yMidPoint, width, hight) = facesList[i].getLeftEyePoint()
                    val bitmapForImageView: Bitmap =
                        overlay(mutableBitmap, mSmily, xMidPoint, yMidPoint, width, hight) as Bitmap

                    var (xMidPoint1, yMidPoint1, width1, hight1) = facesList[i].getRightEyePoint()
                    val bitmapForImageView1: Bitmap = overlay(
                        bitmapForImageView,
                        mSmily,
                        xMidPoint1,
                        yMidPoint1,
                        width1,
                        hight1
                    ) as Bitmap
                    imageView.setImageBitmap(bitmapForImageView1)
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
                    imageView.setImageBitmap(bitmapForImageView)
                }
                SmileyType.MOUTH -> {
                    var (xMidPoint, yMidPoint, width, hight) = facesList[i].getCenterLipPoint()
                    val bitmapForImageView: Bitmap =
                        overlay(mutableBitmap, mSmily, xMidPoint, yMidPoint, width, hight) as Bitmap
                    imageView.setImageBitmap(bitmapForImageView)
                }
                SmileyType.MUSTACHE -> {
                    var (xMidPoint, yMidPoint, width, hight) = facesList[i].getMustachePoint()
                    val bitmapForImageView: Bitmap =
                        overlay(mutableBitmap, mSmily, xMidPoint, yMidPoint, width, hight) as Bitmap
                    imageView.setImageBitmap(bitmapForImageView)
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

    override fun getBitmapFromFragment(imageResource: Int) {
        mSmily = BitmapFactory.decodeResource(resources, imageResource)
        runFaceContourDetection(mBitmap, SmileyType.SMILEY)
    }

    override fun getEyeFromFragment(imageResource: Int) {
        mSmily = BitmapFactory.decodeResource(resources, imageResource)
        runFaceContourDetection(mBitmap, SmileyType.EYE)
    }

    override fun getNoseFromFragment(imageResource: Int) {
        mSmily = BitmapFactory.decodeResource(resources, imageResource)
        runFaceContourDetection(mBitmap, SmileyType.NOSE)
    }

    override fun getMouthFromFragment(imageResource: Int) {
        mSmily = BitmapFactory.decodeResource(resources, imageResource)
        runFaceContourDetection(mBitmap, SmileyType.MOUTH)
    }

    override fun getMustacheFromFragment(imageResource: Int) {
        mSmily = BitmapFactory.decodeResource(resources, imageResource)
        runFaceContourDetection(mBitmap, SmileyType.MUSTACHE)
    }

    fun buttonContinueEdit(bitmap: Bitmap) {
        var editImageFragment = EditImageFragment()
        editImageFragment.setBitmap(bitmap)
        this.supportFragmentManager.beginTransaction().replace(R.id.mainFrame, editImageFragment)
            .commit()

//        var featuersFragment = FaceFeatuersFragment()

    }
}


