package com.rogergcc.barcodescannerdemo

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.mlkit.vision.barcode.Barcode
import com.rogergcc.barcodescannerdemo.databinding.FragmentFirstBinding
import com.rogergcc.barcodescannerdemo.ui.LiveBarcodeScanningActivity
import com.rogergcc.barcodescannerdemo.ui.helper.SoundPoolPlayer


class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null
    private var mSoundPoolPlayer: SoundPoolPlayer? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)

//        runWithPermissions(Permission.CAMERA) {
//            resultLauncher.launch(Intent(requireContext(), CameraActivity::class.java))
//        }


        return binding.root

    }

//    private var resultLauncher =
//        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//            if (result.resultCode == Activity.RESULT_OK) {
//                val data: Intent? = result.data
//                if (data != null) {
//                    val barcodeContent = data.getStringExtra("data")
////                    sharedViewModel.setBarcode(barcodeContent)
//                }
//            }
//        }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnActionScanCode.setOnClickListener {

            logd("onViewCreated: btnActionScanCode ")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(
                    arrayOf(Manifest.permission.CAMERA),
                    DEFINED_CODE
                )
            }

        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //receive result after your activity finished scanning
        super.onActivityResult(requestCode, resultCode, data)
            loge("onActivityResult")
        if (resultCode != RESULT_OK || data == null) {
            return
        }
        if (requestCode == REQUEST_CODE_SCAN) {
            val barcodeMlKit  = data.getStringExtra(LiveBarcodeScanningActivity.SCAN_RESULT)
            if (!TextUtils.isEmpty(barcodeMlKit)) {
                mSoundPoolPlayer?.playShortResource(R.raw.bleep)
                //sharedViewModel.setBarcode(barcodeContent)
                binding.tvResultCodeScan.text = barcodeMlKit
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray,
    ) {
        if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            return
        }
        if (requestCode == DEFINED_CODE) {
            //start your activity for scanning barcode
            mSoundPoolPlayer = SoundPoolPlayer(requireContext())
            startActivityForResult(
                Intent(requireContext(), LiveBarcodeScanningActivity::class.java),
                REQUEST_CODE_SCAN
            )


        }
    }

    companion object {
        private const val DEFINED_CODE = 222
        private const val REQUEST_CODE_SCAN = 0X01
    }

    private fun loge(messageLog: String?) {
        Log.e("FirstFragment", messageLog ?: "Nadaaa..")
    }
    private fun logd(messageLog: String?) {
        Log.d("FirstFragment", messageLog ?: "Nadaaa..")
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}