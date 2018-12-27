package com.kobyakov.nixtesttask.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kobyakov.nixtesttask.R;
import com.kobyakov.nixtesttask.RecyclerViewEmptyObserver;
import com.kobyakov.nixtesttask.RecyclerViewTouchListener;
import com.kobyakov.nixtesttask.adapter.ProductAdapter;
import com.kobyakov.nixtesttask.model.Product;
import com.kobyakov.nixtesttask.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static android.view.View.VISIBLE;

public class ProductFragment extends Fragment {
    private static final int LAYOUT = R.layout.content;
    private static final String IS_BOUGHT = "is_bought";
    private static final String KEY_TAG = "tag";
    private static String TAG;

    View view;
    Unbinder unbinder;
    ProductRepository repository;

    FabStateListener fabStateListener;
    DBWorkerListener DBWorkerListener;
    RemoveFileListener removeFileListener;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.btns_block)
    LinearLayout btnsBlock;
    @BindView(R.id.empty_layout)
    LinearLayout emptyLayout;
    @BindView(R.id.text_for_empty_layout)
    TextView textEmptyLayout;
    @BindView(R.id.btn_add_to_purchased)
    ImageButton btnAddToPurchased;

    ProductAdapter mAdapter;
    private boolean isBought;

    public static ProductFragment newInstance(boolean isBought, String tag) {
        ProductFragment productFragment = new ProductFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(IS_BOUGHT, isBought);
        bundle.putString(KEY_TAG, tag);
        productFragment.setArguments(bundle);

        return productFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            isBought = getArguments().getBoolean(IS_BOUGHT);
            TAG = getArguments().getString(KEY_TAG);
        }

        repository = new ProductRepository(isBought);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");

        view = inflater.inflate(LAYOUT, container, false);

        init();

        repository.getProductsLive().observe(this, products -> {
            if (products != null) {
                mAdapter.registerAdapterDataObserver(new RecyclerViewEmptyObserver(recyclerView, emptyLayout, recyclerView));
                mAdapter.setData(products);

                changeVisibilityBtnsBlock(products);
            }
        });

        return view;
    }

    private void changeVisibilityBtnsBlock(List<Product> products) {
        if (products.size() == 0 && btnsBlock.getVisibility() == VISIBLE) {
            btnsBlock.setVisibility(View.GONE);
        }

        for (Product product : products) {
            if (product.isSelected()) {
                btnsBlock.setVisibility(View.VISIBLE);
                return;
            } else {
                btnsBlock.setVisibility(View.GONE);
            }
        }
    }

    private void init() {
        Log.d(TAG, "init");
        unbinder = ButterKnife.bind(this, view);

        if (isBought) {
            btnAddToPurchased.setVisibility(View.GONE);
            textEmptyLayout.setText(R.string.list_purchased_empty);
        } else {
            textEmptyLayout.setText(R.string.shopping_list_empty);
        }

        setHasOptionsMenu(true);
        initRecyclerViewWithAdapter();
    }

    @OnClick(R.id.btn_remove)
    public void remove() {
        List<Product> productListSelected = new ArrayList<>();
        List<String> fileNames = new ArrayList<>();
        for (Product product : mAdapter.getProducts()) {
            if (product.isSelected()) {
                productListSelected.add(product);
                fileNames.add(product.getImagePath());
            }
        }
        DBWorkerListener.remove(productListSelected, getString(R.string.removed, productListSelected.size()));
        removeFileListener.removeFile(fileNames);
    }

    @OnClick(R.id.btn_add_to_purchased)
    public void addToPurchased() {
        List<Product> productListSelected = new ArrayList<>();
        for (Product product : mAdapter.getProducts()) {
            if (product.isSelected()) {
                product.setBought(true);
                product.setSelected(false);
                productListSelected.add(product);
            }
        }
        DBWorkerListener.addToPurchased(productListSelected, getString(R.string.moved_to_purchase, productListSelected.size()));
    }

    @OnClick(R.id.btn_select_all)
    public void selectAll() {
        List<Product> productListSelected = new ArrayList<>();
        for (Product product : mAdapter.getProducts()) {
            if (!product.isSelected()) {
                product.setSelected(true);
                productListSelected.add(product);
            }
        }
        DBWorkerListener.selectAll(productListSelected, getString(R.string.selected, productListSelected.size()));
    }

    @OnClick(R.id.btn_cancel)
    public void deselectAll() {
        List<Product> productListSelected = new ArrayList<>();
        for (Product product : mAdapter.getProducts()) {
            if (product.isSelected()) {
                product.setSelected(false);
                productListSelected.add(product);
            }
        }
        DBWorkerListener.deselectAll(productListSelected, getString(R.string.deselected, productListSelected.size()));
    }

    private void initRecyclerViewWithAdapter() {
        mAdapter = new ProductAdapter(getContext(), Glide.with(this));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.addOnItemTouchListener(new RecyclerViewTouchListener(getContext(), (view, position) -> {
            Product product = mAdapter.getProducts().get(position);
            product.setSelected(!product.isSelected());
            DBWorkerListener.updateProduct(product);
        }));


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_search, menu);
        super.onCreateOptionsMenu(menu, inflater);

        MenuItem item = menu.findItem(R.id.app_bar_search);

        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, "onQueryTextSubmit " + query);
                mAdapter.filter(query);
                showHideFabBasedOnRowSize(query);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(TAG, "onQueryTextChange " + newText);
                mAdapter.filter(newText);
                showHideFabBasedOnRowSize(newText);
                return false;
            }
        });
    }

    private void showHideFabBasedOnRowSize(String text) {
        if (text.trim().length() > 0) {
            fabStateListener.onFabShowHide(false);
        } else {
            fabStateListener.onFabShowHide(true);
        }
    }

    public interface FabStateListener {
        void onFabShowHide(boolean state);
    }

    public void setFabStateListener(Activity activity) {
        fabStateListener = (FabStateListener) activity;
    }

    public interface DBWorkerListener {
        void remove(List<Product> products, String message);

        void addToPurchased(List<Product> products, String message);

        void selectAll(List<Product> products, String message);

        void deselectAll(List<Product> products, String message);

        void updateProduct(Product product);
    }

    public void setDBWorkerListener(Activity activity) {
        DBWorkerListener = (DBWorkerListener) activity;
    }

    public interface RemoveFileListener {
        void removeFile(List<String> fileNames);
    }

    public void setRemoveFileListener(Activity activity) {
        removeFileListener = (RemoveFileListener) activity;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        Log.d(TAG, "onDestroyView");
    }
}