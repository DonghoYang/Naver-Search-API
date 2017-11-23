package com.success.dongho.naverlabsearch.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.success.dongho.naverlabsearch.R;
import com.success.dongho.naverlabsearch.data.SearchRequestParam;
import com.success.dongho.naverlabsearch.data.SearchResult;
import com.success.dongho.naverlabsearch.data.SearchResult.SearchImageResult.Item;
import com.success.dongho.naverlabsearch.ui.layout.ImageDetailActivity;
import com.success.dongho.naverlabsearch.util.BuildMode;
import com.success.dongho.naverlabsearch.util.ViewUtils;

import java.util.ArrayList;


public class SearchImageAdapter extends RecyclerView.Adapter {
    private static final String TAG = SearchImageAdapter.class.getSimpleName();
    private static final boolean DEBUG = BuildMode.ENABLE_DEBUG;

    public static final int VIEW_TYPE_ITEM = 0;
    public static final int VIEW_TYPE_LOADING = 1;

    private Context mContext;
    private LayoutInflater mLayoutInflater;

    private boolean mIsMoreLoading;
    private SearchResult.SearchImageResult mImageData;

    private int mVisibleThreshold = 5;

    private OnLoadMoreListener mLoadMoreListener;

    public SearchImageAdapter(RecyclerView recyclerView, Context context) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);

        mImageData = new SearchResult.SearchImageResult();
        mImageData.items = new ArrayList<Item>();

        final GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();

        mVisibleThreshold = mVisibleThreshold * layoutManager.getSpanCount();

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
        return mImageData.items.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = mLayoutInflater.inflate(R.layout.item_serch_image, parent, false);
            return new ItemViewHolder(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = mLayoutInflater.inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ItemViewHolder) {
            final Item item = mImageData.items.get(position);
            final String queryText = mImageData.query;
            if (item == null) {
                return;
            }
            ItemViewHolder itemHolder = (ItemViewHolder) holder;
            ViewUtils.setHtmlText(itemHolder.title, item.title);
            Glide.with(mContext).load(item.thumbnail)
                    .placeholder(R.drawable.ic_launcher)
                    .error(R.drawable.ic_launcher)
                    .into(itemHolder.image);

            itemHolder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (DEBUG) {
                        Log.d(TAG, "item onClick() link=" + item.link);
                    }
                    mContext.deleteFile(ViewUtils.IMAGE_RESULT_NAME);
                    mImageData.currentPos = position;
                    ViewUtils.jsonStringWriteToFile(mContext, mImageData.toString(), ViewUtils.IMAGE_RESULT_NAME);

                    Intent intent = new Intent(mContext, ImageDetailActivity.class);
                    intent.putExtra(SearchRequestParam.INDEX, position);
                    intent.putExtra(SearchRequestParam.FILE_NAME, ViewUtils.IMAGE_RESULT_NAME);
                    intent.putExtra(SearchRequestParam.QUERY, queryText);
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
        return mImageData.items.size();
    }

    public void addLoadingView() {
        mImageData.items.add(null);
        notifyItemInserted(mImageData.items.size() - 1);
    }

    public void removeLoadingView() {
        if (mIsMoreLoading) {
            mImageData.items.remove(mImageData.items.size() - 1);
            notifyItemRemoved(mImageData.items.size());
        }
    }

    public void setFinishMoreLoading() {
        mIsMoreLoading = false;
    }

    public boolean isMoreLoading() {
        return mIsMoreLoading;
    }

    public boolean addItems(SearchResult.SearchImageResult item) {
        if (item == null || item.items == null) {
            if (DEBUG) {
                Log.e(TAG, "addItems() item is null!");
            }
            return false;
        }

        mImageData.start = item.start;
        mImageData.display = item.display;
        mImageData.total = item.total;
        mImageData.query = item.query;
        mImageData.items.addAll(item.items);
        notifyDataSetChanged();

        return true;
    }

    public void clear() {
        mImageData.items.clear();
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
        private ImageView image;
        private TextView title;

        ItemViewHolder(View view) {
            super(view);
            cardView = view.findViewById(R.id.card);
            image = view.findViewById(R.id.image);
            title = view.findViewById(R.id.title);
        }
    }
}
