package com.gdx.terraintd.logic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.gdx.terraintd.components.ShopItem;
import com.gdx.terraintd.components.Tower;
import com.gdx.terraintd.screens_and_ui.DetailsWindow;
import com.gdx.terraintd.screens_and_ui.GameScreen;

import java.util.Arrays;
import java.util.Objects;
import java.util.Stack;

public class InputHandler {
    private final GameScreen gameScreen;
    public Tower selectedTower;
    private DetailsWindow detailsWindow;
    private final Stack<Command> commandHistory;

    public InputHandler(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
        this.commandHistory = new Stack<>();
        updateUndoButtonState();
    }

    public void clearCommandHistory() {
        commandHistory.clear();
        updateUndoButtonState();
    }

    public void handleInput() {
        if (Gdx.input.justTouched()) {
            Vector3 touchPos = new Vector3(Gdx.input.getX() - 20, Gdx.input.getY() - 100, 0);
            gameScreen.camera.unproject(touchPos);
            handleClick(touchPos);
        }
    }

    public void handleClick(Vector3 touchPos) {
        int topBarHeight = gameScreen.gameInitializer.topBarHeight;
        float screenX = touchPos.x;
        float screenY = touchPos.y - topBarHeight;
        ShopItem selectedShopItem = gameScreen.uiManager.selectedShopItem;
        String[] gridCell = gameScreen.gridSystem.getGridCell(screenX, screenY);
        int xCoord;
        int yCoord;
        if (gridCell == null) {
            return;
        } else {
            xCoord = Integer.parseInt(gridCell[0]);
            yCoord = Integer.parseInt(gridCell[1]);
            Vector2 hexCenter = gameScreen.gridSystem.getHexCenter(xCoord, yCoord);
            float hexCenterX = hexCenter.x;
            float hexCenterY = hexCenter.y;
            String[] adjacentTerrains = gameScreen.gridSystem.getAdjacentCellValues(xCoord, yCoord);
            System.out.println("X = " + xCoord + "; Y = " + yCoord + "; terrain - " + gridCell[2]);

            if (gameScreen.uiManager.upgradeMode) {
                if (gridCell[2].contains("Tower")) {
                    for (Tower tower : gameScreen.towers) {
                        if (tower.xCoord == xCoord && tower.yCoord == yCoord) {
                            Command upgradeCommand = new UpgradeTowerCommand(gameScreen, tower);
                            upgradeCommand.execute();
                            commandHistory.push(upgradeCommand);
                            updateUndoButtonState();
                            return;
                        }
                    }
                }
                return;
            }
            if (gameScreen.uiManager.sellMode) {
                if (gridCell[2].contains("Tower")) {
                    for (Tower tower : gameScreen.towers) {
                        if (tower.xCoord == xCoord && tower.yCoord == yCoord) {
                            Command sellCommand = new SellTowerCommand(gameScreen, tower);
                            sellCommand.execute();
                            commandHistory.push(sellCommand);
                            updateUndoButtonState();
                            return;
                        }
                    }
                }
                return;
            }
            if (gridCell[2].contains("Tower")) {
                for (Tower tower : gameScreen.towers) {
                    if (tower.xCoord == xCoord && tower.yCoord == yCoord) {
                        if (detailsWindow != null) {
                            detailsWindow.close();
                            detailsWindow = null;
                        }
                        if (selectedTower != tower) {
                            if (selectedTower != null) {
                                selectedTower.isSelected = false;
                            }
                            selectedTower = tower;
                            tower.isSelected = true;
                            detailsWindow = new DetailsWindow(gameScreen.uiManager.stage, tower, screenX, gameScreen);
                            detailsWindow.open();
                        } else {
                            selectedTower = null;
                        }
                        return;
                    }
                }
            } else {
                if (detailsWindow != null) {
                    detailsWindow.close();
                    detailsWindow = null;
                }
                if (selectedTower != null) {
                    selectedTower.isSelected = false;
                    selectedTower = null;
                }
            }
            if (selectedShopItem != null) {
                if (gameScreen.uiManager.buildMode) {
                    commitCommands(screenX, screenY, gameScreen.uiManager.selectedShopItem, adjacentTerrains, xCoord, yCoord, gridCell[2], hexCenterX, hexCenterY);
                }
            }
        }
    }

    public void commitCommands(float screenX, float screenY, ShopItem shopItem, String[] adjacentTerrains, int xCoord, int yCoord, String terrain, float hexCenterX, float hexCenterY) {
        Command command;
        if (!(Objects.equals(terrain, "Grass") || Objects.equals(terrain, "Grass Critical") || Objects.equals(terrain, "Forest") || Objects.equals(terrain, "Hill") || Objects.equals(terrain, "Sea") || Objects.equals(terrain, "Sand")|| Objects.equals(terrain, "Sand Critical"))) {
            return;
        }
        if(Objects.equals(terrain, "Grass Critical") && !Objects.equals(shopItem.name, "Sand") ){
            return;
        }
        if(Objects.equals(terrain, "Sand Critical") && !Objects.equals(shopItem.name, "Grass") ){
            return;
        }
        if (shopItem.name.equals("Forest") || shopItem.name.equals("Grass") || shopItem.name.equals("Sand")) {
            command = new ChangeTerrainCommand(gameScreen, shopItem, xCoord, yCoord, terrain, screenX, screenY);
        } else {
            if ((Objects.equals(terrain, "Sea") || Objects.equals(terrain, "Sand"))) {
                return;
            }
            if (Objects.equals(shopItem.name, "Woodsman") && Arrays.stream(adjacentTerrains)
                    .noneMatch(s -> s.contains("Forest"))) {
                return;
            }
            command = new BuildTowerCommand(gameScreen, shopItem, adjacentTerrains, xCoord, yCoord, terrain, hexCenterX, hexCenterY);
        }
        command.execute();

        if (command instanceof BuildTowerCommand && ((BuildTowerCommand) command).successfullyCompleted) {
            commandHistory.push(command);

            Command adjacencyCommand = new AdjacencyBonusCommand(gameScreen, shopItem, adjacentTerrains, xCoord, yCoord, terrain);
            adjacencyCommand.execute();
            commandHistory.push(adjacencyCommand);
        } else if (command instanceof ChangeTerrainCommand && ((ChangeTerrainCommand) command).successfullyCompleted) {
            commandHistory.push(command);
        }
        updateUndoButtonState();
    }

    public void undo() {
        if (!commandHistory.isEmpty()) {
            Command lastCommand = commandHistory.pop();
            lastCommand.undo();

            if (lastCommand instanceof AdjacencyBonusCommand && !commandHistory.isEmpty()) {
                Command buildCommand = commandHistory.pop();
                buildCommand.undo();
            }
        }
        updateUndoButtonState();
    }

    private void updateUndoButtonState() {
        gameScreen.uiManager.updateUndoButton(commandHistory.isEmpty());
    }
}