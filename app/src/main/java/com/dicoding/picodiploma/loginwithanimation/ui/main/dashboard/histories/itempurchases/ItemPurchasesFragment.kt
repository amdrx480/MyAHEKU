package com.dicoding.picodiploma.loginwithanimation.ui.main.dashboard.histories.itempurchases

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.picodiploma.loginwithanimation.databinding.FragmentItemPurchasesBinding
import com.dicoding.picodiploma.loginwithanimation.data.model.user.UserModel
import com.dicoding.picodiploma.loginwithanimation.ui.ViewModelUserFactory
import com.dicoding.picodiploma.loginwithanimation.ui.main.dashboard.DashboardFragment
import com.dicoding.picodiploma.loginwithanimation.ui.main.dashboard.histories.HistoriesActivity
import com.dicoding.picodiploma.loginwithanimation.ui.main.dashboard.stocks.LoadingStateAdapter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
class ItemPurchasesFragment : Fragment() {

    private lateinit var itemPurchasesAdapter: ItemPurchasesAdapter

    private var token: String = ""
    private val itemPurchasesViewModel: ItemPurchasesViewModel by viewModels {
        ViewModelUserFactory.getInstance(requireContext())
    }

    private var _binding: FragmentItemPurchasesBinding? = null
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentItemPurchasesBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        user = requireArguments().getParcelable(ARG_USER)!!
//        token = requireActivity().intent.getStringExtra(DashboardFragment.EXTRA_TOKEN) ?: ""


        setupAdapter()
//        setupObservers()
    }

    private fun setupAdapter() {
        itemPurchasesAdapter = ItemPurchasesAdapter()
        binding?.itemPurchasesRecyclerView?.apply {
            adapter = itemPurchasesAdapter.withLoadStateHeaderAndFooter(
                footer = LoadingStateAdapter { itemPurchasesAdapter.retry() },
                header = LoadingStateAdapter { itemPurchasesAdapter.retry() }
            )
            binding?.itemPurchasesRecyclerView?.layoutManager =
                LinearLayoutManager(requireContext())
            binding?.itemPurchasesRecyclerView?.setHasFixedSize(true)
        }

        lifecycleScope.launchWhenCreated {
            itemPurchasesAdapter.loadStateFlow.collect {
                binding?.swipeRefresh?.isRefreshing = it.mediator?.refresh is LoadState.Loading
            }
        }

        lifecycleScope.launch {
            itemPurchasesAdapter.loadStateFlow.collectLatest { loadStates ->
                binding?.tvInfo?.root?.isVisible = loadStates.refresh is LoadState.Error
            }
            if (itemPurchasesAdapter.itemCount < 1) binding?.tvInfo?.root?.visibility = View.VISIBLE
            else binding?.tvInfo?.root?.visibility = View.VISIBLE
        }

        itemPurchasesViewModel.getPurchases(token).observe(viewLifecycleOwner) {
            itemPurchasesAdapter.submitData(lifecycle, it)
        }
    }

//    private fun setupObservers() {
//        itemPurchasesViewModel.getPurchases(user.token).observe(viewLifecycleOwner) {
//            lifecycleScope.launch {
//                itemPurchasesAdapter.submitData(it)
//            }
//        }
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

//    companion object {
//        private const val ARG_TOKEN = "extra_token"
//
//        //        fun newInstance(user: UserModel): ItemPurchasesFragment {
////            val fragment = ItemPurchasesFragment()
////            val bundle = Bundle().apply {
////                putParcelable(ARG_TOKEN, user)
////            }
////            fragment.arguments = bundle
////            return fragment
////        }
////        fun newInstance(token: String): ItemPurchasesFragment {
////            val fragment = ItemPurchasesFragment()
////            val bundle = Bundle().apply {
////                putString(ARG_TOKEN, token)
////            }
////            fragment.arguments = bundle
////            return fragment
////        }
//    }

//    companion object {
//        private const val ARG_USER = "user"
//
//        fun newInstance(user: UserModel): ItemPurchasesFragment {
//            val fragment = ItemPurchasesFragment()
//            val bundle = Bundle().apply {
//                putParcelable(ARG_USER, user)
//            }
//            fragment.arguments = bundle
//            return fragment
//        }
//    }

//class ItemPurchasesFragment : Fragment() {
//
//    private lateinit var itemPurchasesAdapter: ItemPurchasesAdapter
//
//    //    private lateinit var user: UserModel
//    private var token: String = ""
//    private val itemPurchasesViewModel: ItemPurchasesViewModel by viewModels {
//        ViewModelFactory.getInstance(requireContext())
//    }
//
//    private var _binding: FragmentItemPurchasesBinding? = null
//    private val binding get() = _binding
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        _binding = FragmentItemPurchasesBinding.inflate(inflater, container, false)
//        return binding?.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
////        user = requireArguments().getParcelable(ARG_USER)!!
//        token = requireActivity().intent.getStringExtra(DashboardFragment.EXTRA_TOKEN) ?: ""
//
//
//        setupAdapter()
////        setupObservers()
//    }
//
//    private fun setupAdapter() {
//        itemPurchasesAdapter = ItemPurchasesAdapter()
//        binding?.itemPurchasesRecyclerView?.apply {
//            adapter = itemPurchasesAdapter.withLoadStateHeaderAndFooter(
//                footer = LoadingStateAdapter { itemPurchasesAdapter.retry() },
//                header = LoadingStateAdapter { itemPurchasesAdapter.retry() }
//            )
//            binding?.itemPurchasesRecyclerView?.layoutManager =
//                LinearLayoutManager(requireContext())
//            binding?.itemPurchasesRecyclerView?.setHasFixedSize(true)
//        }
//
//        lifecycleScope.launchWhenCreated {
//            itemPurchasesAdapter.loadStateFlow.collect {
//                binding?.swipeRefresh?.isRefreshing = it.mediator?.refresh is LoadState.Loading
//            }
//        }
//
//        lifecycleScope.launch {
//            itemPurchasesAdapter.loadStateFlow.collectLatest { loadStates ->
//                binding?.tvInfo?.root?.isVisible = loadStates.refresh is LoadState.Error
//            }
//            if (itemPurchasesAdapter.itemCount < 1) binding?.tvInfo?.root?.visibility = View.VISIBLE
//            else binding?.tvInfo?.root?.visibility = View.VISIBLE
//        }
//
//        itemPurchasesViewModel.getPurchases(token).observe(viewLifecycleOwner) {
//            itemPurchasesAdapter.submitData(lifecycle, it)
//        }
//    }
//
////    private fun setupObservers() {
////        itemPurchasesViewModel.getPurchases(user.token).observe(viewLifecycleOwner) {
////            lifecycleScope.launch {
////                itemPurchasesAdapter.submitData(it)
////            }
////        }
////    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//
//    companion object {
//        private const val ARG_TOKEN = "extra_token"
//
//        //        fun newInstance(user: UserModel): ItemPurchasesFragment {
////            val fragment = ItemPurchasesFragment()
////            val bundle = Bundle().apply {
////                putParcelable(ARG_TOKEN, user)
////            }
////            fragment.arguments = bundle
////            return fragment
////        }
//        fun newInstance(token: String): ItemPurchasesFragment {
//            val fragment = ItemPurchasesFragment()
//            val bundle = Bundle().apply {
//                putString(ARG_TOKEN, token)
//            }
//            fragment.arguments = bundle
//            return fragment
//        }
//    }
//
////    companion object {
////        private const val ARG_USER = "user"
////
////        fun newInstance(user: UserModel): ItemPurchasesFragment {
////            val fragment = ItemPurchasesFragment()
////            val bundle = Bundle().apply {
////                putParcelable(ARG_USER, user)
////            }
////            fragment.arguments = bundle
////            return fragment
////        }
////    }
//}