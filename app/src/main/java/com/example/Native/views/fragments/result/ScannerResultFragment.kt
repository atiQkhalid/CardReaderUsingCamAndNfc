package com.example.Native.views.fragments.result

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.Native.base.BaseFragment
import com.example.Native.databinding.FragmentResultBinding
import com.example.Native.extenssions.showToastMsg
import com.example.Native.utils.Constants.CARD_NUMBER
import com.example.Native.utils.Constants.CVC
import com.example.Native.utils.Constants.EXPIRY

class ScannerResultFragment : BaseFragment() {

    private lateinit var binding: FragmentResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentResultBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cardNumber = arguments?.getString(CARD_NUMBER)
        val expiry = arguments?.getString(EXPIRY)
        val cvc = arguments?.getString(CVC)

        showToastMsg("cardNumber is $cardNumber, expiry date is $expiry and cvc is $cvc")

        binding.tvCardNumber.text = cardNumber
        binding.tvExpiry.text = expiry
        if (cvc?.length!! <= 3){
            cvc == " "
        }
        else{
            binding.tvCvc.text = cvc
        }

    }
}