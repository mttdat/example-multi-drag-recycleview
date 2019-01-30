package mttdat.net.examplemultidragrecyclerview;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.Calendar;

abstract public class OnItemTouchMultiDragListener implements RecyclerView.OnItemTouchListener {
    private static final int MAX_CLICK_DURATION = 200;

    private boolean isIn;   // Is touching still at the same item.
    private String tagTouchableView;    // View catches touch event in each item of recycler view.

    private int countIn;    // How many times touch event in item.
    private long timeDown;  // Time touch down on touchable view.
    protected int startPos; // Position of item to start touching.
    protected int lastPos;  // Position of item last touching in.
    protected int endPos;   // Position of item to end touching (touch up).

    private boolean isTouchDownAtTouchableView; // If touch down event is in a touchable view.

    public OnItemTouchMultiDragListener(String tagTouchableView){
        this.tagTouchableView = tagTouchableView;
    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {

        if(motionEvent.getPointerCount() > 1){

            // Touch with many fingers, don't handle.
            return false;
        }

        int action = motionEvent.getAction();
        if(action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP || action == MotionEvent.ACTION_CANCEL){

            View v = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());
            RecyclerView.ViewHolder holder = null;
            endPos = -1;

            if(v != null) {

                View vTouchable = v.findViewWithTag(tagTouchableView);

                // If up in that item too, since we only in one item.
                if (vTouchable != null && isInView(motionEvent, vTouchable)) {

                    holder = recyclerView.getChildViewHolder(v);
                    endPos = holder.getAdapterPosition();
                }
            }

            // If touch down/ move only in one item.
            if(countIn == 1 && isTouchDownAtTouchableView){
                if(holder != null && endPos != -1) {
                    if (isPossiblyClick()) {
                        onClickUp(endPos, holder);
                    } else {
                        onLongClickUp(endPos, holder);
                    }
                }
            }else if (countIn > 1){
                onDragMultiUp(endPos, lastPos, holder);
            }else {
                onUp();
            }

            // Reset touch status.
            isIn = false;
            isTouchDownAtTouchableView = false;
            countIn = 0;

            return false;
        }

        View v = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());

        if(v != null) {

            View vTouchable = v.findViewWithTag(tagTouchableView);

            if(vTouchable != null && isInView(motionEvent, vTouchable)) {

                if(isIn){
                    return false;
                }

                timeDown = Calendar.getInstance().getTimeInMillis();

                isIn = true;

                RecyclerView.ViewHolder holder = recyclerView.getChildViewHolder(v);
                int pos = holder.getAdapterPosition();

                if(action == MotionEvent.ACTION_DOWN){
                    onDownTouchableView(pos);
                    isTouchDownAtTouchableView = true;
                }else if(isTouchDownAtTouchableView && action == MotionEvent.ACTION_MOVE){
                    onMoveTouchableView(pos);
                }

                onInItemAndInTouchableView(motionEvent, pos);

                if(countIn == 0){
                    startPos = pos;
                }

                lastPos = pos;
                countIn ++;
            }else {
                isIn = false;
                onInItemButNotInTouchable();
            }
        }else {
            isIn = false;
            onOutItem();
        }

        return false;
    }

    @Override
    public void onTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) { }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean b) { }

    private boolean isPossiblyClick() {
        long clickDuration = Calendar.getInstance().getTimeInMillis() - timeDown;
        return clickDuration < MAX_CLICK_DURATION;
    }

    private boolean isInView(MotionEvent ev, View... views) {
        Rect rect = new Rect();
        for (View v : views) {
            v.getGlobalVisibleRect(rect);
            Log.d("","");

            if (rect.contains((int) ev.getRawX(), (int) ev.getRawY()))
                return true;
        }
        return false;
    }

    public void onInItemAndInTouchableView(MotionEvent motionEvent, int pos){}
    abstract public void onDownTouchableView(int pos);
    abstract public void onMoveTouchableView(int pos);
    public void onUp(){}
    public void onClickUp(int pos, RecyclerView.ViewHolder holder){}
    public void onLongClickUp(int pos, RecyclerView.ViewHolder holder){}
    public void onDragMultiUp(int endPos, int lastPos, RecyclerView.ViewHolder holder){}
    public void onInItemButNotInTouchable(){}
    public void onOutItem(){}

    public int getStartPos() {
        return startPos;
    }

    public int getEndPos() {
        return endPos;
    }
}
