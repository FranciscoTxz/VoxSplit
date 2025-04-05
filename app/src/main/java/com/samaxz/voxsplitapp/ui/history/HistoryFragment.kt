package com.samaxz.voxsplitapp.ui.history

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.samaxz.voxsplitapp.databinding.FragmentHistoryBinding
import com.samaxz.voxsplitapp.ui.history.adapter.HistoryAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.getValue

@AndroidEntryPoint
class HistoryFragment : Fragment() {
    private val historyViewModel by viewModels<HistoryViewModel>()
    private lateinit var historyAdapter: HistoryAdapter

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        initList()
        initListeners()
        initUIState()
    }

    private fun initListeners() {
        binding.faBtnDelete.setOnClickListener {
            historyViewModel.deleteHistory()
        }
    }

    private fun initList() {
        historyAdapter = HistoryAdapter(onItemSelected = {
            Log.i("SUPERSAMA", it.toString())
        })
        binding.rvHistory.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = historyAdapter
        }
    }

    private fun initUIState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                historyViewModel.history.collect { list ->
                    if (list.isEmpty()) {
                        binding.rvHistory.isVisible = false
                        binding.tvEmptyText.isVisible = true
                    } else {
                        binding.tvEmptyText.isVisible = false
                        historyAdapter.updateList(list)
                        binding.rvHistory.isVisible = true
                    }
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                historyViewModel.isLoading.collect { value ->
                    binding.pbLoading.isVisible = value
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHistoryBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        historyViewModel.updateList()
    }

}