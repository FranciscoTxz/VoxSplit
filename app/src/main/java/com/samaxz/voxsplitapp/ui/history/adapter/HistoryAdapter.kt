package com.samaxz.voxsplitapp.ui.history.adapter

import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import com.samaxz.voxsplitapp.domain.model.HistoryInfo
import com.samaxz.voxsplitapp.R

class HistoryAdapter(
    private var historyList: List<HistoryInfo> = emptyList(),
    private val onItemSelected: (HistoryInfo) -> Unit
) : RecyclerView.Adapter<HistoryViewHolder>() {

    fun updateList(list: List<HistoryInfo>) {
        historyList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        return HistoryViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.history_item, parent, false)
        )
    }

    override fun getItemCount() = historyList.size

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.render(historyList[position], onItemSelected)
    }
}
