package mttdat.net.examplemultidragrecyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by swagsoft on 12/7/16.
 */

/**
 * This is abstract holder. It can be bound by T item.
 * */
public abstract class RecyclerViewHolder<T> extends RecyclerView.ViewHolder{

    public RecyclerViewHolder(View itemView) {
        super(itemView);
    }

    public abstract void onBind(T sample);

    public void special_clear(){}
}
