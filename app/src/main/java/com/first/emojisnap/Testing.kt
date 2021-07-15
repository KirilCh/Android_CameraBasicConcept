package com.first.emojisnap

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.util.Pair
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.*
import com.google.mlkit.vision.face.R
import java.io.IOException
import java.io.InputStream

class Testing {



/*    private lateinit var imageView: ImageView
    private lateinit var button: Button
    private lateinit var bitmap: Bitmap
    private lateinit var mSelectedImage : Bitmap
    private lateinit var mSmily : Bitmap

    private var mImageMaxWidth: Int? = null
    private var mImageMaxHeight: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        imageView = binding.imageView

        //val drawable: Drawable? = ResourcesCompat.getDrawable(resources, R.drawable.smile, null)

        bitmap = BitmapFactory.decodeResource(resources, R.drawable.smile)

        mSmily = BitmapFactory.decodeResource(resources, R.drawable.smily)
        //imageView.setImageDrawable(drawable)


        //////////////////////
        // High-accuracy landmark detection and face classification
        val highAccuracyOpts = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .build()

// Real-time contour detection
        val realTimeOpts = FaceDetectorOptions.Builder()
            .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
            .build()
        //////////////////////


        val image = InputImage.fromBitmap(bitmap, 0)
        imageView.setImageBitmap(bitmap)

        //val detector = FaceDetection.getClient(highAccuracyOpts)

//        var result = detector.process(image)
//            .addOnSuccessListener { faces ->
//                // Task completed successfully
//                // ...
//                for (face in faces) {
//                    val bounds = face.boundingBox
//                    val rotY = face.headEulerAngleY // Head is rotated to the right rotY degrees
//                    val rotZ = face.headEulerAngleZ // Head is tilted sideways rotZ degrees
//                    val canvas =
//                    Log.d("testing", bounds.toString())
//                    Log.d("testing", rotY.toString())
//                    Log.d("testing", rotZ.toString())
//
//                    // If landmark detection was enabled (mouth, ears, eyes, cheeks, and
//                    // nose available):
//                    val leftEar = face.getLandmark(FaceLandmark.LEFT_EAR)
//                    leftEar?.let {
//                        val leftEarPos = leftEar.position
//                    }
//
//                    // If contour detection was enabled:
//                    val leftEyeContour = face.getContour(FaceContour.LEFT_EYE)?.points
//                    val upperLipBottomContour =
//                        face.getContour(FaceContour.UPPER_LIP_BOTTOM)?.points
//
//
//
//                    // If classification was enabled:
//                    if (face.smilingProbability != null) {
//                        val smileProb = face.smilingProbability
//                    }
//                    if (face.rightEyeOpenProbability != null) {
//                        val rightEyeOpenProb = face.rightEyeOpenProbability
//                    }
//
//
//                    // If face tracking was enabled:
//                    if (face.trackingId != null) {
//                        val id = face.trackingId
//                    }
//                }
//            }
//            .addOnFailureListener { e ->
//                // Task failed with an exception
//                // ...
//            }
//
//        Log.d("testing res", result.toString())
//        imageView.setImageBitmap(bitmap)
        setContentView(binding.root)
    }

    fun buttonClicked(view: View) {

        runFaceContourDetection(bitmap)
//        imageView.setImageBitmap(bitmap)
    }

    private fun runFaceContourDetection(mSelectedImage: Bitmap) {
        val image = InputImage.fromBitmap(mSelectedImage, 0)
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

    private fun processFaceContourDetectionResult(faces: List<Face>) {
        // Task completed successfully
        if (faces.size == 0) {
            showToast("No face found")
            return
        }
        val facesList = ArrayList<FaceDetails>()
        //mGraphicOverlay.clear()
        for (i in faces.indices) {
            val face = faces[i]
            facesList.add(FaceDetails(faces[i]))
            Log.d("testing face", face.toString())
            val contour = face.getContour(FaceContour.FACE)
            contour.points.forEach {
                println("Point at ${it.x}, ${it.y}")

            }


            // UPPER_LIP_BOTTOM middle detection
//            val upperLipBottom = face.getContour(FaceContour.UPPER_LIP_BOTTOM)
//            val xMidPoint = returnMidPoint(upperLipBottom.points[0].x, upperLipBottom.points[upperLipBottom.points.size-1].x)
//            val yMidPoint = returnMidPoint(upperLipBottom.points[0].y, upperLipBottom.points[upperLipBottom.points.size-1].y)
//            Log.d("testing1", xMidPoint.toString())
//            Log.d("testing1", yMidPoint.toString())

            val (xMidPoint, yMidPoint, width1, hight1) = facesList[i].getCenterOfFacePoint()

            val mutableBitmap =
                (imageView.drawable as BitmapDrawable).bitmap.copy(
                    Bitmap.Config.ARGB_8888, true
                )
            val canvas = Canvas(mutableBitmap)

            val myPaint = Paint(Paint.ANTI_ALIAS_FLAG)
            myPaint.color = Color.parseColor("#99ff0000")
            val path = Path()
            path.moveTo(contour.points[0].x, contour.points[0].y)
//            contour.points.forEach {
//                path.lineTo(it.x, it.y)
//            }
//            path.lineTo(contour.points[contour.points.size-1].x, contour.points[contour.points.size-1].y)
//            path.lineTo(contour.points[18].x, contour.points[18].y)
//            println("Point at 1 ${contour.points[contour.points.size-1].x}, ${contour.points[contour.points.size-1].y}")
//            println("Point at 1 ${contour.points[18].x}, ${contour.points[18].y}")
//            for (i in 1..10)
//            {
//                path.lineTo(contour.points[i].x, contour.points[i].y)
//            }
            showToast(contour.points.count().toString())
            path.close()
            canvas.drawPath(path, myPaint)

            imageView.setImageBitmap(overlay(mutableBitmap,mSmily,xMidPoint,yMidPoint,width1,hight1))
            //imageView.setImageBitmap(mutableBitmap)
        }

    }


    fun overlay(faceBitmap: Bitmap, smileyBitmap: Bitmap, xMidPoint : Float, yMidPoint : Float, width1 : Float, hight1 : Float): Bitmap? {
        return try {
            val bmOverlay = Bitmap.createBitmap(faceBitmap.width, faceBitmap.height, faceBitmap.config)
            val canvas = Canvas(bmOverlay)
            canvas.drawBitmap(faceBitmap, Matrix(), null)
            //canvas.drawBitmap(smileyBitmap, Matrix(), null)
            val biti : Bitmap = Bitmap.createScaledBitmap(smileyBitmap,width1.toInt(),hight1.toInt(),false)
            canvas.drawBitmap(biti,xMidPoint-biti.width/2,yMidPoint - biti.height/2,null)
            return bmOverlay
        } catch (e: Exception) {
            // TODO: handle exception
            e.printStackTrace()
            return null
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    // Functions for loading images from app assets.

    // Functions for loading images from app assets.
    // Returns max image width, always for portrait mode. Caller needs to swap width / height for
    // landscape mode.
    private fun getImageMaxWidth(): Int {
        if (mImageMaxWidth == null) {
            // Calculate the max width in portrait mode. This is done lazily since we need to
            // wait for
            // a UI layout pass to get the right values. So delay it to first time image
            // rendering time.
            mImageMaxWidth = imageView.getWidth()
        }
        return mImageMaxWidth as Int
    }

    // Returns max image height, always for portrait mode. Caller needs to swap width / height for
    // landscape mode.
    private fun getImageMaxHeight(): Int {
        if (mImageMaxHeight == null) {
            // Calculate the max width in portrait mode. This is done lazily since we need to
            // wait for
            // a UI layout pass to get the right values. So delay it to first time image
            // rendering time.
            mImageMaxHeight = imageView.getHeight()
        }
        return mImageMaxHeight as Int
    }

    // Gets the targeted width / height.
    private fun getTargetedWidthHeight(): Pair<Int, Int> {
        val targetWidth: Int
        val targetHeight: Int
        val maxWidthForPortraitMode = getImageMaxWidth()
        val maxHeightForPortraitMode = getImageMaxHeight()
        targetWidth = maxWidthForPortraitMode
        targetHeight = maxHeightForPortraitMode
        return Pair(targetWidth, targetHeight)
    }

*//*    fun onItemSelected(parent: AdapterView<*>?, v: View?, position: Int, id: Long) {
        //mGraphicOverlay.clear()
        when (position) {
            0 -> mSelectedImage = getBitmapFromAsset(this, "Please_walk_on_the_grass.jpg")
            1 ->                 // Whatever you want to happen when the thrid item gets selected
                mSelectedImage = getBitmapFromAsset(this, "grace_hopper.jpg")
        }
        if (mSelectedImage != null) {
            // Get the dimensions of the View
            val targetedSize = getTargetedWidthHeight()
            val targetWidth = targetedSize.first
            val maxHeight = targetedSize.second

            // Determine how much to scale down the image
            val scaleFactor = Math.max(
                mSelectedImage.getWidth() as Float / targetWidth.toFloat(),
                mSelectedImage.getHeight() as Float / maxHeight.toFloat()
            )
            val resizedBitmap = Bitmap.createScaledBitmap(
                mSelectedImage,
                (mSelectedImage.getWidth() / scaleFactor) as Int,
                (mSelectedImage.getHeight() / scaleFactor) as Int,
                true
            )
            mImageView.setImageBitmap(resizedBitmap)
            mSelectedImage = resizedBitmap
        }
    }*//*

    fun onNothingSelected(parent: AdapterView<*>?) {
        // Do nothing
    }

    fun getBitmapFromAsset(context: Context, filePath: String?): Bitmap? {
        val assetManager = context.assets
        val `is`: InputStream
        var bitmap: Bitmap? = null
        try {
            `is` = assetManager.open(filePath!!)
            bitmap = BitmapFactory.decodeStream(`is`)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return bitmap
    }*/


    //    fun buttonTakePicClicked() {
//        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//        photoFile = getPhotoFile(FILE_NAME)
//        //Requires additional code in AndroidManifest - <provider> block
//        //Also requires fileprovider.xml file and folder under "res" folder
//
//        val fileProvider = FileProvider.getUriForFile(
//            this,
//            "com.application.provider.emojisnap",
//            photoFile
//        )
//        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)
//
//        if (takePictureIntent.resolveActivity(this.packageManager) != null) {
//            startActivityForResult(takePictureIntent, REQUEST_CODE)
//        } else {
//            Toast.makeText(this, "Unable to open camera", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    fun buttonChooseFromLibraryClicked() {
//        val intent = Intent(Intent.ACTION_PICK)
//        intent.type = "image/*"
//        startActivityForResult(intent, IMAGE_REQUEST_CODE)
//    }

//    private fun getPhotoFile(fileName: String): File {
//        // Use `getExternalFilesDir` on Context to access package-specific directories.
//        val storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
//        return File.createTempFile(fileName, ".*", storageDirectory)
//    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
//            val takenImage = BitmapFactory.decodeFile(photoFile.absolutePath)
//
//            //Image rotation, to display it the proper way
//            val matrix = Matrix()
//            matrix.postRotate(270F)
//            val scaledBitmap =
//                Bitmap.createScaledBitmap(takenImage, takenImage.width, takenImage.height, true)
//
//            //Bitmap image
//            val rotatedBitmap = Bitmap.createBitmap(
//                scaledBitmap,
//                0,
//                0,
//                scaledBitmap.width,
//                scaledBitmap.height,
//                matrix,
//                true
//            )
//
//            mBitmap = rotatedBitmap;
//            imageView.setImageBitmap(rotatedBitmap)
//        } else if (requestCode == IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
//            val inputStream: InputStream? = data?.data?.let { contentResolver.openInputStream(it) }
//            mBitmap = BitmapFactory.decodeStream(inputStream)
//            mBitmap = Bitmap.createScaledBitmap(mBitmap, 500, 500, true)
//
//            imageView.setImageBitmap(mBitmap)
//        } else {
//            super.onActivityResult(requestCode, resultCode, data)
//        }
//
//    }
}