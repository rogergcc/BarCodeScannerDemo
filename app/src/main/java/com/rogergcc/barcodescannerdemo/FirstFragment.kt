package com.rogergcc.barcodescannerdemo

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Camera
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.rogergcc.barcodescannerdemo.databinding.FragmentFirstBinding
import com.rogergcc.barcodescannerdemo.ui.LiveBarcodeScanningActivity
import com.rogergcc.barcodescannerdemo.ui.common.CameraActivity
import com.rogergcc.barcodescannerdemo.ui.helper.Utils


class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

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

        binding.buttonFirst.setOnClickListener {
//            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)

//            if (!Utils.allPermissionsGranted(requireContext())) {
//                Utils.requestRuntimePermissions(requireContext())
//            }
//            resultLauncher.launch(Intent(requireContext(), CameraActivity::class.java))

            val intent = Intent(requireContext(), LiveBarcodeScanningActivity::class.java)
//                startActivity(intent,RC_SELECT_LOCATION);
            //                startActivity(intent,RC_SELECT_LOCATION);
            startActivityForResult(
                intent,
                RESULT_OK
            )

        }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            val data = resultCode.let { data }
            if (data != null) {
                val barcodeContent = data.getStringExtra("data")
//                    sharedViewModel.setBarcode(barcodeContent)
            }
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}