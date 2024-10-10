package com.gdx.terraintd.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class Projectile implements Json.Serializable {
    private float x, y;
    public transient Enemy target;
    private final float speed;
    private boolean reachedTarget;
    public transient Tower owner;
    private int targetId;
    private int ownerId;

    public Projectile() {
        this.speed = 1000f;
    }

    public Projectile(float startX, float startY, Enemy target, Tower owner) {
        this.x = startX;
        this.y = startY;
        this.target = target;
        this.owner = owner;
        this.speed = 1000f;
        this.reachedTarget = false;

        this.targetId = target.getId();
        this.ownerId = owner.getId();
    }

    public void update(float delta) {
        if (reachedTarget) return;

        float targetX = target.getVisualScreenX();
        float targetY = target.getVisualScreenY();

        float dx = targetX - x;
        float dy = targetY - y;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        if (distance <= speed * delta) {
            x = targetX;
            y = targetY;
            reachedTarget = true;
        } else {
            float ratio = speed * delta / distance;
            x += dx * ratio;
            y += dy * ratio;
        }
    }

    public boolean hasReachedTarget() {
        return reachedTarget;
    }

    public void draw(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(new Color(0.78f, 0.251f, 0.212f, 1));
        shapeRenderer.circle(x, y, 10);
    }

    @Override
    public void write(Json json) {
        json.writeValue("x", x);
        json.writeValue("y", y);
        json.writeValue("speed", speed);
        json.writeValue("reachedTarget", reachedTarget);
        json.writeValue("targetId", targetId);
        json.writeValue("ownerId", ownerId);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        x = jsonData.getFloat("x");
        y = jsonData.getFloat("y");
        reachedTarget = jsonData.getBoolean("reachedTarget");
        targetId = jsonData.getInt("targetId");
        ownerId = jsonData.getInt("ownerId");
    }

    public void reconnect(Enemy target, Tower owner) {
        this.target = target;
        this.owner = owner;
    }

    public int getTargetId() {
        return targetId;
    }

    public int getOwnerId() {
        return ownerId;
    }
}