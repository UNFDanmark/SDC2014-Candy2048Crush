package dk.sdc.Candy2048Crush;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.*;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by sdc on 7/15/14.
 */


public class Grid extends GridLayout{
    ArrayList<Tile> tileList = new ArrayList<Tile>();
    int[] tileValues = {2,4,8,16};

    public Grid(Context context) {
        this(context, null);
    }

    public Grid(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Grid(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        //addView(new Tile(this, 1, 1, 2));
    }

    /**
     * Generates a grid of tiles, with width*height amount of tiles.
     * @param width
     * @param height
     */
    public void generateGrid(int width, int height) {
        this.setRowCount(width);
        this.setColumnCount(height);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                generateNewTile(i,j,false);
            }
        }
        for(int i = 0; i < tileList.size(); i++) {
            tileList.get(i).updateTile();
            tileList.get(i).setText("" + tileList.get(i).getValue());
        }
    }

    /**
     * Generates a tile at the specified x and y coordinate. Will remove any previous tile at the x and y before creating the new one. Will return the tile but will also add it to the grids tileList.  Will update the tile and execute combos.
     * @param x
     * @param y
     * @return The newly created tile
     */
    public Tile generateNewTile(int x, int y) {
        return  generateNewTile(x,y,true);
    }

    /**
     * Generates a tile at the specified x and y coordinate. Will remove any previous tile at the x and y before creating the new one. Will return the tile but will also add it to the grids tileList. Will update the tile and execute combos if update is set to true.
     * @param x
     * @param y
     * @param update
     * @return The newly created tile
     */
    public Tile generateNewTile(int x, int y, boolean update) {
        if(getTileAt(x,y).getValue() != -1) {
            removeTileAt(x,y);
        }
        Tile tempTile = new Tile(this,x,y,-1);
        Random rng = new Random();
        tempTile.setValue(tileValues[rng.nextInt(tileValues.length)]);
        tileList.add(tempTile);
        if(update) {
            tileList.get(tileList.size() - 1).updateTile();
        }
        Spec row = GridLayout.spec(y, 1);
        Spec colspan = GridLayout.spec(x, 1);
        GridLayout.LayoutParams gridLayoutParam = new GridLayout.LayoutParams(row,colspan);
        gridLayoutParam.setMargins(5,5,5,5);
        tileList.get(tileList.size()-1).layout(0,0,125,125);
        //Log.w("Creation", tempTile.getParent().toString());
        try {
            addView(tileList.get(tileList.size() - 1), gridLayoutParam);
            final Tile tempT = tileList.get(tileList.size()-1);
            tileList.get(tileList.size()-1).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    tempT.onClick(v);
                }
            });
            //Log.w("Creation", tempTile.getParent().toString());
        }
        catch (Exception e) {

        }
        return tempTile;

        /*Spec row = GridLayout.spec(y, 1);
        Spec colspan = GridLayout.spec(x, 1);
        GridLayout.LayoutParams gridLayoutParam = new GridLayout.LayoutParams(row,colspan);
        final TileTest t = new TileTest(getContext());
        t.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                t.onTileClick(v);
            }
        });
        addView(t, gridLayoutParam);
        return null;*/
    }

    /**
     * Checks if a tile is adjacent to another
     * @param tile1 First tile
     * @param tile2 Second tile
     * @return Returns true if tiles are adjacent
     */
    public boolean isAdjacentTile(Tile tile1, Tile tile2) {
        if((Math.abs(tile1.getxPos()-tile2.getxPos()) == 1 && tile1.getyPos() == tile2.getyPos()) || (Math.abs(tile1.getyPos()-tile2.getyPos()) == 1 && tile1.getxPos() == tile2.getxPos())) {
            return true;
        }
        return false;
    }

    /**
     * Removes a tile at the specified x and y coordinates
     * @param x
     * @param y
     */
    public void removeTileAt(int x, int y) {
        tileList.remove(getTileAt(x,y));
    }

    /**
     * Gets the tile from the grid at the x and y coordinates
     * @param x
     * @param y
     * @return Returns the tile found. If no tile is found will return a tile with a value of -1
     */
    public Tile getTileAt(int x, int y) {
        for(int i = 0; i < tileList.size(); i++) {
            Tile temp = tileList.get(i);
            if(temp.getxPos() == x && temp.getyPos() == y) {
                return tileList.get(i);
            }
        }

        return new Tile(this,-1,-1,-1);
    }

    /**
     * Goes through all tiles and detects selected ones
     * @return a list of selected tiles
     */
    public ArrayList<Tile> getSelectedTiles(){
        ArrayList<Tile> selectedTile = new ArrayList<Tile>();
        for (int i = 0; i< tileList.size(); i++){
            Tile temp = tileList.get(i);
            if (temp.isSelected()) {
                selectedTile.add(tileList.get(i));
            }
        }
        return selectedTile;
    }

    /**
     * Deselects all tiles
     */
    public void deselectAll() {
        for (int i = 0; i<tileList.size(); i++){
            Tile temp = tileList.get(i);
            temp.setSelected(false);
        }
        Toast.makeText(getContext(), "Ready to receive input", Toast.LENGTH_SHORT).show();
    }

    /**
     * Swaps two tiles, and checks if it creates a combo, if not it deselects all and calls isGameOver
     * @param a First tile to be swapped
     * @param b Second tile to be swapped
     * @param restore if the method restores deselects all and checks for game over after re-swapping the tiles
     */
    public void swapTiles(Tile a, Tile b, boolean restore){
        int tempValue = a.getValue();
        a.setValue(b.getValue());
        b.setValue(tempValue);

        a.setText("" + a.getValue());
        b.setText("" + b.getValue());

        if (restore) {
            //TODO: isGameOver
        } else {
            if (!a.isComboAvailable() && !b.isComboAvailable()) {
                Toast.makeText(getContext(), "Error: No combos found, reverting", Toast.LENGTH_SHORT).show();
                swapTiles(a, b, true);
                deselectAll();
            }
            if (a.isComboAvailable()){
                a.executeCombo();
                deselectAll();
            }
            if (b.isComboAvailable()){
                b.executeCombo();
                deselectAll();
            }
        }
    }
}
