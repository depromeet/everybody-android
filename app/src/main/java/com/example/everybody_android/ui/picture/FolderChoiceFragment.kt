package com.example.everybody_android.ui.picture

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.example.everybody_android.R
import com.example.everybody_android.adapter.RecyclerViewAdapter
import com.example.everybody_android.base.BaseFragment
import com.example.everybody_android.databinding.FragmentFolderChoiceBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FolderChoiceFragment : BaseFragment<FragmentFolderChoiceBinding, FolderChoiceViewModel>() {
    override val viewModel: FolderChoiceViewModel by viewModels()
    override val layoutId: Int = R.layout.fragment_folder_choice
    private val adapter by lazy { RecyclerViewAdapter {} }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun init() {
        binding.recyclerFolder.adapter = adapter
        
    }
}