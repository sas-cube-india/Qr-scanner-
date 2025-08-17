package com.example.qrcodescanner.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.qrcodescanner.data.AppDatabase
import com.example.qrcodescanner.data.QRCode
import com.example.qrcodescanner.data.QRCodeDao
import kotlinx.coroutines.launch

class HistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val qrCodeDao: QRCodeDao
    val allQRCodes: LiveData<List<QRCode>>
    val favoriteQRCodes: LiveData<List<QRCode>>

    init {
        qrCodeDao = AppDatabase.getDatabase(application).qrCodeDao()
        allQRCodes = qrCodeDao.getAllQRCodes()
        favoriteQRCodes = qrCodeDao.getFavoriteQRCodes()
    }

    fun updateQRCode(qrCode: QRCode) {
        viewModelScope.launch {
            qrCodeDao.update(qrCode)
        }
    }
}
