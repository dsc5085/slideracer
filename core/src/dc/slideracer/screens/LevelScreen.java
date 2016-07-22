package dc.slideracer.screens;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import dc.slideracer.level.LevelController;
import dc.slideracer.ui.UiPack;
import dclib.system.Input;
import dclib.ui.StageUtils;
import dclib.util.InputUtils;

public final class LevelScreen implements Screen {

	private final LevelController controller;
	private final InputProcessor levelInputProcessor;
	private final UiPack uiPack;
	private final Stage stage;
	private Table worldTable;
	private Table statusTable;
	private Label scoreValueLabel;
	
	public LevelScreen(final LevelController controller, final UiPack uiPack) {
		this.controller = controller;
		this.uiPack = uiPack;
		levelInputProcessor = new LevelInputProcessor();
		Input.addProcessor(levelInputProcessor);
		stage = createStage();
		Input.addProcessor(stage);
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
		StageUtils.resize(stage, width, height);
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
		Input.removeProcessor(stage);
		Input.removeProcessor(levelInputProcessor);
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
		controller.draw();
		stage.draw();
	}
	
	private final class LevelInputProcessor implements InputProcessor {
		
		@Override
		public final boolean keyDown(final int keycode) {
			return false;
		}

		@Override
		public final boolean keyUp(final int keycode) {
			switch (keycode) {
			case Keys.ESCAPE:
				controller.toggleRunning();
				return true;
			};
			return false;
		}

		@Override
		public final boolean keyTyped(final char character) {
			return false;
		}

		@Override
		public final boolean touchDown(final int screenX, final int screenY, final int pointer, final int button) {
			return false;
		}

		@Override
		public final boolean touchUp(final int screenX, final int screenY, final int pointer, final int button) {
			return false;
		}

		@Override
		public final boolean touchDragged(final int screenX, final int screenY, final int pointer) {
			return false;
		}

		@Override
		public final boolean mouseMoved(final int screenX, final int screenY) {
			return false;
		}

		@Override
		public final boolean scrolled(final int amount) {
			return false;
		}

	}

}
