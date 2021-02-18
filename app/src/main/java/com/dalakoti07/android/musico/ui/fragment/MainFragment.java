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
import androidx.recyclerview.widget.DividerItemDecoration;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dalakoti07.android.musico.R;
import com.dalakoti07.android.musico.data.models.SongGenre;
import com.dalakoti07.android.musico.databinding.FragmentMainBinding;
import com.dalakoti07.android.musico.di.qualifier.ActivityContext;
import com.dalakoti07.android.musico.ui.activity.MainActivity;
import com.dalakoti07.android.musico.ui.adapters.GenreAdapter;
import com.dalakoti07.android.musico.utils.ItemDecorationAlbumColumns;
import com.dalakoti07.android.musico.viewmodels.HomeScreenViewModel;
import com.dalakoti07.android.musico.viewmodels.ViewModelProviderFactory;

import java.util.ArrayList;

import javax.inject.Inject;

import timber.log.Timber;


public class MainFragment extends Fragment implements GenreAdapter.genreCardClickListener {
    private FragmentMainBinding mainBinding;
    private NavController navController;
    private GenreAdapter genreAdapter;
    @ActivityContext
    @Inject
    Context context;

    @Inject
    ViewModelProviderFactory viewModelFactory;

    private HomeScreenViewModel viewModel;

    public MainFragment() {
        Timber.d("created main fragment");
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(getActivity()!=null){
            ((MainActivity)getActivity()).mainComponent.inject(this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainBinding=FragmentMainBinding.inflate(inflater,container,false);
        return mainBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController= NavHostFragment.findNavController(this);
        viewModel= ViewModelProviders.of(getActivity(),viewModelFactory).get(HomeScreenViewModel.class);
        genreAdapter= new GenreAdapter(this);
        mainBinding.rvGenres.setAdapter(genreAdapter);
        viewModel.getAllSongGenres().observe(getViewLifecycleOwner(), new Observer<ArrayList<SongGenre>>() {
            @Override
            public void onChanged(ArrayList<SongGenre> songGenres) {
                genreAdapter.addAllData(songGenres);
                genreAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mainBinding=null;
    }

    @Override
    public void cardClicked(SongGenre model) {
        Toast.makeText(context, "clicked", Toast.LENGTH_SHORT).show();
    }
}