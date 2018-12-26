package com.kobyakov.nixtesttask.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.kobyakov.nixtesttask.dao.ProductDao;
import com.kobyakov.nixtesttask.model.Product;

@Database(entities = {Product.class}, version = 1)

public abstract class AppDatabase extends RoomDatabase {
    public abstract ProductDao productDao();
}
