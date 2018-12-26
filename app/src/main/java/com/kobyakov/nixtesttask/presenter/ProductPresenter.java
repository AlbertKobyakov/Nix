package com.kobyakov.nixtesttask.presenter;

import android.os.Environment;
import android.util.Log;

import com.kobyakov.nixtesttask.activity.MainActivity;
import com.kobyakov.nixtesttask.model.Product;
import com.kobyakov.nixtesttask.repository.ProductRepository;

import java.io.File;
import java.util.List;
import java.util.Objects;

public class ProductPresenter {

    private final String TAG = getClass().getSimpleName();

    private MainActivity view;
    private ProductRepository model;

    public ProductPresenter(ProductRepository model) {
        this.model = model;
    }

    public void attachView(MainActivity mainActivity) {
        view = mainActivity;
    }

    public void detachView() {
        view = null;
    }

    public void remove(List<Product> products, String message) {
        model.removeMultipleProducts(products);
        view.displayMaterialSnackBar(message);
    }

    public void addToPurchased(List<Product> products, String message) {
        model.updateProducts(products);
        view.displayMaterialSnackBar(message);
    }

    public void selectAll(List<Product> products, String message) {
        model.updateProducts(products);
        view.displayMaterialSnackBar(message);
    }

    public void deselectAll(List<Product> products, String message) {
        model.updateProducts(products);
        view.displayMaterialSnackBar(message);
    }

    public void update(Product product) {
        model.updateProduct(product);
    }

    public void insertToDB(Product product, String message) {
        model.insertProductToDB(product);
        view.displayMaterialSnackBar(message);
    }

    public void removeFile(String fileName) {
        File storageDir = Objects.requireNonNull(view.getExternalFilesDir(Environment.DIRECTORY_PICTURES));
        File[] files = storageDir.listFiles();
        Log.d(TAG, files.length + "");
        for (File file : files) {
            if (file.getName().contains(fileName)) {
                boolean result = file.delete();
                Log.d(TAG, "File success removed: " + result);
            }
        }
    }
}
