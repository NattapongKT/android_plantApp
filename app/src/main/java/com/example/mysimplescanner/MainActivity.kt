package com.example.mysimplescanner

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter

import android.content.ContentValues
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import java.io.OutputStream

//ปุ่มแสกน
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions

class MainActivity : AppCompatActivity() {

    private lateinit var etText: EditText
    private lateinit var imgQR: ImageView
    private lateinit var btnGenerate: Button
    private lateinit var btnDownload: Button

    private var qrBitmap: Bitmap? = null   // เก็บ bitmap ไว้ใช้ตอน download

    private lateinit var btnScan: Button

    private val scanLauncher = registerForActivityResult(ScanContract()) { result ->
        if (result.contents != null) {
            etText.setText(result.contents)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etText = findViewById(R.id.etText)
        imgQR = findViewById(R.id.imgQR)
        btnGenerate = findViewById(R.id.btnGenerate)
        btnDownload = findViewById(R.id.btnDownload)

        btnGenerate.setOnClickListener {
            val text = etText.text.toString()
            if (text.isNotEmpty()) {
                qrBitmap = generateQRCode(text)
                imgQR.setImageBitmap(qrBitmap)
            }
        }
        btnDownload.setOnClickListener {
            qrBitmap?.let {
                saveImage(it)
            }
        }

        btnScan = findViewById(R.id.btnScan)

        btnScan.setOnClickListener {
            val options = ScanOptions()
            options.setPrompt("ส่อง QR Code")
            options.setBeepEnabled(true)
            options.setOrientationLocked(false)

            scanLauncher.launch(options)
        }

    }

    private fun generateQRCode(text: String): Bitmap {
        val size = 500
        val bits = MultiFormatWriter().encode(
            text,
            BarcodeFormat.QR_CODE,
            size,
            size
        )

        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565)
        for (x in 0 until size) {
            for (y in 0 until size) {
                bitmap.setPixel(x, y, if (bits[x, y]) Color.BLACK else Color.WHITE)
            }
        }
        return bitmap
    }

    private fun saveImage(bitmap: Bitmap) {
        val filename = "QR_${System.currentTimeMillis()}.png"

        val outputStream: OutputStream?

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            }

            val uri = contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )

            outputStream = uri?.let { contentResolver.openOutputStream(it) }
        } else {
            val imagesDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES
            )
            val image = java.io.File(imagesDir, filename)
            outputStream = image.outputStream()
        }

        outputStream?.use {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            Toast.makeText(this, "Saved to Gallery", Toast.LENGTH_SHORT).show()
        }
    }

}
