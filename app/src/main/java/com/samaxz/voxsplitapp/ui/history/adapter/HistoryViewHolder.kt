package com.samaxz.voxsplitapp.ui.history.adapter

import android.view.View
import android.view.animation.LinearInterpolator
import androidx.recyclerview.widget.RecyclerView
import com.samaxz.voxsplitapp.databinding.HistoryItemBinding
import com.samaxz.voxsplitapp.domain.model.HistoryInfo

class HistoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val binding = HistoryItemBinding.bind(view)
    fun render(historyInfo: HistoryInfo, onItemSelected: (HistoryInfo) -> Unit) {
        binding.tvName.text = historyInfo.name
        binding.tvDescription.text = historyInfo.description

        binding.parent.setOnClickListener {
//            startRotationAnimation(
//                binding.parent,
//                newLambda = { onItemSelected(historyInfo) }
//            )
            onItemSelected(historyInfo)
        }
    }

    private fun startRotationAnimation(view: View, newLambda: () -> Unit) {
        view.animate().apply {
            duration = 500
            interpolator = LinearInterpolator()
            rotationBy(360f)
            withEndAction { newLambda() }
            start()
        }
    }
}