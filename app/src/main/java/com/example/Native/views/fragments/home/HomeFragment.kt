package com.example.Native.views.fragments.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.Native.R
import com.example.Native.base.BaseFragment
import com.example.Native.databinding.FragmentHomeBinding
import com.example.Native.extenssions.replaceFragment
import com.example.Native.views.activities.nfc.NFCscanActivity
import com.example.Native.views.fragments.camscanner.CamScannerFragment

class HomeFragment : BaseFragment(), View.OnClickListener{

    private lateinit var binding: FragmentHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // clickListeners
        binding.btCamScan.setOnClickListener(this)
        binding.btNfc.setOnClickListener(this)

    }

    // click handlers
    override fun onClick(v: View?) {
        when(v?.id){
            R.id.bt_camScan -> replaceFragment(CamScannerFragment())
            R.id.bt_nfc -> startActivity(Intent(requireContext(), NFCscanActivity::class.java))
        }
    }
}