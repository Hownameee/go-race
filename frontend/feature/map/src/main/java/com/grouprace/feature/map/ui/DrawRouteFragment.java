package com.grouprace.feature.map.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.grouprace.feature.map.R;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class DrawRouteFragment extends Fragment {

    private DrawRouteViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_draw_route, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(DrawRouteViewModel.class);

        observeViewModel();
        setupBackPress();
    }

    private void observeViewModel() {
        viewModel.screenMode.observe(getViewLifecycleOwner(), mode -> {
            switchFragment(mode);
        });
    }

    private void switchFragment(DrawRouteViewModel.ScreenMode mode) {
        Fragment fragment;
        String tag;

        if (mode == DrawRouteViewModel.ScreenMode.BROWSE) {
            fragment = new SavedRoutesFragment();
            tag = "SAVED_ROUTES";
        } else {
            // Both DRAWING and PREVIEW use the map-based editor
            fragment = new RouteEditorFragment();
            tag = "ROUTE_EDITOR";
        }

        getChildFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment, tag)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }

    private void setupBackPress() {
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                DrawRouteViewModel.ScreenMode mode = viewModel.screenMode.getValue();
                if (mode == DrawRouteViewModel.ScreenMode.DRAWING) {
                    viewModel.exitDrawingMode();
                } else if (mode == DrawRouteViewModel.ScreenMode.PREVIEW) {
                    viewModel.exitPreview();
                } else {
                    setEnabled(false);
                    requireActivity().onBackPressed();
                }
            }
        });
    }
}
