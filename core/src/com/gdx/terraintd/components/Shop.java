package com.gdx.terraintd.components;

import com.badlogic.gdx.Gdx;
import com.gdx.terraintd.logic.GameConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Shop {
    private final List<ShopItem> items;

    public Shop() {
        items = new ArrayList<>();
        initializeItems();
    }

    private void initializeItems() {
        items.add(new ShopItem("forest", "Forest", 2, 0, 0, 0, 0, null, null));
        items.add(new ShopItem("grass", "Grass", 2, 0, 0, 0, 0, null, null));
        items.add(new ShopItem("sand", "Sand", 0, 0, 0, 0, 0, null, null));

        String[] towerIds = {"woodsman1", "spearman1", "alchemist1", "berserker1", "ranger1", "bastion1", "marshal1"};

        for (String towerId : towerIds) {
            ShopItem towerItem = createShopItemFromTowerData(towerId);
            if (towerItem != null) {
                items.add(towerItem);
            }
        }
    }

    public List<ShopItem> getItems() {
        return items;
    }

    public ShopItem getItemByName(String name) {
        for (ShopItem item : items) {
            if (item.getName().equals(name)) {
                return item;
            }
        }
        return null;
    }

    public ShopItem createShopItemFromTowerData(String towerId) {
        Map<String, String> towerData = GameConstants.getTowerById(towerId);
        if (towerData == null) {
            Gdx.app.error("Shop", "Tower data not found for ID: " + towerId);
            return null;
        }

        return new ShopItem(
                towerData.get("id"),
                towerData.get("name"),
                (int) parseFloatSafely(towerData.get("totCost"), 0f),
                (int) parseFloatSafely(towerData.get("upgCost"), 0f),
                (int) parseFloatSafely(towerData.get("dmg"), 0f),
                parseFloatSafely(towerData.get("as"), 0f),
                parseFloatSafely(towerData.get("range"), 0f),
                towerData.get("effect"),
                "units/" + towerData.get("name")+ ".png"
        );
    }

    private float parseFloatSafely(String value, float defaultValue) {
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Float.parseFloat(value);
        } catch (NumberFormatException e) {
            Gdx.app.error("Shop", "Failed to parse float: " + value, e);
            return defaultValue;
        }
    }
}