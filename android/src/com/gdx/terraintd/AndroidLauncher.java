package com.gdx.terraintd;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.gdx.terraintd.state_and_managers.LibGDXColorManager;
import com.gdx.terraintd.logic.TerrainTDGame;

public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		AndroidColorLoader colorLoader = new AndroidColorLoader(getResources());

		float[] towerRangeFillColorValues = colorLoader.loadColor(R.color.tower_range_fill);
		float[] towerRangeBorderColorValues = colorLoader.loadColor(R.color.tower_range_border);
		float[] grassTileColorValues = colorLoader.loadColor(R.color.grass_tile);
		float[] seaTileColorValues = colorLoader.loadColor(R.color.sea_tile);
		float[] hillTileColorValues = colorLoader.loadColor(R.color.hill_tile);
		float[] sandTileColorValues = colorLoader.loadColor(R.color.sand_tile);
		float[] forestTileColorValues = colorLoader.loadColor(R.color.forest_tile);
		float[] startTileColorValues = colorLoader.loadColor(R.color.start_tile);
		float[] endTileColorValues = colorLoader.loadColor(R.color.end_tile);
		float[] titleTextNormalColorValues = colorLoader.loadColor(R.color.title_text_normal);
		float[] titleTextPressedColorValues = colorLoader.loadColor(R.color.title_text_pressed);
		float[] towerGrassColorValues = colorLoader.loadColor(R.color.tower_grass);
		float[] towerHillColorValues = colorLoader.loadColor(R.color.tower_hill);

		LibGDXColorManager.getInstance().initialize(
				towerRangeFillColorValues, towerRangeBorderColorValues, grassTileColorValues,
				seaTileColorValues, hillTileColorValues, sandTileColorValues, forestTileColorValues,
				startTileColorValues, endTileColorValues, titleTextNormalColorValues, titleTextPressedColorValues,
				towerGrassColorValues, towerHillColorValues
		);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		initialize(new TerrainTDGame(), config);
	}
}
