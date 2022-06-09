/*
 * Copyright © 2021 YUMEMI Inc. All rights reserved.
 */
package jp.co.yumemi.android.code_check

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import coil.load
import jp.co.yumemi.android.code_check.TopActivity.Companion.lastSearchDate
import jp.co.yumemi.android.code_check.databinding.FragmentTwoBinding

class TwoFragment : Fragment(R.layout.fragment_two) {

    private val args: TwoFragmentArgs by navArgs()

    private var fragmentTwoBinding: FragmentTwoBinding? = null
    private val _fragmentTwoBinding get() = fragmentTwoBinding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentTwoBinding = FragmentTwoBinding.inflate(inflater, container, false)
        val view = _fragmentTwoBinding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("検索した日時", lastSearchDate.toString())

        var item = args.item
        itemSet(item)
    }

    fun itemSet(item: item){
        _fragmentTwoBinding.ownerIconView.load(item.ownerIconUrl)
        _fragmentTwoBinding.nameView.text = item.name
        _fragmentTwoBinding.languageView.text = item.language
        _fragmentTwoBinding.starsView.text = "${item.stargazersCount} stars"
        _fragmentTwoBinding.watchersView.text = "${item.watchersCount} watchers"
        _fragmentTwoBinding.forksView.text = "${item.forksCount} forks"
        _fragmentTwoBinding.openIssuesView.text = "${item.openIssuesCount} open issues"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentTwoBinding = null
    }

}
