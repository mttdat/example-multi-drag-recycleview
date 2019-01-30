package mttdat.net.examplemultidragrecyclerview;

import android.content.Context;
import android.view.View;

import java.util.LinkedList;
import java.util.List;

public class BoardAdapter extends RecyclerAdapter<Cell> {

    public static final int NO_UPDATE = 0;
    public static final int ADD = 1;
    public static final int REMOVE_LAST = 2;
    public static final int TOUCH = 3;

    public static final int ACTION_FIRST_DOWN_TO_ADD = 1;
    public static final int ACTION_FIRST_DOWN_TO_REMOVE = 2;

    private int itemSize;

    private LinkedList<Integer> chosenPositions;    // List of character position in board we chose.

    public BoardAdapter(Context context, int resource, List<Cell> listItem, int itemSize) {
        super(context, resource, listItem);
        this.itemSize = itemSize;

        chosenPositions = new LinkedList<>();
    }

    @Override
    public RecyclerViewHolder createHolder(View v) {
        return new BoardHolder(context, v, itemSize);
    }

    public int determineUpdateWhenFinishingMultiDrag(int pos){
        switch (actionAtFirstDown){
            case ACTION_FIRST_DOWN_TO_ADD:

                // If up out of item.
                if(pos == -1){
                    return REMOVE_LAST;
                }
                break;
            case ACTION_FIRST_DOWN_TO_REMOVE:
                break;
        }

        return NO_UPDATE;
    }

    private int actionAtFirstDown;
    public int determineUpdateWhenBeingInPos(int pos){

        // If visited this pos already.
        if(chosenPositions.contains(pos)){

            switch (actionAtFirstDown){
                case ACTION_FIRST_DOWN_TO_ADD:

                    // If coincidentally it's the previous latest pos.
                    if (isPreviousTheLatestPos(pos)) {
                        return REMOVE_LAST;
                    }

                    break;
                case ACTION_FIRST_DOWN_TO_REMOVE:

                    // If coincidentally it's the latest pos.
                    if (isTheLatestPos(pos)) {
                        return REMOVE_LAST;
                    }

                    break;
            }
        }else {

            if(listItem.get(pos).isDisable()){
                return NO_UPDATE;
            }

            switch (actionAtFirstDown){
                case ACTION_FIRST_DOWN_TO_ADD:

                    // Keep adding if had been adding.
                    return ADD;
                case ACTION_FIRST_DOWN_TO_REMOVE:

                    // Don't do anything more if had been removing.
                    return NO_UPDATE;
            }


        }

        return NO_UPDATE;
    }

    public void performClickHolder(int pos){

        // This is the first time touch down at pos.
        if(!listItem.get(pos).isDisable()){

            // Determine if the first touch is add or remove.
            if(listItem.get(pos).isChosen()){
                actionAtFirstDown = ACTION_FIRST_DOWN_TO_REMOVE;
            }else {
                actionAtFirstDown = ACTION_FIRST_DOWN_TO_ADD;
            }

            ((BoardHolder) recyclerView.findViewHolderForAdapterPosition(pos)).onClick();
        }
    }

    public int getLatestChosen(){
        return chosenPositions == null ? -1 : chosenPositions.getLast();
    }

    private boolean isPreviousTheLatestPos(int pos){

        if(chosenPositions.size() < 2){
            return false;
        }

        return chosenPositions.get(chosenPositions.size() - 2) == pos;
    }

    private boolean isTheLatestPos(int pos){

        if(chosenPositions.size() == 0){
            return false;
        }

        return chosenPositions.get(chosenPositions.size() - 1) == pos;
    }

    public LinkedList<Integer> getChosenPositions() {
        return chosenPositions;
    }
}
