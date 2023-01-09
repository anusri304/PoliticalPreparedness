package com.example.android.politicalpreparedness.election

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding

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

        return binding.root

    }


}