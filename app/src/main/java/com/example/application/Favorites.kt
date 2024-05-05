package com.example.application

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import com.example.application.databinding.FragmentHomeBinding
import com.example.application.models.CapableFragment
import com.example.application.models.ListAdapter
import com.example.application.models.Segue
import com.example.application.models.StorageItem
import com.example.application.models.serializable

private const val ARG_PARAM1 = "param1"

class Favorites : CapableFragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var listAdapter: ListAdapter
    private lateinit var dataList: List<StorageItem>

    override fun setData(vararg items: Any) {
        TODO("Not yet implemented")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            dataList = it.serializable<ArrayList<StorageItem>>(ARG_PARAM1)!!
        }
        this.listAdapter = ListAdapter(requireContext(), this.dataList)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.listView.adapter = listAdapter
        binding.listView.isClickable = true

        binding.listView.onItemClickListener =
            AdapterView.OnItemClickListener { adapterView, view, i, l ->
                Segue(requireContext(), DetailedActivity::class.java)(
                    listOf(
                        Pair("object", dataList[i])
                    )
                )
            }

        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @return A new instance of fragment Favorites.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: List<StorageItem>) =
            Favorites().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, ArrayList(param1))
                }
            }
    }
}