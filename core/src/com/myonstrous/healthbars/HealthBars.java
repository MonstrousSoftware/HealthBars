package com.myonstrous.healthbars;

import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

public class HealthBars implements Disposable {

    public static final float Y_OFFSET = 1.5f;          // height above the unit
    public static final int NUM_STEPS = 32;             // granularity of health bar


    private final DecalBatch decalBatch;
    private final Camera cam;
    private final Vector3 viewDir = new Vector3();
    private final Vector3 position = new Vector3();
    private final TextureRegion[] textures;           // different texture regions for different levels of health


    public HealthBars(Camera cam) {
        this.cam = cam;

        decalBatch = new DecalBatch(new CameraGroupStrategy(cam));
        textures = prepareTextures(NUM_STEPS);
    }

    private TextureRegion[] prepareTextures(int steps) {
        TextureRegion[] regions = new TextureRegion[steps];
        for(int h = 0; h < steps; h++) {
            // select colour to match health level
            Color col  = Color.GREEN;
            if(h < 0.25f*steps)
                col = Color.RED;
            else if(h < 0.5f * steps)
                col = Color.ORANGE;
            Texture tex = makeBarTexture(32, 5, h/(float)(steps-1), col);
            regions[h] = new TextureRegion(tex);
        }
        return regions;
    }

    private TextureRegion selectTexture(float health) {
        health = MathUtils.clamp(health, 0f, 1f);
        int h = Math.round(health * (NUM_STEPS-1));
        return textures[h];
    }


    public void show(Array<Creature> creatures) {
        for(Creature creature : creatures) {
            if(creature.healthBarDecal == null) {
                creature.healthBarDecal = Decal.newDecal(selectTexture(creature.health));
                creature.healthBarDecal.setDimensions(2f, 0.4f);                    // world units
            }else
                creature.healthBarDecal.setTextureRegion(selectTexture(creature.health));
        }

        viewDir.set(cam.direction).scl(-1);    // direction that decals should be facing: opposite of camera view vector
        // (don't point the decals at the (perspective) camera because then the rectangles get skewed with horrible jaggies)

        for(Creature creature : creatures) {
            creature.transform.getTranslation(position);
            position.y += Y_OFFSET;     // place health bar above the creature
            creature.healthBarDecal.setPosition(position);
            creature.healthBarDecal.setRotation(viewDir, Vector3.Y);
            decalBatch.add(creature.healthBarDecal);
        }
        decalBatch.flush();
    }

    @Override
    public void dispose() {
        decalBatch.dispose();
        for(TextureRegion r : textures)
            r.getTexture().dispose();
    }

    private Texture makeBarTexture(int width, int height, float health, Color color) {
        Pixmap pixmap = new Pixmap(width+4, height+2, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.BLACK);
        pixmap.fill();
        pixmap.setColor(Color.GRAY);
        pixmap.fillRectangle(2,1, width, height);
        pixmap.setColor(color);
        int w = MathUtils.round((float)width*health);
        if(w > 0)
            pixmap.fillRectangle(2,1, w, height);
        return new Texture(pixmap);
    }

}
