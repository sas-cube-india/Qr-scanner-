package com.example.qrcodescanner.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "qr_codes")
data class QRCode(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val data: String,
    val timestamp: Long,
    val isFavorite: Boolean = false
)
