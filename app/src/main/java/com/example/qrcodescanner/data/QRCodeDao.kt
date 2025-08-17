package com.example.qrcodescanner.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface QRCodeDao {
    @Insert
    suspend fun insert(qrCode: QRCode)

    @Query("SELECT * FROM qr_codes ORDER BY timestamp DESC")
    fun getAllQRCodes(): LiveData<List<QRCode>>

    @Query("SELECT * FROM qr_codes WHERE isFavorite = 1 ORDER BY timestamp DESC")
    fun getFavoriteQRCodes(): LiveData<List<QRCode>>

    @Update
    suspend fun update(qrCode: QRCode)
}
