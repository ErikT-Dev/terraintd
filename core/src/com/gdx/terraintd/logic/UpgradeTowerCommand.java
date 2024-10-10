package com.gdx.terraintd.logic;

import com.gdx.terraintd.components.Tower;
import com.gdx.terraintd.components.ShopItem;
import com.gdx.terraintd.screens_and_ui.GameScreen;

public class UpgradeTowerCommand implements Command {
    private final GameScreen gameScreen;
    private final Tower tower;
    private final int previousLevel;
    private final int previousUpgCost;
    private final int previousDmg;
    private final float previousAs;
    private final float previousRange;
    private final String previousEffect;
    private final int upgradeCost;

    public UpgradeTowerCommand(GameScreen gameScreen, Tower tower) {
        this.gameScreen = gameScreen;
        this.tower = tower;
        this.upgradeCost = tower.upgCost;
        this.previousLevel = tower.level;
        this.previousUpgCost = tower.upgCost;
        this.previousDmg = tower.dmg;
        this.previousAs = tower.as;
        this.previousRange = tower.range;
        this.previousEffect = tower.effect;
    }

    @Override
    public void execute() {
        if (gameScreen.statsManager.canAfford(upgradeCost) && upgradeCost != 0) {
            gameScreen.statsManager.spend(upgradeCost);
            String oldItemId = tower.towerName.toLowerCase() + tower.level;
            ShopItem oldTower = gameScreen.shop.createShopItemFromTowerData(oldItemId);
            int newLevel = tower.level + 1;
            String newItemId = tower.towerName.toLowerCase() + newLevel;
            ShopItem newTower = gameScreen.shop.createShopItemFromTowerData(newItemId);
            tower.upgradeTower(oldTower, newTower);
        }
    }

    @Override
    public void undo() {
        if (tower.level > previousLevel) {
            gameScreen.statsManager.gainGold(upgradeCost);
            tower.level = previousLevel;
            tower.upgCost = previousUpgCost;
            tower.dmg = previousDmg;
            tower.as = previousAs;
            tower.range = previousRange;
            tower.effect = previousEffect;
        }
    }
}