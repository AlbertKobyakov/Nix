package com.kobyakov.nixtesttask.di.component;

import com.kobyakov.nixtesttask.activity.MainActivity;
import com.kobyakov.nixtesttask.di.module.HomeNavigationFragmentModule;
import com.kobyakov.nixtesttask.di.module.NewProductFragmentModule;
import com.kobyakov.nixtesttask.di.module.ProductPresenterModule;

import dagger.Component;

@Component(modules = {HomeNavigationFragmentModule.class, NewProductFragmentModule.class, ProductPresenterModule.class})
public interface AppComponent {
    void injectsMainActivity(MainActivity mainActivity);
}
