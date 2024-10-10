package com.gdx.terraintd.state_and_managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.gdx.terraintd.components.Enemy;
import com.gdx.terraintd.components.GridSystem;
import com.gdx.terraintd.components.Projectile;
import com.gdx.terraintd.components.Tower;
import com.gdx.terraintd.logic.GameConstants;
import com.gdx.terraintd.screens_and_ui.GameScreen;

import java.util.HashMap;
import java.util.Map;

public class GameRenderer {
    private final GameScreen gameScreen;
    private final SpriteBatch batch;
    private SpriteBatch textBatch;
    private final ShapeRenderer shapeRenderer;
    private final Map<String, Texture> enemyTextures;
    private final Map<String, Texture> towerTextures;
    private static final float BORDER_THICKNESS = 5f;
    private final Color towerRangeFillColor;
    private final Color towerRangeBorderColor;
    private final ShaderProgram maskShader;
    private final Texture hexagonMask;
    private final Map<String, Color> cellColors;
    private final BitmapFont levelFont = GameConstants.getFont(45, Color.WHITE);
    private final BitmapFont costFont = GameConstants.getFont(45, Color.CHARTREUSE);
    private final BitmapFont sellFont = GameConstants.getFont(50, Color.YELLOW);
    private final Map<Vector2, Texture> terrainTextureAssignments;

    public GameRenderer(GameScreen gameScreen) {
        this.batch = new SpriteBatch();
        this.textBatch = new SpriteBatch();
        this.shapeRenderer = new ShapeRenderer();
        this.shapeRenderer.setAutoShapeType(true);
        this.enemyTextures = new HashMap<>();
        this.towerTextures = new HashMap<>();
        this.gameScreen = gameScreen;
        this.maskShader = createMaskShader();
        this.hexagonMask = createHexagonMask();
        LibGDXColorManager colorManager = LibGDXColorManager.getInstance();
        towerRangeFillColor = colorManager.towerRangeFillColor;
        towerRangeBorderColor = colorManager.towerRangeBorderColor;
        this.cellColors = initializeCellColors();
        batch.setShader(maskShader);
        this.terrainTextureAssignments = new HashMap<>();
    }

    public void render() {
        renderGrid();
        renderEnemies();
        if (gameScreen.towers != null) {
            renderTowers();
        }
        renderProjectiles();
        renderTowerLevels();
    }

    private Map<String, Color> initializeCellColors() {
        LibGDXColorManager colorManager = LibGDXColorManager.getInstance();
        Map<String, Color> colors = new HashMap<>();

        colors.put("Grass", colorManager.grassTileColor);
        colors.put("Forest", colorManager.forestTileColor);
        colors.put("Hill", colorManager.hillTileColor);
        colors.put("Sea", colorManager.seaTileColor);
        colors.put("Sand", colorManager.sandTileColor);
        colors.put("Start", colorManager.startTileColor);
        colors.put("End", colorManager.endTileColor);

        colors.put("Critical", new Color(0.58f, 0.201f, 0.112f, 1));
        colors.put("Path", new Color(0.478f, 0.404f, 0.322f, 1));

        Color grassTowerColor = colorManager.towerGrassColor;
        Color hillTowerColor = colorManager.towerHillColor;
        Color forestTowerColor = colorManager.towerGrassColor;

        String[] terrainTypes = {"Grass", "Hill", "Forest"};
        String[] towerTypes = {"Woodsman", "Spearman", "Alchemist", "Berserker", "Ranger", "Bastion", "Marshal"};

        for (String terrain : terrainTypes) {
            Color towerColor = terrain.equals("Hill") ? hillTowerColor :
                    terrain.equals("Forest") ? forestTowerColor :
                            grassTowerColor;

            for (String towerType : towerTypes) {
                String key = terrain + " Tower " + towerType;
                colors.put(key, towerColor);
            }
        }

        return colors;
    }

    private ShaderProgram createMaskShader() {
        String vertexShader = "attribute vec4 a_position;\n" +
                "attribute vec4 a_color;\n" +
                "attribute vec2 a_texCoord0;\n" +
                "uniform mat4 u_projTrans;\n" +
                "varying vec4 v_color;\n" +
                "varying vec2 v_texCoords;\n" +
                "void main() {\n" +
                "    v_color = a_color;\n" +
                "    v_texCoords = a_texCoord0;\n" +
                "    gl_Position = u_projTrans * a_position;\n" +
                "}";

        String fragmentShader = "#ifdef GL_ES\n" +
                "precision mediump float;\n" +
                "#endif\n" +
                "varying vec4 v_color;\n" +
                "varying vec2 v_texCoords;\n" +
                "uniform sampler2D u_texture;\n" +
                "uniform sampler2D u_mask;\n" +
                "void main() {\n" +
                "    vec4 texColor = texture2D(u_texture, v_texCoords);\n" +
                "    vec4 maskColor = texture2D(u_mask, v_texCoords);\n" +
                "    gl_FragColor = v_color * texColor * maskColor.r;\n" +
                "}";

        ShaderProgram shader = new ShaderProgram(vertexShader, fragmentShader);
        if (!shader.isCompiled()) {
            Gdx.app.error("GameRenderer", "Shader compilation failed:\n" + shader.getLog());
        }
        return shader;
    }

    private Texture createHexagonMask() {
        int size = 128;
        Pixmap pixmap = new Pixmap(size, size, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);

        float centerX = size / 2f;
        float centerY = size / 2f;
        float radius = size / 2f - 1;

        float[] vertices = new float[12];
        for (int i = 0; i < 6; i++) {
            float angle = (float) (2 * Math.PI * i / 6);
            vertices[i * 2] = centerX + radius * (float) Math.cos(angle);
            vertices[i * 2 + 1] = centerY + radius * (float) Math.sin(angle);
        }

        for (int i = 0; i < 6; i++) {
            int nextI = (i + 1) % 6;
            pixmap.fillTriangle(
                    (int) centerX, (int) centerY,
                    (int) vertices[i * 2], (int) vertices[i * 2 + 1],
                    (int) vertices[nextI * 2], (int) vertices[nextI * 2 + 1]
            );
        }

        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    private void renderGrid() {
        GridSystem gridSystem = gameScreen.gridSystem;

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (String[] cell : gridSystem.getGridArray()) {
            int x = Integer.parseInt(cell[0]);
            int y = Integer.parseInt(cell[1]);
            Vector2 center = gridSystem.axialToPixel(x, y);
            String cellState = cell[2].replace(" Critical", "");
            String terrain = cellState.split(" ")[0];
            Color fillColor = cellColors.getOrDefault(cellState, Color.WHITE);
            drawColoredHexagon(center.x, center.y, GameConstants.HEX_R_BIG, fillColor);
        }
        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        for (String[] cell : gridSystem.getGridArray()) {
            int x = Integer.parseInt(cell[0]);
            int y = Integer.parseInt(cell[1]);
            Vector2 center = gridSystem.axialToPixel(x, y);
            drawHexagonBorder(center.x, center.y, GameConstants.HEX_R_BIG, Color.BLACK);
        }
        shapeRenderer.end();

        batch.begin();
        maskShader.setUniformi("u_mask", 1);
        hexagonMask.bind(1);
        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);

        for (String[] cell : gridSystem.getGridArray()) {
            int x = Integer.parseInt(cell[0]);
            int y = Integer.parseInt(cell[1]);
            Vector2 center = gridSystem.axialToPixel(x, y);
            String cellState = cell[2].replace(" Critical", "");

            Texture texture;
            Vector2 gridPosition = new Vector2(x, y);

            if (isRandomTextureTerrain(cellState)) {
                if (!terrainTextureAssignments.containsKey(gridPosition)) {
                    terrainTextureAssignments.put(gridPosition, gameScreen.textureManager.getRandomTexture(cellState));
                }
                texture = terrainTextureAssignments.get(gridPosition);
            } else {
                texture = gridSystem.getCellTextures().get(cellState);
            }

            if (texture != null) {
                float textureSize = GameConstants.HEX_R_BIG * 1.6f;
                batch.draw(texture,
                        center.x - textureSize / 2, center.y - textureSize / 2,
                        textureSize, textureSize);
            }
        }
        batch.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (String[] cell : gridSystem.getGridArray()) {
            if (cell[2].contains("Critical")) {
                int x = Integer.parseInt(cell[0]);
                int y = Integer.parseInt(cell[1]);
                Vector2 center = gridSystem.axialToPixel(x, y);
                drawCriticalIndicator(center.x, center.y, GameConstants.HEX_R_BIG);
            }
        }
        shapeRenderer.end();
    }

    private boolean isRandomTextureTerrain(String cellState) {
        return cellState.equals("Grass") || cellState.equals("Hill") ||
                cellState.equals("Sea") || cellState.equals("Sand") ||
                cellState.equals("Forest");
    }

    public void updateTerrainTextureAssignment(Vector2 gridPosition, Texture newTexture) {
        terrainTextureAssignments.put(gridPosition, newTexture);
    }

    public Texture getTerrainTextureAssignment(Vector2 gridPosition) {
        return terrainTextureAssignments.get(gridPosition);
    }

    public void removeTerrainTextureAssignment(Vector2 gridPosition) {
        terrainTextureAssignments.remove(gridPosition);
    }

    public void resetTerrainTextures() {
        terrainTextureAssignments.clear();
    }

    private void drawColoredHexagon(float centerX, float centerY, float size, Color fillColor) {
        shapeRenderer.setColor(fillColor);
        for (int i = 0; i < 6; i++) {
            float angle = (float) (60 * i * Math.PI / 180);
            float nextAngle = (float) (60 * (i + 1) * Math.PI / 180);
            shapeRenderer.triangle(
                    centerX, centerY,
                    centerX + size * (float) Math.cos(angle), centerY + size * (float) Math.sin(angle),
                    centerX + size * (float) Math.cos(nextAngle), centerY + size * (float) Math.sin(nextAngle)
            );
        }
    }

    private void drawHexagonBorder(float centerX, float centerY, float size, Color borderColor) {
        shapeRenderer.setColor(borderColor);
        for (int i = 0; i < 6; i++) {
            float angle = (float) (60 * i * Math.PI / 180);
            float nextAngle = (float) (60 * (i + 1) * Math.PI / 180);
            float x1 = centerX + size * (float) Math.cos(angle);
            float y1 = centerY + size * (float) Math.sin(angle);
            float x2 = centerX + size * (float) Math.cos(nextAngle);
            float y2 = centerY + size * (float) Math.sin(nextAngle);
            shapeRenderer.line(x1, y1, x2, y2);
        }
    }

    private void drawCriticalIndicator(float centerX, float centerY, float size) {
        shapeRenderer.setColor(Color.RED);
        float circleRadius = size * 0.25f;
        shapeRenderer.circle(centerX, centerY, circleRadius);
    }

    private void renderEnemy(Enemy enemy) {
        Texture texture = enemyTextures.computeIfAbsent(enemy.texturePath, Texture::new);
        Vector2 drawPos = enemy.getDrawPosition();

        Color tintColor = (enemy.slowFactor < 1f) ? new Color(0.5f, 1f, 0.5f, 1f) : Color.WHITE;
        batch.setColor(tintColor);

        batch.draw(texture,
                drawPos.x - enemy.textureSize / 2,
                drawPos.y - enemy.textureSize / 2,
                enemy.textureSize * 1.3f,
                enemy.textureSize * 1.3f);

        batch.setColor(Color.WHITE);
    }

    private void renderEnemies() {
        batch.begin();
        for (Enemy enemy : gameScreen.enemies) {
            renderEnemy(enemy);
        }
        batch.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (Enemy enemy : gameScreen.enemies) {
            renderEnemyHealthBar(enemy);
        }
        shapeRenderer.end();
    }

    private void renderEnemyHealthBar(Enemy enemy) {
        Vector2 drawPos = enemy.getDrawPosition();
        float healthBarWidth = enemy.textureSize * 1.2f;
        float healthBarHeight = enemy.textureSize * 0.2f;
        float healthBarY = drawPos.y - enemy.textureSize * 0.9f;

        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(drawPos.x - healthBarWidth / 2, healthBarY, healthBarWidth, healthBarHeight);

        shapeRenderer.setColor(Color.GREEN);
        shapeRenderer.rect(drawPos.x - healthBarWidth / 2, healthBarY, healthBarWidth * enemy.getHealthPercentage(), healthBarHeight);

        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.rectLine(drawPos.x - healthBarWidth / 2, healthBarY, drawPos.x + healthBarWidth / 2, healthBarY, 2);
        shapeRenderer.rectLine(drawPos.x - healthBarWidth / 2, healthBarY + healthBarHeight, drawPos.x + healthBarWidth / 2, healthBarY + healthBarHeight, 2);
        shapeRenderer.rectLine(drawPos.x - healthBarWidth / 2, healthBarY, drawPos.x - healthBarWidth / 2, healthBarY + healthBarHeight, 2);
        shapeRenderer.rectLine(drawPos.x + healthBarWidth / 2, healthBarY, drawPos.x + healthBarWidth / 2, healthBarY + healthBarHeight, 2);
    }

    private void drawSelectionCircle(Tower tower) {
        float radius = tower.range;
        float x = tower.position.x;
        float y = tower.position.y;
        int segments = 64;

        shapeRenderer.setColor(towerRangeBorderColor);
        drawCircleBorder(x, y, radius, BORDER_THICKNESS, segments);

        shapeRenderer.setColor(towerRangeFillColor);
        shapeRenderer.circle(x, y, radius - BORDER_THICKNESS / 2, segments);
    }

    private void drawCircleBorder(float x, float y, float radius, float thickness, int segments) {
        float innerRadius = radius - thickness / 2;
        float outerRadius = radius + thickness / 2;

        for (int i = 0; i < segments; i++) {
            float angle = (float) (i * 2 * Math.PI / segments);
            float nextAngle = (float) ((i + 1) * 2 * Math.PI / segments);

            float cos = (float) Math.cos(angle);
            float sin = (float) Math.sin(angle);
            float nextCos = (float) Math.cos(nextAngle);
            float nextSin = (float) Math.sin(nextAngle);

            shapeRenderer.triangle(
                    x + cos * innerRadius, y + sin * innerRadius,
                    x + cos * outerRadius, y + sin * outerRadius,
                    x + nextCos * outerRadius, y + nextSin * outerRadius
            );
            shapeRenderer.triangle(
                    x + cos * innerRadius, y + sin * innerRadius,
                    x + nextCos * outerRadius, y + nextSin * outerRadius,
                    x + nextCos * innerRadius, y + nextSin * innerRadius
            );
        }
    }

    private void renderTower(Tower tower) {
        Texture texture = towerTextures.computeIfAbsent(tower.texturePath, Texture::new);
        float towerSize = GameConstants.HEX_R_BIG * 1.5f;

        batch.draw(texture,
                tower.position.x - towerSize / 2,
                tower.position.y - towerSize / 2,
                towerSize,
                towerSize);
    }

    private void renderTowers() {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (Tower tower : gameScreen.towers) {
            if (tower.isSelected) {
                drawSelectionCircle(tower);
            }
        }
        shapeRenderer.end();

        batch.begin();
        for (Tower tower : gameScreen.towers) {
            renderTower(tower);
        }
        batch.end();
    }

    private void renderTowerLevels() {
        if (gameScreen.uiManager.upgradeMode) {
            textBatch.begin();
            for (Tower tower : gameScreen.towers) {
                String levelText = String.valueOf(tower.level);
                String costText = tower.upgCost != 0 ? "$" + tower.upgCost : "M";
                float towerSize = GameConstants.HEX_R_BIG * 1.5f;

                GlyphLayout levelLayout = new GlyphLayout(levelFont, levelText);
                float levelX = tower.position.x + towerSize / 2 - levelLayout.width - 10;
                float levelY = tower.position.y;

                GlyphLayout costLayout = new GlyphLayout(costFont, costText);
                float costX = tower.position.x - towerSize / 2 + 5;
                float costY = tower.position.y;

                levelFont.draw(textBatch, levelText, levelX, levelY);
                costFont.draw(textBatch, costText, costX, costY);
            }
            textBatch.end();
        }
        if (gameScreen.uiManager.sellMode) {
            textBatch.begin();
            for (Tower tower : gameScreen.towers) {
                String sellText = "$" + tower.sellReward;
                float towerSize = GameConstants.HEX_R_BIG * 1.5f;

                GlyphLayout textLayout = new GlyphLayout(sellFont, sellText);
                float textX = tower.position.x- towerSize / 4;
                float textY = tower.position.y ;

                sellFont.draw(textBatch, sellText, textX, textY);
            }
            textBatch.end();
        }
    }

    private void renderProjectiles() {
        gameScreen.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (Projectile projectile : gameScreen.projectiles) {
            projectile.draw(gameScreen.shapeRenderer);
        }
        gameScreen.shapeRenderer.end();
    }

    public void dispose() {
        batch.dispose();
        textBatch.dispose();
        shapeRenderer.dispose();
        maskShader.dispose();
        hexagonMask.dispose();
        terrainTextureAssignments.clear();
        for (Texture texture : enemyTextures.values()) {
            texture.dispose();
        }
        for (Texture texture : towerTextures.values()) {
            texture.dispose();
        }
    }
}