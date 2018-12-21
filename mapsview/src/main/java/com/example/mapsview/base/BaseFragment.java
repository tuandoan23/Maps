package com.example.mapsview.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseFragment extends Fragment {
    private Unbinder unbinder;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(initLayout(), container, false);
        unbinder = ButterKnife.bind(this,view);
        getExtraData();
        createPresenter();
        createAdapter();
        loadData();
        return view;
    }

    public abstract int initLayout();

    public abstract void getExtraData();

    public abstract void createPresenter();

    public abstract void createAdapter();

    public abstract void loadData();

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
