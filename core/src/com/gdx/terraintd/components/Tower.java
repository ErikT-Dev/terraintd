package com.gdx.terraintd.components;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.gdx.terraintd.logic.GameConstants;

import java.util.List;
import java.util.Objects;

public class Tower implements Json.Serializable {
    public float hexCenterX;
    public float hexCenterY;
    private float hexRBig = GameConstants.HEX_R_BIG;
    public int xCoord;
    public int yCoord;
    public Vector2 position;
    private float bonusRange;
    public float timeSinceLastShot;
    public ShopItem shopItem;
    public boolean isSelected;
    public String terrain;
    public String effect;
    public String texturePath;
    public float textureSize;
    private static int nextId = 0;
    private int id;
    public String towerName;
    public String[] adjacentTerrains;
    public int level;
    public int upgCost;
    public int dmg;
    public float as;
    public float range;
    public float maxDps;
    public float totalCost;
    public int sellReward;

    public Tower(ShopItem shopItem, String[] adjacentTerrains, int xCoord, int yCoord, String terrain, float hexCenterX, float hexCenterY) {
        this.isSelected = false;
        this.level = 1;
        this.upgCost = shopItem.upgCost;
        this.adjacentTerrains = adjacentTerrains;
        this.shopItem = shopItem;
        this.towerName = shopItem.name;
        this.terrain = terrain;
        this.xCoord = xCoord;
        this.yCoord = yCoord;
        this.hexCenterX = hexCenterX;
        this.hexCenterY = hexCenterY;
        this.timeSinceLastShot = 0f;
        this.bonusRange = Objects.equals(terrain, "Hill") ? 2 * hexRBig : 0;
        this.effect = shopItem.effect;
        this.position = new Vector2(hexCenterX, hexCenterY);
        this.texturePath = shopItem.texturePath;
        this.textureSize = hexRBig * 0.8f;
        this.id = nextId++;
        this.dmg = shopItem.dmg;
        this.range = shopItem.range * hexRBig + bonusRange;
        this.as = shopItem.as;
        this.maxDps = Objects.equals(towerName, "Bastion") ? 5 * dmg / as : dmg / as;
        this.totalCost = shopItem.cost;
        this.sellReward = (int) (totalCost / 2);
    }

    public Tower() {
    }

    public void upgradeTower(ShopItem oldShopItem, ShopItem newShopItem) {
        level++;
        dmg += newShopItem.dmg - oldShopItem.dmg;
        as *= newShopItem.as / oldShopItem.as;
        range += (newShopItem.range - oldShopItem.range) * hexRBig;
        upgCost = newShopItem.upgCost;
        effect = newShopItem.effect;
        maxDps = Objects.equals(towerName, "Bastion") ? 5 * dmg / as : dmg / as;
        totalCost = newShopItem.cost;
        sellReward = (int) (totalCost / 2);
    }

    public void giveAdjacencyBonus(int bonusDmg, float bonusAs, float bonusRange) {
        dmg += bonusDmg;
        as *= (1 - bonusAs);
        range += bonusRange * hexRBig;
        maxDps = Objects.equals(towerName, "Bastion") ? 5 * dmg / as : dmg / as;
    }

    public void findTowerByCoordinates(int xCoord, int yCoord) {

    }

    public void update(float delta) {
        timeSinceLastShot += delta;
    }

    public boolean canFire() {
        return timeSinceLastShot >= as;
    }

    public void fire() {
        timeSinceLastShot = 0;
    }

    public Enemy findNearestEnemy(List<Enemy> enemies) {
        Enemy nearest = null;
        float nearestDistance = Float.MAX_VALUE;

        for (Enemy enemy : enemies) {
            float distance = Vector2.dst(position.x, position.y, enemy.getVisualScreenX(), enemy.getVisualScreenY());
            if (distance <= range && distance < nearestDistance) {
                nearest = enemy;
                nearestDistance = distance;
            }
        }
        return nearest;
    }

    public Projectile addProjectile(Enemy target) {
        return new Projectile(position.x, position.y, target, this);
    }

    @Override
    public void write(Json json) {
        json.writeValue("as", as);
        json.writeValue("range", range);
        json.writeValue("dmg", dmg);
        json.writeValue("xCoord", xCoord);
        json.writeValue("yCoord", yCoord);
        json.writeValue("terrain", terrain);
        json.writeValue("effect", effect);
        json.writeValue("position", position);
        json.writeValue("texturePath", texturePath);
        json.writeValue("textureSize", textureSize);
        json.writeValue("hexCenterX", hexCenterX);
        json.writeValue("hexCenterY", hexCenterY);
        json.writeValue("id", id);
        json.writeValue("nextId", nextId);
        json.writeValue("towerName", towerName);
        json.writeValue("level", level);
        json.writeValue("adjacentTerrains", adjacentTerrains);
        json.writeValue("upgCost", upgCost);
        json.writeValue("totalCost", totalCost);
        json.writeValue("sellReward", sellReward);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        as = jsonData.getFloat("as");
        range = jsonData.getFloat("range");
        dmg = jsonData.getInt("dmg");
        xCoord = jsonData.getInt("xCoord");
        yCoord = jsonData.getInt("yCoord");
        terrain = jsonData.getString("terrain");
        bonusRange = Objects.equals(terrain, "Hill") ? 2 * hexRBig : 0;
        effect = jsonData.getString("effect");
        texturePath = jsonData.getString("texturePath");
        textureSize = jsonData.getFloat("textureSize");
        hexCenterX = jsonData.getFloat("hexCenterX");
        hexCenterY = jsonData.getFloat("hexCenterY");
        position = new Vector2(hexCenterX, hexCenterY);
        id = jsonData.getInt("id");
        nextId = jsonData.getInt("nextId");
        towerName = jsonData.getString("towerName");
        level = jsonData.getInt("level");
        upgCost = jsonData.getInt("upgCost");
        totalCost = jsonData.getInt("totalCost");
        sellReward = jsonData.getInt("sellReward");
        JsonValue adjacentTerrainsJson = jsonData.get("adjacentTerrains");
        if (adjacentTerrainsJson != null && adjacentTerrainsJson.isArray()) {
            adjacentTerrains = new String[adjacentTerrainsJson.size];
            for (int i = 0; i < adjacentTerrainsJson.size; i++) {
                adjacentTerrains[i] = adjacentTerrainsJson.getString(i);
            }
        } else {
            adjacentTerrains = new String[0];
        }
    }

    public int getId() {
        return id;
    }
}