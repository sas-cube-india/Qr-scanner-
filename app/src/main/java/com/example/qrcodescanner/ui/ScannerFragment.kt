package com.example.qrcodescanner.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.qrcodescanner.databinding.FragmentScannerBinding
import com.example.qrcodescanner.data.AppDatabase
import com.example.qrcodescanner.data.QRCode
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ScannerFragment : Fragment() {

    private var _binding: FragmentScannerBinding? = null
    private val binding get() = _binding!!

    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var cameraExecutor: ExecutorService
    private val imageAnalysis = ImageAnalysis.Builder()
        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
        .build()

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                startCamera()
            } else {
                Toast.makeText(requireContext(), "Camera permission is required", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScannerBinding.inflate(inflater, container, false)
        return binding.root
    }

    private var isBulkScanning = false
    private val bulkScannedCodes = mutableListOf<String>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TODO: Check for pro status
        val isProUser = false
        if (isProUser) {
            binding.bulkScanButton.visibility = View.VISIBLE
        }

        cameraExecutor = Executors.newSingleThreadExecutor()

        if (isCameraPermissionGranted()) {
            startCamera()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

        binding.scanFromGalleryButton.setOnClickListener {
            scanFromGallery()
        }

        binding.bulkScanButton.setOnClickListener {
            if (isBulkScanning) {
                stopBulkScan()
            } else {
                startBulkScan()
            }
        }
    }

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                val inputImage = InputImage.fromFilePath(requireContext(), uri)
                processImage(inputImage)
            }
        }

    private fun scanFromGallery() {
        pickImageLauncher.launch("image/*")
    }

    private fun processImage(image: InputImage) {
        val scanner = BarcodeScanning.getClient()
        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                for (barcode in barcodes) {
                    showResultDialog(barcode.rawValue ?: "")
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to scan QR code", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showResultDialog(qrCode: String) {
        saveQRCodeToDatabase(qrCode)
        val dialog = android.app.AlertDialog.Builder(requireContext())
            .setTitle("QR Code Scanned")
            .setMessage(qrCode)
            .setPositiveButton("Copy") { _, _ ->
                val clipboard =
                    requireContext().getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                val clip = android.content.ClipData.newPlainText("QR Code", qrCode)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(requireContext(), "Copied to clipboard", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Close") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
        dialog.show()
    }

    private fun isCameraPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun startCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            bindPreview(cameraProvider)
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun bindPreview(cameraProvider: ProcessCameraProvider) {
        val preview: Preview = Preview.Builder().build()
        val cameraSelector: CameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()
        preview.setSurfaceProvider(binding.previewView.surfaceProvider)

        val qrCodeAnalyzer = QRCodeAnalyzer { qrCode ->
            activity?.runOnUiThread {
                if (isBulkScanning) {
                    if (!bulkScannedCodes.contains(qrCode)) {
                        bulkScannedCodes.add(qrCode)
                        // TODO: Add vibration or sound feedback
                    }
                } else {
                    showResultDialog(qrCode)
                }
            }
        }
        imageAnalysis.setAnalyzer(cameraExecutor, qrCodeAnalyzer)

        cameraProvider.bindToLifecycle(viewLifecycleOwner, cameraSelector, preview, imageAnalysis)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        cameraExecutor.shutdown()
    }

    private fun saveQRCodeToDatabase(qrCode: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val qrCodeDao = AppDatabase.getDatabase(requireContext()).qrCodeDao()
            qrCodeDao.insert(QRCode(data = qrCode, timestamp = System.currentTimeMillis()))
        }
    }

    private fun startBulkScan() {
        isBulkScanning = true
        bulkScannedCodes.clear()
        binding.bulkScanButton.text = "Stop Bulk Scan"
    }

    private fun stopBulkScan() {
        isBulkScanning = false
        binding.bulkScanButton.text = "Start Bulk Scan"
        showBulkScanResultDialog()
    }

    private fun showBulkScanResultDialog() {
        val message = if (bulkScannedCodes.isEmpty()) {
            "No QR codes scanned."
        } else {
            bulkScannedCodes.joinToString("\n")
        }
        val dialog = android.app.AlertDialog.Builder(requireContext())
            .setTitle("Bulk Scan Results")
            .setMessage(message)
            .setPositiveButton("Save All") { _, _ ->
                for (code in bulkScannedCodes) {
                    saveQRCodeToDatabase(code)
                }
                Toast.makeText(requireContext(), "Saved all scanned codes", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Close") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
        dialog.show()
    }
}
