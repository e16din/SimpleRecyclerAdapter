package com.e16din.simplerecycler.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.e16din.simplerecycler.R;

import java.util.ArrayList;
import java.util.List;


public abstract class SimpleRecyclerAdapter<M>
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int TYPE_DEFAULT = 0;

    private final Context mContext;

    private List<Object> mItems;

    private int mItemLayoutId;

    private OnItemClickListener<M> mOnItemClickListener;
    private Runnable onLastItemListener;

    private boolean mRippleEffectEnabled = true;


    public void setItemLayoutId(@LayoutRes int layoutId) {
        mItemLayoutId = layoutId;
    }

    public SimpleRecyclerAdapter(@NonNull Context context, @NonNull List<Object> items, int resId,
                                 OnItemClickListener<M> onItemClickListener) {
        mContext = context;
        mItems = items;
        mItemLayoutId = resId;
        mOnItemClickListener = onItemClickListener;
        onInit();
    }

    public SimpleRecyclerAdapter(@NonNull Context context, @NonNull List<Object> items, int resId) {
        this(context, items, resId, null);
    }

    public SimpleRecyclerAdapter(@NonNull Context context, @NonNull List<Object> items) {
        this(context, items, 0, null);
    }

    public SimpleRecyclerAdapter(@NonNull Context context) {
        this(context, new ArrayList<>());
    }

    protected void onInit() {
        //for override
    }

    protected int calcInsertPosition(int position) {
        return position;
    }

    public void add(int position, Object item) {
        try {
            int insertPosition = calcInsertPosition(position);

            mItems.add(insertPosition, item);

            notifyItemInserted(insertPosition);
        } catch (IllegalStateException e) {
            //todo: update this way
            e.printStackTrace();
        }
    }

    public void add(Object item) {
        add(getItemCount() == 0 ? 0 : getLastPosition(), item);
    }

    public void remove(int position) {
        try {
            mItems.remove(position);

            notifyItemRemoved(position);
        } catch (IllegalStateException e) {
            //todo: update this way
            e.printStackTrace();
        }
    }

    protected void removeLast() {
        remove(getLastPosition());
    }

    protected void removeFirst() {
        remove(0);
    }

    public void addItem(int position, M item) {
        add(position, item);
    }

    public void addItem(M item) {
        add(item);
    }

    public void addAll(int position, List items) {
        try {
            int insertPosition = calcInsertPosition(position);

            mItems.addAll(insertPosition, items);

            notifyItemRangeInserted(insertPosition, items.size());
        } catch (IllegalStateException e) {
            //todo: update this way
            e.printStackTrace();
        }
    }

    public void addAll(List items) {
        addAll(getItemCount() == 0 ? 0 : getLastPosition(), items);
    }

    public void clearAll() {
        try {
            mItems.clear();
            notifyDataSetChanged();
        } catch (IllegalStateException e) {
            //todo: update this way
            e.printStackTrace();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        FrameLayout vContainer = (FrameLayout)
                LayoutInflater.from(getContext()).inflate(R.layout.container, parent, false);
        View v = LayoutInflater.from(parent.getContext()).inflate(mItemLayoutId, parent, false);
        vContainer.addView(v);
        return newViewHolder(vContainer);
    }

    protected abstract RecyclerView.ViewHolder newViewHolder(View v);

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        onBindItemViewHolder(holder, position);
    }

    protected void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewGroup vItem = (ViewGroup) holder.itemView;
        if (mRippleEffectEnabled) {
            vItem = addRippleEffect((ViewGroup) holder.itemView, position);
        }
        updateItemClickListener(position, vItem);
    }

    protected ViewGroup addRippleEffect(ViewGroup vContainer, final int position) {
        Drawable bgDrawable = vContainer.getChildAt(0).getBackground();
        ViewGroup vResult = vContainer;

        if (bgDrawable == null) {
            addRippleToView(vContainer);

        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                vContainer.setBackground(bgDrawable);
            } else {
                vContainer.setBackgroundDrawable(bgDrawable);
            }

            vResult = (ViewGroup) vContainer.getChildAt(0);
        }

        addRippleToView(vResult);
        return vResult;
    }

    protected void updateItemClickListener(final int position, ViewGroup vItem) {
        if (mOnItemClickListener != null) {
            final M item = getItem(position);

            vItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onClick(item, position);
                }
            });
        }
    }

    protected void addRippleToView(ViewGroup vContainer) {
        //update ripple effect (or selector for old androids)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            vContainer.setBackgroundResource(getItemSelectorId());

        } else {
            TypedValue outValue = new TypedValue();
            mContext.getTheme().resolveAttribute(R.attr.selectableItemBackground, outValue, true);
            vContainer.setBackgroundResource(outValue.resourceId);
        }
    }

    @DrawableRes
    protected int getItemSelectorId() {
        return R.drawable.selector_list_item_default;
    }

    @Override
    public int getItemCount() {
        return mItems == null ? 0 : mItems.size();
    }

    public M getItem(int position) {
        return (M) mItems.get(position);
    }

    public void setOnItemClickListener(OnItemClickListener<M> onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    protected Context getContext() {
        return mContext;
    }

    public void onLastItem() {
        if (onLastItemListener != null) {
            onLastItemListener.run();
        }
    }

    public List getItems() {
        return mItems;
    }

    public Runnable getOnLastItemListener() {
        return onLastItemListener;
    }

    public OnItemClickListener<M> getOnItemClickListener() {
        return mOnItemClickListener;
    }

    //use with SimpleRecyclerView
    public void setOnLastItemListener(Runnable onLastItemListener) {
        this.onLastItemListener = onLastItemListener;
    }

    public boolean isRippleEffectEnabled() {
        return mRippleEffectEnabled;
    }

    public void setRippleEffectEnabled(boolean rippleEffectEnabled) {
        mRippleEffectEnabled = rippleEffectEnabled;
    }

    protected int getLastPosition() {
        return getItemCount() - 1;
    }

    public interface OnItemClickListener<M> {
        void onClick(M item, int position);
    }
}