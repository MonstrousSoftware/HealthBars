package com.myonstrous.healthbars;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.decals.Decal;

public class Creature extends ModelInstance {

    float health;       // between 0 and 1
    Decal healthBarDecal;

    public Creature(Model model, float x, float y, float z) {
        super(model, x, y, z);
        health = (float) Math.random();
        healthBarDecal = null;
    }
}
