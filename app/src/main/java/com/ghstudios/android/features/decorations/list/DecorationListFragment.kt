package com.ghstudios.android.features.decorations.list

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.View

import com.ghstudios.android.ClickListeners.DecorationClickListener
import com.ghstudios.android.RecyclerViewFragment
import com.ghstudios.android.features.armorsetbuilder.detail.ASBPagerActivity
import com.ghstudios.android.features.decorations.detail.DecorationDetailActivity


class DecorationListFragment : RecyclerViewFragment() {
    private val viewModel by lazy {
        ViewModelProviders.of(this).get(DecorationListViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Enable the search filter and dividers
        enableDivider()
        enableFilter {
            viewModel.setFilter(it)
        }

        // Determine if we're arriving from the Armor Set Builder (ASB).
        // If so, we'll also need the number of slots
        val intent = activity!!.intent
        val fromAsb = intent.getBooleanExtra(ASBPagerActivity.EXTRA_FROM_SET_BUILDER, false)
        val maxSlots = when {
            fromAsb -> intent.getIntExtra(ASBPagerActivity.EXTRA_DECORATION_MAX_SLOTS, 3)
            else -> Int.MAX_VALUE
        }

        // Create and set the adapter
        val adapter = DecorationListAdapter(maxSlots) { decoration, view ->
            // if from asb, clicking should resolve asb, otherwise go to decoration
            if (fromAsb) {
                intent?.putExtra(DecorationDetailActivity.EXTRA_DECORATION_ID, decoration.id)
                activity?.setResult(Activity.RESULT_OK, intent)
                activity?.finish()
            } else {
                DecorationClickListener(context, decoration.id).onClick(view)
            }
        }
        setAdapter(adapter)

        // Listen for decoration data. This updates when the filter updates as well
        viewModel.decorationData.observe(this, Observer {
            if (it == null) return@Observer
            adapter.setItems(it)
        })
    }
}
