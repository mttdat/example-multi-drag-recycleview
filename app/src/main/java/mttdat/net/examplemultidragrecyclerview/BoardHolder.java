package mttdat.net.examplemultidragrecyclerview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

public class BoardHolder extends RecyclerViewHolder<Cell> {

    private TextView tvChar;
    private View background;

    private MainActivity mainActivity;
    private Cell cell;

    public BoardHolder(Context context, View itemView, int itemSize) {
        super(itemView);

        this.mainActivity = (MainActivity) context;

        tvChar = itemView.findViewById(R.id.tv_text);
        background = itemView.findViewById(R.id.v_background);

        itemView.getLayoutParams().width = itemSize;
        itemView.getLayoutParams().height = itemSize;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBind(final Cell cell) {

        this.cell = cell;

        tvChar.setTag(getAdapterPosition());

        tvChar.setText(cell.getCharacter());

        if(cell.isChosen()){
            background.setBackgroundResource(R.drawable.bkg_item_board_chosen);
        }else {
            background.setBackgroundResource(R.drawable.bkg_corner_gray);
        }

        if(cell.isDisable() && !cell.isChosen()){
            tvChar.setAlpha(0.5f);
        }else {
            tvChar.setAlpha(1f);
        }
    }

    public void onClick() {
        if (!cell.isDisable()) {
            if (mainActivity == null) {
                return;
            }

            int itemPos = getAdapterPosition();

            if (!cell.isChosen()) {
                mainActivity.onChooseACharFromBoard(itemPos, cell.getCharacter());
            } else {
                mainActivity.sendBackChosenCharToBoard(itemPos);
            }
        }
    }
}
