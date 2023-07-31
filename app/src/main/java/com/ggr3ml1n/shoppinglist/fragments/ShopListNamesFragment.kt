package com.ggr3ml1n.shoppinglist.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ggr3ml1n.shoppinglist.activities.MainApp
import com.ggr3ml1n.shoppinglist.activities.ShopListActivity
import com.ggr3ml1n.shoppinglist.adapters.ShopListNameAdapter
import com.ggr3ml1n.shoppinglist.databinding.FragmentShopListNamesBinding
import com.ggr3ml1n.shoppinglist.dialogs.DeleteDialog
import com.ggr3ml1n.shoppinglist.dialogs.NewListDialog
import com.ggr3ml1n.shoppinglist.entities.ShopListNameItem
import com.ggr3ml1n.shoppinglist.utils.TimeManager
import com.ggr3ml1n.shoppinglist.vm.MainViewModel


class ShopListNamesFragment : BaseFragment() {
    private var _binding: FragmentShopListNamesBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ShopListNameAdapter

    private val mainViewModel: MainViewModel by activityViewModels {
        MainViewModel.MainViewModelFactory((context?.applicationContext as MainApp).database)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShopListNamesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRcView()
        observer()
    }

    override fun onClickNew() {
        NewListDialog.showDialog(
            activity as AppCompatActivity,
            ""
        ) {
            val shopListNameItem = ShopListNameItem(
                null, it, TimeManager.getCurrentTime(), 0, 0, ""
            )
            mainViewModel.insertShopListName(shopListNameItem)
        }
    }

    private fun initRcView() = with(binding) {
        rcShopListName.layoutManager = LinearLayoutManager(activity)
        adapter = ShopListNameAdapter(object : ShopListNameAdapter.Listener {
            override fun deleteItem(id: Int) {
                DeleteDialog.showDialog(context as AppCompatActivity) {
                    mainViewModel.deleteShopList(id)
                }
            }

            override fun onClickItem(shopListName: ShopListNameItem) {
                val intent = Intent(activity, ShopListActivity::class.java).apply {
                    putExtra(ShopListActivity.SHOP_LIST_NAME, shopListName)
                }
                startActivity(intent)
            }

            override fun editItem(shopListName: ShopListNameItem) {
                NewListDialog.showDialog(context as AppCompatActivity, shopListName.name) {
                    mainViewModel.updateShopListName(shopListName.copy(name = it))
                }
            }
        })
        rcShopListName.adapter = adapter
    }

    private fun observer() {
        mainViewModel.allShopListNames.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = ShopListNamesFragment()
    }
}