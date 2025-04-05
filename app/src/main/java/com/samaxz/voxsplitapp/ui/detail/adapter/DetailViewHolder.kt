package com.samaxz.voxsplitapp.ui.detail.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.samaxz.voxsplitapp.databinding.DetailItemBinding
import com.samaxz.voxsplitapp.domain.model.SegmentD

class DetailViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val binding = DetailItemBinding.bind(view)
    fun render(segment: SegmentD, onItemSelected: (SegmentD) -> Unit) {
        binding.tvSegmentTitle.text = "Segment ${segment.id + 1}"
        binding.tvSpeakers.text = segment.prob.joinToString(", ")
        val timeEnd = String.format("%.2f", segment.end)
        binding.tvTimeEnd.text = "TE: $timeEnd"
        val timeStart = String.format("%.2f", segment.start)
        binding.tvTimeStart.text = "TS: $timeStart"
        binding.tvOverlay.text = "OL: ${segment.overlapp}"
        binding.tvTranscription.text = segment.text
        
        binding.parent.setOnClickListener {
            onItemSelected(segment)
        }

    }
}