package com.first.emojisnap.Fragments

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import com.first.emojisnap.MainActivity
import com.first.emojisnap.R
import java.io.File
import java.io.InputStream
import java.time.format.ResolverStyle
import java.util.jar.Manifest

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MainFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MainFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var mMainActivity : MainActivity

    private lateinit var mBitmap : Bitmap
    private lateinit var imageView : ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_main, container, false)

        imageView = view.findViewById(R.id.imageView)

        val buttonTakePic : Button = view.findViewById(R.id.btnTakePicture)
        buttonTakePic.setOnClickListener(View.OnClickListener {
            buttonTakePicClicked()
        })


        val btnChoseFromLibrary : Button = view.findViewById(R.id.btnChoseFromLibrary)
        btnChoseFromLibrary.setOnClickListener(View.OnClickListener {
            buttonChooseFromLibraryClicked()
        })

        val btnContinueEdit : Button = view.findViewById(R.id.btnContinueEdit)
        btnContinueEdit.setOnClickListener(View.OnClickListener {
            buttonContinueEdit()
        })

        return view
    }

    fun setMainActivity(mainActivity: MainActivity)
    {
        mMainActivity = mainActivity
    }

    private fun buttonChooseFromLibraryClicked()
    {
        if( context != null) {
            if (ContextCompat.checkSelfPermission(
                            requireContext(),
                            android.Manifest.permission.READ_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED) {
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                startActivityForResult(intent, IMAGE_REQUEST_CODE)
            } else {
                ActivityCompat.requestPermissions(
                        context as Activity,
                        arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                        IMAGE_PERMISSION_CODE
                )
            }
        }
    }

    private fun buttonTakePicClicked()
    {
        if( context != null) {
            if (ContextCompat.checkSelfPermission( requireContext(), android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent, CAMERA_REQUEST_CODE)
            } else {
                ActivityCompat.requestPermissions(
                        context as Activity,
                        arrayOf(android.Manifest.permission.CAMERA),
                        CAMERA_PERMISSION_CODE
                )
            }
        }
    }


    private fun buttonContinueEdit()
    {
        mMainActivity.buttonContinueEdit(mBitmap)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK)
        {
            val takenImage = data!!.extras!!.get("data") as Bitmap

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
            imageView.setImageBitmap(mBitmap)
        }

        else if(requestCode == IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
                mBitmap = MediaStore.Images.Media.getBitmap(context?.contentResolver, data?.data)
                mBitmap = Bitmap.createScaledBitmap(mBitmap, 500, 500, true)
                imageView.setImageBitmap(mBitmap)
            } else {
                Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent, CAMERA_REQUEST_CODE)
            } else {
                Toast.makeText(context, "You need to allow Camera permission", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {

        private const val IMAGE_REQUEST_CODE = 100
        private const val IMAGE_PERMISSION_CODE = 43
        private const val CAMERA_PERMISSION_CODE = 42
        private const val CAMERA_REQUEST_CODE = 2

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MainFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MainFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}