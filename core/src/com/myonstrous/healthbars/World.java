package com.myonstrous.healthbars;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

public class World implements Disposable {

    public final static int WORLD_WIDTH = 20;           // width of tile world (in tiles)
    public final static int WORLD_HEIGHT = 10;          // depth

    public final Array<ModelInstance> instances;
    public final Array<Creature> creatures;
    private final Array<Model> models;                    // keep track of models to dispose them at shutdown

    public World() {
        instances = new Array<>();
        creatures = new Array<>();
        models = new Array<>();
        ModelBuilder modelBuilder = new ModelBuilder();

        // create models and instances

        Model modelBox = modelBuilder.createBox(1f, 1f, 1f,
                new Material(ColorAttribute.createDiffuse(Color.CYAN)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal  );

        creatures.add( new Creature(modelBox, -1f, 0.7f,-3f));
        creatures.add( new Creature(modelBox, -5f, 0.7f,3f));
        creatures.add( new Creature(modelBox, 5f, 0.7f,4f));

        for(Creature c : creatures)
            instances.add(c);
        models.add(modelBox);

        Model modelGrid = modelBuilder.createLineGrid(30, 30, 1, 1, new Material(ColorAttribute.createDiffuse(Color.WHITE)),   VertexAttributes.Usage.Position);
        instances.add( new ModelInstance(modelGrid, 0.5f, 0, 0.5f) );   // offset to match tile positioning
        models.add(modelGrid);

        Model modelTile = modelBuilder.createBox(.95f, 0.2f, .95f,
                new Material(ColorAttribute.createDiffuse(Color.LIME)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal  );
        models.add(modelTile);

        // lay the tiles
        for(int x = -WORLD_WIDTH/2; x <= WORLD_WIDTH/2; x++) {
            for(int z = -WORLD_HEIGHT/2; z <= WORLD_HEIGHT/2; z++){
                instances.add( new ModelInstance(modelTile, x, .1f, z) );
            }
        }
    }

    public void update( float deltaTime ) {
        for(Creature c : creatures) {
            c.health = MathUtils.clamp(c.health - deltaTime*0.05f, 0f, 1f);

        }

    }

    @Override
    public void dispose() {
        for(Model model: models)
            model.dispose();
    }



}
