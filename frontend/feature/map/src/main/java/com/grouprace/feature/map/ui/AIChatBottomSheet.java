package com.grouprace.feature.map.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.grouprace.feature.map.databinding.BottomSheetAiChatBinding;

import java.util.ArrayList;
import java.util.List;
import com.grouprace.core.model.ChatMessage;
import com.grouprace.feature.map.ui.adapter.ChatAdapter;
import com.grouprace.core.common.result.Result;
import com.grouprace.core.model.PlannedRoute;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AIChatBottomSheet extends BottomSheetDialogFragment {
    private BottomSheetAiChatBinding binding;
    private AIChatViewModel viewModel;
    private ChatAdapter adapter;
    
    private double currentLat;
    private double currentLng;
    private String mapboxAccessToken;
    private List<double[]> currentWaypoints;
    
    public interface OnRouteGeneratedListener {
        void onRouteGenerated(AIRoutingResult result);
    }
    
    private OnRouteGeneratedListener listener;

    public static AIChatBottomSheet newInstance(double lat, double lng, String token, List<double[]> waypoints) {
        AIChatBottomSheet fragment = new AIChatBottomSheet();
        Bundle args = new Bundle();
        args.putDouble("lat", lat);
        args.putDouble("lng", lng);
        args.putString("token", token);
        args.putSerializable("waypoints", new ArrayList<>(waypoints));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentLat = getArguments().getDouble("lat");
            currentLng = getArguments().getDouble("lng");
            mapboxAccessToken = getArguments().getString("token");
            currentWaypoints = (List<double[]>) getArguments().getSerializable("waypoints");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = BottomSheetAiChatBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Scope ViewModel to parent fragment to persist history until fragment exit
        viewModel = new ViewModelProvider(requireParentFragment()).get(AIChatViewModel.class);
        
        setupRecyclerView();
        setupListeners();
        observeViewModel();
    }

    private void setupRecyclerView() {
        adapter = new ChatAdapter();
        binding.rvChat.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvChat.setAdapter(adapter);
    }

    private void setupListeners() {
        binding.btnSend.setOnClickListener(v -> {
            String prompt = String.valueOf(binding.etMessage.getText()).trim();
            if (!prompt.isEmpty()) {
                binding.etMessage.setText("");
                viewModel.sendMessage(prompt, currentLat, currentLng, mapboxAccessToken, currentWaypoints);
            }
        });
    }

    private void observeViewModel() {
        viewModel.allMessages.observe(getViewLifecycleOwner(), messages -> {
            if (messages != null) {
                adapter.setMessages(messages);
                binding.rvChat.smoothScrollToPosition(adapter.getItemCount() - 1);
            }
        });

        viewModel.chatResult.observe(getViewLifecycleOwner(), result -> {
            if (result instanceof com.grouprace.core.common.result.Result.Success) {
                AIRoutingResult aiResult = ((Result.Success<AIRoutingResult>) result).data;
                if (aiResult != null) {
                    if (listener != null) {
                        listener.onRouteGenerated(aiResult);
                    }
                }
                setLoading(false);
            } else if (result instanceof Result.Error) {
                String errorMsg = ((Result.Error<?>) result).message;
                Toast.makeText(getContext(), errorMsg != null ? errorMsg : "An error occurred", Toast.LENGTH_SHORT).show();
                setLoading(false);
            } else if (result instanceof Result.Loading) {
                setLoading(true);
            }
        });
    }

    private void setLoading(boolean isLoading) {
        binding.btnSend.setEnabled(!isLoading);
        binding.etMessage.setEnabled(!isLoading);
    }

    public void setOnRouteGeneratedListener(OnRouteGeneratedListener listener) {
        this.listener = listener;
    }
}
