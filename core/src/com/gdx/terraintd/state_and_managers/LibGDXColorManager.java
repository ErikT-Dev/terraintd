package com.gdx.terraintd.state_and_managers;

import com.badlogic.gdx.graphics.Color;

public class LibGDXColorManager {
    private static LibGDXColorManager instance;

    public Color towerRangeFillColor;
    public Color towerRangeBorderColor;
    public Color grassTileColor;
    public Color seaTileColor;
    public Color hillTileColor;
    public Color sandTileColor;
    public Color forestTileColor;
    public Color startTileColor;
    public Color endTileColor;
    public Color titleTextNormalColor;
    public Color titleTextPressedColor;
    public Color towerGrassColor;
    public Color towerHillColor;

    private LibGDXColorManager() {
    }

    public static LibGDXColorManager getInstance() {
        if (instance == null) {
            instance = new LibGDXColorManager();
        }
        return instance;
    }

    public void initialize(float[] towerRangeFillColorValues, float[] towerRangeBorderColorValues,
                           float[] grassTileColorValues, float[] seaTileColorValues,
                           float[] hillTileColorValues, float[] sandTileColorValues,
                           float[] forestTileColorValues, float[] startTileColorValues,
                           float[] endTileColorValues, float[] titleTextNormalColorValues,
                           float[] titleTextPressedColorValues, float[] towerGrassColorValues,
                           float[] towerHillColorValues) {
        towerRangeFillColor = new Color(towerRangeFillColorValues[0], towerRangeFillColorValues[1], towerRangeFillColorValues[2], towerRangeFillColorValues[3]);
        towerRangeBorderColor = new Color(towerRangeBorderColorValues[0], towerRangeBorderColorValues[1], towerRangeBorderColorValues[2], towerRangeBorderColorValues[3]);
        grassTileColor = new Color(grassTileColorValues[0], grassTileColorValues[1], grassTileColorValues[2], grassTileColorValues[3]);
        seaTileColor = new Color(seaTileColorValues[0], seaTileColorValues[1], seaTileColorValues[2], seaTileColorValues[3]);
        hillTileColor = new Color(hillTileColorValues[0], hillTileColorValues[1], hillTileColorValues[2], hillTileColorValues[3]);
        sandTileColor = new Color(sandTileColorValues[0], sandTileColorValues[1], sandTileColorValues[2], sandTileColorValues[3]);
        forestTileColor = new Color(forestTileColorValues[0], forestTileColorValues[1], forestTileColorValues[2], forestTileColorValues[3]);
        startTileColor = new Color(startTileColorValues[0], startTileColorValues[1], startTileColorValues[2], startTileColorValues[3]);
        endTileColor = new Color(endTileColorValues[0], endTileColorValues[1], endTileColorValues[2], endTileColorValues[3]);
        titleTextNormalColor = new Color(titleTextNormalColorValues[0], titleTextNormalColorValues[1], titleTextNormalColorValues[2], titleTextNormalColorValues[3]);
        titleTextPressedColor = new Color(titleTextPressedColorValues[0], titleTextPressedColorValues[1], titleTextPressedColorValues[2], titleTextPressedColorValues[3]);
        towerGrassColor = new Color(towerGrassColorValues[0], towerGrassColorValues[1], towerGrassColorValues[2], towerGrassColorValues[3]);
        towerHillColor = new Color(towerHillColorValues[0], towerHillColorValues[1], towerHillColorValues[2], towerHillColorValues[3]);
    }

    public void updateSelectionColor(float[] colorValues) {
        towerRangeFillColor.set(colorValues[0], colorValues[1], colorValues[2], colorValues[3]);
    }
}