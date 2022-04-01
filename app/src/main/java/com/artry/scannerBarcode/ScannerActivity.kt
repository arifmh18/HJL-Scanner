package com.artry.scannerBarcode

import android.os.Bundle
import com.artry.scannerBarcode.databinding.ActivityScannerBinding
import android.content.pm.PackageManager
import com.journeyapps.barcodescanner.CaptureManager
import com.journeyapps.barcodescanner.ViewfinderView
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import android.app.Activity
import android.graphics.Color
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import com.artry.scannerBarcode.databinding.CustomScannerBinding
import com.journeyapps.barcodescanner.DecoratedBarcodeView.TorchListener
import java.util.*

class ScannerActivity : Activity(), TorchListener {
    private var capture: CaptureManager? = null
    private var barcodeScannerView: DecoratedBarcodeView? = null
    private var switchFlashlightButton: Button? = null
    private var viewfinderView: ViewfinderView? = null
    private lateinit var binding: ActivityScannerBinding
    private lateinit var bindingFinder: CustomScannerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScannerBinding.inflate(layoutInflater)
        bindingFinder = CustomScannerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        barcodeScannerView = binding.zxingBarcodeScanner
        barcodeScannerView!!.setTorchListener(this)

        switchFlashlightButton = binding.switchFlashlight
        viewfinderView = bindingFinder.zxingViewfinderView

        // if the device does not have flashlight in its camera,
        // then remove the switch flashlight button...
        if (!hasFlash()) {
            switchFlashlightButton!!.visibility = View.GONE
        }
        capture = CaptureManager(this, barcodeScannerView)
        capture!!.initializeFromIntent(intent, savedInstanceState)
        capture!!.setShowMissingCameraPermissionDialog(false)
        capture!!.decode()
        capture!!.onResume()
        changeMaskColor(null)
        changeLaserVisibility(true)
    }

    override fun onResume() {
        super.onResume()
        capture!!.onResume()
    }

    override fun onPause() {
        super.onPause()
        capture!!.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        capture!!.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        capture!!.onSaveInstanceState(outState)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return barcodeScannerView!!.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event)
    }

    private fun hasFlash(): Boolean {
        return applicationContext.packageManager
            .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)
    }

    fun switchFlashlight(view: View?) {
        if ("Hidupkan Flash" == switchFlashlightButton!!.text) {
            barcodeScannerView!!.setTorchOn()
        } else {
            barcodeScannerView!!.setTorchOff()
        }
    }

    fun changeMaskColor(view: View?) {
        val rnd = Random()
        val color: Int = Color.argb(100, 0, 0, 0)
        viewfinderView!!.setMaskColor(color)
    }

    fun changeLaserVisibility(visible: Boolean) {
        viewfinderView!!.setLaserVisibility(visible)
    }

    override fun onTorchOn() {
        switchFlashlightButton!!.setText("Matikan Flash")
    }

    override fun onTorchOff() {
        switchFlashlightButton!!.setText("Hidupkan Flash")
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        capture!!.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}
