package com.gdx.terraintd.logic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

import com.gdx.terraintd.components.Pathfinder;
import com.gdx.terraintd.components.GameMap;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameConstants {
    public static final float VERTICAL_PADDING_PERCENT = 0.13f;
    public static final int HORIZONTAL_CELLS = 20;
    public static final int VERTICAL_CELLS = 7;
    public static final int TOTAL_CELLS = HORIZONTAL_CELLS*VERTICAL_CELLS;
    public static final int SCREEN_WIDTH = Gdx.graphics.getWidth();
    public static final int SCREEN_HEIGHT = Gdx.graphics.getHeight();
    public static final float TOTAL_GRID_HEIGHT = SCREEN_HEIGHT*(1-2* VERTICAL_PADDING_PERCENT);
    public static final float HEX_R_SMALL = TOTAL_GRID_HEIGHT/(VERTICAL_CELLS*2+1);
    public static final float HEX_R_BIG =  (float) (HEX_R_SMALL *2 /Math.sqrt(3));
    public static final float TOTAL_GRID_WIDTH = HORIZONTAL_CELLS * HEX_R_BIG*1.5f;
    public static final float PADDING_Y = VERTICAL_PADDING_PERCENT*SCREEN_HEIGHT/2;
    public static final float PADDING_X = (SCREEN_WIDTH  - TOTAL_GRID_WIDTH)/2;
    public static final float VERTICAL_OFFSET = HEX_R_SMALL*5;
    public static final float HORIZONTAL_OFFSET = HEX_R_BIG*1;

    public static final List<GameMap> GAME_MAPS = Arrays.asList(
            new GameMap("Mystical Woods", 42, 14, 7, 5, 5, 2, 0, -1,0, 5, 7, -5, 19, -5),
            new GameMap("Archipelago", 25, 35, 7, 5, 5, 2, 4, 3,19, -11, 14, -2, 0, -1),
            new GameMap("Mountain Forest", 40, 10, 3, 11, 4, 3, 14, -2,19, -11,0, 5,  7, -5),
            new GameMap("Coastal Front", 16, 24, 14, 5, 6,1, 0, 5,19, -10,7, -5,  10, 0)
    );
    public static GameMap findGameMapByName(String mapName) {
        for (GameMap map : GAME_MAPS) {
            if (map.getName().equals(mapName)) {
                return map;
            }
        }
        return null;
    }

    public static BitmapFont GAME_FONT;
    private static FreeTypeFontGenerator fontGenerator;
    private static Map<String, BitmapFont> fontCache = new HashMap<>();

    private static Map<String, List<Map<String, String>>> gameData;

    public static void loadGameData() {
        gameData = ExcelDataReader.readExcelFile("gameData.xlsx");
    }

    public static void initializeFonts() {
        fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/GaMaamli-Regular.ttf"));
    }

    public static List<Map<String, String>> getTowerData() {
        return gameData.get("towers");
    }

    public static Map<String, String> getTowerById(String id) {
        List<Map<String, String>> towers = getTowerData();
        for (Map<String, String> tower : towers) {
            if (tower.get("id").equals(id)) {
                return tower;
            }
        }
        return null;
    }

    public static List<Map<String, String>> getSpawnerData() {
        return gameData.get("spawners");
    }

    public static Map<String, String> getSpawnersByWaveNumber(String waveNumber) {
        List<Map<String, String>> spawners = getSpawnerData();
        for (Map<String, String> spawner : spawners) {
            String excelWaveNumber = spawner.get("wave");
            if (compareWaveNumbers(excelWaveNumber, waveNumber)) {
                return spawner;
            }
        }
        return null;
    }

    private static boolean compareWaveNumbers(String excelWaveNumber, String inputWaveNumber) {
        try {
            float excelWave = Float.parseFloat(excelWaveNumber);
            float inputWave = Float.parseFloat(inputWaveNumber);
            return Math.floor(excelWave) == Math.floor(inputWave);
        } catch (NumberFormatException e) {
            return excelWaveNumber.equals(inputWaveNumber);
        }
    }

    public static List<Map<String, String>> getEnemiesData() {
        return gameData.get("enemies");
    }

    public static Map<String, String> getEnemyById(String id) {
        List<Map<String, String>> enemies = getEnemiesData();
        for (Map<String, String> enemy : enemies) {
            if (enemy.get("id").equals(id)) {
                return enemy;
            }
        }
        return null;
    }

    public static BitmapFont getFont(int size, Color color) {
        String key = size + "_" + color.toString();
        if (!fontCache.containsKey(key)) {
            FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
            parameter.size = size;
            parameter.color = color;
            parameter.borderWidth = 1;
            parameter.borderColor = Color.BLACK;
            BitmapFont font = fontGenerator.generateFont(parameter);
            fontCache.put(key, font);
        }
        return fontCache.get(key);
    }

    public static void dispose() {
        if (fontGenerator != null) {
            fontGenerator.dispose();
        }
        for (BitmapFont font : fontCache.values()) {
            font.dispose();
        }
        fontCache.clear();
        gameData = null;
    }

    private GameConstants() {}
}