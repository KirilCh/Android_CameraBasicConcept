package com.first.emojisnap.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.first.emojisnap.Adapters.SmileyAdapter
import com.first.emojisnap.Adapters.SmileyItem
import com.first.emojisnap.R
import com.first.emojisnap.SmileyType
import com.first.emojisnap.model.ICommunicator

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FaceFeaturesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

class FaceFeaturesFragment : Fragment(), SmileyAdapter.OnItemClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var mFacesList : ArrayList<SmileyItem>
    private lateinit var mEyesList : ArrayList<SmileyItem>
    private lateinit var mNoseList : ArrayList<SmileyItem>
    private lateinit var mMouthList : ArrayList<SmileyItem>
    private lateinit var mMoustache : ArrayList<SmileyItem>
    private lateinit var mCommunicate : ICommunicator

    private lateinit var mMoustacheRecycler : RecyclerView
    private lateinit var mMouthRecycler : RecyclerView
    private lateinit var mNoseRecycler : RecyclerView
    private lateinit var mEyesRecycler : RecyclerView
    private lateinit var mFacesRecycler : RecyclerView
    private lateinit var mTextViewType : TextView

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
        val view = inflater.inflate(R.layout.fragment_face_featuers, container, false)

        mCommunicate = activity as ICommunicator

        mTextViewType = view.findViewById<TextView>(R.id.textView_type)

        mFacesRecycler = view.findViewById(R.id.faces_recycler)
        mFacesList = generateSmileyList(14,"smiley", R.drawable.smiley1) as ArrayList<SmileyItem>
        mFacesRecycler.adapter = SmileyAdapter(mFacesList, this, SmileyType.SMILEY)
        mFacesRecycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        mFacesRecycler.setHasFixedSize(true)

        mEyesRecycler = view.findViewById(R.id.eyes_recycler)
        mEyesList = generateSmileyList(9,"eyes", R.drawable.eyes1) as ArrayList<SmileyItem>
        mEyesRecycler.adapter = SmileyAdapter(mEyesList, this, SmileyType.EYE)
        mEyesRecycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        mEyesRecycler.setHasFixedSize(true)

        mNoseRecycler = view.findViewById(R.id.nose_recycler)
        mNoseList = generateSmileyList(9,"nose", R.drawable.nose1) as ArrayList<SmileyItem>
        mNoseRecycler.adapter = SmileyAdapter(mNoseList, this, SmileyType.NOSE)
        mNoseRecycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        mNoseRecycler.setHasFixedSize(true)

        mMouthRecycler = view.findViewById(R.id.mouth_recycler)
        mMouthList = generateSmileyList(6,"mouth", R.drawable.mouth1) as ArrayList<SmileyItem>
        mMouthRecycler.adapter = SmileyAdapter(mMouthList, this, SmileyType.MOUTH)
        mMouthRecycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        mMouthRecycler.setHasFixedSize(true)

        mMoustacheRecycler = view.findViewById(R.id.mustache_recycler)
        mMoustache = generateSmileyList(4,"mustache", R.drawable.mustache1) as ArrayList<SmileyItem>
        mMoustacheRecycler.adapter = SmileyAdapter(mMoustache, this, SmileyType.MUSTACHE)
        mMoustacheRecycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        mMoustacheRecycler.setHasFixedSize(true)

        return view
    }

    private fun generateSmileyList(iSize: Int, iType: String, iDrawable : Int) : List<SmileyItem> {
        val listOfImages = ArrayList<SmileyItem>()
        for(i in 0 until iSize) {
            val drawable = iDrawable+i
            val item = SmileyItem(drawable,"$iType $i")
            listOfImages+=item
        }
        return listOfImages
    }

    override fun onItemClick(position: Int, smileyType: SmileyType) {

        var clickedItem : SmileyItem? = null

        when(smileyType)
        {
            SmileyType.EYE -> {
                clickedItem = mEyesList[position]
                Toast.makeText(context, "eye", Toast.LENGTH_SHORT).show()
                mCommunicate.getEyeFromFragment(clickedItem.imageResource)
            }
            SmileyType.NOSE -> {
                clickedItem = mNoseList[position]
                Toast.makeText(context, "Nose", Toast.LENGTH_SHORT).show()
                mCommunicate.getNoseFromFragment(clickedItem.imageResource)
            }
            SmileyType.MOUTH -> {
                clickedItem = mMouthList[position]
                Toast.makeText(context, "MOUTH", Toast.LENGTH_SHORT).show()
                mCommunicate.getMouthFromFragment(clickedItem.imageResource)
            }
            SmileyType.MUSTACHE -> {
                clickedItem = mMoustache[position]
                Toast.makeText(context, "Mustache", Toast.LENGTH_SHORT).show()
                mCommunicate.getMouthFromFragment(clickedItem.imageResource)
            }
            SmileyType.SMILEY -> {
                clickedItem = mFacesList[position]
                Toast.makeText(context, "Face", Toast.LENGTH_SHORT).show()
                mCommunicate.getFaceFromFragment(clickedItem.imageResource)
            }
        }
    }

    fun showFaces()
    {
        goneAllRecycler()
        mFacesRecycler.visibility = View.VISIBLE
        mTextViewType.text = "Faces"
    }

    fun showEyes()
    {
        goneAllRecycler()
        mEyesRecycler.visibility = View.VISIBLE
        mTextViewType.text = "Eyes"
    }

    fun showNoses() {
        goneAllRecycler()
        mNoseRecycler.visibility = View.VISIBLE
        mTextViewType.text = "Noses"
    }

    fun showMoustaches() {
        goneAllRecycler()
        mMoustacheRecycler.visibility = View.VISIBLE
        mTextViewType.text = "Moustaches"
    }

    fun showMouth() {
        goneAllRecycler()
        mMouthRecycler.visibility = View.VISIBLE
        mTextViewType.text = "Mouth"
    }

    fun goneAllRecycler()
    {
        mMoustacheRecycler.visibility = View.GONE
        mMouthRecycler.visibility = View.GONE
        mNoseRecycler.visibility = View.GONE
        mEyesRecycler.visibility = View.GONE
        mFacesRecycler.visibility = View.GONE
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FaceFeatuersFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                FaceFeaturesFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}