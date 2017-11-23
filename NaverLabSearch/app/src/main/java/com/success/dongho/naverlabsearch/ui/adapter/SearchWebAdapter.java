package com.success.dongho.naverlabsearch.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.success.dongho.naverlabsearch.R;
import com.success.dongho.naverlabsearch.data.SearchResult;
import com.success.dongho.naverlabsearch.data.SearchResult.SearchWebResult.Item;
import com.success.dongho.naverlabsearch.util.BuildMode;
import com.success.dongho.naverlabsearch.util.ViewUtils;

import java.util.ArrayList;


public class SearchWebAdapter extends RecyclerView.Adapter {
    private static final String TAG = SearchWebAdapter.class.getSimpleName();
    private static final boolean DEBUG = BuildMode.ENABLE_DEBUG;

    public static final int VIEW_TYPE_ITEM = 0;
    public static final int VIEW_TYPE_LOADING = 1;

    private Context mContext;
    private LayoutInflater mLayoutInflater;

    private boolean mIsMoreLoading;
    private ArrayList<Item>  mItems = new ArrayList<Item>();

    private int mVisibleThreshold = 5;

    private OnLoadMoreListener mLoadMoreListener;

    public SearchWebAdapter(RecyclerView recyclerView, Context context) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);

        final LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int totalItemCount = layoutManager.getItemCount();
                int lastVisibleItem = layoutManager.findLastVisibleItemPosition();

                if (totalItemCount == 0) return;

                if (!mIsMoreLoading && totalItemCount <= (lastVisibleItem + mVisibleThreshold)) {
                    if (mLoadMoreListener != null) {
                        mLoadMoreListener.onLoadMore();
                    }
                    mIsMoreLoading = true;
                }
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return mItems.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = mLayoutInflater.inflate(R.layout.item_serch_web, parent, false);
            return new ItemViewHolder(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = mLayoutInflater.inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            final Item item = mItems.get(position);
            if (item == null) {
                return;
            }
            ItemViewHolder itemHolder = (ItemViewHolder) holder;
            ViewUtils.setHtmlText(itemHolder.title, item.title);
            itemHolder.link.setText(item.link);
            ViewUtils.setHtmlText(itemHolder.description, item.description);
            itemHolder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (DEBUG) {
                        Log.d(TAG, "item onClick() link=" + item.link);
                    }
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    Uri u = Uri.parse(item.link);
                    intent.setData(u);
                    mContext.startActivity(intent);
                }
            });
        } else if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void addLoadingView() {
        mItems.add(null);
        notifyItemInserted(mItems.size() - 1);
    }

    public void removeLoadingView() {
        if (mIsMoreLoading) {
            mItems.remove(mItems.size() - 1);
            notifyItemRemoved(mItems.size());
        }
    }

    public void setFinishMoreLoading() {
        mIsMoreLoading = false;
    }

    public boolean isMoreLoading() {
        return mIsMoreLoading;
    }

    public boolean addItems(SearchResult.SearchWebResult item) {
        if (item == null || item.items == null) {
            if (DEBUG) {
                Log.e(TAG, "addItems() item is null!");
            }
            return false;
        }

        mItems.addAll(item.items);
        notifyDataSetChanged();

        return true;
    }

    public void clear() {
        mItems.clear();
        notifyDataSetChanged();
    }

    public void setLoadMoreListener(OnLoadMoreListener listener) {
        mLoadMoreListener = listener;
    }

    static class LoadingViewHolder extends RecyclerView.ViewHolder {
        private ProgressBar progressBar;

        LoadingViewHolder(View view) {
            super(view);
            progressBar = view.findViewById(R.id.progressBar);
        }
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        private TextView title;
        private TextView link;
        private TextView description;

        ItemViewHolder(View view) {
            super(view);
            cardView = view.findViewById(R.id.card);
            title = view.findViewById(R.id.title);
            link = view.findViewById(R.id.link);
            description = view.findViewById(R.id.description);
        }
    }
}
