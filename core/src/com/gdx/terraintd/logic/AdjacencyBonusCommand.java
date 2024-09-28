package com.gdx.terraintd.logic;

import com.gdx.terraintd.components.Tower;
import com.gdx.terraintd.components.ShopItem;
import com.gdx.terraintd.screens_and_ui.GameScreen;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

public class AdjacencyBonusCommand implements Command {
    private final GameScreen gameScreen;
    private final ShopItem shopItem;
    private final int xCoord;
    private final int yCoord;
    private final List<TowerBonus> affectedTowers;
    private final String[] adjacentTerrains;

    private static class TowerBonus {
        Tower tower;
        int originalDmg;
        float originalAs;
        float originalRange;

        TowerBonus(Tower tower) {
            this.tower = tower;
            this.originalDmg = tower.dmg;
            this.originalAs = tower.as;
            this.originalRange = tower.range;
        }

        void applyBonus(int bonusDmg, float bonusAs, float bonusRange) {
            tower.giveAdjacencyBonus(bonusDmg, bonusAs, bonusRange);
        }

        void revertBonus() {
            tower.dmg = originalDmg;
            tower.as = originalAs;
            tower.range = originalRange;
        }
    }

    public AdjacencyBonusCommand(GameScreen gameScreen, ShopItem shopItem, String[] adjacentTerrains, int xCoord, int yCoord, String terrain) {
        this.gameScreen = gameScreen;
        this.shopItem = shopItem;
        this.adjacentTerrains = adjacentTerrains;
        this.xCoord = xCoord;
        this.yCoord = yCoord;
        this.affectedTowers = new ArrayList<>();
    }

    @Override
    public void execute() {
        Tower builtTower = null;
        List<Tower> adjacentTowers = new ArrayList<>();

        for (Tower tower : gameScreen.towers) {
            if (tower.xCoord == xCoord && tower.yCoord == yCoord) {
                builtTower = tower;
                break;
            }
        }

        if (builtTower == null) return;

        int[][] directions = {{1, 0}, {1, -1}, {0, -1}, {-1, 0}, {-1, 1}, {0, 1}};
        for (int[] dir : directions) {
            int adjX = xCoord + dir[0];
            int adjY = yCoord + dir[1];
            for (Tower tower : gameScreen.towers) {
                if (tower.xCoord == adjX && tower.yCoord == adjY) {
                    adjacentTowers.add(tower);
                    break;
                }
            }
        }

        String itemName = shopItem.name;
        if (itemName.equals("Berserker")) {
            if (Arrays.stream(adjacentTerrains)
                    .anyMatch(s -> s.contains("Berserker"))) {
                TowerBonus bonus = new TowerBonus(builtTower);
                bonus.applyBonus(0, 0.12f, 0);
                affectedTowers.add(bonus);
            }
            for (Tower adjTower : adjacentTowers) {
                if (adjTower.towerName.equals("Berserker")) {
                    if(countAdjacentBerserkers(adjTower)<2){
                        TowerBonus bonus = new TowerBonus(adjTower);
                        bonus.applyBonus(0, 0.12f, 0);
                        affectedTowers.add(bonus);
                    }
                }
            }
        } else if (itemName.equals("Ranger")) {
            if (Arrays.stream(adjacentTerrains)
                    .anyMatch(s -> s.contains("Hill"))) {
                TowerBonus bonus = new TowerBonus(builtTower);
                bonus.applyBonus(4, 0.1f, 0);
                affectedTowers.add(bonus);
            }
        } else if (itemName.equals("Marshal")) {
            for (Tower adjTower : adjacentTowers) {
                if (!Objects.equals(adjTower.towerName, "Marshal")) {
                    TowerBonus bonus = new TowerBonus(adjTower);
                    bonus.applyBonus(0, 0.16f, 0);
                    affectedTowers.add(bonus);
                }
            }
        }
        if (!itemName.equals("Marshal")) {
            for (Tower adjTower : adjacentTowers) {
                if (adjTower.towerName.equals("Marshal")) {
                    TowerBonus bonus = new TowerBonus(builtTower);
                    bonus.applyBonus(0, 0.16f, 0);
                    affectedTowers.add(bonus);
                }
            }
        }
    }

    private int countAdjacentBerserkers(Tower tower) {
        int count = 0;
        int[][] directions = {{1, 0}, {1, -1}, {0, -1}, {-1, 0}, {-1, 1}, {0, 1}};
        for (int[] dir : directions) {
            int adjX = tower.xCoord + dir[0];
            int adjY = tower.yCoord + dir[1];
            for (Tower otherTower : gameScreen.towers) {
                if (otherTower.xCoord == adjX && otherTower.yCoord == adjY && otherTower.towerName.equals("Berserker")) {
                    count++;
                    break;
                }
            }
        }
        return count;
    }

    @Override
    public void undo() {
        for (TowerBonus bonus : affectedTowers) {
            bonus.revertBonus();
        }
    }
}