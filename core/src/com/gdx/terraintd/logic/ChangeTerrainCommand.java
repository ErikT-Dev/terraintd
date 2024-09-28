package com.gdx.terraintd.logic;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.gdx.terraintd.components.ShopItem;
import com.gdx.terraintd.screens_and_ui.GameScreen;

public class ChangeTerrainCommand implements Command {
    private final GameScreen gameScreen;
    private final int xCoord;
    private final int yCoord;
    private final String previousTerrain;
    private final String newTerrain;
    private final float screenX;
    private final float screenY;
    private final int cost;
    private boolean usedFreeItem;
    public boolean successfullyCompleted;

    public ChangeTerrainCommand(GameScreen gameScreen, ShopItem shopItem, int xCoord, int yCoord, String previousTerrain, float screenX, float screenY) {
        this.gameScreen = gameScreen;
        this.xCoord = xCoord;
        this.yCoord = yCoord;
        this.previousTerrain = previousTerrain;
        this.screenX = screenX;
        this.screenY = screenY;
        this.newTerrain = shopItem.name;
        this.cost = shopItem.cost;
        this.successfullyCompleted = false;
    }

    @Override
    public void execute() {
        if (canExecute()) {
            Texture newTexture = gameScreen.textureManager.getRandomTexture(newTerrain);
            gameScreen.gridSystem.updateCellValue(screenX, screenY, newTerrain, false);
            gameScreen.gameRenderer.updateTerrainTextureAssignment(new Vector2(xCoord, yCoord), newTexture);

            if (usedFreeItem) {
                if (newTerrain.equals("Forest")) gameScreen.statsManager.useAForestToken();
                else if (newTerrain.equals("Grass")) gameScreen.statsManager.useAGrassToken();
                else if (newTerrain.equals("Sand")) gameScreen.statsManager.useASandToken();
            } else {
                gameScreen.statsManager.spend(cost);
            }
            updatePathfinding();
            successfullyCompleted = true;
        }
    }

    @Override
    public void undo() {
        if (successfullyCompleted) {
            Texture previousTexture = gameScreen.textureManager.getRandomTexture(previousTerrain);
            gameScreen.gridSystem.updateCellValue(screenX, screenY, previousTerrain, false);
            gameScreen.gameRenderer.updateTerrainTextureAssignment(new Vector2(xCoord, yCoord), previousTexture);

            if (usedFreeItem) {
                if (newTerrain.equals("Forest")) gameScreen.statsManager.forestTokens++;
                else if (newTerrain.equals("Grass")) gameScreen.statsManager.grassTokens++;
                else if (newTerrain.equals("Sand")) gameScreen.statsManager.sandTokens++;
            } else {
                gameScreen.statsManager.gainGold(cost);
            }
            updatePathfinding();
        }
    }

    private boolean canExecute() {
        if (newTerrain.equals("Forest") && previousTerrain.equals("Grass")) {
            if (gameScreen.statsManager.forestTokens > 0) {
                usedFreeItem = true;
                return true;
            }
            return gameScreen.statsManager.canAfford(cost);
        }
        if (newTerrain.equals("Grass") && (previousTerrain.equals("Forest") || previousTerrain.equals("Hill") || previousTerrain.equals("Sea") || previousTerrain.equals("Sand")|| previousTerrain.equals("Sand Critical"))) {
            if (gameScreen.statsManager.grassTokens > 0) {
                usedFreeItem = true;
                return true;
            }
            return gameScreen.statsManager.canAfford(cost);
        }
        if (newTerrain.equals("Sand") && (previousTerrain.equals("Grass") || previousTerrain.equals("Grass Critical"))) {
            String[] adjacentTerrains = gameScreen.gridSystem.getAdjacentCellValues(xCoord, yCoord);
            boolean hasAdjacentSpearman = java.util.Arrays.stream(adjacentTerrains).anyMatch(s -> s.contains("Spearman"));
            if (hasAdjacentSpearman && gameScreen.statsManager.sandTokens > 0) {
                usedFreeItem = true;
                return true;
            }
        }
        return false;
    }

    private void updatePathfinding() {
        gameScreen.gridSystem.cleanUpGrid();
        gameScreen.gridSystem.displayCriticalCells(
                gameScreen.enemySpawner.pathfinder.findCriticalNodes(gameScreen.gridSystem.gridArray),
                gameScreen.enemySpawner2.pathfinder.findCriticalNodes(gameScreen.gridSystem.gridArray),
                gameScreen.enemySpawner3.pathfinder.findCriticalNodes(gameScreen.gridSystem.gridArray));
    }
}