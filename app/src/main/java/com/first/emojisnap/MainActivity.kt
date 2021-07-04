package com.first.emojisnap

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

private const val FILE_NAME = "myEmoji.jpg" //Can be later changed to dynamic naming
private const val REQUEST_CODE = 42
private lateinit var photoFile: File

class MainActivity : AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
            matrix.postRotate(90F)
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

            //Setting bitmap image to ImageView to display it on screen
            imageView.setImageBitmap(rotatedBitmap)
        }
        else
        {
            super.onActivityResult(requestCode, resultCode, data)
        }

    }
}