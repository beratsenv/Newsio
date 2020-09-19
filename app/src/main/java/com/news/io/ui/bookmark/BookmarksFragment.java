package com.news.io.ui.bookmark;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

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
import com.news.io.ui.RecyclerViewAdapter;
import com.news.io.viewmodel.BookmarksView;
import com.news.io.viewmodel.BookmarksViewModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.news.io.NewsioApplication.getAppContext;

public class BookmarksFragment extends Fragment {

    @BindView(R.id.emptyView_text)
    TextView emptyViewTextView;
    @BindView(R.id.emptyView_button)
    Button emptyViewButton;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private RecyclerViewAdapter mAdapter;
    private BookmarksView mBookmarksViewModel;

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

        refreshLayout.setEnabled(false);
        emptyViewButton.setEnabled(false);
        emptyViewButton.setVisibility(View.GONE);
    }

    private void configureRecyclerView(Context context) {
        mAdapter = new RecyclerViewAdapter(context, null, BookmarksFragment.class.getSimpleName());
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

    }

    private void configureViewModel() {
        BookmarksViewModel viewModelFactory =
                new BookmarksViewModel(getAppContext());
        mBookmarksViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(BookmarksView.class);
        mBookmarksViewModel.init();
        mBookmarksViewModel.getBookmarks().observe(this, this::updateList);
    }

    private void updateList(List<Article> list) {
        if (list != null && list.size() > 0) {
            mAdapter.updateAdapter(list, mBookmarksViewModel);
            recyclerView.setVisibility(View.VISIBLE);
            emptyViewTextView.setVisibility(View.INVISIBLE);
        } else {
            recyclerView.setVisibility(View.INVISIBLE);
            emptyViewTextView.setVisibility(View.VISIBLE);
            emptyViewTextView.setText(getString(R.string.no_bookmark));
        }
    }
}
