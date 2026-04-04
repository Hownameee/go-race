package com.grouprace.feature.posts.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.grouprace.feature.posts.R;

public class ShareActivityFragment extends BottomSheetDialogFragment {

    private static final String ARG_TITLE = "title";
    private static final String ARG_DISTANCE = "distance";
    private static final String ARG_PACE = "pace";
    private static final String ARG_DURATION = "duration";
    private static final String ARG_USERNAME = "username";

    public static ShareActivityFragment newInstance(String title, String distance, String pace, String duration, String username) {
        ShareActivityFragment fragment = new ShareActivityFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_DISTANCE, distance);
        args.putString(ARG_PACE, pace);
        args.putString(ARG_DURATION, duration);
        args.putString(ARG_USERNAME, username);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_share_activity, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayout cardBasic = view.findViewById(R.id.card_template_basic);
        LinearLayout cardVisual = view.findViewById(R.id.card_template_visual);

        cardBasic.setOnClickListener(v -> shareBasicTemplate());

        cardVisual.setOnClickListener(v ->
            Toast.makeText(getContext(), "Visual template coming soon!", Toast.LENGTH_SHORT).show()
        );
    }

    private void shareBasicTemplate() {
        Bundle args = getArguments();
        if (args == null) return;

        String title = args.getString(ARG_TITLE, "Activity");
        String distance = args.getString(ARG_DISTANCE, "--");
        String pace = args.getString(ARG_PACE, "--");
        String duration = args.getString(ARG_DURATION, "--");
        String username = args.getString(ARG_USERNAME, "");

        String shareText = "🏃 " + title + "\n\n"
                + "📏 Distance: " + distance + "\n"
                + "⚡ Pace: " + pace + "\n"
                + "⏱ Time: " + duration + "\n\n"
                + "— " + username + " on GoRace";

        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, "Share your activity");
        startActivity(shareIntent);
        dismiss();
    }
}
