package mttdat.net.examplemultidragrecyclerview;

public class Cell {
    private String character;
    private boolean disable;
    private boolean chosen;

    public Cell(String character) {
        this.character = character;
//        this.disable = false;
    }

    public String getCharacter() {
        return character;
    }

    public void setCharacter(String character) {
        this.character = character;
    }

    public boolean isDisable() {
        return disable;
    }

    public void setDisable(boolean disable) {
        this.disable = disable;
    }

    public boolean isChosen() {
        return chosen;
    }

    public void setChosen(boolean chosen) {
        this.chosen = chosen;
    }
}
