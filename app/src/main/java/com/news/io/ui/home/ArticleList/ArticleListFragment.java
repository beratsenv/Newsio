package com.news.io.ui.home.ArticleList;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import com.news.io.R;
import com.news.io.model.Article;
import com.news.io.model.ArticleList;
import com.news.io.model.Resource;
import com.news.io.ui.RecyclerViewAdapter;
import com.news.io.util.QueryUtils;
import com.news.io.viewmodel.ArticleListView;
import com.news.io.viewmodel.ArticleListViewFactory;
import com.news.io.viewmodel.BookmarksView;
import com.news.io.viewmodel.BookmarksViewModel;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.news.io.NewsioApplication.getAppContext;

public class ArticleListFragment extends Fragment {
    private static final String ARG_SECTION = "section";
    private static final String TAG = ArticleListFragment.class.getName();

    @BindView(R.id.emptyView_text)
    TextView emptyViewTextView;
    @BindView(R.id.emptyView_button)
    Button emptyViewButton;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private String mCategory;
    private RecyclerViewAdapter mAdapter;
    private ArticleListView mArticleListViewModel;
    private BookmarksView mBookmarksViewModel;

    public ArticleListFragment() {
    }

    public static ArticleListFragment newInstance(String section) {
        ArticleListFragment fragment = new ArticleListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SECTION, section);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCategory = getArguments().getString(ARG_SECTION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.article_list, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.configureRecyclerView(view.getContext());
        this.configureViewModel();

        refreshLayout.setOnRefreshListener(this::refreshData);
        emptyViewButton.setOnClickListener(v -> refreshData());
    }

    @Override
    public void onResume() {
        super.onResume();
        // TODO: 03-05-2019 not idle solution to update preference changes
        if (mArticleListViewModel != null)
            refreshData();
    }

    private void configureRecyclerView(Context context) {
        mAdapter = new RecyclerViewAdapter(context, null, ArticleListFragment.class.getSimpleName());
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        DividerItemDecoration divider =
                new DividerItemDecoration(context, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(divider);
    }

    private void configureViewModel() {
        ArticleListViewFactory articleListViewModelFactory =
                new ArticleListViewFactory(getAppContext());
        mArticleListViewModel = ViewModelProviders.of(this, articleListViewModelFactory)
                .get(ArticleListView.class);
        BookmarksViewModel bookmarkViewModelFactory =
                new BookmarksViewModel(getAppContext());
        mBookmarksViewModel = ViewModelProviders.of(this, bookmarkViewModelFactory)
                .get(BookmarksView.class);
        mArticleListViewModel.init(mCategory);
        if (mArticleListViewModel.getArticles().hasObservers())
            mArticleListViewModel.getArticles().removeObservers(this);
        mArticleListViewModel.getArticles().observe(this, this::observe);
    }

    private void refreshData() {
        if (mArticleListViewModel != null) {
            if (QueryUtils.isConnected(requireContext())) {
                if (mArticleListViewModel.refreshArticles().hasObservers())
                    mArticleListViewModel.refreshArticles().removeObservers(this);
                mArticleListViewModel.refreshArticles().observe(this, this::observe);
            } else {
                refreshLayout.setRefreshing(false);
                View view = requireActivity().findViewById(android.R.id.content);
                Snackbar.make(view, getString(R.string.no_internet), Snackbar.LENGTH_SHORT).show();
                if (mAdapter.getItemCount() == 0)
                    toggleEmptyState();
            }
        }
    }

    private void observe(Resource<ArticleList> listResource) {
        ArticleList data = listResource.data;
        switch (listResource.status) {
            case LOADING:
                refreshLayout.setRefreshing(true);
                break;
            case SUCCESS:
                refreshLayout.setRefreshing(false);
                updateList(data);
                break;
            case EMPTY:
                refreshLayout.setRefreshing(false);
                toggleEmptyState();
                break;
            case ERROR:
                refreshLayout.setRefreshing(false);
                String errorMsg = listResource.msg;
                if (errorMsg != null) {
                    Toast.makeText(getContext(), getString(R.string.error), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "configureViewModel: " + errorMsg);
                }
                updateList(data);
                break;
        }
    }

    private void updateList(ArticleList data) {
        if (data != null) {
            List<Article> list = data.getArticles();
            mAdapter.updateAdapter(list, mBookmarksViewModel);
            recyclerView.setVisibility(View.VISIBLE);
            emptyViewButton.setVisibility(View.INVISIBLE);
            emptyViewTextView.setVisibility(View.INVISIBLE);
        } else toggleEmptyState();
    }

    private void toggleEmptyState() {
        recyclerView.setVisibility(View.INVISIBLE);
        emptyViewButton.setVisibility(View.VISIBLE);
        emptyViewTextView.setVisibility(View.VISIBLE);
        emptyViewTextView.setText(getString(R.string.no_news));
    }
}

