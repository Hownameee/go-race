package com.grouprace.gorace.di;

import com.grouprace.core.navigation.AppNavigator;
import com.grouprace.gorace.navigation.AppNavigatorImpl;

import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public abstract class NavigationModule {

    @Binds
    public abstract AppNavigator bindAppNavigator(AppNavigatorImpl impl);
}
