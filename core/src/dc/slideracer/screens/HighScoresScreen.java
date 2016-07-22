package dc.slideracer.screens;

import java.util.Collections;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import dc.slideracer.session.GameSession;
import dc.slideracer.session.ScoreEntry;
import dc.slideracer.ui.FontSize;
import dc.slideracer.ui.UiPack;
import dclib.eventing.DefaultEvent;
import dclib.eventing.DefaultListener;
import dclib.eventing.EventDelegate;
import dclib.system.Input;
import dclib.ui.StageUtils;
import dclib.util.InputUtils;
import dclib.util.Timer;
import dclib.util.XmlContext;

public class HighScoresScreen implements Screen {
	
	private final EventDelegate<DefaultListener> closedDelegate = new EventDelegate<DefaultListener>();
	
	private final UiPack uiPack;
	private final GameSession gameSession;
	private XmlContext xmlContext = null;
	private ScoreEntry newScoreEntry = null;
	private TextField nameField = null;
	private final Stage stage;
	private final InputProcessor highScoresInputProcessor;
	private final Timer transitionTimer = new Timer(1);

	public HighScoresScreen(final UiPack uiPack, final GameSession gameSession, final XmlContext xmlContext, 
			final int newScore) {
		this.uiPack = uiPack;
		this.gameSession = gameSession;
		this.xmlContext = xmlContext;
		if (gameSession.canAddHighScore(newScore)) {
			newScoreEntry = new ScoreEntry("", newScore);
		}
		stage = createStage();
		Input.addProcessor(stage);
		highScoresInputProcessor = new HighScoresInputProcessor();
		Input.addProcessor(highScoresInputProcessor);
	}
	
	public final void addClosedListener(final DefaultListener listener) {
		closedDelegate.listen(listener);
	}

	@Override
	public void render(final float delta) {
		stage.act(delta);
		transitionTimer.tick(delta);
		stage.draw();
	}

	@Override
	public void resize(final int width, final int height) {
		StageUtils.resize(stage, width, height);
	}

	@Override
	public void show() {
		InputUtils.setCursorVisible(true);
	}

	@Override
	public void hide() {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
		Input.removeProcessor(highScoresInputProcessor);
		Input.removeProcessor(stage);
		stage.dispose();
	}
	
	private Stage createStage() {
		Stage stage = new Stage(new ScreenViewport());
		Table mainTable = createMainTable(stage);
		stage.addActor(mainTable);
		return stage;
	}
	
	private Table createMainTable(final Stage stage) {
		Table mainTable = uiPack.table();
		mainTable.setFillParent(true);
		if (newScoreEntry == null) {
			mainTable.add(uiPack.label("High Scores")).row();
		}
		else {
			mainTable.add(uiPack.label("New High Score!  Enter your name below")).row();
		}
		mainTable.add(uiPack.lineBreak()).row();
		mainTable.add(createScoresTable(stage)).row();
		mainTable.add(uiPack.lineBreak()).row();
		mainTable.add(uiPack.label("Click or touch to continue...")).row();
		return mainTable;
	}
	
	private Table createScoresTable(final Stage stage) {
		Table scoresTable = uiPack.table();
		List<ScoreEntry> sortedHighScores = getSortedHighScores();
		for (ScoreEntry highScore : sortedHighScores) {
			addScoreRow(stage, scoresTable, highScore);
		}
		return scoresTable;
	}

	private void addScoreRow(final Stage stage, final Table scoresTable, final ScoreEntry highScore) {
		final int scoreSpaceLeft = 100;
		Color fontColor;
		if (highScore == newScoreEntry) {
			fontColor = Color.YELLOW.cpy();
			nameField = createNameField(fontColor);
			scoresTable.add(nameField).left();
			stage.setKeyboardFocus(nameField);
		} else {
			fontColor = Color.WHITE.cpy();
			scoresTable.add(uiPack.label(highScore.name, FontSize.SMALL)).left();
		}
		String scoreString = Integer.toString(highScore.score);
		Label scoreLabel = uiPack.label(scoreString, FontSize.SMALL, fontColor);
		scoresTable.add(scoreLabel).spaceLeft(scoreSpaceLeft).right().row();
	}
	
	private List<ScoreEntry> getSortedHighScores() {
		List<ScoreEntry> sortedHighScores = gameSession.getSortedHighScores();
		if (newScoreEntry != null) {
			sortedHighScores.add(newScoreEntry);
		}
		Collections.sort(sortedHighScores);
		Collections.reverse(sortedHighScores);
		return sortedHighScores;
	}
	
	private TextField createNameField(final Color fontColor) {
		TextField nameField = uiPack.textField(FontSize.SMALL);
		nameField.getStyle().background = null;
		nameField.getStyle().fontColor = fontColor;
		final int maxNameLength = 10;
		nameField.setMaxLength(maxNameLength);
		return nameField;
	}
	
	private final class HighScoresInputProcessor implements InputProcessor {
		
		@Override
		public final boolean keyDown(final int keycode) {
			return false;
		}

		@Override
		public final boolean keyUp(final int keycode) {
			return false;
		}

		@Override
		public final boolean keyTyped(final char character) {
			return false;
		}

		@Override
		public final boolean touchDown(final int screenX, final int screenY, final int pointer, final int button) {
			if (transitionTimer.isElapsed()) {
				if (newScoreEntry != null) {
					newScoreEntry.name = nameField.getText();
					gameSession.addHighScore(newScoreEntry);
					FileHandle gameSessionFile = Gdx.files.local(GameSession.FILE_PATH);
					xmlContext.marshal(gameSession, gameSessionFile);
				}
				closedDelegate.notify(new DefaultEvent());
				return true;
			}
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
