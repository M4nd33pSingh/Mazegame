package de.mazegame.algo;

import de.mazegame.model.Node;

import java.util.ArrayList;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ThreadLocalRandom;

public class PathFinder {

    private final Stack<Node> solutionPath;
    private final Map<Node, ArrayList<Node>> spanning3;

    public PathFinder(Map<Node,ArrayList<Node>> generatedSpanningTree, Node start, Node end) {

        //     ↓  is a copy of the original spanning3, because this spanning3
        //     ↓  gets destroyed while searching the only path solution
        this.solutionPath = new Stack<>();
        this.spanning3 = generatedSpanningTree;

        setSolutionPath(start, end);
    }

    public Stack<Node> getSolutionPath() {
        return solutionPath;
    }

    /**
     * Finds the Solution-Path from Players current position (currentNode) to the target position (targetNode).
     * <pre>
     * 1.  PUSH the currentNode to the solutionPath (stack)
     * 2.  WHILE the nextNode is not equal to the targetNode
     * 2.1.  POP a Node from the stack and make it a current Node
     * 2.2.  IF the current Node has any connection to other Nodes
     * 2.2.1.  PUSH the currentNode to the stack
     * 2.2.2.  CHOOSE a random path from currentNode as nextNode. (by randomizing the index to access a Node in the currentNode's ArrayList)
     * 2.2.3.  REMOVE the path from currentNode to nextNode.      (by removing nextNode from currentNode's ArrayList)
     * 2.2.4.  REMOVE the path from nextNode to currentNode.      (by removing currentNode from nextNode's ArrayList)
     * 2.2.5.  PUSH the nextNode to the stack
     * 2.3.  GOTO Point 2
     * 3. REMOVE the first Node from the stack
     * 4. POP the last Node from the stack
     * </pre>
     *
     *
     * 2) while (next != end & !solutionPath.isEmpty()){ ← solutionPath will NEVER be empty, because a ...
     * ...'perfect' will have always one and only solution from any Node start to any other Node end!
     *
     * 2.1)Backtracking meaning the use of a stack, to pop to the previous path position,
     * if there are no other paths at that particular node, which also deletes the nodes
     * from the solution path, that are not a part of the only solution path.
     * 3+4)← removes first and last elements, because otherwise marking the solution-pathway in the Maze.nodeFields,...
     * ... which visualizes a Node instance's walls (Node.hasNorth, Node.hasWest)
     * with UniCode-Strings, would overwrite the Node instance's 'field-marker' instance's String representation at the Player's start ("PLY") and end ("END") indices'  Node representation's String of Maze.nodeFields.
     *
     *
     * @param currentNode
     * @param targetNode
     */
    private void setSolutionPath(Node currentNode, Node targetNode) {
        Node nextNode = currentNode;
        this.solutionPath.push(currentNode);					  // 1.

        while(nextNode != targetNode) {  						  // 2.
            currentNode = solutionPath.pop();  					  // 2.1.

            if ( !spanning3.get(currentNode).isEmpty()) {		  // 2.2.
                this.solutionPath.push(currentNode);			  // 2.2.1.

                nextNode = getRandomPathNodeFrom(currentNode);    // 2.2.2.
                this.spanning3.get(currentNode).remove(nextNode); // 2.2.3.
                this.spanning3.get(nextNode).remove(currentNode); // 2.2.4.

                this.solutionPath.push(nextNode);                 // 2.2.5.
            }													  // 2.3. -> 2.
        }
        this.solutionPath.remove(0);						  // 3.
        this.solutionPath.pop();								  // 4.
    }

    private Node getRandomPathNodeFrom(Node node) {
        int randomIndex = ThreadLocalRandom.current().nextInt(0, spanning3.get(node).size());
        return spanning3.get(node).get(randomIndex);
    }
}
