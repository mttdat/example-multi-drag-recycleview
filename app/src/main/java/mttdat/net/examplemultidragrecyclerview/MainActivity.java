package mttdat.net.examplemultidragrecyclerview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private final int NUM_COLUMN = 3;
    private int NUM_ROW;

    RecyclerView rvBoard;

    ArrayList<Cell> cells;
    BoardAdapter boardAdapter;

    String string = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvBoard = findViewById(R.id.rv_board);

        // Init cells.
        cells = new ArrayList<>();
        cells.add(new Cell("几"));
        cells.add(new Cell("个"));
        cells.add(new Cell("儿"));
        cells.add(new Cell("三"));
        cells.add(new Cell("我"));
        cells.add(new Cell("人"));
        cells.add(new Cell("小"));
        cells.add(new Cell("天"));
        cells.add(new Cell("长"));

        NUM_ROW = cells.size() / NUM_COLUMN;

        boardAdapter = new BoardAdapter(
                this, R.layout.item, this.cells,
                (int) (ViewUtils.getScreenWidth(this) * 0.24074074074f));
        rvBoard.setAdapter(boardAdapter);

        rvBoard.addOnItemTouchListener(onItemTouchMultiDragListener);
    }

    private OnItemTouchMultiDragListener onItemTouchMultiDragListener = new OnItemTouchMultiDragListener("touchable") {

        @Override
        public void onDragMultiUp(int endPos, int lastPos, RecyclerView.ViewHolder holder) {

            // After dragging, there is only 1 char chosen.
            if(string.length() == 1){
                int rs = boardAdapter.determineUpdateWhenFinishingMultiDrag(endPos);

                switch (rs){
                    case BoardAdapter.NO_UPDATE:
                        break;
                    case BoardAdapter.REMOVE_LAST:
                        sendBackChosenCharToBoard(boardAdapter.getLatestChosen());
                        break;
                }

                Toast.makeText(MainActivity.this, string, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onDownTouchableView(int pos) {
            boardAdapter.performClickHolder(pos);
        }

        @Override
        public void onMoveTouchableView(int pos) {
            int rs = boardAdapter.determineUpdateWhenBeingInPos(pos);

            switch (rs){
                case BoardAdapter.NO_UPDATE:
                    break;

                case BoardAdapter.ADD:
                    onChooseACharFromBoard(pos, cells.get(pos).getCharacter());
                    break;

                case BoardAdapter.REMOVE_LAST:

                    if(boardAdapter.getLatestChosen() != -1) {
                        sendBackChosenCharToBoard(boardAdapter.getLatestChosen());
                    }

                    break;
            }
        }
    };

    public void onChooseACharFromBoard(int chosenPos, String character){

        string += character;

        // Update board, which cell can be dragged next.
        // Mark that we chose this pos in board.
        boardAdapter.getChosenPositions().addLast(chosenPos);
        cells.get(chosenPos).setChosen(true);

        int x = chosenPos % NUM_COLUMN;
        int y = chosenPos / NUM_COLUMN;

        for(int i = 0; i < NUM_ROW; i ++){
            for(int j = 0; j < NUM_COLUMN; j++){

                // Convert i, j (2-coordinate) --> pos (1-coordinate).
                int _pos = i * NUM_COLUMN + j;

                // Unable pos: neither adjacent nor current pos.
                if(_pos != chosenPos){
                    if( Math.abs(i - y) > 1 || Math.abs(j - x) > 1 || boardAdapter.getChosenPositions().contains(_pos)){
                        cells.get(_pos).setDisable(true);
                    }else {
                        cells.get(_pos).setDisable(false);
                    }
                }else {
                    cells.get(_pos).setDisable(false);
                }
            }
        }

        boardAdapter.notifyDataSetChanged();
    }

    public void sendBackChosenCharToBoard(int chosenPos) {

        string = string.substring(0, string.length() - 1);

        // Mark that we un-chose this pos in board.
        boardAdapter.getChosenPositions().removeLast();
        cells.get(chosenPos).setChosen(false);

        /* Get the next to last pos after removing the last pos. */

        // But if this is the last chosen pos.
        if(boardAdapter.getChosenPositions().size() == 0){

            for(int i = 0; i < NUM_ROW * NUM_COLUMN; i ++){
                cells.get(i).setDisable(false);
            }

            boardAdapter.notifyDataSetChanged();
        }else {
            int curPreviousPos = boardAdapter.getChosenPositions().getLast();

            int x = curPreviousPos % NUM_COLUMN;
            int y = curPreviousPos / NUM_COLUMN;

            for(int i = 0; i < NUM_ROW; i ++){
                for(int j = 0; j < NUM_COLUMN; j++){

                    // Convert i, j (2-coordinate) --> pos (1-coordinate).
                    int _pos = i * NUM_COLUMN + j;

                    // Unable pos: neither adjacent nor current pos.
                    if(_pos != curPreviousPos){
                        if( Math.abs(i - y) > 1 || Math.abs(j - x) > 1 || boardAdapter.getChosenPositions().contains(_pos)){
                            cells.get(_pos).setDisable(true);
                        }else {
                            cells.get(_pos).setDisable(false);
                        }
                    }else {
                        cells.get(_pos).setDisable(false);
                    }
                }
            }

            boardAdapter.notifyDataSetChanged();
        }
    }

}
