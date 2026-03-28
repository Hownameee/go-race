package com.grouprace.feature.profile.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.grouprace.core.network.utils.SessionManager;
import com.grouprace.feature.profile.R;

public class ProfileFragment extends Fragment {

    private TextView tvUserId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Nạp layout
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        tvUserId = view.findViewById(R.id.tv_user_id);
//
//        SessionManager sessionManager = new SessionManager(requireContext());
//        String savedToken = sessionManager.getAuthToken();
//
//        // 3. In ra màn hình!
//        tvUserId.setText("Token của bạn: \n" + savedToken);
    }
}