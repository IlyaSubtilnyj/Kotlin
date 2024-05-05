package com.example.application

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.application.databinding.FragmentHomeBinding
import com.example.application.databinding.FragmentProfileBinding
import com.example.application.models.CapableActivity
import com.example.application.models.StorageItem
import com.example.application.models.User
import com.example.application.models.serializable


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
class Profile : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var user: User
    private lateinit var email: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            user    = it.serializable<User>(ARG_PARAM1)!!
            email   = it.getString(ARG_PARAM2)!!
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        binding.email.text = this.email
        binding.firstName.text = this.user.firstName
        binding.lastName.text = this.user.lastName

        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Profile.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: User, param2: String) =
            Profile().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

}