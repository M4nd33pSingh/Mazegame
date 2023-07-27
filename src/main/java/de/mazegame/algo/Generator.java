package de.mazegame.algo;

import de.mazegame.model.Maze;
import de.mazegame.model.Node;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom; 
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class Generator {
    private final Maze maze;
    private final int startX, startY;
    private final Stack<Node> stack = new Stack<>();
    
    /**
     * The spanning3 is a HashMap, which stores a Node as the key and an ArrayList as the value of the key. 
     * Only valid Nodes-paths are stored in the ArrayList.                                                    <p>
     * The spanning3 is needed for finding the solution-path of the maze and for the maze-game, where it's 
     * used to validate the intended player movement direction by checking, if destination-node exists in
     * the key-node's value-ArrayList.  
     */
    private final Map<Node, ArrayList<Node>> spanning3 = new HashMap<>();

    public Generator(Maze maze) {
        this.maze = maze;
        this.startX = ThreadLocalRandom.current().nextInt(0, maze.getColumnCount() - 1);
        this.startY = ThreadLocalRandom.current().nextInt(0,    maze.getRowCount() - 1);
        generateMaze();
    }

    public int getStartX() {
        return startX;
    }

    public int getStartY() {
        return startY;
    }

    /**
     * Returns a newly created copy of the spanning3 and not just a reference (shallow/deep copy) to the _spanning3.                <p>
     *
     * Creates a new dictionary-object copyOfSpanning3 and copys every object in the spanning3-HashMap by allocating 
     * memory. There are no references to the object of the original spanning3 stored in the copyOfSpanning3.                       <br>
     *
     * @return A copy of the spanning3-HashMap, which was created seperately and then populated by the objects in 
     * the original spanning3, without referencing to any objects in the original spanning3.
     */
    public Map<Node, ArrayList<Node>> getSpanning3() {
        Map<Node, ArrayList<Node>> copyOfSpanning3 = new HashMap<>();
        for (Node node:spanning3.keySet()) {
            copyOfSpanning3.put(node, new ArrayList<Node>());
            for (int i=0; i<spanning3.get(node).size(); i++)
                copyOfSpanning3.get(node).add(spanning3.get(node).get(i));
        }
        return copyOfSpanning3;
    }

    /**
     * Generates maze with the iterative approach (uses Stack) of the randomized depth-first search with backtracking algorithm.    <p>
     *
     * While generating the maze the spanning-tree (spanning3) of the maze is also built. The spanning3 is a HashMap, which stores
     * a node as the key and an ArrayList as the value of the key in which only valid path-Nodes are stored. It's needed for 
     * finding the solution-path of the maze and for the maze-game, where it's used to validate the intended player movement 
     * direction by checking, if destination-node exists in the key-node value-ArrayList.                                           <br><p>
     *                                             
     * 1. Choose the initial cell*, mark it as visited and push it to the stack                                                     <br>
     * 2. While the stack is not empty                                                                                              <br>
     * 2.1. Pop a cell from the stack and make it a current cell*                                                                   <br>
     * 2.2. If the current cell* has any neighbors which have not been visited                                                      <br>
     * 2.2.1.  Push the current cell* to the stack                                                                                  <br>
     * 2.2.2.  Choose one of the unvisited neighbors                                                                                <br>
     * 2.2.3.  Remove the wall between the current cell* and the chosen cell**                                                      <br>
     * 2.2.4.  Mark the chosen cell** as visited and push it to the stack                                                           <p>
     * (* currentNode, ** nextNode)                                                                                                 <p>
     *                                                                          
     * Pseudo-code source:                                                                                                          <br>
     * https://en.wikipedia.org/wiki/Maze_generation_algorithm#:~:text=in%20the%20area.-,Iterative%20implementation,-%5Bedit%5D
     */
    private void generateMaze() {
        int x, y, direction, neighborIndex;
        final int right=0, down=1, left=2, up=3;
        Node nextNode, currentNode = maze.getNode(startX, startY);               // 1
        currentNode.setVisited(true);                                            // 1
        stack.push(currentNode);                                                 // 1
        spanning3.put(currentNode, new ArrayList<>());

        while(!stack.empty()) {                                                  // 2
            currentNode = stack.pop();                                           // 2.1

            if (currentNode.hasNeighbors()) {                                    // 2.2
                stack.push(currentNode);                                         // 2.2.1

                neighborIndex = ThreadLocalRandom.current().nextInt(0, currentNode.getNeighbors().size());  // 2.2.2
                x = currentNode.getNeighbors().get(neighborIndex)[0];            // 2.2.2
                y = currentNode.getNeighbors().get(neighborIndex)[1];            // 2.2.2
                direction = currentNode.getNeighbors().get(neighborIndex)[2];    // 2.2.2
                currentNode.getNeighbors().remove(neighborIndex);                // 2.2.2

                nextNode = maze.getNode(x, y);                                   // 2.2.2
                if (!nextNode.isVisited()) {                                     // 2.2
                    
                    switch (direction) {                                         // 2.2.3
                        case right -> nextNode.setWest(false);                   // wall belongs to next node:   east from current node
                        case down  -> nextNode.setNorth(false);                  // wall belongs to next node:  south from current node
                        case left  -> currentNode.setWest(false);                // wall belongs to current node:  west in current node
                        case up    -> currentNode.setNorth(false);               // wall belongs to current node: north in current node
                    }
                    spanning3.get(currentNode).add(nextNode);     
                    if (spanning3.containsKey(nextNode))     
                        spanning3.get(nextNode).add(currentNode); 
                    else {                                
                        spanning3.put(nextNode, new ArrayList<>()); 
                        spanning3.get(nextNode).add(currentNode); 
                    }
                    nextNode.setVisited(true);                                  // 2.2.4
                    stack.push(nextNode);                                       // 2.2.4
                }
            }
        }
    }
}
