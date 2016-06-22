package me.williandrade.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.williandrade.R;
import me.williandrade.adapter.UserAdapater;

public class IndexUserFragment extends Fragment {

    private RecyclerView userRecycleView;


    public IndexUserFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_index_user, container, false);

        userRecycleView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1, GridLayoutManager.HORIZONTAL, false);
        userRecycleView.setLayoutManager(gridLayoutManager);

        UserAdapater userAdapter = new UserAdapater("logged", getActivity());
        userRecycleView.setAdapter(userAdapter);


        return rootView;
    }
}
