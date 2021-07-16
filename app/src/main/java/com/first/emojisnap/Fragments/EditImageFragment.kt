package com.first.emojisnap.Fragments

import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.first.emojisnap.MainActivity
import com.first.emojisnap.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [EditImageFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EditImageFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var mBitmap : Bitmap
    private lateinit var imageViewEdit : ImageView
    private lateinit var mMainActivity: MainActivity
    private lateinit var btn_show_orignal_bitmap : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_edit_image, container, false)

        imageViewEdit = view.findViewById<ImageView>(R.id.imageView_for_edit)

        imageViewEdit.setImageBitmap(mBitmap)

        btn_show_orignal_bitmap = view.findViewById(R.id.btn_show_orignal_bitmap)
        btn_show_orignal_bitmap.setOnClickListener(View.OnClickListener {
            mBitmap = mMainActivity.getOriginalBitmap()
            imageViewEdit.setImageBitmap(mBitmap)
        })

        var btn_open_faces = view.findViewById<Button>(R.id.btn_open_faces)
        btn_open_faces.setOnClickListener(View.OnClickListener {
            mMainActivity.changeEditFrameToFaces()
        })

        var btn_open_eyes = view.findViewById<Button>(R.id.btn_open_eyes)
        btn_open_eyes.setOnClickListener(View.OnClickListener {
            mMainActivity.changeEditFrameToEyes()
        })

        var btn_open_noses = view.findViewById<Button>(R.id.btn_open_noses)
        btn_open_noses.setOnClickListener(View.OnClickListener {
            mMainActivity.changeEditFrameToNoses()
        })

        var btn_open_moustache = view.findViewById<Button>(R.id.btn_open_moustache)
        btn_open_moustache.setOnClickListener(View.OnClickListener {
            mMainActivity.changeEditFrameToMoustaches()
        })

        var btn_open_mouth = view.findViewById<Button>(R.id.btn_open_mouth)
        btn_open_mouth.setOnClickListener(View.OnClickListener {
            mMainActivity.changeEditFrameToMouth()
        })

        return view
    }

    fun setBitmap(bitmap: Bitmap) {
        mBitmap = bitmap
    }

    fun setMainActivity(mainActivity: MainActivity) {
        mMainActivity = mainActivity
    }

    fun changeBitmap(bitmap: Bitmap)
    {
        mBitmap = bitmap
        imageViewEdit.setImageBitmap(mBitmap)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment EditImageFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                EditImageFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}