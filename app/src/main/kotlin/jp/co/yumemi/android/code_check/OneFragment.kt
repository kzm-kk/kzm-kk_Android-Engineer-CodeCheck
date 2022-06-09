/*
 * Copyright © 2021 YUMEMI Inc. All rights reserved.
 */
package jp.co.yumemi.android.code_check

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.*
import jp.co.yumemi.android.code_check.databinding.FragmentOneBinding

class OneFragment: Fragment(R.layout.fragment_one){

    private var fragmentOneBinding: FragmentOneBinding? = null
    private val _fragmentOneBinding get() = fragmentOneBinding!!

    val oneViewModel by lazy {
        OneViewModel(requireContext())
    }

    /*lateinit var linearLayoutManager: LinearLayoutManager

    val dividerItemDecoration by lazy {
        DividerItemDecoration(requireContext(), linearLayoutManager.orientation)
    }*/

    val customAdapter = CustomAdapter(object : CustomAdapter.OnItemClickListener{
        override fun itemClick(item: item){
            gotoRepositoryFragment(item)
        }
    })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentOneBinding = FragmentOneBinding.inflate(inflater, container, false)
        val view = _fragmentOneBinding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)

        val linearLayoutManager = LinearLayoutManager(requireContext())
        val dividerItemDecoration =
            DividerItemDecoration(requireContext(), linearLayoutManager.orientation)

        _fragmentOneBinding.searchInputText.setOnEditorActionListener(event)



        _fragmentOneBinding.recyclerView.also{
            it.layoutManager = linearLayoutManager
            it.addItemDecoration(dividerItemDecoration)
            it.adapter = customAdapter
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentOneBinding = null
    }

    var event:TextView.OnEditorActionListener = TextView.OnEditorActionListener{ editText, action, _ ->
        if (action == EditorInfo.IME_ACTION_SEARCH){
            editText.text.toString().let {
                oneViewModel.searchResults(it).apply{
                    customAdapter.submitList(this)
                }
            }
            return@OnEditorActionListener true
        }
        return@OnEditorActionListener false
    }

    fun gotoRepositoryFragment(_item: item){
        val action = OneFragmentDirections
            .actionRepositoriesFragmentToRepositoryFragment(item = _item)
        findNavController().navigate(action)
    }
}

val diff_util = object: DiffUtil.ItemCallback<item>(){
    override fun areItemsTheSame(oldItem: item, newItem: item): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: item, newItem: item): Boolean {
        return oldItem == newItem
    }

}

class CustomAdapter(
    private val itemClickListener: OnItemClickListener
) : ListAdapter<item, CustomAdapter.ViewHolder>(diff_util){

    class ViewHolder(view: View): RecyclerView.ViewHolder(view)

    interface OnItemClickListener{
    	fun itemClick(item: item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    	val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_item, parent, false)
    	return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    	val item = getItem(position)

        //スマートキャストを使用するために一度valに代入
        val view = holder.itemView.findViewById<View>(R.id.repositoryNameView)
        if(view is TextView)
                view.text = item.name

    	holder.itemView.setOnClickListener{
     		itemClickListener.itemClick(item)
    	}
    }
}
