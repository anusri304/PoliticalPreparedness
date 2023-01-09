package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter

class ElectionsFragment: Fragment() {

    private val navController by lazy { findNavController() }

    private val viewModel: ElectionsViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onViewCreated()"
        }
        // ViewModelProvider(this).get(MainViewModel::class.java)
        ViewModelProvider(this,ElectionsViewModelFactory(activity.application)).get(ElectionsViewModel::class.java)


    }
    //Adapter for all elections from teh API
    val adapter = ElectionListAdapter(ElectionListAdapter.ElectionListener {
        viewModel.displayElection(it)
    })
  //Adapter for saved elections
    val followedELectionAdapter = ElectionListAdapter(ElectionListAdapter.ElectionListener {
        viewModel.displayElection(it)
    })

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding = FragmentElectionBinding.inflate(inflater)

        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        // Bind recycler election view to adapter to display all elections from the API
        binding.recyclerElection.adapter = adapter

        // Bind recycler election view to adapter to display saved elections from the database
        binding.recyclerSavedElection.adapter = followedELectionAdapter


       //Observe saved elections to display in the UI
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

       // setHasOptionsMenu(true)

        return binding.root

   }

}