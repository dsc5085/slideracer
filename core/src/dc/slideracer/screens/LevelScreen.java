package dc.slideracer.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import dc.slideracer.level.Level;
import dc.slideracer.level.LevelController;
import dclib.graphics.TextureCache;
import dclib.util.InputUtils;

public final class LevelScreen implements Screen {

	private final LevelController controller;
	
	public LevelScreen(final Level level, final TextureCache textureCache, final PolygonSpriteBatch spriteBatch, 
			final ShapeRenderer shapeRenderer) {
		controller = new LevelController(level, textureCache, spriteBatch, shapeRenderer);
	}
	
	@Override
	public void show() {
		InputUtils.setCursorVisible(false);
	}

	@Override
	public void render(final float delta) {
		controller.update(delta);
		draw();
	}

	@Override
	public void resize(final int width, final int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void hide() {
	}

	@Override
	public void dispose() {
		controller.dispose();
	}

	private void draw() {
		clearScreen();
		controller.draw();
	}
	
	private void clearScreen() {
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	}

}
