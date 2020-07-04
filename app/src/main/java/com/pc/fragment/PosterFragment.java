package com.pc.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mobsandgeeks.saripaar.Validator;
import com.pc.R;
import com.pc.adapter.PosterAdapter;
import com.pc.model.Poster;
import com.pc.retrofit.Connector;

import java.util.List;
import java.util.zip.Inflater;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PosterFragment extends Fragment {

    @BindView(R.id.list_of_posters)
    RecyclerView recyclerView;

    private List<Poster> posters;

    public static PosterFragment newInstance(List<Poster> posters){
        PosterFragment posterFragment = new PosterFragment();
        posterFragment.posters = posters;
        return posterFragment;
    }

    private PosterFragment(){ }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_poster, container, false);
        ButterKnife.bind(this, view);
        PosterAdapter posterAdapter = new PosterAdapter(getActivity(), posters);
        recyclerView.setAdapter(posterAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

}
