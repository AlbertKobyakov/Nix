package com.kobyakov.nixtesttask.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.kobyakov.nixtesttask.model.Product;

import java.util.List;

import io.reactivex.Single;

@Dao
public interface ProductDao {

    @Query("SELECT * FROM product")
    Single<List<Product>> getAllProduct();

    @Query("SELECT * FROM product WHERE isBought == :isBought")
    LiveData<List<Product>> getAllProductsLive(boolean isBought);

    @Update
    void updateProduct(Product product);

    @Update
    void updateProducts(List<Product> products);

    @Insert
    void insertProduct(Product product);

    @Delete
    void delete(Product product);

    @Delete
    void deleteMultipleProduct(List<Product> products);
}
