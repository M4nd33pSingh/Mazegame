package de.mazegame.model;

import java.util.concurrent.ThreadLocalRandom;

public class Player {
    private int[] pos = {0,0};
    private int[] target = {0,0};
    private int mazeSizeX, mazeSizeY;

    public Player(int sizeX, int sizeY) {
        // define random Player position and target between 0 to sizeX-1 and 0 to sizeY-1
        // the smaller the sizes the higher the chance that pos and target have the same random number for x and y
        setPosTarget(sizeX, sizeY);
    }

    public boolean isPosTargetEqual() {
        return pos[0]==target[0] & pos[1]==target[1];
    }

    public int[] getPos() {
        return pos;
    }

    public int getPosX() {
        return pos[0];
    }

    public int getPosY() {
        return pos[1];
    }

    public void setPos() {
        do {
            this.pos[0] = ThreadLocalRandom.current().nextInt(0, mazeSizeX-1);
            this.pos[1] = ThreadLocalRandom.current().nextInt(0, mazeSizeY-1);
        } while (isPosTargetEqual());
    }

    public void setPos(int x, int y) {
        this.pos[0] = x;
        this.pos[1] = y;
    }

    public int[] getTarget() {
        return target;
    }

    public int getTargetX() {
        return target[0];
    }

    public int getTargetY() {
        return target[1];
    }

    public void setTarget() {
        do {
            this.target[0] = ThreadLocalRandom.current().nextInt(0, mazeSizeX-1);
            this.target[1] = ThreadLocalRandom.current().nextInt(0, mazeSizeY-1);
        } while (isPosTargetEqual());
    }

    public void setPosTarget(int sizeX, int sizeY) {
        this.mazeSizeX = sizeX;
        this.mazeSizeY = sizeY;
        setPos();
        setTarget();
    }

    public boolean isDestinationInMaze(int x, int y) {
        return x >= 0  &  x < mazeSizeX  &  y >= 0  &  y < mazeSizeY;
    }
}
