package com.gdx.terraintd.logic;

import com.gdx.terraintd.components.Tower;
import com.gdx.terraintd.components.ShopItem;
import com.gdx.terraintd.screens_and_ui.GameScreen;

import java.util.Objects;

public class BuildTowerCommand implements Command {
    private final GameScreen gameScreen;
    private final ShopItem shopItem;
    private final int xCoord;
    private final int yCoord;
    private final String terrain;
    private final float hexCenterX;
    private final float hexCenterY;
    private Tower builtTower;
    private final int cost;
    private final String[] adjacentTerrains;
    public boolean successfullyCompleted;

    public BuildTowerCommand(GameScreen gameScreen, ShopItem shopItem,String[] adjacentTerrains, int xCoord, int yCoord, String terrain, float hexCenterX, float hexCenterY) {
        this.gameScreen = gameScreen;
        this.shopItem = shopItem;
        this.xCoord = xCoord;
        this.yCoord = yCoord;
        this.terrain = terrain;
        this.hexCenterX = hexCenterX;
        this.hexCenterY = hexCenterY;
        this.cost = shopItem.cost;
        this.adjacentTerrains = adjacentTerrains;
        this.successfullyCompleted =false;
    }

    @Override
    public void execute() {
        if (gameScreen.statsManager.canAfford(cost)) {
            builtTower = new Tower(shopItem, adjacentTerrains, xCoord, yCoord, terrain, hexCenterX, hexCenterY);
            gameScreen.towers.add(builtTower);
            gameScreen.statsManager.spend(cost);
            gameScreen.gridSystem.updateCellValue(hexCenterX, hexCenterY, "Tower " + shopItem.name, true);
            gameScreen.gridSystem.cleanUpGrid();
            gameScreen.gridSystem.displayCriticalCells(
                    gameScreen.enemySpawner.pathfinder.findCriticalNodes(gameScreen.gridSystem.gridArray),
                    gameScreen.enemySpawner2.pathfinder.findCriticalNodes(gameScreen.gridSystem.gridArray),
                    gameScreen.enemySpawner3.pathfinder.findCriticalNodes(gameScreen.gridSystem.gridArray));
            if (Objects.equals(shopItem.name, "Spearman")) {
                gameScreen.statsManager.sandTokens++;
            }
            successfullyCompleted = true;
        }
    }

    @Override
    public void undo() {
        if (builtTower != null) {
            gameScreen.towers.remove(builtTower);
            gameScreen.statsManager.gainGold(cost);
            gameScreen.gridSystem.updateCellValue(hexCenterX, hexCenterY, terrain, false);
            gameScreen.gridSystem.cleanUpGrid();
            gameScreen.gridSystem.displayCriticalCells(
                    gameScreen.enemySpawner.pathfinder.findCriticalNodes(gameScreen.gridSystem.gridArray),
                    gameScreen.enemySpawner2.pathfinder.findCriticalNodes(gameScreen.gridSystem.gridArray),
                    gameScreen.enemySpawner3.pathfinder.findCriticalNodes(gameScreen.gridSystem.gridArray));
            if (Objects.equals(shopItem.name, "Spearman")) {
                gameScreen.statsManager.sandTokens--;
            }
        }
    }
}