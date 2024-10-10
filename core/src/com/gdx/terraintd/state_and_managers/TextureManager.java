package com.gdx.terraintd.state_and_managers;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.Gdx;
import java.util.*;

public class TextureManager {
    private Map<String, List<Texture>> terrainTextures;
    private Random random;

    public TextureManager() {
        terrainTextures = new HashMap<>();
        random = new Random();
        initializeTextures();
    }

    private void initializeTextures() {
        loadTexturesForTerrain("grass", 6);
        loadTexturesForTerrain("hill", 5);
        loadTexturesForTerrain("sea", 3);
        loadTexturesForTerrain("sand", 3);
        loadTexturesForTerrain("forest", 3);
    }

    private void loadTexturesForTerrain(String terrainType, int count) {
        List<Texture> textures = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            String fileName = i == 1 ? terrainType + ".png" : terrainType + i + ".png";
            textures.add(new Texture(Gdx.files.internal("images/" + fileName)));
        }
        terrainTextures.put(terrainType, textures);
    }

    public Texture getRandomTexture(String terrainType) {
        String cleanTerrainType = terrainType.replace(" Critical", "");
        List<Texture> textures = terrainTextures.get(cleanTerrainType.toLowerCase());
        if (textures == null || textures.isEmpty()) {
            throw new IllegalArgumentException("No textures available for terrain type: " + cleanTerrainType);
        }
        return textures.get(random.nextInt(textures.size()));
    }

    public void dispose() {
        for (List<Texture> textureList : terrainTextures.values()) {
            for (Texture texture : textureList) {
                texture.dispose();
            }
        }
        terrainTextures.clear();
    }
}