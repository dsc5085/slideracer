package dc.slideracer.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import dc.slideracer.level.LevelController;
import dc.slideracer.ui.UiPack;
import dclib.graphics.TextureCache;
import dclib.util.InputUtils;

public final class LevelScreen implements Screen {

	private final LevelController controller;
	private final UiPack uiPack;
	private final Stage stage;
	private Table worldTable;
	private Table statusTable;
	private Label scoreValueLabel;
	
	public LevelScreen(final TextureCache textureCache, final PolygonSpriteBatch spriteBatch, 
			final ShapeRenderer shapeRenderer, final UiPack uiPack) {
		this.uiPack = uiPack;
		controller = new LevelController(textureCache, spriteBatch, shapeRenderer);
		stage = createStage();
	}
	
	@Override
	public void show() {
		InputUtils.setCursorVisible(false);
	}

	@Override
	public void render(final float delta) {
		stage.act(delta);
		updateUi();
		controller.update(delta);
		draw();
	}

	@Override
	public void resize(final int width, final int height) {
	    stage.getViewport().update(width, height, true);
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
		stage.dispose();
		controller.dispose();
	}
	
	private Stage createStage() {
		Stage stage = new Stage(new ScreenViewport());
		worldTable = uiPack.table();
		Table statusTable = createStatusTable();
		Table mainTable = createMainTable(worldTable, statusTable);
		stage.addActor(mainTable);
		return stage;
	}
	
	private Table createStatusTable() {
		statusTable = uiPack.table();
		statusTable.add(uiPack.label("SCORE: ")).right();
		scoreValueLabel = uiPack.label("");
		statusTable.add(scoreValueLabel).left();
		return statusTable;
	}
	
	private Table createMainTable(final Table worldTable, final Table statusTable) {
		Table mainTable = uiPack.table().top().left();
		mainTable.setFillParent(true);
		mainTable.add(worldTable).expand().fill().row();
		mainTable.add(statusTable).left().row();
		return mainTable;
	}
	
	private void updateUi() {
		String scoreText = Integer.toString(controller.getScore());
		scoreValueLabel.setText(scoreText);
	}

	private void draw() {
		clearScreen();
		controller.draw();
		stage.draw();
	}
	
	private void clearScreen() {
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	}

}
