package com.example.android.politicalpreparedness.election

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding
import com.example.android.politicalpreparedness.network.ApiStatus
import com.google.android.material.snackbar.Snackbar

class VoterInfoFragment : Fragment() {

    private val viewModel: VoterInfoViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onViewCreated()"
        }
        ViewModelProvider(this, VoterInfoViewModelFactory(activity.application)).get(
            VoterInfoViewModel::class.java
        )

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentVoterInfoBinding.inflate(inflater)

        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        binding.electionName.setTitleTextColor(Color.WHITE);

        val arguments = VoterInfoFragmentArgs.fromBundle(requireArguments())
        viewModel.displayVoterInfo(arguments.selectedElection)

        viewModel.status.observe(viewLifecycleOwner, Observer<ApiStatus> { apiStatus ->
            when(apiStatus) {
                ApiStatus.ERROR -> {
                    Snackbar.make(requireView(), R.string.error_voter_info, Snackbar.LENGTH_LONG).show()
                }
                else -> {}
            }
        })

        return binding.root

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.showSnackBar.observe(viewLifecycleOwner) {
            Snackbar.make(requireView(), it, Snackbar.LENGTH_LONG).show()
        }
    }


}