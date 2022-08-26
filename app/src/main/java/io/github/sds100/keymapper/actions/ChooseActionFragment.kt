package io.github.sds100.keymapper.actions

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.airbnb.epoxy.EpoxyRecyclerView
import dagger.hilt.android.AndroidEntryPoint
import io.github.sds100.keymapper.databinding.FragmentSimpleRecyclerviewBinding
import io.github.sds100.keymapper.sectionHeader
import io.github.sds100.keymapper.simple
import io.github.sds100.keymapper.simpleGrid
import io.github.sds100.keymapper.util.State
import io.github.sds100.keymapper.util.ui.*
import kotlinx.coroutines.flow.Flow

/**
 * Created by sds100 on 22/07/2021.
 */
@AndroidEntryPoint
class ChooseActionFragment : SimpleRecyclerViewFragment<ListItem>() {

    override var searchStateKey: String? = "choose_action_fragment_search"

//    private val args: ChooseActionFragmentArgs by navArgs()

    private val viewModel by viewModels<ChooseActionViewModel>()

    override val listItems: Flow<State<List<ListItem>>>
        get() = viewModel.listItems

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.setupNavigation(this)
//
//        launchRepeatOnLifecycle(Lifecycle.State.CREATED) {
//            viewModel.returnAction.collectLatest { action ->
//                viewLifecycleScope.launchWhenResumed {
//                    returnResult(EXTRA_ACTION to Json.encodeToString(action))
//                }
//            }
//        }
    }

    override fun subscribeUi(binding: FragmentSimpleRecyclerviewBinding) {
        super.subscribeUi(binding)

        viewModel.showPopups(this, binding)

        binding.epoxyRecyclerView.apply {
            RecyclerViewUtils.applySimpleListItemDecorations(this)

            layoutManager = GridLayoutManager(requireContext(), 1)
            clipToPadding = false
            clipChildren = false
        }
    }

    override fun populateList(
        recyclerView: EpoxyRecyclerView,
        listItems: List<ListItem>
    ) {
        recyclerView.setRecycledViewPool(null)
        RecyclerViewUtils.setSpanCountForSimpleListItemGrid(recyclerView)

        recyclerView.withModels {
            listItems.forEach { listItem ->
                if (listItem is DefaultSimpleListItem) {
                    if (spanCount == 1) {
                        simple {
                            id(listItem.id)
                            model(listItem)

                            onClickListener { _ ->
                                viewModel.onListItemClick(listItem.id)
                            }
                        }
                    } else {
                        simpleGrid {
                            id(listItem.id)
                            model(listItem)

                            onClickListener { _ ->
                                viewModel.onListItemClick(listItem.id)
                            }
                        }
                    }
                }

                if (listItem is SectionHeaderListItem) {
                    sectionHeader {
                        id(listItem.id)
                        header(listItem.text)

                        //headers should always go across the whole recycler view.
                        spanSizeOverride { totalSpanCount, position, itemCount ->
                            totalSpanCount
                        }
                    }
                }
            }
        }
    }

    override fun onSearchQuery(query: String?) {
        super.onSearchQuery(query)

        viewModel.searchQuery.value = query
    }

    override fun getRequestKey(): String {
        return ""
//        return args.requestKey
    }
}