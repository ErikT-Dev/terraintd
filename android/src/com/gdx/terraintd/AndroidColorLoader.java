// Place this in your Android project (android/src/main/java/com/yourgame/colormanagement/)
package com.gdx.terraintd;

import android.content.res.Resources;
import android.graphics.Color;

public class AndroidColorLoader {
    private Resources resources;

    public AndroidColorLoader(Resources resources) {
        this.resources = resources;
    }

    public float[] loadColor(int colorResId) {
        int androidColor = resources.getColor(colorResId);
        float[] color = new float[4];
        color[0] = Color.red(androidColor) / 255f;
        color[1] = Color.green(androidColor) / 255f;
        color[2] = Color.blue(androidColor) / 255f;
        color[3] = Color.alpha(androidColor) / 255f;
        return color;
    }
}