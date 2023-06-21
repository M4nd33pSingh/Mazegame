package de.mazegame.model;

import java.util.Arrays;


public class Maze {
    // saves the Maze as a 2D-Array of Node instances.
    private final Node[][] nodes;
    /**
     *
     */
    private final String[][] nodeFields;


    public Maze(int sizeX, int sizeY) {
        // y+1, x+1? which are only added to visualize the right and the bottom maze wall border.
        this.nodes = new Node[sizeY + 1][sizeX + 1];
        this.nodeFields = new String[2 * (sizeY + 1)][sizeX + 1];
        setNodes();
    }

    private void setNodes() {
        // populates the 2D-array Node[][] nodes with object instances of the Node-Class.
        // Also by initializing, the grid walls are "generated" and the maze
        // itself gets built by removing the specific wall to create paths
        for(int y=0; y < getRowCount(); y++)
            for(int x=0; x < getColumnCount(); x++)
                this.nodes[y][x] = new Node(x, y, getColumnCount(), getRowCount());

        // set in each row (y-axis) the north wall of the last node (last column of each row) of to false and
        // setVisited to true to get ignored by the generator algorithm,
        // because they (sizeX + 1) exist only to show the right border of the maze.
        for(int y=0; y < getRowCount(); y++) { // to show the right maze wall border
            nodes[y][getColumnCount() - 1].setNorth(false);
            nodes[y][getColumnCount() - 1].setVisited(true);
        }
        // set west wall in each node (x-axis) of last row to false, in order
        // to show a clear bottom maze wall border.
        for(int x=0; x < getColumnCount(); x++) {
            nodes[getRowCount() - 1][x].setWest(false);
            nodes[getRowCount() - 1][x].setVisited(true);
        }
    }

    public Node[][] getNodes() {
        return nodes;
    }

    public Node getNode(int x, int y) {
        return nodes[y][x];
    }

    public int getRowCount() {
        return nodes.length;
    }

    public int getColumnCount() {
        return nodes[0].length;
    }

    /**
     * @return the nodeFields
     */
    public String[][] getNodeFields() {
        return nodeFields;
    }

    /**
     *
     *
     *    it's merely an 'illusion' that there are node-cells to see, which form a path.
     *    Also each node has only 2 edges (horizontal hasNorth() and vertical hasWest()), which is all is needed to create
     *    the optical illusion of below node-cell/field.
     *                    ┏━━━━┳━━━━┓                  ┏    ┳    ┓          ┏━━━━━━━━━┓                  ┏    ━    ┓
     *  to get the output ┃    ┃    ┃    these node-                        ┃         ┃    these wall-
     *  to look like this ┣━━━━╋━━━━┫ characters need  ┣    ╋    ┫    or    ┃    ╻    ┃ characters need  ┃    ╻    ┃
     *                    ┃    ┃    ┃ to be calculated                      ┃    ┃    ┃ to be calculated
     *                    ┗━━━━┻━━━━┛                  ┗    ┻    ┛	        ┗━━━━┻━━━━┛                  ┗    ┻    ┛
     */
    private int getIndex(int x, int y) {
        String binaryCode = "%d%d%d%d".formatted(
                nodes[y][x].hasNorth() ?   1:0,    // if this  node at y,x    has upper horizontal wall
                nodes[y][x].hasWest()  ?   1:0,    // if this  node at y,x    has left  vertical   wall
                (x-1 >= 0)? nodes[y][x-1].hasNorth() ? 1:0:0,  // if left  node at y,x-1* has upper horizontal wall *the Node previous from current Node
                (y-1 >= 0)? nodes[y-1][x].hasWest()  ? 1:0:0); // if upper node at y-1,x* has left  vertical   wall *the Node    above from current Node

        return Integer.parseInt(binaryCode, 2);  // Converts the binary code to an integer-index to access the specific wall connecting UniCode Char
    }

    /**
     *
     <pre>
     Binary-Code:                    "0000","0001",   "0010",   "0011",   "0100",   "0101",   "0110",   "0111",   "1000",   "1001",   "1010",   "1011",   "1100",   "1101",   "1110",   "1111"
     Binary to Integer index:           0      1         2         3         4         5         6         7         8         9        10        11        12        13        14        15
     String[] nodeWallConnectorChar  = {" ",  "╹",      "╸",      "┛",      "╻",      "┃",      "┓",      "┫",      "╺",      "┗",      "━",      "┻",      "┏",      "┣",      "┳",      "╋"}
     String[] nodeWallConnectorChar  = {" ", "\u2579", "\u2578", "\u251B", "\u257B", "\u2503", "\u2513", "\u252B", "\u257A", "\u2517", "\u2501", "\u253B", "\u250F", "\u2523", "\u2533", "\u254B"}
     </pre>
     */
    public void setNodeFields() {
        String[] wallConnection = {" ", "╹", "╸", "┛", "╻",  "┃", "┓", "┫", "╺", "┗", "━", "┻", "┏", "┣", "┳", "╋"};
        for(int row=0; row < getRowCount(); row++)
            for(int col=0; col < getColumnCount(); col++)  {
                nodeFields[2 * row][col]     = "%s%s".formatted( wallConnection[getIndex(col, row)], // \u2501: ━
                         nodes[row][col].hasNorth()? "\u2501\u2501\u2501" : "   ");

                nodeFields[2 * row + 1][col] = "%s   ".formatted(nodes[row][col].hasWest() ? "\u2503" : " "); // \u2503: ┃
            }
    }

    public void setMarker(int x, int y, String marker) {
        // charAt(0) stores the UniCode-Char for the vertical wall in the vertical row (2 * y + 1),
        // if the Node's (x,y) hasWest was true when setNodeFields() was called, else charAt(0) is " "
        nodeFields[2 * y + 1][x] = nodeFields[2*y+1][x].charAt(0) + marker;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int y = 0; y < 2*getRowCount()-1; y++) {
            for (int x = 0; x < getColumnCount(); x++)
                stringBuilder.append(nodeFields[y][x]);
            stringBuilder.append(System.lineSeparator());
        }
        return stringBuilder.toString();
    }

    /**
     * Builds a human-readable String output of the 2-D Array String[][] nodeFields,
     * which looks like a 2D-Array. After every 2nd row a blank line is inserted
     * to visualize that 2 vertical columns represent one node in the Array nodes.
     * @return a String with line separators to get a 2-D Array representation of the maze.
     */
    public String toStringArray() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[");
        for (int row = 0; row < 2*getRowCount(); row++)
            stringBuilder.append(row>0?" ":"").append(Arrays.toString(nodeFields[row])).append(",").append(System.lineSeparator()).append((row % 2 != 0) ? System.lineSeparator() : "");
        stringBuilder.setCharAt(stringBuilder.length()-2, ']');
        return stringBuilder.toString();
    }
}
