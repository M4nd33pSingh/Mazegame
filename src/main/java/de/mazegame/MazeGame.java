package de.mazegame;

import de.mazegame.algo.Generator;
import de.mazegame.algo.PathFinder;
import de.mazegame.model.Maze;
import de.mazegame.model.Node;
import de.mazegame.model.Player;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;

/**
 * The program can be started either with positional arguments for the field-counts of x columns and y rows and the
 * program would try to interpret the input or without arguments. In both cases the user has to choose the option [0]
 * to actually generate an iterative based randomized depth first with backtracking 'perfect'(*) maze. The start and
 * end position in the maze are randomly generated. The minimum maze size is set to x=10, y=10 in order to avoid more
 * than one attempt of setting the player start and end position. <p>
 * The maze output is represented by Box Drawing UTF-8 characters and is indistinguishable from a GUI output aside from
 * the color restriction and the markers which mark the player's start and end position, solution-path and user
 * movements, while playing the Maze-Game in the console.
 *
 * <a href="https://www.ssec.wisc.edu/~tomw/java/unicode.html#x2500">...</a>
 */
public class MazeGame {
    private static Maze maze;
    private static Generator generator;
    private static Map<Node,ArrayList<Node>> mazeSpanningTree;
    private static Player player;
    private static PathFinder pathFinder;
    private static Scanner input;
    private static PrintStream output;
    private static boolean canPlay;
    private static long startTime, mazeDurTime, buildMazeOutputTime, printDurTime, markDurTime, solutionDurTime;
    private static int solutionPathLength;

    /**
     *
     *
     * @param args Positional arguments for setting x- and y-axis field count.
     */
    public static void main(String[] args) {
        int x=0,y=0;
        boolean isRunning = true;
        input = new Scanner(System.in);
        output = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        canPlay = false;

        if (isValid(args)) {
            x = Integer.parseInt(args[0]);
            y = Integer.parseInt(args[1]);
        } else
            args = null;
        printAbout();
        while(isRunning) {
            printMenu();
            try {
                switch(input.next().charAt(0)) {
                    case '0':
                        if (maze != null | args == null) {
                            do {
                                x = y = 0;
                                System.out.print("\nx-axis field count: ");
                                x = input.nextInt();
                                System.out.print("y-axis field count: ");
                                y = input.nextInt();
                                if ((x < 10) | (y < 10)) System.out.println(getErrorMazeSize());
                            } while((x < 10) | (y < 10));
                        }
                        setMaze(x,y);

                        startTime = System.nanoTime();
                        maze.setNodeFields();
                        buildMazeOutputTime = System.nanoTime() - startTime;
                        maze.setMarker(generator.getStartX(), generator.getStartY(), "...");

                        if (player != null) player = null; // set to null to reset the max x and y range...
                        setPlayer(); //... for the randomly chosen Player start and end positions.

                        System.out.printf("\n Perfect Maze with %d fields.", (y * x));
                        printPlayerMazeSetPathFinder();
                        printMazeDurationStats();
                        break;
                    case '1':
                        if (canPlay) {
                            printMaze(true);
                            play();
                        } else
                            printCantPlayError();
                        break;
                    case '2':
                        startTime = System.nanoTime();
                        markSolutionPath(true);
                        markDurTime = System.nanoTime() - startTime;

                        System.out.printf("""

                                 The solution path is from start to end %d fields long:
                                """, solutionPathLength + 1);
                        printMaze(true);
                        printSolutionDurationStats();
                        // deletes solution marker in Maze for the next maze printout
                        markSolutionPath(false);
                        break;
                    case '3': // print the maze as a 2D-Array to get a better understanding...
                        printMaze(false); //... of how the Maze output in the console is stored...
                        break; //... as an Array in maze.getNodefields.
                    case '4':
                        setPlayerStart();
                        printPlayerMazeSetPathFinder();
                        break;
                    case '5':
                        setPlayerEnd();
                        printPlayerMazeSetPathFinder();
                        break;
                    case '6':
                        setPlayer();
                        printPlayerMazeSetPathFinder();
                        break;
                    case '7':
                        printAbout();
                        break;
                    case '8':
                        printInstruction4UTF8InCMD();
                        break;
                    case '9': case 'q':
                        isRunning = false;
                        break;
                    default:
                        System.out.println(" Wrong input!\n");
                }
            } catch (Exception e) { System.out.println(" Error: " + e + "\n"); }
        }
        System.out.println("Exit");
        output.close();
    }

    private static boolean isValid(String[] args) {
        String optionZeroMSG = "\n\n Please choose option [0] to set the maze-size.";
        if (args.length != 0) {
            try {
                int x = Integer.parseInt(args[0]);
                int y = Integer.parseInt(args[1]);
                if (x>=0 & y>=0) {
                    System.out.printf(" Positional arguments interpreted as:\n x-axis (columns): %d\n y-axis (rows): %d%s",
                            x, y, optionZeroMSG.substring(0, 31) + "generate a maze.\n");
                    return true;
                } else
                    System.out.println(getErrorMazeSize() + optionZeroMSG);
            } catch(ArrayIndexOutOfBoundsException e) {
                System.out.println(" Error:\n Found only 1 positional argument instead of 2!"
                        + "\n Positional arguments are separated by one space."  + optionZeroMSG);
            } catch (Exception e) {
                System.out.println(" Error:\n " + e + optionZeroMSG);
            }
        }
        return false;
    }

    public static Maze getMaze() {
        return maze;
    }

    public static void setMaze(int x, int y) {
        startTime = System.nanoTime();
        maze = new Maze(x,y);
        generator = new Generator(maze);
        mazeDurTime = System.nanoTime() - startTime;
        mazeSpanningTree = generator.getSpanning3();
    }

    public static Player getPlayer() {
        return player;
    }

    public static void setPlayer() {
        if (player != null) {
            // deletes the old player start and end marker in these node-fields, if User chooses to set new start and new end
            maze.setMarker(   player.getPosX(),    player.getPosY(), "   ");
            maze.setMarker(player.getTargetX(), player.getTargetY(), "   ");
            // sets new player start and end coordinates for x,y
            player.setPosTarget(maze.getColumnCount(), maze.getRowCount());
        } else
            player = new Player(maze.getColumnCount(), maze.getRowCount());
        // sets start and end marker (PLY, END) in the specific node-field
        maze.setMarker(   player.getPosX(),    player.getPosY(), "PLY");
        maze.setMarker(player.getTargetX(), player.getTargetY(), "END");
    }

    public static void setPlayerStart() {
        maze.setMarker(player.getPosX(), player.getPosY(), "   ");
        player.setPos();
        maze.setMarker(player.getPosX(), player.getPosY(), "PLY");
    }

    public static void setPlayerEnd() {
        maze.setMarker(player.getTargetX(), player.getTargetY(), "   ");
        player.setTarget();
        maze.setMarker(player.getTargetX(), player.getTargetY(), "END");
    }

    public static PathFinder getPathFinder() {
        return pathFinder;
    }

    public static void setPathFinder() {
        startTime = System.nanoTime();
        pathFinder = new PathFinder(generator.getSpanning3(),maze.getNode(player.getPosX(),player.getPosY()), maze.getNode(player.getTargetX(),player.getTargetY()));
        solutionDurTime = System.nanoTime() - startTime;
    }

    public static void markSolutionPath(boolean isMarked) {
        String marker = isMarked ? " ■ " : "   ";
        for (Node node : pathFinder.getSolutionPath())
            maze.setMarker(node.getX(), node.getY(), marker);
    }

    public static Generator getGenerator() {
        return generator;
    }

    /**
     *
     * Is a sub-loop and works the same way as the main-loop.
     * User is able to change the current position of Player by pressing on of the following keys:
     * <pre>
     *  ↑ [w] OR [8] ↑ / ← [a] OR [4] ← / ↓ [s] OR [5] ↓ /  →  [d] OR [6]  →
     * </pre>
     * and then the Enter-key.
     *
     */
    private static void play() {
        String marker = "", inputMsg = "Press key for intended direction and then the key [Enter] or [Return]: \n [w] OR [8]: ↑    [a] OR [4]: ←    [s] OR [5]: ↓    [d] OR [6]: →    |[2]: Solution    [9]: Stop Game | ~ ";
        boolean isPlaying = true, isShowSolution = false;
        int destinationColumn, destinationRow, direction=0, plyCounter=0;
        //	                  none,  up,left, down, right
        int[] columnModifier = { 0,   0,  -1,   0,    1};
        int[] rowModifier    = { 0,  -1,   0,   1,    0};

        Node currentNode = maze.getNode(player.getPosX(), player.getPosY()), destinationNode;
        while(isPlaying) {
            try {
                output.print(inputMsg); // output? Because inputMSG contains UniCode-Chars like ← ↓ → ↑
                switch (input.next().charAt(0)) {
                    case '8', 'w' -> {
                        direction = 1;
                        marker = " ↑ ";
                    }
                    case '4', 'a' -> {
                        direction = 2;
                        marker = " ← ";
                    }
                    case '5', 's' -> {
                        direction = 3;
                        marker = " ↓ ";
                    }
                    case '6', 'd' -> {
                        direction = 4;
                        marker = " → ";
                    }
                    case '2' -> {
                        isShowSolution = true;
                        direction = 0;
                    }
                    case '9' -> isPlaying = false;
                    default  -> direction = 0;
                }
                if (isPlaying) {
                    if (!isShowSolution) plyCounter++;

                    destinationColumn = player.getPosX() + columnModifier[direction];
                    destinationRow    = player.getPosY() +    rowModifier[direction];
                    // checks whether player movement direction is within the maze's boundary
                    if (player.isDestinationInMaze(destinationColumn, destinationRow)) {
                        destinationNode = maze.getNode(destinationColumn, destinationRow);

                        // checks whether there is a path from currentNode to player movement direction's targetNode. If it does not contain the targetNode, it means there...
                        if (mazeSpanningTree.get(currentNode).contains(destinationNode) & !isShowSolution) { //...is a wall between currentNode-field and targetNode-field
                            player.setPos(destinationColumn, destinationRow);

                            maze.setMarker(currentNode.getX(), currentNode.getY(), marker);
                            maze.setMarker( destinationColumn,     destinationRow,  "PLY");
                            currentNode = destinationNode;

                            if (player.isPosTargetEqual()) {
                                isPlaying = false;
                                System.out.printf(("\n\t\tCongratulations! You reached the end after %d steps! "
                                + "Solution-Path Length: %d%n"), plyCounter, solutionPathLength);
                                canPlay = false; // indicates that a new maze should be generated, as this maze was successfully solved by the User.
                            }
                            // shows the solution path, with one downside. It will override the nodeField's data, if the User went along the solution path and...
                        } else if (isShowSolution) { //... than differed at some point deleting all the player-movements which lie on the solution-path ahead.
                            setPathFinder(); // recalculates the solution-pathway with player's new current position
                            markSolutionPath(true); // marks the solution in the maze and therefore might override past player-movements
                        } else if (currentNode == destinationNode) {
                            System.out.println("\n\tERROR!   Invalid KEY entered\n");
                        } else
                            System.out.println("\n\tINVALID DIRECTION!   Wall\n");
                    } else
                        System.out.println("\n\tINVALID DIRECTION!   Out of Maze boundary\n");

                    printMaze(true);

                    if (isShowSolution) markSolutionPath(false); // erases solution-pathway-marker from maze with 3 space characters
                    isShowSolution = false;
                } else
                    System.out.println("\nPlayer ended Game!\n To Continue the previous game choose option [1].");
            } catch (Exception ignored) {
            }
        }
    }

    private static String getErrorMazeSize() {
        return "Error: Input for x and y should be greater than 9!";
    }

    private static void printCantPlayError() {
        System.out.println("""
                 Error!
                \tPlease generate either a new Maze with option [0]
                \tor generate a new end position with option [5] or [6].""");
    }

    /**
     * Before printing the maze in the console in either form, it first checks whether the maze-output
     * for the console was ever built or not. If not (represented as isBuildMazeOutPut==true) then the method
     * buildMazeOutPut() is called to save the visualized representation of each node as a string
     * in a 2D-Array. <p>
     *
     * Prints either the maze as a very long String of UniCodes, Spaces and line separators
     * OR the mazes' 2D-Array string representation with its specific value represented as a string
     * which shows the character behind the Encode and not the code itself.<p>
     *
     * The purpose of the maze's 2D-array representation is to give a rough idea about the maze data
     * structure by visualizing (only the necessary data) of each indices y,x (maze.nodes[y][x]), at which
     * a new instance of the Node class is stored.
     * It also shows why just 2 edges instead of 4 (other maze solutions) are sufficient to visualize
     * the complete maze in the console or in a GUI. <br>
     *
     * @param isPrintMaze a boolean:<br>- <b>true</b> to print the <b>maze</b> or <br>- <b>false</b> to print
     * the <b>maze, but as an array</b> to visualize the maze's nodes for better understanding.
     *
     */
    public static void printMaze(boolean isPrintMaze) {
        startTime = System.nanoTime();
        output.print(isPrintMaze ? maze : maze.toStringArray());
        printDurTime = System.nanoTime() - startTime;
    }

    /**
     * Converts the duration of nanoseconds into its appropriate time unit to be human-readable.<p>
     * Duration / 1,000 = microseconds.<br>
     * Duration / 1,000,000 = milliseconds. <br>
     * Duration / 1,000,000,000 = seconds.
     *
     * @param duration	The duration in nanoseconds as a long Integer value.
     * @return			The converted duration with the appropriate time unit
     */
    public static String getDurTimeUnit(long duration) {
        double mil = 1000000.0;
        return (duration<mil ? String.format("%6.2f microseconds",duration/1000.0) : duration/mil<1000 ?
                String.format("%6.2f milliseconds",duration/mil) : String.format("%6.2f seconds",duration/(mil*1000)));
    }

    private static void printPlayerMazeSetPathFinder() {
        System.out.printf(" Player start-coordinate at x=%d,y=%d and end-coordinate at x=%d,y=%d:\n",
                player.getPosX() + 1, player.getPosY() + 1, player.getTargetX() + 1, player.getTargetY() + 1);
        printMaze(true);
        canPlay = true;
        setPathFinder();
        solutionPathLength = pathFinder.getSolutionPath().size() + 1;
    }

    private static void printMazeDurationStats() {
        System.out.printf("""
                         It took %s to generate this maze,
                                 %s to build the Maze-Output-Array
                             and %s to print it out.
                        """,
                getDurTimeUnit(mazeDurTime), getDurTimeUnit(buildMazeOutputTime), getDurTimeUnit(printDurTime));
    }

    private static void printSolutionDurationStats() {
        System.out.printf("""
                         It took %s to find the only Path connecting PLY-Node at %s to END-Node at %s
                             and %s to mark the Solution-Path in the maze to see it in the maze's printout
                           after %s.
                        """, getDurTimeUnit(solutionDurTime),
                "x=%d,y=%d".formatted(player.getPosX()+1, player.getPosY()+1),
                "x=%d,y=%d".formatted(player.getTargetX()+1, player.getTargetY()+1),
                getDurTimeUnit(markDurTime), getDurTimeUnit(printDurTime));

    }
    private static void printMenu() {
        System.out.println("""

                 [0] Generate a random 'perfect' maze.
                 [1] Play the Maze-Game (in console).\s
                 [2] Print solution.\s
                 [3] Print the maze as a 2D-Array\s
                 [4] New random Player start position
                 [5] New random Player end position\s
                 [6] New random Player start and end position.

                 [7] About Maze-Game
                 [8] Guide to activate UTF-8 Encoding for windows command terminal (cmd.exe), if the maze printout
                     did not print in UTF-8.
                 [9] Exit program
                """);
    }
    private static void printInstruction4UTF8InCMD() {
        System.out.println("""

                 1) Exit Program by choosing option [9].
                 2) Type in console: chcp 65001 and press enter.
                    It activates the UTF-8 encoding code page for this command window (session).
                    Opening a new command window resets the code page to default.
                    Using just the command: chcp  shows the default activated code page.
                 3) Left-click on the icon in the command window title and choose Properties*.
                 4) Then choose the tab Fonts* to change the font to 'Consolas', save and close the properties-window.**
                 5) Start Mazegame with same command as before (use arrow-key up to switch between previous commands).

                  * Not sure if it is called like so, because author's MS Windows is in German.
                 ** For the author only 'Consolas'-font worked.
                    The 'Lucida'-font, as suggested on the following web-page, didn't work properly:
                 https://stackoverflow.com/questions/388490/how-to-use-unicode-characters-in-windows-command-line

                """);
    }
    private static void printAbout() {
        System.out.print("""
                 Author: Mandeep Singh
                 Version: 1.0
                 Date: 21.09.2022
                 GitHub: www.github.com/M4nd33pSingh/

                \s""");
    }
}