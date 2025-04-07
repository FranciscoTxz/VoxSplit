package com.samaxz.voxsplitapp.ui.detailhistory.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.samaxz.voxsplitapp.R
import com.samaxz.voxsplitapp.domain.model.SegmentD

class DetailHistoryAdapter(
    private var segmentList: List<SegmentD> = emptyList(),
    private val onItemSelected: (SegmentD) -> Unit
) : RecyclerView.Adapter<DetailHistoryViewHolder>() {

    fun updateList(list: List<SegmentD>) {
        segmentList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DetailHistoryViewHolder {
        return DetailHistoryViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.detail_item, parent, false)
        )
    }

    override fun onBindViewHolder(
        holder: DetailHistoryViewHolder,
        position: Int
    ) {
        holder.render(segmentList[position], onItemSelected)
    }

    override fun getItemCount() = segmentList.size


}