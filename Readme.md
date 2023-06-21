This is a Maze-Generator, a Maze-Solver and the Maze-Game is played in the console without a Text-User-Interface (TUI).

The algorithm used to generate the Maze is a randomized Depth-First-Search with Backtracking using a stack (iterative approach). While generating the Maze the algorithm builds also the Spanning-Tree, which is needed for the game itself, because it stores which paths are valid for all Nodes in the Maze. If the User moves from one Nodefield (cell) to another it simply checks wheater there is a path from current player Node to destination Node.

A real copy of the Spanning-Tree is also used to find a Solution-Path from the current Player position to the target Node. While finding the Solution-Path it will destroy the copy of the Spanning-Tree, which is why a real copy of the Spanning-Tree is needed.

#### What's so special about this Maze-Game?  <br>

**~ Well**, the Box Drawing Unicode Characters which are used to print out the Maze in the console, but looks like drawn in a GUI. This has been accomplished by calculating one of the 14 different wall connectors for each Node. The calculation is done once for every new Maze and is saved in a String-Array, which is a 2D-Representation of the Maze's 2D-Nodes(objects)-Array.

Each Node stores only 2 walls, which are the North-Wall and the West-Wall. If you put lots of
"L"s in a 2D-Sequence, like so:
<pre>
LLLLLLLLLLL
LLLLLLLLLLL
LLLLLLLLLLL
</pre>

and **FLIP** it, you get a grid of cells apart from the bottom and right border. To get a right and bottom border x and y are upped by one to get x+1 times y+1 Nodes, but only x times y cells. That is the initial state before generating the Maze by the Generator-Class.

Coming back to the wall connectors, these are the 14 different possibilities:

"╹", "╸", "┛", "╻",  "┃", "┓", "┫", "╺", "┗", "━", "┻", "┏", "┣", "┳", "╋"

which are stored in a String-Array. The indices at which what character is stored is important here, because to choose the right character a 4 Bit Binary-Code is built, which gets converted to a specific index. Looking at the initial state of the Maze, which is a grid, you see lots of "╋". The Binary-Code for this character is 1111, which converted to an Integer 15 and because an Array starts counting at 0 an empty String is added at Index 0 like so:

" ", "╹", "╸", "┛", "╻",  "┃", "┓", "┫", "╺", "┗", "━", "┻", "┏", "┣", "┳", "╋"

To build the Binary-Code it has to check 4 states, whether it has a North-Wall and/or West-Wall in 3 different Nodes. It checks first, if the current Node has a North-Wall, then if the current Node has a West-Wall, if the previous Node has a North-Wall (horizontal) and if the upper Node has a West-Wall (vertical). If everything checks to true the Binary-Code 1111 is built.

How is 1111 an Integer 15?

8 4 2 1  <br>
1 1 1 1 → 8 + 4 + 2 + 1 = 15

For the character "┳" at index 14: 

8 4 2 1  <br>
1 1 1 0 →  8 + 4 + 2 = 14

meaning the current Node has a North-Wall and West-Wall and previous Node has a North-Wall, but the upper Node has **no** West-Wall.

The opposite example would be at Index 1 with "╹":

8 4 2 1  <br>
0 0 0 1 → = 1

meaning the current Node has no North-Wall, no West-Wall and the previous Node has no North-Wall, but the upper Node **has** a West-Wall.

That is why the Maze looks very smooth and almost indistinguishable to a GUI output:
<pre>
x-axis field count: 10
y-axis field count: 10

Perfect Maze with 100 fields. Player start-coordinate at x=3,y=1 and end-coordinate at x=6,y=5: <br>
┏━━━━━━━━━━━━━━━━━━━┳━━━━━━━━━━━┳━━━━━━━┓   
┃        PLY        ┃           ┃       ┃   
┃   ┏━━━┓   ╺━━━┓   ╹   ╻   ┏━━━┛   ╻   ┃   
┃   ┃   ┃       ┃       ┃   ┃       ┃...┃   
┃   ╹   ┣━━━┓   ┗━━━━━━━┻━━━┫   ╺━━━┻━━━┫   
┃       ┃   ┃               ┃           ┃   
┣━━━╸   ╹   ┗━━━┳━━━━━━━╸   ┣━━━━━━━┓   ┃   
┃               ┃           ┃       ┃   ┃   
┃   ╺━━━┳━━━━━━━┫   ┏━━━╸   ┃   ┏━━━┛   ┃   
┃       ┃       ┃   ┃END    ┃   ┃       ┃   
┣━━━━━━━┛   ╻   ╹   ┃   ┏━━━┛   ┃   ╺━━━┫   
┃           ┃       ┃   ┃       ┃       ┃   
┃   ┏━━━━━━━┻━━━━━━━┫   ┃   ╺━━━╋━━━╸   ┃   
┃   ┃               ┃   ┃       ┃       ┃   
┃   ┗━━━┓   ┏━━━━━━━┛   ┗━━━╸   ┃   ┏━━━┫   
┃       ┃   ┃                   ┃   ┃   ┃   
┃   ╻   ┃   ╹   ╺━━━━━━━┳━━━━━━━┫   ┃   ┃   
┃   ┃   ┃               ┃       ┃   ┃   ┃   
┃   ┃   ┗━━━━━━━━━━━━━━━┛   ╻   ╹   ╹   ┃   
┃   ┃                       ┃           ┃   
┗━━━┻━━━━━━━━━━━━━━━━━━━━━━━┻━━━━━━━━━━━┛   

It took  12,14 milliseconds to generate this maze,
31,00 milliseconds to build the Maze-Output-Array
and 232,15 microseconds to print it out.
</pre>
<ul>
<li>PLY ~ Starting position for the user  </li>
<li>END ~ Target position  </li>
<li>... ~ The starting position where Generator started generating the Maze, which is irrelevant for the game itself, but 
would be needed to animate the building process of the Maze. </li>
<li>fields ~ cells </li>
</ul>

To get a better understanding of what a Node looks like as a 2D-String-Array representation: <br>
(_choose in the menu input option 3 after choosing option 0 for the Maze size_)
<pre>
[[┏━━━, ━━━━, ━━━━, ━━━━, ━━━━, ┳━━━, ━━━━, ━━━━, ┳━━━, ━━━━, ┓   ],
 [┃   ,     ,  PLY,     ,     , ┃   ,     ,     , ┃   ,     , ┃   ],

 [┃   , ┏━━━, ┓   , ╺━━━, ┓   , ╹   , ╻   , ┏━━━, ┛   , ╻   , ┃   ],
 [┃   , ┃   , ┃   ,     , ┃   ,     , ┃   , ┃   ,     , ┃..., ┃   ],

 [┃   , ╹   , ┣━━━, ┓   , ┗━━━, ━━━━, ┻━━━, ┫   , ╺━━━, ┻━━━, ┫   ],
 [┃   ,     , ┃   , ┃   ,     ,     ,     , ┃   ,     ,     , ┃   ],

 [┣━━━, ╸   , ╹   , ┗━━━, ┳━━━, ━━━━, ╸   , ┣━━━, ━━━━, ┓   , ┃   ],
 [┃   ,     ,     ,     , ┃   ,     ,     , ┃   ,     , ┃   , ┃   ],

 [┃   , ╺━━━, ┳━━━, ━━━━, ┫   , ┏━━━, ╸   , ┃   , ┏━━━, ┛   , ┃   ],
 [┃   ,     , ┃   ,     , ┃   , ┃END,     , ┃   , ┃   ,     , ┃   ],
 
 [┣━━━, ━━━━, ┛   , ╻   , ╹   , ┃   , ┏━━━, ┛   , ┃   , ╺━━━, ┫   ],
 [┃   ,     ,     , ┃   ,     , ┃   , ┃   ,     , ┃   ,     , ┃   ],
 
 [┃   , ┏━━━, ━━━━, ┻━━━, ━━━━, ┫   , ┃   , ╺━━━, ╋━━━, ╸   , ┃   ],
 [┃   , ┃   ,     ,     ,     , ┃   , ┃   ,     , ┃   ,     , ┃   ],
 
 [┃   , ┗━━━, ┓   , ┏━━━, ━━━━, ┛   , ┗━━━, ╸   , ┃   , ┏━━━, ┫   ],
 [┃   ,     , ┃   , ┃   ,     ,     ,     ,     , ┃   , ┃   , ┃   ],
 
 [┃   , ╻   , ┃   , ╹   , ╺━━━, ━━━━, ┳━━━, ━━━━, ┫   , ┃   , ┃   ],
 [┃   , ┃   , ┃   ,     ,     ,     , ┃   ,     , ┃   , ┃   , ┃   ],
 
 [┃   , ┃   , ┗━━━, ━━━━, ━━━━, ━━━━, ┛   , ╻   , ╹   , ╹   , ┃   ],
 [┃   , ┃   ,     ,     ,     ,     ,     , ┃   ,     ,     , ┃   ],
 
 [┗━━━, ┻━━━, ━━━━, ━━━━, ━━━━, ━━━━, ━━━━, ┻━━━, ━━━━, ━━━━, ┛   ],
 [    ,     ,     ,     ,     ,     ,     ,     ,     ,     ,     ],
]
</pre>
You see after every 2 rows a line gap, which shows that 2 rows represent 1 row of the Maze's 2D-Node-Objects-Array. As
mentioned above this representation is only calculated once for every new Maze. 

**~ And** the second reason why this program is special is, because you can actually play the Maze-Game in console 
without the need of a TUI. You just need 2 hands. One pressing the desired directional Key and the other pressing the 
Enter-Key. <br> **side-feature:** if the maze is small enough and character buffer size high enough, then scrolling 
upwards in the command window shows the current game's history of User movements.


#### This Java Game was ported from the python version below:

https://github.com/Malkogiannidou/MazeGame_MazeSolver_MazeGenerator

which prints out the Maze in the console, but uses the PyGame-Module as a GUI to play the game and animates how the Maze 
was created. It was a university assignment, which I coded on my own for a group of 3 people.

Here is a more "complete picture" of the Binary-Encoding from the previous Maze-Game Python-Project, which used a
dictionary (the Java version converts the binary-code to an Integer index) for the Binary-Code and is read vertically here:
<pre>
                            
                 ┌─────┬─────────┬─────────────┬─────────┐
                 │How many arms stick out from the center│
┌────────────────┤      of these Box-Drawing-Chars?      │
│   wall-state   │ 4 0 │    3    │      2      │    1    │
│ to check order │ ╋   │ ┫ ┻ ┣ ┳ │ ┛ ┗ ┏ ┓ ┃ ━ │ ╹ ╺ ╻ ╸ │ ←── reading this line (horizontally) has a different  
├┄┄┄┄┄┄┄┄┄┄┄┄┄┄─┄┼┄┼┄┼┄┼┄┼┄┼┄┼┄┼┄┼┄┼┄┼┄┼┄┼┄┼┄┼┄┼┄┼┄┼┄┼┄┼┄┤     sequence order than how it is stored in the Java-Version, 
│ hasNorth  *: ━ ┼ 1 0 │ 0 1 1 1 │ 0 1 1 0 0 1 │ 0 1 0 0 │ 1   because the Python-Version uses a dictionary, whereas the 
│ hasWest   *: ┃ ┼ 1 0 │ 1 0 1 1 │ 0 0 1 1 1 0 │ 0 0 1 0 │ 2   Java-Version uses just an Array. 
│ hasNorth **: ━ ┼ 1 0 │ 1 1 0 1 │ 1 0 0 1 0 1 │ 0 0 0 1 │ 4
│ hasWest ***: ┃ ┼ 1 0 │ 1 1 1 0 │ 1 1 0 0 1 0 │ 1 0 0 0 │ 8
└────────────────┴━┼─┼─┴─┼─┼─┼─┼─┴─┼─┼─┼─┼─┼─┼─┴─┼─┼─┼─┼─┘
                  15 0  14 |11 7  12 | 3 |10 |   8 1 2 4   ┐
                     |    13         9   6   5             ┴ Converted to Integer-Indices
                     |
                     ┗ This case will never become true, but it's needed just as a placeholder at index 0 or else the
                       index parsed from the Binary-Code would access the UniCode-Char-Array at index+1 which could 
                       cause also an IndexOutOfBounds Exception. In the Python-Version there are false-positive cases 
                       which get corrected. 

 *   the current  Node at x,   y 
 **  the previous Node at x-1, y 
 *** the upper    Node at x,   y-1

'x' can be interpreted also as the columns and 'y' as the rows in the Maze. 
</pre>

Source _edited to resemble the Java-Version_: https://github.com/Malkogiannidou/MazeGame_MazeSolver_MazeGenerator/blob/6ac76bcbf01b19ef6d55b28d8c8612d64b064227/model.py#L171


At some time in the future I'll rewrite the python version to be also playable in the console with the same speedy Maze 
output optimization with an easier understandable Python-Code. The Python-Version didn't need an optimization for the 
Maze-Printout, because it uses the pygame Module, which uses a GUI for the Maze drawing. The Python-Version might look 
more complicated, which is true partly, because some of the elements, like the rectangular shape to draw the Maze in GUI,
are outsourced from the class which uses the PyGame-Module. Before the optimization for the Java-Version was done, it was 
almost as "slow" as the Python-Version.

If the Maze output looks something like this:
<pre>
ÔöÅÔöüÔöüÔöüÔöüÔöüÔöüÔöüÔöüÔöüÔöüÔöüÔö│ÔöüÔöüÔöüÔöüÔöüÔöüÔöüÔöüÔöüÔöüÔöüÔöüÔöüÔöüÔöüÔöüÔöüÔöüÔöüÔö│ÔöüÔöüÔöüÔöüÔöüÔöüÔöüÔöô
Ôöâ           Ôöâ                   ÔöâEND    Ôöâ
Ôöâ   ÔöÅÔöüÔöüÔöüÔöô   Ôöâ   Ôò║ÔöüÔöüÔöüÔö│ÔöüÔöüÔöüÔò©   ÔöÅÔöüÔöüÔöüÔöø   Ôò╗   Ôöâ
Ôöâ   Ôöâ   Ôöâ   Ôöâ       Ôöâ       Ôöâ       Ôöâ   Ôöâ
Ôöâ   Ôöâ   Ôò╣   ÔöùÔöüÔöüÔöüÔò©   ÔöúÔöüÔöüÔöüÔöüÔöüÔöüÔöüÔöø   Ôò║ÔöüÔöüÔöüÔö½   Ôöâ
Ôöâ   Ôöâ    PLY        Ôöâ               Ôöâ   Ôöâ
Ôöâ   ÔöùÔöüÔöüÔöüÔö│ÔöüÔöüÔöüÔöüÔöüÔöüÔöüÔöô   Ôöâ   ÔöÅÔöüÔöüÔöüÔöüÔöüÔöüÔöüÔö│ÔöüÔöüÔöüÔö½   Ôöâ
Ôöâ       Ôöâ       Ôöâ   Ôöâ   Ôöâ       Ôöâ...Ôöâ   Ôöâ
Ôöâ   Ôò╗   Ôöâ   Ôò╗   ÔöùÔöüÔöüÔöüÔöø   Ôöâ   Ôò╗   Ôöâ   Ôò╣   Ôöâ
Ôöâ   Ôöâ   Ôöâ   Ôöâ           Ôöâ   Ôöâ   Ôöâ       Ôöâ
Ôöâ   Ôöâ   Ôò╣   ÔöúÔöüÔöüÔöüÔöüÔöüÔöüÔöüÔö│ÔöüÔöüÔöüÔöø   ÔöùÔöüÔöüÔöüÔö╗ÔöüÔöüÔöüÔöüÔöüÔöüÔöüÔö½
Ôöâ   Ôöâ       Ôöâ       Ôöâ                   Ôöâ
Ôöâ   ÔöúÔöüÔöüÔöüÔöüÔöüÔöüÔöüÔöø   Ôò║ÔöüÔöüÔöüÔöø   ÔöÅÔöüÔöüÔöüÔò©   ÔöÅÔöüÔöüÔöüÔò©   Ôöâ
Ôöâ   Ôöâ                   Ôöâ       Ôöâ       Ôöâ
Ôöâ   ÔöùÔöüÔöüÔöüÔöüÔöüÔöüÔöüÔöüÔöüÔöüÔöüÔö│ÔöüÔöüÔöüÔöüÔöüÔöüÔöüÔöø   ÔöÅÔöüÔöüÔöüÔö½   Ôò║ÔöüÔöüÔöüÔö½
Ôöâ               Ôöâ           Ôöâ   Ôöâ       Ôöâ
ÔöúÔöüÔöüÔöüÔöüÔöüÔöüÔöüÔö│ÔöüÔöüÔöüÔò©   Ôöâ   Ôò║ÔöüÔöüÔöüÔö│ÔöüÔöüÔöüÔöø   ÔöúÔöüÔöüÔöüÔò©   Ôöâ
Ôöâ       Ôöâ       Ôöâ       Ôöâ       Ôöâ       Ôöâ
Ôöâ   Ôò╗   Ôò╣   Ôò║ÔöüÔöüÔöüÔö╗ÔöüÔöüÔöüÔò©   Ôöâ   Ôò║ÔöüÔöüÔöüÔöø   Ôò║ÔöüÔöüÔöüÔö½
Ôöâ   Ôöâ                   Ôöâ               Ôöâ
ÔöùÔöüÔöüÔöüÔö╗ÔöüÔöüÔöüÔöüÔöüÔöüÔöüÔöüÔöüÔöüÔöüÔöüÔöüÔöüÔöüÔöüÔöüÔöüÔöüÔö╗ÔöüÔöüÔöüÔöüÔöüÔöüÔöüÔöüÔöüÔöüÔöüÔöüÔöüÔöüÔöüÔöø
</pre>

then choose the option 8 in the menu and the instruction which follows on the console screen. 