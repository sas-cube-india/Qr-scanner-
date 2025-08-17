package com.example.qrcodescanner.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.qrcodescanner.R
import com.example.qrcodescanner.data.QRCode
import com.example.qrcodescanner.databinding.ItemQrCodeBinding
import java.text.SimpleDateFormat
import java.util.*

class HistoryAdapter(
    private val onFavoriteClicked: (QRCode) -> Unit
) : ListAdapter<QRCode, HistoryAdapter.QRCodeViewHolder>(QRCodeDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QRCodeViewHolder {
        val binding = ItemQrCodeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return QRCodeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: QRCodeViewHolder, position: Int) {
        val qrCode = getItem(position)
        holder.bind(qrCode, onFavoriteClicked)
    }

    class QRCodeViewHolder(private val binding: ItemQrCodeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(qrCode: QRCode, onFavoriteClicked: (QRCode) -> Unit) {
            binding.qrCodeData.text = qrCode.data
            binding.qrCodeTimestamp.text = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(qrCode.timestamp))

            if (android.util.Patterns.WEB_URL.matcher(qrCode.data).matches()) {
                binding.qrCodeIcon.setImageResource(R.drawable.ic_link)
            } else {
                binding.qrCodeIcon.setImageResource(R.drawable.ic_text)
            }

            if (qrCode.isFavorite) {
                binding.favoriteButton.setImageResource(R.drawable.ic_favorite)
            } else {
                binding.favoriteButton.setImageResource(R.drawable.ic_favorite_border)
            }
            binding.favoriteButton.setOnClickListener {
                onFavoriteClicked(qrCode)
            }
        }
    }
}

class QRCodeDiffCallback : DiffUtil.ItemCallback<QRCode>() {
    override fun areItemsTheSame(oldItem: QRCode, newItem: QRCode): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: QRCode, newItem: QRCode): Boolean {
        return oldItem == newItem
    }
}
