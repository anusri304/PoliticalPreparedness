package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.network.jsonadapter.ElectionAdapter

class ElectionsFragment: Fragment() {

    private val navController by lazy { findNavController() }

    //TODO: Declare ViewModel
    private val viewModel: ElectionsViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onViewCreated()"
        }
        // ViewModelProvider(this).get(MainViewModel::class.java)
        ViewModelProvider(this,ElectionsViewModelFactory(activity.application)).get(ElectionsViewModel::class.java)


    }
    val adapter = ElectionListAdapter(ElectionListAdapter.ElectionListener {
        viewModel.displayElection(it)
    })

    val followedELectionAdapter = ElectionListAdapter(ElectionListAdapter.ElectionListener {
        viewModel.displayElection(it)
    })

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding = FragmentElectionBinding.inflate(inflater)

        binding.lifecycleOwner = this

        binding.viewModel = viewModel
        //TODO: Add ViewModel values and create ViewModel

        binding.recyclerElection.adapter = adapter

        binding.recyclerSavedElection.adapter = followedELectionAdapter

        viewModel.followedElections?.observe(viewLifecycleOwner, Observer {
            followedELectionAdapter.submitList(it)
        })

        viewModel.navigateToSelectedElection.observe(viewLifecycleOwner, Observer {
            if ( null != it ) {
                // Must find the NavController from the Fragment
                    navController.navigate(
                        ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(
                            it
                        )
                    )
                    // Tell the ViewModel we've made the navigate call to prevent multiple navigation
                    viewModel.displayElectionComplete()
            }
        })





        //TODO: Add binding values

        //TODO: Link elections to voter info

        //TODO: Initiate recycler adapters

        //TODO: Populate recycler adapters

       // setHasOptionsMenu(true)

        return binding.root

    }

    //TODO: Refresh adapters when fragment loads

}