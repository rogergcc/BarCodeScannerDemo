package com.rogergcc.barcodescannerdemo.ui.common

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Camera
import android.graphics.Rect
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.huawei.hms.hmsscankit.RemoteView
import com.huawei.hms.ml.scan.HmsScan
import com.rogergcc.barcodescannerdemo.R
import com.rogergcc.barcodescannerdemo.databinding.ActivityCameraBinding
import com.rogergcc.barcodescannerdemo.ui.helper.TimberAppLogger
import com.rogergcc.qrunlockclientapp.helper.SoundPoolPlayer


class CameraActivity : AppCompatActivity() {

    private lateinit var camera: Camera
    private lateinit var binding: ActivityCameraBinding

    private lateinit var remoteView: RemoteView
    private var flashFlag: Boolean = false


    private var showTorchToggle = false
    private var showCloseButton = false
    private var useFrontCamera = false
    private var mSoundPoolPlayer: SoundPoolPlayer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupEdgeToEdgeUI()

        mSoundPoolPlayer = SoundPoolPlayer(this)

        //1.get screen density to caculate viewfinder's rect
        val dm = resources.displayMetrics
        val density = dm.density
        //2.get screen size
        mScreenWidth = resources.displayMetrics.widthPixels
        mScreenHeight = resources.displayMetrics.heightPixels

        scanFrameSize = (SCAN_FRAME_SIZE * density).toInt()

        TimberAppLogger.i("SCAN_FRAME_SIZE $SCAN_FRAME_SIZE")
        TimberAppLogger.i("density $density")
        TimberAppLogger.i("mScreenWidth $mScreenWidth")
        TimberAppLogger.i("mScreenHeight $mScreenHeight")
        TimberAppLogger.i("scanFrameSize $scanFrameSize")

        //3.caculate viewfinder's rect,it's in the middle of the layout
        //set scanning area(Optional, rect can be null,If not configure,default is in the center of layout)
//        val rect = Rect()
//        rect.left = mScreenWidth / 2 - scanFrameSize / 2
//        rect.right = mScreenWidth / 2 + scanFrameSize / 2
//        rect.top = mScreenHeight / 2 - scanFrameSize / 2
//        rect.bottom = mScreenHeight / 2 + scanFrameSize / 2
//

        // Proporciones del rectángulo del área de escaneo (ajusta según tus necesidades)
        val aspectRatioWidth = 3
        val aspectRatioHeight = 1

        // Código para calcular el rectángulo del área de escaneo
        val rect = Rect()
        rect.left = mScreenWidth / 2 - scanFrameSize * aspectRatioWidth / 2
        rect.right = mScreenWidth / 2 + scanFrameSize * aspectRatioWidth / 2
        rect.top = mScreenHeight / 2 - scanFrameSize * aspectRatioHeight / 2
        rect.bottom = mScreenHeight / 2 + scanFrameSize * aspectRatioHeight / 2

        // Establecer el rectángulo en la vista
        //        binding.overlayView.setScanRect(rect)

        //initialize RemoteView instance, and set calling back for scanning result
        remoteView = RemoteView.Builder()
            .setContext(this)
            .setBoundingBox(rect)
            .setFormat(HmsScan.ALL_SCAN_TYPE).build()


        remoteView.setOnResultCallback { result -> //judge the result is effective

            //                mSoundPoolPlayer.playShortResource(R.raw.bleep);
            if (result != null && result.isNotEmpty() && result[0] != null &&
                !TextUtils.isEmpty(result[0].getOriginalValue())
            ) {
                TimberAppLogger.e("OriginalValue QRSC ${result[0].originalValue} ")

                toast("QRSC ${result[0].originalValue} ")
                mSoundPoolPlayer?.playShortResource(R.raw.bleep)
                binding.tvScannedData.text = result[0].originalValue
//                val intent = Intent()
//                intent.putExtra(SCAN_RESULT, result[0])
//                setResult(RESULT_OK, intent)
//                finish()
            }
        }

        remoteView.onCreate(savedInstanceState)
        val params = FrameLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        binding.rim.addView(remoteView, params)
        // Set the back, photo scanning, and flashlight operations.
        // Set the back, photo scanning, and flashlight operations.
        applyScannerConfig()
//        setupCamera() 11
    }

    private fun applyScannerConfig() {
        binding.overlayView.setCustomText(R.string.place_the_qr_code)
        binding.overlayView.setCustomIcon(R.drawable.quickie_ic_qrcode)
//        binding.overlayView.setHorizontalFrameRatio(1.2F)//QR
        binding.overlayView.setHorizontalFrameRatio(2.2F) // BARCODE AL OJO
//        binding.overlayView.setCloseVisibilityAndOnClick(true) { finish() }

        val hasFlash = this.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)

        showTorchToggle = true
        showCloseButton = true
        useFrontCamera = false

        binding.overlayView.setCloseVisibilityAndOnClick(showCloseButton) { finish() }
//        binding.overlayView.setTorchVisibilityAndOnClick(showTorchToggle) {
//            remoteView.switchLight()
//        }


//        if (showTorchToggle && hasFlash) {
//            binding.overlayView.setTorchVisibilityAndOnClick(true) {
//                flashFlag = !flashFlag
//                binding.overlayView.setTorchState(flashFlag)
//                cameraControl.enableTorch(flashFlag)
//
//
//            }
//        }

        if (showTorchToggle && hasFlash) {
            binding.overlayView.setTorchVisibilityAndOnClick(true) {
                remoteView.switchLight()
                if (remoteView.lightStatus) {
                    binding.overlayView.setTorchState(true)
                } else {
                    binding.overlayView.setTorchState(false)
                }
            }
        }


    }

    private fun setupEdgeToEdgeUI() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        ViewCompat.setOnApplyWindowInsetsListener(binding.overlayView) { v, insets ->
            insets.getInsets(WindowInsetsCompat.Type.systemBars())
                .let { v.setPadding(it.left, it.top, it.right, it.bottom) }
            WindowInsetsCompat.CONSUMED
        }
    }

    private fun setupCamera() {//11


    }


    //region Region LIFE


    //manage remoteView lifecycle
    override fun onStart() {
        super.onStart()
        remoteView.onStart()
    }

    override fun onResume() {
        super.onResume()
        remoteView.onResume()
    }

    override fun onPause() {
        super.onPause()
        remoteView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        remoteView.onDestroy()
//        if (remoteView != null) {
//            remoteView = null;
//        }

//        if(mSoundPoolPlayer != null){
//            mSoundPoolPlayer.release();
//            mSoundPoolPlayer = null;
//        }
    }

    override fun onStop() {
        super.onStop()
        remoteView.onStop()
    }
    //endregion

    private fun bindPreviewUseCase() {

        try {

        } catch (illegalStateException: IllegalStateException) {
            Log.e(TAG, illegalStateException.message ?: "IllegalStateException")
        } catch (illegalArgumentException: IllegalArgumentException) {
            Log.e(TAG, illegalArgumentException.message ?: "IllegalArgumentException")
        }
    }

    private fun bindAnalyseUseCase() {

        try {

        } catch (illegalStateException: IllegalStateException) {
            Log.e(TAG, illegalStateException.message ?: "IllegalStateException")
        } catch (illegalArgumentException: IllegalArgumentException) {
            Log.e(TAG, illegalArgumentException.message ?: "IllegalArgumentException")
        } catch (nullPointerException: NullPointerException) {
            Log.e(TAG, nullPointerException.message ?: "Exception")
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        if (requestCode == PERMISSION_CAMERA_REQUEST) {
            if (isCameraPermissionGranted()) {
                toast("Camera permission granted")

            } else {
                toast("Camera permission denied")
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun isCameraPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            baseContext,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private const val PERMISSION_CAMERA_REQUEST = 1

        //declare the key ,used to get the value returned from scankit
        const val SCAN_RESULT = "scanResult"
        private const val TAG = "CameraActivity"

        //scan_view_finder width & height is  300dp
        const val SCAN_FRAME_SIZE = 120
        var mScreenHeight = 0
        var mScreenWidth = 0
        var scanFrameSize = 0
    }
}