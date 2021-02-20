package com.dalakoti07.android.musico.ui.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.dalakoti07.android.musico.R;
import com.dalakoti07.android.musico.databinding.FragmentArtistDetailBinding;
import com.dalakoti07.android.musico.networks.response.AlbumDetailsResponse;
import com.dalakoti07.android.musico.networks.response.ArtistDetailsResponse;
import com.dalakoti07.android.musico.networks.response.GenreDetailsResponse;
import com.dalakoti07.android.musico.ui.activity.MainActivity;
import com.dalakoti07.android.musico.ui.adapters.SimilarArtistAdapter;
import com.dalakoti07.android.musico.utils.Constants;
import com.dalakoti07.android.musico.viewmodels.AlbumDetailsViewModel;
import com.dalakoti07.android.musico.viewmodels.ArtistDetailsViewModel;
import com.dalakoti07.android.musico.viewmodels.ViewModelProviderFactory;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import javax.inject.Inject;

public class ArtistDetailFragment extends Fragment {
    private FragmentArtistDetailBinding binding;
    private NavController navController;
    private SimilarArtistAdapter adapter;

    //todo add a web client inside the app that opens artists link inside app

    Context context;

    @Inject
    ViewModelProviderFactory viewModelFactory;

    private ArtistDetailsViewModel viewModel;

    public ArtistDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        this.context = context;
        super.onAttach(context);
        if (getActivity() != null) {
            ((MainActivity) getActivity()).mainComponent.inject(this);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding=FragmentArtistDetailBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = NavHostFragment.findNavController(this);
        viewModel = ViewModelProviders.of(this,viewModelFactory).get(ArtistDetailsViewModel.class);
        String artistName=getArguments().getString(Constants.artistName);
        viewModel.getArtistDetails(artistName)
                .observe(getViewLifecycleOwner(), new Observer<ArtistDetailsResponse>() {
                    @Override
                    public void onChanged(ArtistDetailsResponse artistDetailsResponse) {
                        fillTheDataInUI(artistDetailsResponse);
                    }
                });
        binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        binding.toolbar.setOnClickListener(v->{navController.navigateUp();});
        binding.toolbar.setTitle(artistName);
        adapter= new SimilarArtistAdapter();
        binding.contentWrapper.rvSimilarArtist.setAdapter(adapter);
    }

    private void fillTheDataInUI(ArtistDetailsResponse artistDetailsResponse) {
        int index = -1;
        // if possible get the high quality image
        switch (artistDetailsResponse.getArtist().getImage().size()) {
            case 6:
            case 5:
            case 4:
                index = 3;break;
            case 3:
                index = 2;break;
            case 2:
                index = 1;break;
            default:
                index = 0;
        }
        Glide.with(binding.ivCover.getContext())
                .load(artistDetailsResponse.getArtist().getImage().get(index).getText()).fitCenter()
                .into(binding.ivCover);
        index=0;
        for(AlbumDetailsResponse.MusicTags tag: artistDetailsResponse.getArtist().getTags().getPublished()){
            Chip chip=new Chip(context);
            chip.setText(tag.getName());
            chip.setTag(tag.getUrl());
            chip.setOnClickListener(this::chipClicked);
            binding.contentWrapper.chipGroup.addView(chip,index);
            index++;
        }
        binding.contentWrapper.tvBioSummary.setText(artistDetailsResponse.getArtist().getBio().summary);
        adapter.addData(artistDetailsResponse.getArtist().similar.similarArtist);
    }

    private void chipClicked(View v) {
        //todo launch web activity
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding=null;
    }
}