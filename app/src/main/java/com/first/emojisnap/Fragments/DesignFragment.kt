package com.first.emojisnap.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
 * Use the [DesignFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

private lateinit var mSmileyList : ArrayList<SmileyItem>
private lateinit var mComunicate : ICommunicator

class DesignFragment : Fragment(), SmileyAdapter.OnItemClickListener{
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_design, container, false)

        mComunicate = activity as ICommunicator

        val design_recycler : RecyclerView = view.findViewById(R.id.smiley_recycler)
        mSmileyList = generateSmileyList(24) as ArrayList<SmileyItem>

        design_recycler.adapter = SmileyAdapter(mSmileyList, this, SmileyType.SMILEY)
        design_recycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        design_recycler.setHasFixedSize(true)

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
         * @return A new instance of fragment DesignFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DesignFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onItemClick(position: Int, smileyType: SmileyType) {
        //Toast.makeText(context, "item $position clicked", Toast.LENGTH_SHORT).show()
        val clickedItem = mSmileyList[position]

        mComunicate.getFaceFromFragment(clickedItem.imageResource)
    }
}