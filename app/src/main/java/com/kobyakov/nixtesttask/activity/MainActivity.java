package com.kobyakov.nixtesttask.activity;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.kobyakov.nixtesttask.App;
import com.kobyakov.nixtesttask.R;
import com.kobyakov.nixtesttask.bottomsheet.HomeNavigationFragment;
import com.kobyakov.nixtesttask.bottomsheet.AddNewProductFragment;
import com.kobyakov.nixtesttask.fragments.ProductFragment;
import com.kobyakov.nixtesttask.model.Product;
import com.kobyakov.nixtesttask.presenter.ProductPresenter;
import com.kobyakov.nixtesttask.repository.ProductRepository;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements HomeNavigationFragment.MenuItemClickListener,
        ProductFragment.FabStateListener,
        ProductFragment.DBWorkerListener,
        AddNewProductFragment.InsertListener {

    private final String TAG = getClass().getSimpleName();
    private final static String TAG_BUY_FRAGMENT = "BuyProducts";
    private final static String TAG_PURCHASE_FRAGMENT = "PurchasedProducts";
    private final static String TAG_HOME_BOTTOM_SHEET_FRAGMENT = "homeNavigationFragment";
    private final static String TAG_ADD_NEW_PRODUCT_BOTTOM_SHEET_FRAGMENT = "addNewProductFragment";
    private static final int LAYOUT = R.layout.activity_main;

    @BindView(R.id.bottom_app_bar)
    Toolbar toolbar;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.coordinatorLayout2)
    CoordinatorLayout coordinatorLayout2;

    ProductFragment fragmentBuyProducts;
    ProductFragment fragmentPurchasedProducts;

    @Inject
    AddNewProductFragment addNewProductFragment;
    @Inject
    HomeNavigationFragment homeNavigationFragment;
    @Inject
    ProductPresenter presenter;
    @Inject
    ProductRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);
        App.getComponent().injectsMainActivity(this);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        fragmentBuyProducts = ProductFragment.newInstance(false, TAG_BUY_FRAGMENT);
        fragmentPurchasedProducts = ProductFragment.newInstance(true, TAG_PURCHASE_FRAGMENT);

        if (savedInstanceState == null) {
            onMenuItemClick(R.id.buy_products);
        }
        presenter.attachView(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            homeNavigationFragment.show(getSupportFragmentManager(), TAG_HOME_BOTTOM_SHEET_FRAGMENT);
        }

        return true;
    }

    public void changeFragment(Fragment fragment, String tagFragmentName) {
        Log.d(TAG, "changeFragment");
        FragmentManager mFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();

        Fragment curFrag = mFragmentManager.getPrimaryNavigationFragment();
        if (curFrag != null) {
            fragmentTransaction.detach(curFrag);
        }

        Fragment fragmentTemp = mFragmentManager.findFragmentByTag(tagFragmentName);
        if (fragmentTemp == null) {
            fragmentTemp = fragment;
            fragmentTransaction.add(R.id.content, fragmentTemp, tagFragmentName);
        } else {
            fragmentTransaction.attach(fragmentTemp);
        }

        fragmentTransaction.setPrimaryNavigationFragment(fragmentTemp);
        fragmentTransaction.setReorderingAllowed(true);
        fragmentTransaction.commitAllowingStateLoss();
    }

    @OnClick(R.id.fab)
    public void fabClick() {
        addNewProductFragment.show(getSupportFragmentManager(), TAG_ADD_NEW_PRODUCT_BOTTOM_SHEET_FRAGMENT);
    }

    public void displayMaterialSnackBar(String message) {
        int marginSide = 0;
        int marginBottom = 100;
        Snackbar snackbar = Snackbar.make(coordinatorLayout2, message, Snackbar.LENGTH_SHORT);

        View snackbarView = snackbar.getView();
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) snackbarView.getLayoutParams();

        params.setMargins(
                params.leftMargin + marginSide,
                params.topMargin,
                params.rightMargin + marginSide,
                params.bottomMargin + marginBottom
        );

        snackbarView.setLayoutParams(params);
        snackbar.show();
    }

    @Override
    public void onMenuItemClick(int fragmentId) {
        switch (fragmentId) {
            case R.id.buy_products:
                changeFragment(fragmentBuyProducts, TAG_BUY_FRAGMENT);
                break;
            case R.id.purchased_products:
                changeFragment(fragmentPurchasedProducts, TAG_PURCHASE_FRAGMENT);
                break;
        }
    }

    @Override
    public void onFabShowHide(boolean state) {
        if (!state) {
            fab.hide();
        } else {
            fab.show();
        }
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        if (fragment instanceof ProductFragment) {
            ProductFragment productFragment = (ProductFragment) fragment;
            productFragment.setFabStateListener(this);
            productFragment.setDBWorkerListener(this);
        }
    }

    @Override
    public void remove(List<Product> products, String message) {
        presenter.remove(products, message);
    }

    @Override
    public void addToPurchased(List<Product> products, String message) {
        presenter.addToPurchased(products, message);
    }

    @Override
    public void selectAll(List<Product> products, String message) {
        presenter.selectAll(products, message);
    }

    @Override
    public void deselectAll(List<Product> products, String message) {
        presenter.deselectAll(products, message);
    }

    @Override
    public void updateProduct(Product product) {
        presenter.update(product);
    }

    @Override
    public void insertToDb(Product product, String message) {
        presenter.insertToDB(product, message);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }
}