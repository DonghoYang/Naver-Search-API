package com.success.dongho.naverlabsearch.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.success.dongho.naverlabsearch.R;
import com.success.dongho.naverlabsearch.data.SearchResult;
import com.success.dongho.naverlabsearch.data.SearchResult.SearchImageResult.Item;
import com.success.dongho.naverlabsearch.util.BuildMode;

import java.util.ArrayList;

public class ImagePagerAdapter extends PagerAdapter{
    private static final String TAG = ImagePagerAdapter.class.getSimpleName();
    private static final boolean DEBUG = BuildMode.ENABLE_DEBUG;

    private ArrayList<Item>  mItems = new ArrayList<Item>();
    private LayoutInflater mLayoutInflater;
    private Context mContext;

    public ImagePagerAdapter(Context context) {
        mLayoutInflater = LayoutInflater.from(context);
        mContext = context;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        if (DEBUG) {
            Log.d(TAG, "instantiateItem() position=" + position);
        }
        View view = mLayoutInflater.inflate(R.layout.item_detail_image, null);

        ImageView image = view.findViewById(R.id.image);

        String imgLink = getItem(position);
        if (!TextUtils.isEmpty(imgLink)) {
            Glide.with(mContext).load(imgLink)
                    .error(R.drawable.ic_launcher_foreground)
                    .into(image);
        }
        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }

    public void updateItem(SearchResult.SearchImageResult item) {
        if (item == null || item.items == null) {
            if (DEBUG) {
                Log.e(TAG, "updateITem() item is null!");
            }
            return;
        }
        mItems.clear();
        mItems.addAll(item.items);
        notifyDataSetChanged();
    }

    public boolean addItems(SearchResult.SearchImageResult item) {
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

    public String getItem(int position) {
        Item item = mItems.get(position);
        if (item != null) {
            return item.link;
        }
        return null;
    }
}
