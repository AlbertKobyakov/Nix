package com.kobyakov.nixtesttask.di.module;

import com.kobyakov.nixtesttask.bottomsheet.HomeNavigationFragment;

import dagger.Module;
import dagger.Provides;

@Module
public class HomeNavigationFragmentModule {
    @Provides
    HomeNavigationFragment provideHomeNavigationFragment() {
        return new HomeNavigationFragment();
    }
}
