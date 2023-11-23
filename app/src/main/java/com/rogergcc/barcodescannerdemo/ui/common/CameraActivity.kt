package com.rogergcc.barcodescannerdemo.ui.common

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.*
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.core.Camera
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.rogergcc.barcodescannerdemo.databinding.ActivityCameraBinding
import com.rogergcc.barcodescannerdemo.ui.helper.rotate
import com.rogergcc.barcodescannerdemo.ui.helper.toBitmap
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.util.concurrent.Executors


class CameraActivity : AppCompatActivity() {

    private lateinit var camera: Camera
    private lateinit var binding: ActivityCameraBinding

    private var previewView: PreviewView? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var cameraSelector: CameraSelector? = null
    private var lensFacing = CameraSelector.LENS_FACING_BACK
    private var previewUseCase: Preview? = null
    private var analysisUseCase: ImageAnalysis? = null

    lateinit var cameraControl: CameraControl
    private var flashFlag: Boolean = false


    private var showTorchToggle = false
    private var showCloseButton = false
    private var useFrontCamera = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupEdgeToEdgeUI()
        applyScannerConfig()
//        setupCamera() 11
    }

    private fun applyScannerConfig() {

//        binding.overlayView.setCustomText(R.string.place_the_qr_code_in_the_indicated_rectangle)
//        binding.overlayView.setCustomIcon(R.drawable.quickie_ic_qrcode)
//        binding.overlayView.setHorizontalFrameRatio(1.2F) //TODO QR OVERLAY
        binding.overlayView.setHorizontalFrameRatio(2.2F) //TODO BARCODE OVERLAY
//        binding.overlayView.setTorchState(true)

        val hasFlash = this.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)


        showTorchToggle = true
        showCloseButton = true
        useFrontCamera = false


        binding.overlayView.setCloseVisibilityAndOnClick(showCloseButton) { finish() }
//        binding.overlayView.setTorchVisibilityAndOnClick(showTorchToggle) {
//            remoteView.switchLight()
//        }

        setupCamera()


//        if (showTorchToggle && hasFlash) {
//            binding.overlayView.setTorchVisibilityAndOnClick(true) {
////                remoteView.switchLight()
//                cameraControl.enableTorch(showTorchToggle)
//                if (camera.cameraInfo.hasFlashUnit()) {
//                    binding.overlayView.setTorchState(true)
//                } else {
//                    binding.overlayView.setTorchState(false)
//                }
//            }
//        }

        if (showTorchToggle && hasFlash) {
            binding.overlayView.setTorchVisibilityAndOnClick(true) {
                flashFlag = !flashFlag
                binding.overlayView.setTorchState(flashFlag)
                cameraControl.enableTorch(flashFlag)


            }
        }

    }

//    private fun setFlashOperation() {
//        val hasFlash = this.packageManager
//            .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)
//        if (!hasFlash) {
//            binding.overlayView.setTorchState(false)
//        } else {
////            binding.flashBtn.visibility = View.VISIBLE
//            binding.overlayView.setTorchState(true)
//        }
//        binding.flashBtn.setOnClickListener {
//            if (remoteView.lightStatus) {
//                remoteView.switchLight()
//                binding.flashBtn.setImageResource(R.drawable.scankit_flashlight_layer_off)
//            } else {
//                remoteView.switchLight()
//                binding.flashBtn.setImageResource(R.drawable.scankit_flashlight_layer_on)
//            }
//        }
//    }

    private fun setupEdgeToEdgeUI() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        ViewCompat.setOnApplyWindowInsetsListener(binding.overlayView) { v, insets ->
            insets.getInsets(WindowInsetsCompat.Type.systemBars())
                .let { v.setPadding(it.left, it.top, it.right, it.bottom) }
            WindowInsetsCompat.CONSUMED
        }
    }

    private fun setupCamera() {//11
        previewView = binding.previewView
        cameraSelector = CameraSelector.Builder()

            .requireLensFacing(lensFacing)

            .build()
        ViewModelProvider(
            this, ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ).get(CameraXViewModel::class.java)
            .processCameraProvider
            .observe(this) { provider: ProcessCameraProvider? ->
                cameraProvider = provider
                if (isCameraPermissionGranted()) {
                    bindCameraUseCases()
                } else {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.CAMERA),
                        PERMISSION_CAMERA_REQUEST
                    )
                }
            }
    }

    private fun bindCameraUseCases() {
        bindPreviewUseCase()
        bindAnalyseUseCase()
    }

    private fun bindPreviewUseCase() {
        if (cameraProvider == null) {
            return
        }
        if (previewUseCase != null) {
            cameraProvider!!.unbind(previewUseCase)
        }

        previewUseCase = Preview.Builder()
            .setTargetRotation(previewView?.display?.rotation ?: 0)
//            .setTargetAspectRatio(AspectRatio.RATIO_16_9)
            .build()
        previewUseCase!!.setSurfaceProvider(previewView!!.surfaceProvider)

        try {
//            val imageCapture = ImageCapture.Builder() // de una foto
//                .setTargetAspectRatio(AspectRatio.RATIO_4_3)
//                .build()

            camera= cameraProvider!!.bindToLifecycle(
                this,
                cameraSelector!!,
                previewUseCase,
            )
//            camera.cameraControl.enableTorch(true)

            cameraControl = camera.cameraControl
//            cameraControl.setZoomRatio(0.9f)
            cameraControl.enableTorch(flashFlag)



        } catch (illegalStateException: IllegalStateException) {
            Log.e(TAG, illegalStateException.message ?: "IllegalStateException")
        } catch (illegalArgumentException: IllegalArgumentException) {
            Log.e(TAG, illegalArgumentException.message ?: "IllegalArgumentException")
        }
    }

    private fun bindAnalyseUseCase() {
        // Configura el escáner de códigos de barras con opciones personalizadas si es necesario
        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(
//                Barcode.FORMAT_QR_CODE,
                Barcode.FORMAT_EAN_13,
                Barcode.FORMAT_CODE_128)
            .build()

//        val options = BarcodeScannerOptions.Builder()
//            .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS).build()

        val barcodeScanner: BarcodeScanner = BarcodeScanning.getClient(options)

        if (cameraProvider == null) {
            return
        }
        if (analysisUseCase != null) {
            cameraProvider!!.unbind(analysisUseCase)
        }

//        analysisUseCase.camera.cameraControl.setZoomRatio(0.5f)

        analysisUseCase = ImageAnalysis.Builder()
            .setTargetRotation(previewView?.display?.rotation?:0)
//            .setTargetAspectRatio(AspectRatio.RATIO_16_9)
//            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()

        // Initialize our background executor
        val cameraExecutor = Executors.newSingleThreadExecutor()

        analysisUseCase?.setAnalyzer(cameraExecutor) { imageProxy ->
            processImageProxy(barcodeScanner, imageProxy)
        }


        try {
            cameraProvider!!.bindToLifecycle(
               this,
                cameraSelector!!,
                analysisUseCase
            )
        } catch (illegalStateException: IllegalStateException) {
            Log.e(TAG, illegalStateException.message ?: "IllegalStateException")
        } catch (illegalArgumentException: IllegalArgumentException) {
            Log.e(TAG, illegalArgumentException.message ?: "IllegalArgumentException")
        } catch (nullPointerException: NullPointerException) {
            Log.e(TAG, nullPointerException.message ?: "Exception")
        }
    }

    private fun cropNv21(nv21: ByteArray, width: Int, height: Int, roi: Rect): ByteArray {
        // Calcular las nuevas dimensiones después del recorte
        val newWidth = roi.width()
        val newHeight = roi.height()

        // Crear un objeto YuvImage para el recorte
        val yuvImage = YuvImage(nv21, ImageFormat.NV21, width, height, null)
        val outputStream = ByteArrayOutputStream()
        yuvImage.compressToJpeg(roi, 100, outputStream)

        // Obtener los bytes JPEG recortados
        return outputStream.toByteArray()
    }

    private fun ImageProxy.toNv21(): ByteArray {
        val nv21 = ByteArray(width * height * 3 / 2)
        imageToByteBuffer(this, nv21, width * height * 3 / 2)
        return nv21
    }

    private fun imageToByteBuffer(image: ImageProxy, outputBuffer: ByteArray, pixelCount: Int) {
        // Implementa la lógica para convertir la imagen a formato NV21
        // Utiliza las funciones de ImageProxy y ByteBuffer
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun processImageProxy(
        barcodeScanner: BarcodeScanner,
        imageProxy: ImageProxy,
    ) {


        val mediaImage = imageProxy.image
        val rotationDegrees = imageProxy.imageInfo.rotationDegrees
        val inputImage = mediaImage?.let { InputImage.fromMediaImage(it, imageProxy.imageInfo.rotationDegrees) }

        if (mediaImage != null) {

            // Calcular las coordenadas del rectángulo para que esté en el centro
            val rectWidth = 100 // Ancho del rectángulo (ajústalo según tus necesidades)
            val rectHeight = 400 // Altura del rectángulo (ajústalo según tus necesidades)

            val height = mediaImage.height
            val width = mediaImage.width
            val c1x = (width - rectWidth) / 2
            val c1y = (height - rectHeight) / 2
            val c2x = c1x + rectWidth
            val c2y = c1y + rectHeight

// Crear un rectángulo a partir de las coordenadas
            val rect = Rect(c1x, c1y, c2x, c2y)
//            val height = mediaImage.height
//            val width = mediaImage.width
//
//            //Since in the end the image will rotate clockwise 90 degree
//            //left -> top, top -> right, right -> bottom, bottom -> left
//
//            //Top    : (far) -value > 0 > +value (closer)
//            val c1x = (width * 0.125).toInt() + 150
//
//            //Right  : (far) -value > 0 > +value (closer)
//            val c1y = (height * 0.25).toInt() - 25
//
//            //Bottom : (closer) -value > 0 > +value (far)
//            val c2x = (width * 0.875).toInt() - 150
//
//            //Left   : (closer) -value > 0 > +value (far)
//            val c2y = (height * 0.75).toInt() + 25
//
//            val rect = Rect()
//            rect.left = c1x
//            rect.top = c1y
//            rect.right = c2x
//            rect.bottom = c2y
//
//
            val ori: Bitmap = imageProxy.toBitmap()!!
            val crop = Bitmap.createBitmap(ori, rect.left, rect.top, rect.width(), rect.height())
            val rImage = crop.rotate(90F)
//
            val image = InputImage.fromBitmap(rImage,rotationDegrees)

            barcodeScanner.process(image)
            .addOnSuccessListener { barcodes ->

                barcodes.forEach { barcode ->


                    val bounds = barcode.boundingBox
                    val corners = barcode.cornerPoints
                    val rawValue = barcode.rawValue


                    binding.tvScannedData.text = barcode.rawValue
                    Toast.makeText(this, "scan=> ${barcode.rawValue}", Toast.LENGTH_SHORT).show()
                    val valueType = barcode.valueType
                    // See API reference for complete list of supported types
                    when (valueType) {
                        Barcode.FORMAT_QR_CODE -> {
                            val qrCode = barcode.rawValue
                            binding.tvScannedData.text = "qr $qrCode"
                        }
                        Barcode.TYPE_WIFI -> {
                            val ssid = barcode.wifi!!.ssid
                            val password = barcode.wifi!!.password
                            val type = barcode.wifi!!.encryptionType
                            binding.tvScannedData.text = "ssid: $ssid\npassword: $password\ntype: $type"
                        }
                        Barcode.TYPE_URL -> {
                            val title = barcode.url!!.title
                            val url = barcode.url!!.url

                            binding.tvScannedData.text = "Title: $title\nURL: $url"
                        }
                    }
                }
            }
            .addOnFailureListener {
                Log.e(TAG, it.message ?: it.toString())
            }
            .addOnCompleteListener {
                // When the image is from CameraX analysis use case, must call image.close() on received
                // images when finished using them. Otherwise, new images may not be received or the camera
                // may stall.
                imageProxy.close()

            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        if (requestCode == PERMISSION_CAMERA_REQUEST) {
            if (isCameraPermissionGranted()) {
                bindCameraUseCases()
            } else {
                Log.e(TAG, "no camera permission")
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

        private const val TAG = "CameraActivity"

        private const val PERMISSION_CAMERA_REQUEST = 1

        //scan_view_finder width & height is  300dp
        const val SCAN_FRAME_SIZE = 120
        var mScreenHeight = 0
        var mScreenWidth = 0
    }
}