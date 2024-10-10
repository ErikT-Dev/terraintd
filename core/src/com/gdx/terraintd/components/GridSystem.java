package com.gdx.terraintd.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.gdx.terraintd.logic.GameConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.function.Supplier;

public class GridSystem {
    public final int horizontalCells = GameConstants.HORIZONTAL_CELLS;
    public final int verticalCells = GameConstants.VERTICAL_CELLS;
    public final int totalCells = GameConstants.TOTAL_CELLS;
    public final float hexRBig = GameConstants.HEX_R_BIG;
    private final float paddingBottom = GameConstants.PADDING_X + GameConstants.HORIZONTAL_OFFSET;
    private final float paddingLeft = GameConstants.PADDING_Y + GameConstants.VERTICAL_OFFSET;
    public String[][] gridArray = new String[totalCells][3];
    private Map<String, Texture> cellTextures;
    public GameMap map;
    public Pathfinder pathfinder1;
    public Pathfinder pathfinder2;
    public Pathfinder pathfinder3;

    public GridSystem(GameMap map, String[][] loadedGrid) {
        if (loadedGrid != null) {
            this.gridArray = loadedGrid;
        } else {
            this.map = map;
            this.pathfinder1 = new Pathfinder(map.spawner1X, map.spawner1Y, map.endX, map.endY);
            this.pathfinder2 = new Pathfinder(map.spawner2X, map.spawner2Y, map.endX, map.endY);
            this.pathfinder3 = new Pathfinder(map.spawner3X, map.spawner3Y, map.endX, map.endY);
            initializeTerrain(map);
        }
        initializeCellTextures();
    }

    private void initializeCellTextures() {
        cellTextures = new HashMap<>();
        cellTextures.put("Start", new Texture(Gdx.files.internal("images/castle.png")));
        cellTextures.put("End", new Texture(Gdx.files.internal("images/castle2.png")));
    }

    private void initializeGridArray() {
        int index = 0;
        for (int x = 0; x < horizontalCells; x++) {
            for (int y = 0; y < verticalCells; y++) {
                int offset = 1 + x / 2 + x % 2;
                gridArray[index][0] = String.valueOf(x);
                gridArray[index][1] = String.valueOf(y - offset);
                gridArray[index][2] = "Grass";
                index++;
            }
        }

        gridArray[getCellIndexNumber(map.spawner1X, map.spawner1Y)][2] = "Start";
        gridArray[getCellIndexNumber(map.spawner2X, map.spawner2Y)][2] = "Start";
        gridArray[getCellIndexNumber(map.spawner3X, map.spawner3Y)][2] = "Start";
        gridArray[getCellIndexNumber(map.endX, map.endY)][2] = "End";
    }

    public void changeCellValue(int index, String newValue) {
        this.gridArray[index][2] = newValue;
    }

    public String getCellValue(int xCoord, int yCoord) {
        for (String[] cell : gridArray) {
            if (Integer.parseInt(cell[0]) == xCoord && Integer.parseInt(cell[1]) == yCoord) {
                return cell[2];
            }
        }
        return null;
    }

    public int getCellIndexNumber(int xCoord, int yCoord) {
        for (int i = 0; i < totalCells; i++) {
            if (Integer.parseInt(gridArray[i][0]) == xCoord && Integer.parseInt(gridArray[i][1]) == yCoord) {
                return i;
            }
        }
        return -1;
    }

    public String[] getAdjacentCellValues(int x, int y) {
        List<String> adjacentValues = new ArrayList<>();

        int[][] directions = {{1, 0}, {1, -1}, {0, -1}, {-1, 0}, {-1, 1}, {0, 1}};

        for (int[] dir : directions) {
            int newX = x + dir[0];
            int newY = y + dir[1];
            String cellValue = getCellValue(newX, newY);
            if (cellValue != null) {
                adjacentValues.add(cellValue);
            }
        }

        return adjacentValues.toArray(new String[0]);
    }


    private void initializeTerrain(GameMap map) {
        int maxAttempts = 1000;
        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            initializeGridArray();
            if (attemptInitializeTerrain(map)) {
                System.out.println("Terrain initialized successfully on attempt " + (attempt + 1));
                return;
            }
        }
        throw new RuntimeException("Failed to initialize terrain after " + maxAttempts + " attempts");
    }

    private boolean attemptInitializeTerrain(GameMap map) {
        Supplier<Integer> getRandomCell = getIntegerSupplier();
        for (int i = 0; i < map.forests; i++) {
            changeCellValue(getRandomCell.get(), "Forest");
        }
        for (int i = 0; i < map.seas; i++) {
            changeCellValue(getRandomCell.get(), "Sea");
        }
        for (int i = 0; i < map.sands; i++) {
            changeCellValue(getRandomCell.get(), "Sand");
        }
        for (int i = 0; i < map.hills; i++) {
            changeCellValue(getRandomCell.get(), "Hill");
        }

        List<int[]> path1 = pathfinder1.findPath(gridArray);
        List<int[]> path2 = pathfinder2.findPath(gridArray);
        List<int[]> path3 = pathfinder3.findPath(gridArray);
        return path1 != null && path2 != null && path3 != null;
    }

    private Supplier<Integer> getIntegerSupplier() {
        List<Integer> availableCells = new ArrayList<>();
        for (int i = 0; i < totalCells; i++) {
            availableCells.add(i);
        }
        availableCells.remove(Integer.valueOf(getCellIndexNumber(map.spawner1X, map.spawner1Y)));
        availableCells.remove(Integer.valueOf(getCellIndexNumber(map.spawner2X, map.spawner2Y)));
        availableCells.remove(Integer.valueOf(getCellIndexNumber(map.spawner3X, map.spawner3Y)));
        availableCells.remove(Integer.valueOf(getCellIndexNumber(map.endX, map.endY)));

        Supplier<Integer> getRandomCell = () -> {
            if (availableCells.isEmpty()) {
                throw new IllegalStateException("No more available cells");
            }
            int index = new Random().nextInt(availableCells.size());
            return availableCells.remove(index);
        };
        return getRandomCell;
    }

    public Vector2 axialToPixel(int q, int r) {
        float x = hexRBig * (3f / 2f * q);
        float y = hexRBig * (float) (Math.sqrt(3) * (r + q / 2f));
        return new Vector2(
                paddingBottom + x,
                paddingLeft + y
        );
    }

    public Vector2 pixelToAxial(float px, float py) {
        px = (px - paddingBottom) / hexRBig;
        py = (py - paddingLeft) / hexRBig;
        float q = (2f / 3f * px);
        float r = (float) (-1f / 3f * px + Math.sqrt(3) / 3 * py);
        return hexRound(q, r);
    }

    private Vector2 hexRound(float q, float r) {
        float s = -q - r;
        int qi = Math.round(q);
        int ri = Math.round(r);
        int si = Math.round(s);
        float q_diff = Math.abs(qi - q);
        float r_diff = Math.abs(ri - r);
        float s_diff = Math.abs(si - s);
        if (q_diff > r_diff && q_diff > s_diff) {
            qi = -ri - si;
        } else if (r_diff > s_diff) {
            ri = -qi - si;
        }
        return new Vector2(qi, ri);
    }

    public void updateCellValue(float screenX, float screenY, String newValue, boolean isAddingATower) {
        Vector2 axialCoords = pixelToAxial(screenX, screenY);
        int q = (int) axialCoords.x;
        int r = (int) axialCoords.y;
        for (String[] cell : gridArray) {
            if (Integer.parseInt(cell[0]) == q && Integer.parseInt(cell[1]) == r) {
                if (isAddingATower) {
                    cell[2] += " " + newValue;
                } else {
                    cell[2] = newValue;
                }
            }
        }
    }

    public String[] getGridCell(float screenX, float screenY) {
        Vector2 axialCoords = pixelToAxial(screenX, screenY);
        int x = (int) axialCoords.x;
        int y = (int) axialCoords.y;
        for (String[] cell : gridArray) {
            if (Integer.parseInt(cell[0]) == x && Integer.parseInt(cell[1]) == y) {
                return cell;
            }
        }
        return null;
    }

    public Vector2 getHexCenter(int gridX, int gridY) {
        return axialToPixel(gridX, gridY);
    }

    public void cleanUpGrid() {
        for (String[] cell : gridArray) {
            if (cell[2].contains("Critical")) {
                cell[2] = cell[2].replace(" Critical", "");
            }
        }
    }

    public void displayShortestPath(List<int[]> path) {
        if (path == null || path.isEmpty()) {
            return;
        }
        for (int[] node : path) {
            int x = node[0];
            int y = node[1];
            for (String[] cell : gridArray) {
                if (Integer.parseInt(cell[0]) == x && Integer.parseInt(cell[1]) == y) {
                    if (Objects.equals(cell[2], "Grass")) {
                        cell[2] = "Path";
                    }
                    break;
                }
            }
        }
    }

    public void displayCriticalCells(List<int[]> path1, List<int[]> path2, List<int[]> path3) {
        List<int[]> combinedPath = new ArrayList<>();
        if (path1 != null) combinedPath.addAll(path1);
        if (path2 != null) combinedPath.addAll(path2);
        if (path3 != null) combinedPath.addAll(path3);

        if (combinedPath.isEmpty()) {
            return;
        }

        for (int[] node : combinedPath) {
            int x = node[0];
            int y = node[1];
            for (String[] cell : gridArray) {
                if (!cell[2].contains("Critical")) {
                    if (Integer.parseInt(cell[0]) == x && Integer.parseInt(cell[1]) == y) {
                        cell[2] += " Critical";
                        break;
                    }
                }
            }
        }
    }

    public Map<String, Texture> getCellTextures() {
        return cellTextures;
    }

    public String[][] getGridArray() {
        return gridArray;
    }
}