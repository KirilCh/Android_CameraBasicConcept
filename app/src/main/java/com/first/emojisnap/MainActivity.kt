package com.first.emojisnap

import android.app.Activity
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.first.emojisnap.databinding.ActivityMainBinding
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceContour
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import java.io.File

private const val FILE_NAME = "myEmoji.jpg" //Can be later changed to dynamic naming
private const val REQUEST_CODE = 42
private lateinit var photoFile: File

class MainActivity : AppCompatActivity(), ICommunicator
{

    private lateinit var imageView: ImageView
    private lateinit var button: Button
    private lateinit var mBitmap: Bitmap
    private lateinit var mSelectedImage : Bitmap
    private lateinit var mSmily : Bitmap

    private lateinit var mFragment : Fragment

    private var mImageMaxWidth: Int? = null
    private var mImageMaxHeight: Int? = null

    private lateinit var btnTakePicture : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)

        val binding = ActivityMainBinding.inflate(layoutInflater)

        btnTakePicture = binding.btnTakePicture
        //Camera action started upon pressing the button in MainActivity layout
        btnTakePicture.setOnClickListener {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            photoFile = getPhotoFile(FILE_NAME)

            //Requires additional code in AndroidManifest - <provider> block
            //Also requires fileprovider.xml file and folder under "res" folder

            val fileProvider = FileProvider.getUriForFile(
                this,
                "com.application.provider.emojisnap",
                photoFile)
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)

            if (takePictureIntent.resolveActivity(this.packageManager) != null)
            {
                startActivityForResult(takePictureIntent, REQUEST_CODE)
            }
            else
            {
                Toast.makeText(this, "Unable to open camera", Toast.LENGTH_SHORT).show()
            }
        }

        var editFragment = binding.editFragment

        var designFragment = DesignFragment()
        supportFragmentManager.beginTransaction().replace(R.id.editFragment, designFragment).commit()
        /// Yonatan

        imageView = binding.imageView

        //val drawable: Drawable? = ResourcesCompat.getDrawable(resources, R.drawable.smile, null)

//        bitmap = BitmapFactory.decodeResource(resources, R.drawable.smile)
//
        mSmily = BitmapFactory.decodeResource(resources, R.drawable.smiley1)


        setContentView(binding.root)
    }

    private fun getPhotoFile(fileName: String): File
    {
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        val storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(fileName, ".jpg", storageDirectory)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK)
        {
            val takenImage = BitmapFactory.decodeFile(photoFile.absolutePath)

            //Image rotation, to display it the proper way
            val matrix = Matrix()
            matrix.postRotate(270F)
            val scaledBitmap = Bitmap.createScaledBitmap(takenImage, takenImage.width, takenImage.height, true)

            //Bitmap image
            val rotatedBitmap = Bitmap.createBitmap(
                scaledBitmap,
                0,
                0,
                scaledBitmap.width,
                scaledBitmap.height,
                matrix,
                true)

            mBitmap = rotatedBitmap;
            //Setting bitmap image to ImageView to display it on screen
            imageView.setImageBitmap(rotatedBitmap)
        }
        else
        {
            super.onActivityResult(requestCode, resultCode, data)
        }

    }




    // From here - Start Processing The Image
    fun buttonClicked(view: View) {
        runFaceContourDetection(mBitmap)
//        imageView.setImageBitmap(bitmap)
    }

    private fun runFaceContourDetection(iSelectedImage: Bitmap) {
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
                processFaceContourDetectionResult(faces)


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

    private fun processFaceContourDetectionResult(faces: List<Face>) {
        // Task completed successfully
        if (faces.size == 0) {
            showToast("No face found")
            return
        }
        val facesList = ArrayList<FaceDetails>()

        for (i in faces.indices) {
            val face = faces[i]
            facesList.add(FaceDetails(faces[i]))
            Log.d("testing face", face.toString())
            val contour = face.getContour(FaceContour.FACE)

            // testing in console
            contour.points.forEach {
                println("Point at ${it.x}, ${it.y}")
            }


            val (xMidPoint, yMidPoint, width1, hight1) = facesList[i].getCenterOfFacePoint()

            val mutableBitmap =
                (imageView.drawable as BitmapDrawable).bitmap.copy(
                    Bitmap.Config.ARGB_8888, true
                )
            val canvas = Canvas(mBitmap)

            val myPaint = Paint(Paint.ANTI_ALIAS_FLAG)
            myPaint.color = Color.parseColor("#99ff0000")
            val path = Path()
            path.moveTo(contour.points[0].x, contour.points[0].y)

            showToast(contour.points.count().toString())
            path.close()
            canvas.drawPath(path, myPaint)

            val bitmapForImageView : Bitmap = overlay(mBitmap,mSmily,xMidPoint,yMidPoint,width1,hight1) as Bitmap
            imageView.setImageBitmap(bitmapForImageView)

        }

    }

    fun overlay(faceBitmap: Bitmap, smileyBitmap: Bitmap, xMidPoint : Float, yMidPoint : Float, width1 : Float, hight1 : Float): Bitmap? {
        return try {
            val bmOverlay = Bitmap.createBitmap(faceBitmap.width, faceBitmap.height, faceBitmap.config)
            val canvas = Canvas(bmOverlay)
            canvas.drawBitmap(faceBitmap, Matrix(), null)
            //canvas.drawBitmap(smileyBitmap, Matrix(), null)
            val biti : Bitmap = Bitmap.createScaledBitmap(smileyBitmap,width1.toInt() + width1.toInt()/3,hight1.toInt() + hight1.toInt()/2,false)
            canvas.drawBitmap(biti,xMidPoint-biti.width/2,yMidPoint - biti.height/2,null)
            return bmOverlay
        } catch (e: Exception) {
            // TODO: handle exception
            e.printStackTrace()
            return null
        }
    }

    override fun getBitmapFromFragment(imageResource: Int) {
        //Toast.makeText(this, "$imageResource", Toast.LENGTH_SHORT).show()
        mSmily = BitmapFactory.decodeResource(resources,imageResource)
        runFaceContourDetection(mBitmap)

    }


}