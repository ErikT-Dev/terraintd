package com.gdx.terraintd.logic;

import com.gdx.terraintd.components.Tower;
import com.gdx.terraintd.screens_and_ui.GameScreen;

public class SellTowerCommand implements Command {
    private final GameScreen gameScreen;
    private final Tower tower;
    private final String originalTerrain;
    private final int refundAmount;

    public SellTowerCommand(GameScreen gameScreen, Tower tower) {
        this.gameScreen = gameScreen;
        this.tower = tower;
        this.originalTerrain = tower.terrain;
        this.refundAmount = tower.sellReward;
    }

    @Override
    public void execute() {
        gameScreen.towers.remove(tower);
        gameScreen.gridSystem.updateCellValue(tower.hexCenterX, tower.hexCenterY, originalTerrain, false);
        gameScreen.statsManager.gainGold(refundAmount);
    }

    @Override
    public void undo() {
        gameScreen.towers.add(tower);
        gameScreen.gridSystem.updateCellValue(tower.hexCenterX, tower.hexCenterY, "Tower " + tower.towerName, true);
        gameScreen.statsManager.spend(refundAmount);
    }
}