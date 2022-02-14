package com.example.Native.views.fragments.camscanner

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.text.isDigitsOnly
import com.example.Native.base.BaseFragment
import com.example.Native.databinding.FragmentCamScannerBinding
import com.example.Native.extenssions.replaceFragment
import com.example.Native.utils.Constants
import com.example.Native.views.fragments.result.ScannerResultFragment
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.text.TextBlock
import com.google.android.gms.vision.text.TextRecognizer

class CamScannerFragment : BaseFragment() {

    private val MY_PERMISSIONS_REQUEST_CAMERA: Int = 101
    private lateinit var cameraSource: CameraSource
    private lateinit var textRecognizer: TextRecognizer

    private lateinit var binding: FragmentCamScannerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCamScannerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requestForCameraPermission()

        //  Create text Recognizer
        textRecognizer = TextRecognizer.Builder(requireContext()).build()
        if (!textRecognizer.isOperational) {
            toast("Dependencies are not loaded yet...please try after few moment!!")
            return
        }

        //  Init camera source to use high resolution and auto focus
        cameraSource = CameraSource.Builder(requireActivity(), textRecognizer)
            .setFacing(CameraSource.CAMERA_FACING_BACK)
            .setRequestedPreviewSize(1280, 1080)
            .setAutoFocusEnabled(true)
            .setRequestedFps(2.0f)
            .build()

        binding.surfaceCameraPreview.holder.addCallback(object : SurfaceHolder.Callback {

            @SuppressLint("MissingPermission")
            override fun surfaceCreated(holder: SurfaceHolder) {
                try {
                    if (isCameraPermissionGranted()) {
                        cameraSource.start(binding.surfaceCameraPreview.holder)
                    } else {
                        requestForCameraPermission()
                    }
                } catch (e: Exception) {
                    toast("Error:" + e.message)
                }
            }

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                cameraSource.stop()
            }
        }
        )

        textRecognizer.setProcessor(object : Detector.Processor<TextBlock> {
            override fun release() {}

            override fun receiveDetections(detections: Detector.Detections<TextBlock>) {
                val items = detections.detectedItems

                if (items.size() <= 0) {
                    return
                }
                val splitString = "\n"

                val stringBuilder = StringBuilder()
                for (i in 0 until items.size()) {
                    val item = items.valueAt(i)
                    stringBuilder.append(item.value)
                    stringBuilder.append(splitString)
                }

                val cardDetails = stringBuilder.toString()
                val splitCardDetails = cardDetails.replace(" ".toRegex(), "")
                val digitsInStringBuilder = "[^0-9/ ]".toRegex()
                val allDigits = digitsInStringBuilder.replace(splitCardDetails, "_")

                val cardNumber = allDigits.split("_").find {
                    it.length == 16
                }
                val expiryDate = allDigits.split("_").findLast {
                    it.contains("/") && it.length == 5
                }

                val cvc = allDigits.split("_").find {
                    it.length == 3 && it.isDigitsOnly()
                }

                if (cardNumber.isNullOrBlank().not() && expiryDate.isNullOrBlank().not()) {
                    replaceFragment(
                        ScannerResultFragment(),
                        bundle = Bundle().also {
                            it.putString(Constants.CARD_NUMBER, cardNumber)
                            it.putString(Constants.EXPIRY, expiryDate)
                            it.putString(Constants.CVC, cvc)
                        }
                    )
                }
            }
        })
    }

    //    request for permission
    private fun requestForCameraPermission() {

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            )
            != PackageManager.PERMISSION_GRANTED
        ) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    Manifest.permission.CAMERA
                )
            ) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(
                        Manifest.permission.CAMERA
                    ),
                    MY_PERMISSIONS_REQUEST_CAMERA
                )
            }
        }
    }

    private fun isCameraPermissionGranted(): Boolean {

        return ContextCompat.checkSelfPermission(
            requireActivity(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    //method for toast
    fun toast(text: String) {
        Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
    }

    //for handling permissions
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_CAMERA -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    requestForCameraPermission()
                }
                return
            }
        }
    }

}