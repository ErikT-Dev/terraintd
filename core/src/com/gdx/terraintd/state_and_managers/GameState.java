package com.gdx.terraintd.state_and_managers;

import com.gdx.terraintd.components.Enemy;
import com.gdx.terraintd.components.EnemySpawner;
import com.gdx.terraintd.components.Projectile;
import com.gdx.terraintd.components.Tower;
import java.util.List;

public class GameState {
    public StatsManager statsManager;
    public List<Tower> towers;
    public List<Enemy> enemies;
    public List<Projectile> projectiles;
    public EnemySpawner enemySpawner;
    public EnemySpawner enemySpawner2;
    public EnemySpawner enemySpawner3;
    public String[][] gridArray;
    public String mapName;
    public GameState(){}
}