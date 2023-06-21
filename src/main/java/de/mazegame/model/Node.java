package de.mazegame.model;

import java.util.ArrayList;

public class Node {
    private final int x,y;
    private boolean hasNorth = true, hasWest = true, isVisited = false;
    private final ArrayList<Integer[]> neighbors = new ArrayList<>();

    public Node (int x, int y, int maxX, int maxY) {
        this.x = x;
        this.y = y;
        setNeighbors(maxX-1, maxY-1);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean hasNorth() {
        return hasNorth;
    }

    public void setNorth(boolean hasNorth) {
        this.hasNorth = hasNorth;
    }

    public boolean hasWest() {
        return hasWest;
    }

    public void setWest(boolean hasWest) {
        this.hasWest = hasWest;
    }

    public boolean isVisited() {
        return isVisited;
    }

    public void setVisited(boolean isVisited) {
        this.isVisited = isVisited;
    }

    public boolean hasNeighbors() {
        return neighbors.size() > 0;
    }

    public ArrayList<Integer[]> getNeighbors() {
        return neighbors;
    }

    /**
     * Adds valid neighbors to neighbors ArrayList for this Node. <p>
     *
     * Each neighbor is an Array of 3 Integers: <br> <pre>
     * 		{x, y, direction}, </pre>
     * which gets added to the ArrayList neighbors only after checking whether x (columnIndex) and
     * y (rowIndex) indices are within the maze's boundary. <p>
     * in the neighbor-ArrayList
     * {columnIndex, rowIndex, directionIndex} and this Array is saved in an ArrayList. The Array of
     * Integer contains the x,y coordinates to access the right node in the Maze and a third Integer
     * for the direction relative to this node, so the Generator of the maze knows which
     * wall (hasNorth, hasWest) lies in between.
     *
     * @param columnCount the total amount of columns (x-fields) as an Integer.
     * @param rowCount the total amount of rows (y-fields) as an Integer.
     */
    private void setNeighbors(int columnCount, int rowCount) {
        // 		  right,down,left, up
        int[] modX = {1,   0,  -1,  0};
        int[] modY = {0,   1,   0, -1};
        int columnIndex, rowIndex;
        for(int directionIndex=0; directionIndex < 4; directionIndex++) {
            columnIndex = x + modX[directionIndex];
            rowIndex    = y + modY[directionIndex];
            if (columnIndex >= 0 & columnIndex < columnCount & rowIndex >= 0 & rowIndex < rowCount) {
                Integer[] validNeighbor = {columnIndex, rowIndex, directionIndex};
                this.neighbors.add(validNeighbor);
            }
        }
    }
}
