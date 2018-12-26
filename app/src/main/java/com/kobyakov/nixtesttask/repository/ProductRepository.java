package com.kobyakov.nixtesttask.repository;

import android.arch.lifecycle.LiveData;
import android.util.Log;

import com.kobyakov.nixtesttask.App;
import com.kobyakov.nixtesttask.database.AppDatabase;
import com.kobyakov.nixtesttask.model.Product;

import java.util.List;

import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ProductRepository {
    private AppDatabase db;
    private LiveData<List<Product>> productsLive;
    private final String TAG = getClass().getSimpleName();
    private String result = "success";

    public ProductRepository() {
        db = App.get().getDB();
    }

    public ProductRepository(boolean isBought) {
        db = App.get().getDB();
        productsLive = db.productDao().getAllProductsLive(isBought);
    }

    public LiveData<List<Product>> getProductsLive() {
        return productsLive;
    }

    public void insertProductToDB(Product product) {
        Disposable disposable = Single.fromCallable(() -> {
            db.productDao().insertProduct(product);
            return result;
        }).subscribeOn(Schedulers.io())
                .subscribe(
                        result -> Log.d(TAG, result),
                        Throwable::printStackTrace
                );
    }

    public void updateProduct(Product product) {
        Disposable disposable = Single.fromCallable(() -> {
            db.productDao().updateProduct(product);
            return result;
        }).subscribeOn(Schedulers.io())
                .subscribe(
                        result -> Log.d(TAG, result),
                        Throwable::printStackTrace
                );
    }

    public void updateProducts(List<Product> products) {
        Disposable disposable = Single.fromCallable(() -> {
            db.productDao().updateProducts(products);
            return result;
        }).subscribeOn(Schedulers.io())
                .subscribe(
                        result -> Log.d(TAG, result),
                        Throwable::printStackTrace
                );
    }

    public void removeMultipleProducts(List<Product> products) {
        Disposable disposable = Single.fromCallable(() -> {
            db.productDao().deleteMultipleProduct(products);
            return result;
        }).subscribeOn(Schedulers.io())
                .subscribe(
                        result -> Log.d(TAG, result),
                        Throwable::printStackTrace
                );
    }
}