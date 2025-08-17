package com.example.qrcodescanner.ui

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.qrcodescanner.databinding.FragmentGeneratorBinding
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder

class GeneratorFragment : Fragment() {

    private var _binding: FragmentGeneratorBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGeneratorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.generateButton.setOnClickListener {
            val text = binding.textInput.text.toString().trim()
            if (text.isNotEmpty()) {
                generateQRCode(text)
            }
        }

        binding.saveButton.setOnClickListener {
            saveQRCode()
        }

        binding.shareButton.setOnClickListener {
            shareQRCode()
        }
    }

    private fun saveQRCode() {
        val bitmap = (binding.qrCodeImageView.drawable as? android.graphics.drawable.BitmapDrawable)?.bitmap
        if (bitmap != null) {
            // TODO: Add WRITE_EXTERNAL_STORAGE permission for Android < 10
            val contentValues = android.content.ContentValues().apply {
                put(android.provider.MediaStore.MediaColumns.DISPLAY_NAME, "QRCode_${System.currentTimeMillis()}.jpg")
                put(android.provider.MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                    put(android.provider.MediaStore.MediaColumns.RELATIVE_PATH, android.os.Environment.DIRECTORY_PICTURES)
                }
            }
            val resolver = requireContext().contentResolver
            val uri = resolver.insert(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            if (uri != null) {
                resolver.openOutputStream(uri).use { outputStream ->
                    bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 100, outputStream)
                }
                android.widget.Toast.makeText(requireContext(), "Saved to gallery", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun shareQRCode() {
        val bitmap = (binding.qrCodeImageView.drawable as? android.graphics.drawable.BitmapDrawable)?.bitmap
        if (bitmap != null) {
            try {
                val cachePath = java.io.File(requireContext().cacheDir, "images")
                cachePath.mkdirs()
                val stream = java.io.FileOutputStream("$cachePath/image.png")
                bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, stream)
                stream.close()

                val imagePath = java.io.File(requireContext().cacheDir, "images")
                val newFile = java.io.File(imagePath, "image.png")
                // TODO: Add FileProvider to AndroidManifest.xml
                val contentUri = androidx.core.content.FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.fileprovider", newFile)

                if (contentUri != null) {
                    val shareIntent = android.content.Intent()
                    shareIntent.action = android.content.Intent.ACTION_SEND
                    shareIntent.addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    shareIntent.setDataAndType(contentUri, requireContext().contentResolver.getType(contentUri))
                    shareIntent.putExtra(android.content.Intent.EXTRA_STREAM, contentUri)
                    startActivity(android.content.Intent.createChooser(shareIntent, "Choose an app"))
                }
            } catch (e: java.io.IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun generateQRCode(text: String) {
        val multiFormatWriter = MultiFormatWriter()
        try {
            val bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE, 300, 300)
            val barcodeEncoder = BarcodeEncoder()
            val bitmap: Bitmap = barcodeEncoder.createBitmap(bitMatrix)
            binding.qrCodeImageView.setImageBitmap(bitmap)
            binding.saveButton.visibility = View.VISIBLE
            binding.shareButton.visibility = View.VISIBLE
            (activity as? com.example.qrcodescanner.MainActivity)?.showInterstitialAd()
        } catch (e: WriterException) {
            e.printStackTrace()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
