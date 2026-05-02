package com.grouprace.feature.map.ui;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.grouprace.core.model.UserRoute;
import com.grouprace.core.system.ui.TopAppBarConfig;
import com.grouprace.core.system.ui.TopAppBarHelper;
import com.grouprace.feature.map.R;
import com.grouprace.feature.map.ui.adapter.SavedRouteAdapter;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SavedRoutesFragment extends Fragment implements SavedRouteAdapter.OnRouteClickListener {

    private DrawRouteViewModel viewModel;
    private SavedRouteAdapter adapter;
    private RecyclerView rvSavedRoutes;
    private View tvEmpty;
    private View btnCreateRoute;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_saved_routes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireParentFragment()).get(DrawRouteViewModel.class);

        bindViews(view);
        setupTopBar(view);
        setupRecyclerView();
        observeViewModel();
        setupListeners();
    }

    private void bindViews(View view) {
        rvSavedRoutes = view.findViewById(R.id.rv_saved_routes);
        tvEmpty = view.findViewById(R.id.tv_empty);
        btnCreateRoute = view.findViewById(R.id.btn_create_route);
    }

    private void setupTopBar(View view) {
        TopAppBarConfig config = new TopAppBarConfig.Builder()
                .setTitle(getString(R.string.title_my_routes))
                .setLeftIcon(com.grouprace.core.system.R.drawable.ic_app)
                .build();
        TopAppBarHelper.setupTopAppBar(view, config);
    }

    private void setupRecyclerView() {
        adapter = new SavedRouteAdapter(this);
        rvSavedRoutes.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvSavedRoutes.setAdapter(adapter);
    }

    private void observeViewModel() {
        viewModel.savedRoutes.observe(getViewLifecycleOwner(), routes -> {
            adapter.submitList(routes);
            tvEmpty.setVisibility(routes == null || routes.isEmpty() ? View.VISIBLE : View.GONE);
        });

        viewModel.errorMessage.observe(getViewLifecycleOwner(), msg -> {
            if (msg != null) {
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
                viewModel.clearErrorMessage();
            }
        });
    }

    private void setupListeners() {
        btnCreateRoute.setOnClickListener(v -> {
            if (isOnline()) {
                viewModel.enterDrawingMode();
            } else {
                Toast.makeText(requireContext(), R.string.msg_no_internet, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRouteClick(UserRoute route) {
        viewModel.previewRoute(route);
    }

    @Override
    public void onDeleteClick(UserRoute route) {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_delete_route, null);
        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        dialogView.findViewById(R.id.btn_dialog_delete_cancel).setOnClickListener(v -> dialog.dismiss());
        dialogView.findViewById(R.id.btn_dialog_delete_confirm).setOnClickListener(v -> {
            viewModel.deleteRoute(route);
            Toast.makeText(requireContext(), R.string.msg_route_deleted, Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        dialog.show();
    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }
}
