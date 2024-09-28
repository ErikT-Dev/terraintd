package com.gdx.terraintd.components;

public class ShopItem {
    public String name;
    public String id;
    public int cost;
    public int upgCost;
    public int dmg;
    public float range;
    public float as;
    public String effect;
    public String texturePath;

    public ShopItem(String id, String name, int cost, int upgCost, int dmg, float as, float range, String effect, String texturePath) {
        this.name = name;
        this.id = id;
        this.cost = cost;
        this.upgCost = upgCost;
        this.dmg = dmg;
        this.as = as;
        this.range = range;
        this.effect = effect;
        this.texturePath =texturePath;
    }

    public String getName() {
        return name;
    }
}