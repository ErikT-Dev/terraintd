package com.gdx.terraintd.logic;

public interface Command {
    void execute();
    void undo();
}