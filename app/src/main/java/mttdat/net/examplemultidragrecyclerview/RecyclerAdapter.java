package mttdat.net.examplemultidragrecyclerview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by swagsoft on 12/7/16.
 */

/**
 * Recycler adapter with list of T
 * @param:
 *  + Recycler is abstract and RecyclerViewHolder is abstract.
 *  + RecyclerViewHolder is view displaying info of T.
 *  + T is an object to onBind (to give info) into RecyclerViewHolder.
 * */
public abstract class RecyclerAdapter<T> extends RecyclerView.Adapter<RecyclerViewHolder> {

    protected Context context;
    int resource; // Resource for holder.
    protected List<T> listItem; // List of Viewholder, and viewHolder here still an abstract class which needs overriding.

    protected HashMap<Integer, Boolean> listChosen;

    protected RecyclerView recyclerView;    // Recycler view is attached to adapter.
    protected RecyclerView.LayoutManager layoutManager;

    private OnItemClickListener mClickListener;
    private boolean disableClick = false;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    private OnItemClickWithItemListener mClickListenerWithItem;

    public interface OnItemClickWithItemListener<T> {
        void onItemClick(View view, int position, T item);
    }

    private OnItemTouchListener mTouchListener;

    public interface OnItemTouchListener {
        boolean onItemTouch(View view, MotionEvent motionEvent, int position);
    }

    public RecyclerAdapter(int resource, List<T> listItem) {
        this.resource = resource;
        this.listItem = listItem;
    }

    public RecyclerAdapter(Context context, int resource, List<T> listItem) {
        this.context = context;
        this.resource = resource;
        this.listItem = listItem;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        this.recyclerView = recyclerView;
        this.layoutManager = recyclerView.getLayoutManager();
    }

    /**
     * Create view of every item in the list
     */
    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        // If we write like this:
        // View v = inflater.inflate(resource, null); // Old version.
        // It wouldn't get the info, params from parents --> raise some bull shit error.
        // Learn more: https://www.bignerdranch.com/blog/understanding-androids-layoutinflater-inflate/
        View v = inflater.inflate(resource, viewGroup, false);

        return createHolder(v);
    }

    // Depends on how to set out and handle elements in one holder; every recycler
    // adapter has their own ways.
    public abstract RecyclerViewHolder createHolder(View v);

    /**
     * This method's used to set values for holder. However, every holder has their own ways.
     */
    @Override
    public void onBindViewHolder(final RecyclerViewHolder recyclerViewHolder, final int position) {
        final T item = listItem.get(position);

        if (mClickListener != null) {

            recyclerViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

//                    if (!disableClick) {
                        mClickListener.onItemClick(view, position);
//                        disableClick = true;
//
//                        // Enable click again after 250ms.
//                        new Timer().schedule(new TimerTask() {
//                            @Override
//                            public void run() {
//                                disableClick = false;
//                            }
//                        }, 250);
//                    }
                }
            });
        }

        if (mClickListenerWithItem != null) {

            recyclerViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (!disableClick) {
                        mClickListenerWithItem.onItemClick(view, position, item);
                        disableClick = true;

                        // Enable click again after 500ms.
                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                disableClick = false;
                            }
                        }, 500);
                    }
                }
            });
        }

        if (mTouchListener != null) {

            recyclerViewHolder.itemView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    return mTouchListener.onItemTouch(view, motionEvent, position);
                }
            });
        }

        recyclerViewHolder.onBind(item);
    }

    @Override
    public int getItemCount() {
        return listItem == null ? 0 : listItem.size();
    }

    public void swap(List<T> data) {

        listItem.clear();
        listItem.addAll(data);

        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        listItem.remove(position);
        notifyItemRemoved(position);

        /*
            Have to call notifyItemRangeChanged because after remove one item (suppose at 2),
            other below items (at 3, 4, 5...) will be going upto (2, 3, 4,...). So call this
            method to notify that position in this list was changed.
        */
        notifyItemRangeChanged(position,                       // Position from which other belows change.
                listItem.size() - position);    // How many items below pos have to change.
    }

    public void add_item(T object, int pos){
        listItem.add(pos, object);

        notifyItemInserted(pos);
    }

    public void clear(){
        listItem.clear();
        notifyDataSetChanged();
    }

    public void add_item(T object){
        add_item(object, listItem.size());
    }

    public void setOnItemTouchListener(OnItemTouchListener mTouchListener) {
        this.mTouchListener = mTouchListener;
    }

    public HashMap<Integer, Boolean> getListChosen() {
        return listChosen;
    }

    public void initListChosen(int firstChosenPos) {

        // Do nothing if there is no item in list.
        if (listItem == null) {
            return;
        }

        this.listChosen = new HashMap<>();

        for (int i = 0; i < listItem.size(); i++) {

            if (i == firstChosenPos) {
                listChosen.put(i, true);
            } else {
                listChosen.put(i, false);
            }
        }
    }

    /* Get - set */
    public List<T> getListItem() {
        return listItem;
    }

    public void setListItem(List<T> listItem) {
        this.listItem = listItem;
    }

//    public void setListItemAndReInitChosenList(List<T> listItem, int firstChosenPos) {
//        this.listItem = listItem;
//        initListChosen(firstChosenPos);
//    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mClickListener = listener;
    }

    public void setOnItemClickListenerWithItem(OnItemClickWithItemListener mClickListenerWithItem) {
        this.mClickListenerWithItem = mClickListenerWithItem;
    }

    public Context getContext() {
        return context;
    }
}