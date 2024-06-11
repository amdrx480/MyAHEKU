package com.dicoding.picodiploma.loginwithanimation.ui.main.dashboard.histories.itemtransactions

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
import com.dicoding.picodiploma.loginwithanimation.databinding.FragmentItemTransactionsBinding
import com.dicoding.picodiploma.loginwithanimation.ui.ViewModelUserFactory
import com.dicoding.picodiploma.loginwithanimation.ui.main.dashboard.stocks.LoadingStateAdapter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ItemTransactionsFragment : Fragment() {

    private lateinit var itemTransactionsAdapter: ItemTransactionsAdapter

    //    private lateinit var user: UserModel
    private var token: String = ""
    private val itemTransactionsViewModel: ItemTransactionsViewModel by viewModels {
        ViewModelUserFactory.getInstance(requireContext())
    }

    private var _binding: FragmentItemTransactionsBinding? = null
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentItemTransactionsBinding.inflate(inflater, container, false)
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
        itemTransactionsAdapter = ItemTransactionsAdapter()
        binding?.itemTransactionsRecyclerView?.apply {
            adapter = itemTransactionsAdapter.withLoadStateHeaderAndFooter(
                footer = LoadingStateAdapter { itemTransactionsAdapter.retry() },
                header = LoadingStateAdapter { itemTransactionsAdapter.retry() }
            )
            binding?.itemTransactionsRecyclerView?.layoutManager =
                LinearLayoutManager(requireContext())
            binding?.itemTransactionsRecyclerView?.setHasFixedSize(true)
        }

        lifecycleScope.launchWhenCreated {
            itemTransactionsAdapter.loadStateFlow.collect {
                binding?.swipeRefresh?.isRefreshing = it.mediator?.refresh is LoadState.Loading
            }
        }

        lifecycleScope.launch {
            itemTransactionsAdapter.loadStateFlow.collectLatest { loadStates ->
                binding?.tvInfo?.root?.isVisible = loadStates.refresh is LoadState.Error
            }
            if (itemTransactionsAdapter.itemCount < 1) binding?.tvInfo?.root?.visibility = View.VISIBLE
            else binding?.tvInfo?.root?.visibility = View.VISIBLE
        }

        itemTransactionsViewModel.getItemTransactions(token).observe(viewLifecycleOwner) {
            itemTransactionsAdapter.submitData(lifecycle, it)
        }
    }

//    private fun setupObservers() {
//        itemPurchasesViewModel.getPurchases(user.token).observe(viewLifecycleOwner) {
//            lifecycleScope.launch {
//                itemTransactionsAdapter.submitData(it)
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
////        fun newInstance(token: String): ItemTransactionsFragment {
////            val fragment = ItemTransactionsFragment()
////            val bundle = Bundle().apply {
////                putString(ARG_TOKEN, token)
////            }
////            fragment.arguments = bundle
////            return fragment
////        }
//    }
//
//fun newInstance(token: String): ItemPurchasesFragment {
//    val fragment = ItemPurchasesFragment()
//    val bundle = Bundle().apply {
//        putString(ItemPurchasesFragment.ARG_TOKEN, token)
//    }
//    fragment.arguments = bundle
//    return fragment
//}