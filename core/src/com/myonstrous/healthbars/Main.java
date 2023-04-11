package com.myonstrous.healthbars;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalShadowLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;

// test of health bars using decals
//


public class Main extends ApplicationAdapter {

	final static float VIEW_SIZE = 20f;

	private PerspectiveCamera cam;
	private Environment environment;
	private CameraInputController camController;
	private ModelBatch modelBatch;
	private World world;
	private DirectionalShadowLight shadowLight;
	private ModelBatch shadowBatch;
	private HealthBars healthBars;

	@Override
	public void create () {
		cam = new PerspectiveCamera(67, VIEW_SIZE, VIEW_SIZE);

		cam.position.set(10f, 10f, 10f);
		cam.lookAt(0,0,0);
		cam.near = 1f;
		cam.far = 200f;
		cam.update();

		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.6f, 0.6f, 0.6f, 1f));

		// use the 'deprecated' shadow light
		// tweak these values so that all the scene is covered and the shadows are not too blocky
		shadowLight = new DirectionalShadowLight(1024, 1024, 32, 32, 5f, 50);

		Vector3 lightVector = new Vector3(.4f, -.8f, -0.4f).nor();
		float dl = 0.6f;
		shadowLight.set(new Color(dl, dl, dl, 1), lightVector);
		environment.add(shadowLight);
		environment.shadowMap = shadowLight;
		shadowBatch = new ModelBatch(new DepthShaderProvider());

		world = new World();


		modelBatch = new ModelBatch();

		camController = new CameraInputController(cam);
		Gdx.input.setInputProcessor(camController);

		healthBars = new HealthBars(cam);
	}


	@Override
	public void resize (int width, int height) {
		// define view port in logical units (tiles) but maintain aspect ratio if the screen/window
		cam.viewportWidth = VIEW_SIZE;
		cam.viewportHeight = VIEW_SIZE * height/width;
		cam.update();
	}



	@Override
	public void render () {
		camController.update();

		float deltaTime = Gdx.graphics.getDeltaTime();
		world.update(deltaTime);

		// prepare shadow buffer
		shadowLight.begin(Vector3.Zero, cam.direction);
		shadowBatch.begin(shadowLight.getCamera());
		shadowBatch.render(world.instances, environment);
		shadowBatch.end();
		shadowLight.end();

		// clear screen
		ScreenUtils.clear(0.8f, 0.8f, 1.0f, 1, true);

		// render world
		modelBatch.begin(cam);
		modelBatch.render(world.instances, environment);
		modelBatch.end();

		healthBars.show(world.creatures);
	}


	@Override
	public void dispose () {
		modelBatch.dispose();
		world.dispose();
		shadowBatch.dispose();
		shadowLight.dispose();
		healthBars.dispose();
	}
}
