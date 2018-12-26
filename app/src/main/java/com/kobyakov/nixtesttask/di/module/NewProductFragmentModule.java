package com.kobyakov.nixtesttask.di.module;

import com.kobyakov.nixtesttask.bottomsheet.AddNewProductFragment;

import dagger.Module;
import dagger.Provides;

@Module
public class NewProductFragmentModule {
    @Provides
    AddNewProductFragment provideAddNewProductFragment() {
        return new AddNewProductFragment();
    }
}
