package com.grouprace.feature.club.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.grouprace.core.system.ui.TopAppBarConfig;
import com.grouprace.core.system.ui.TopAppBarHelper;
import com.grouprace.feature.club.R;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CreateClubFragment extends Fragment {

    private EditText etName;
    private EditText etDesc;
    private RadioGroup rgPrivacy;
    private Button btnSubmit;

    public CreateClubFragment() {
        super(R.layout.fragment_create_club);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TopAppBarHelper.setupTopAppBar(view, getTopAppBarConfig());

        etName = view.findViewById(R.id.create_club_name_input);
        etDesc = view.findViewById(R.id.create_club_desc_input);
        rgPrivacy = view.findViewById(R.id.create_club_privacy_group);
        btnSubmit = view.findViewById(R.id.create_club_submit_button);

        btnSubmit.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String desc = etDesc.getText().toString().trim();
            int selectedId = rgPrivacy.getCheckedRadioButtonId();
            String privacy = (selectedId == R.id.radio_public) ? "public" : "private";

            if (name.isEmpty() || desc.isEmpty()) {
                Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Disable button during network call to prevent duplicate submissions
            btnSubmit.setEnabled(false);
            btnSubmit.setText("Creating...");

            CreateClubViewModel viewModel = new androidx.lifecycle.ViewModelProvider(this).get(CreateClubViewModel.class);
            viewModel.createClub(name, desc, privacy).observe(getViewLifecycleOwner(), result -> {
                if (result instanceof com.grouprace.core.common.result.Result.Success) {
                    Toast.makeText(getContext(), "Club created successfully!", Toast.LENGTH_SHORT).show();
                    if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                        getParentFragmentManager().popBackStack();
                    }
                } else if (result instanceof com.grouprace.core.common.result.Result.Error) {
                    btnSubmit.setEnabled(true);
                    btnSubmit.setText("Create Club");
                    com.grouprace.core.common.result.Result.Error<?> error = (com.grouprace.core.common.result.Result.Error<?>) result;
                    Toast.makeText(getContext(), "Error: " + error.message, Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    private TopAppBarConfig getTopAppBarConfig() {
        return new TopAppBarConfig.Builder()
                .setTitle("Create Club")
                .setLeftIcon(com.grouprace.core.system.R.drawable.ic_back, v -> {
                    if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                        getParentFragmentManager().popBackStack();
                    }
                })
                .build();
    }
}
