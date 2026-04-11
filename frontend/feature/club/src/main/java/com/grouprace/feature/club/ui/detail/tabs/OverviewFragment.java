package com.grouprace.feature.club.ui.detail.tabs;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.model.Club;
import com.grouprace.feature.club.R;
import com.grouprace.feature.club.ui.ClubDetailViewModel;

public class OverviewFragment extends Fragment {

    private ClubDetailViewModel viewModel;
    private TextView tvName, tvBio, tvLeaderName, tvAdminsNames;
    private Button btnLeave, btnDelete;

    public OverviewFragment() {
        super(R.layout.fragment_club_detail_overview);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireParentFragment()).get(ClubDetailViewModel.class);

        tvName = view.findViewById(R.id.text_club_name);
        tvBio = view.findViewById(R.id.text_bio);
        tvLeaderName = view.findViewById(R.id.text_leader_name);
        tvAdminsNames = view.findViewById(R.id.text_admins_names);
        
        btnLeave = view.findViewById(R.id.button_leave_club);
        btnDelete = view.findViewById(R.id.button_delete_club);

        btnLeave.setOnClickListener(v -> {
            viewModel.leaveClub().observe(getViewLifecycleOwner(), res -> {
                if(res instanceof Result.Success) Toast.makeText(getContext(), "Left club", Toast.LENGTH_SHORT).show();
            });
        });

        btnDelete.setOnClickListener(v -> {
            viewModel.deleteClub().observe(getViewLifecycleOwner(), res -> {
                if(res instanceof Result.Success) Toast.makeText(getContext(), "Club deleted", Toast.LENGTH_SHORT).show();
            });
        });

        viewModel.getClubDetails().observe(getViewLifecycleOwner(), result -> {
            if (result instanceof Result.Success) {
                Club club = ((Result.Success<Club>) result).data;
                tvName.setText(club.getName());
                tvBio.setText(club.getDescription());
                tvLeaderName.setText(club.getLeaderName());
                
//                if (club.getAdmins() != null && !club.getAdmins().isEmpty()) {
//                    tvAdminsNames.setText(TextUtils.join(", ", club.getAdmins()));
//                } else {
//                    tvAdminsNames.setText("None");
//                }
                
                // MOCK logic: Show delete if somehow you are the leader (mocking "user1" check)
                if ("user1".equals(club.getLeaderId())) {
                    btnDelete.setVisibility(View.VISIBLE);
                } else {
                    btnDelete.setVisibility(View.GONE);
                }
            }
        });
    }
}
