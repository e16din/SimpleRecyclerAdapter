package com.e16din.simplerecycler.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import java.util.List;

public abstract class SimpleAdapter<MODEL> extends StrongSimpleAdapter<RecyclerView.ViewHolder, MODEL> {

    //SimpleBaseAdapter - base logic
    //SimpleListAdapter - List interface
    //SimpleClickAdapter - click listeners
    //SimpleAsyncAdapter - async inflating
    //SimpleBindListenerAdapter - bind listeners
    //SimpleRippleAdapter - ripple effect
    //SimpleInsertsAdapter - headers, footers, insertions
    //SimplePagingAdapter - paging logic

    public SimpleAdapter(@NonNull Context context, @NonNull List<MODEL> items) {
        super(context, items);
    }

    public SimpleAdapter(@NonNull Context context) {
        super(context);
    }

}
