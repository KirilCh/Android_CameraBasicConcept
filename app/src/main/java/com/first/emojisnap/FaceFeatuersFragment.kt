package com.first.emojisnap

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FaceFeatuersFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

private lateinit var mEyesList : ArrayList<SmileyItem>
private lateinit var mNoseList : ArrayList<SmileyItem>
private lateinit var mMouthList : ArrayList<SmileyItem>
private lateinit var mComunicate : ICommunicator

class FaceFeatuersFragment : Fragment(), SmileyAdapter.OnItemClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

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

        mComunicate = activity as ICommunicator

        val eyes_recycler : RecyclerView = view.findViewById(R.id.eyes_recycler)
        mEyesList = generateSmileyList(24) as ArrayList<SmileyItem>

        eyes_recycler.adapter = SmileyAdapter(mEyesList, this)
        eyes_recycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        eyes_recycler.setHasFixedSize(true)

        val nose_recycler : RecyclerView = view.findViewById(R.id.nose_recycler)
        mNoseList = generateSmileyList(24) as ArrayList<SmileyItem>

        nose_recycler.adapter = SmileyAdapter(mEyesList, this)
        nose_recycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        nose_recycler.setHasFixedSize(true)

        val mouth_recycler : RecyclerView = view.findViewById(R.id.mouth_recycler)
        mMouthList = generateSmileyList(24) as ArrayList<SmileyItem>

        mouth_recycler.adapter = SmileyAdapter(mEyesList, this)
        mouth_recycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        mouth_recycler.setHasFixedSize(true)



        return view
    }


    private fun generateSmileyList(size: Int) : List<SmileyItem> {
        val list = ArrayList<SmileyItem>()

        for(i in 1 until size) {
            val drawable = R.drawable.smiley1+i
            val item = SmileyItem(drawable,"Smiley $i")
            list+=item
        }
        return list
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
                FaceFeatuersFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }

    override fun onItemClick(position: Int) {
        TODO("Not yet implemented")
    }
}