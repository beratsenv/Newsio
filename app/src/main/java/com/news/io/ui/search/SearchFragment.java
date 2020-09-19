package com.news.io.ui.search;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.news.io.R;
import com.news.io.model.Article;
import com.news.io.model.ArticleList;
import com.news.io.model.Resource;
import com.news.io.ui.RecyclerViewAdapter;
import com.news.io.ui.home.ArticleList.ArticleListFragment;
import com.news.io.util.QueryUtils;
import com.news.io.viewmodel.ArticleListView;
import com.news.io.viewmodel.ArticleListViewFactory;
import com.news.io.viewmodel.BookmarksView;
import com.news.io.viewmodel.BookmarksViewModel;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.news.io.NewsioApplication.getAppContext;

public class SearchFragment extends Fragment implements SearchView.OnQueryTextListener {

    private static final String TAG = SearchFragment.class.getSimpleName();
    @BindView(R.id.search_view)
    SearchView mSearchView;
    @BindView(R.id.result_list)
    RecyclerView mRecyclerView;
    @BindView(R.id.emptyView_text_search)
    TextView mEmptyViewTextView;


    private RecyclerViewAdapter mAdapter;
    private ArticleListView mArticleListViewModel;
    private BookmarksView mBookmarksViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSearchView.setOnQueryTextListener(this);


        this.configureRecyclerView(requireContext());
        this.configureViewModel();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if (mArticleListViewModel != null) {
            if (QueryUtils.isConnected(requireContext())) {
                mArticleListViewModel.searchQuery(query).removeObservers(this);
                mArticleListViewModel.searchQuery(query).observe(this, this::observe);
            } else {
                View view = requireActivity().findViewById(android.R.id.content);
                Snackbar.make(view, getString(R.string.no_internet), Snackbar.LENGTH_SHORT).show();
                toggleEmptyState();
            }
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    private void configureRecyclerView(Context context) {
        mAdapter = new RecyclerViewAdapter(context, null, ArticleListFragment.class.getSimpleName());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        DividerItemDecoration divider =
                new DividerItemDecoration(context, DividerItemDecoration.VERTICAL);
        mRecyclerView.addItemDecoration(divider);
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
    }

    private void observe(Resource<ArticleList> listResource) {
        ArticleList data = listResource.data;
        switch (listResource.status) {
            case LOADING:
                break;
            case SUCCESS:
                updateList(data);
                break;
            case EMPTY:
                Toast.makeText(getContext(), getString(R.string.no_result), Toast.LENGTH_SHORT).show();
                toggleEmptyState();
                break;
            case ERROR:
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
            mRecyclerView.setVisibility(View.VISIBLE);
            mEmptyViewTextView.setVisibility(View.INVISIBLE);
        } else toggleEmptyState();
    }

    private void toggleEmptyState() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mEmptyViewTextView.setVisibility(View.VISIBLE);
        mEmptyViewTextView.setText(getString(R.string.no_bookmark));
    }

    @Override
    public void onPause() {
        super.onPause();
        mSearchView.setQuery("", false);
        mSearchView.clearFocus();
        mAdapter.updateAdapter(new ArrayList<>(), null);
        mArticleListViewModel = null;
        mBookmarksViewModel = null;
    }
}