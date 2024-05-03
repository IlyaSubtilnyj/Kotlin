package com.example.application

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import com.example.application.databinding.FragmentHomeBinding
import com.example.application.models.FragmentUtils
import com.example.application.models.ListAdapter
import com.example.application.models.Segue
import com.example.application.models.StorageItem
import com.example.application.models.serializable
import java.io.Serializable
import kotlin.reflect.full.declaredMemberProperties

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val ARG_PARAM3 = "param3"

/**
 * A simple [Fragment] subclass.
 * Use the [Home.newInstance] factory method to
 * create an instance of this fragment.
 */
class Home : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var listAdapter: ListAdapter
    private lateinit var dataList: List<StorageItem>
    private lateinit var userSettings: UserSettings
    private var fragmentLayout = 0

    override fun onCreate(savedInstanceState: Bundle?) {

        Log.i("Createlol", "lol")
        super.onCreate(savedInstanceState)
        arguments?.let {
            userSettings    = it.serializable<UserSettings>(ARG_PARAM1)!!
            fragmentLayout  = it.getInt(ARG_PARAM2)
            dataList        = it.serializable<ArrayList<StorageItem>>(ARG_PARAM3)!!
        }

        this.listAdapter = ListAdapter(requireContext(), this.dataList)
    }

    private fun convertDataClassToPairs(data: Any): List<Pair<String, Any?>> {
        return data.javaClass.kotlin.declaredMemberProperties.map { property ->
            property.name to property.get(data)
        }
    }

    fun setData(data: List<StorageItem>) {
        Log.i("there", this.dataList.toString())
        val hash1 = this.dataList.hashCode()
        val hash2 = data.hashCode()
        val areEqual = hash1 == hash2

        if (areEqual) return
        this.dataList = data
        this.listAdapter = ListAdapter(requireContext(), dataList)
        updateView()
    }

    private fun updateView() {
        FragmentUtils.refreshFragment(context, fragmentLayout)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        Log.i("onCreateView", "Home")
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
         * @param param2 Parameter 2.
         * @return A new instance of fragment Home.
         */
        @JvmStatic
        fun newInstance(param1: UserSettings, list: List<StorageItem>, fragmentLayout: Int) =
            Home().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, param1)
                    putInt(ARG_PARAM2, fragmentLayout)
                    putSerializable(ARG_PARAM3, ArrayList(list))
                }
            }
    }
}

data class UserSettings(var data: String): Serializable