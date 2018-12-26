package com.kobyakov.nixtesttask.di.module;

import com.kobyakov.nixtesttask.presenter.ProductPresenter;
import com.kobyakov.nixtesttask.repository.ProductRepository;

import dagger.Module;
import dagger.Provides;

@Module
public class ProductPresenterModule {

    @Provides
    ProductRepository provideProductRepository() {
        return new ProductRepository();
    }

    @Provides
    ProductPresenter provideProductPresenter(ProductRepository repository) {
        return new ProductPresenter(repository);
    }
}
