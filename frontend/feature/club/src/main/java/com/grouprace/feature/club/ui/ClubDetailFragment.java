package com.grouprace.feature.club.ui;

import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.grouprace.core.system.ui.TopAppBarConfig;
import com.grouprace.core.system.ui.TopAppBarHelper;
import com.grouprace.feature.club.R;
import com.grouprace.feature.club.ui.detail.tabs.ActivitiesFragment;
import com.grouprace.feature.club.ui.detail.tabs.EventsFragment;
import com.grouprace.feature.club.ui.detail.tabs.NewsfeedFragment;
import com.grouprace.feature.club.ui.detail.tabs.OverviewFragment;
import com.grouprace.feature.club.ui.detail.tabs.StatisticsFragment;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ClubDetailFragment extends Fragment {

    private ClubDetailViewModel viewModel;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    private static final String[] TAB_TITLES = new String[]{
        "Newsfeed", "Overview", "Activities", "Events", "Statistics"
    };

    public ClubDetailFragment() {
        super(R.layout.fragment_club_detail);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        viewModel = new ViewModelProvider(this).get(ClubDetailViewModel.class);
        
        String clubId = getArguments() != null ? getArguments().getString("CLUB_ID", "club1") : "club1";
        viewModel.setClubId(clubId);

        TopAppBarHelper.setupTopAppBar(view, getTopAppBarConfig());

        tabLayout = view.findViewById(R.id.tab_layout);
        viewPager = view.findViewById(R.id.view_pager);

        viewPager.setAdapter(new ClubDetailPagerAdapter(this));
        
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(TAB_TITLES[position])
        ).attach();
    }

    private TopAppBarConfig getTopAppBarConfig() {
        return new TopAppBarConfig.Builder()
                .setTitle("Club Details")
                .setLeftIcon(com.grouprace.core.system.R.drawable.ic_app)
                .build();
    }

    private static class ClubDetailPagerAdapter extends FragmentStateAdapter {
        public ClubDetailPagerAdapter(@NonNull Fragment fragment) {
            super(fragment);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0: return new NewsfeedFragment();
                case 1: return new OverviewFragment();
                case 2: return new ActivitiesFragment();
                case 3: return new EventsFragment();
                case 4: return new StatisticsFragment();
                default: return new NewsfeedFragment();
            }
        }

        @Override
        public int getItemCount() {
            return TAB_TITLES.length;
        }
    }
}
