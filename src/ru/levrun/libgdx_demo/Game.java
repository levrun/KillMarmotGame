package ru.levrun.libgdx_demo;

import android.util.Log;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

import java.util.Timer;
import java.util.TimerTask;

public class Game implements ApplicationListener {

	public static final int CAMERA_WIDTH = 480;
	public static final int CAMERA_HEIGHT = 800;

	public static final int FIELD_ROWS = 3;
	public static final int FIELD_COLS = 3;

	public static final String MARMOT_EMPTY_IMAGE_FILE_NAME = "surok_empty.png";
	public static final String MARMOT_PRESENT_IMAGE_FILE_NAME = "surok_present.png";
	public static final String MARMOT_HIT_IMAGE_FILE_NAME = "surok_hitt.png";

	public static final String INTRO_SOUND_FILE_NAME = "start_game.mp3";
	public static final String KICK_MARMOT_SOUND_FILE_NAME = "kick_marmot.wav";
	public static final String FINISH_GAME_SOUND_FILE_NAME = "game_over.wav";

	public static final int CELL_WEIGHT = 128;
	public static final int CELL_HEIGHT = 128;

	public static final String MARMOT_GAME_TAG = "MARMOT_GAME";

	public static final String TIME_COUNTER_TEXT_MESSAGE = "Time : ";
	public static final String SCORE_COUNTER_TEXT_MESSAGE = "Score : ";
	public static final String START_MENU_TEXT_MESSAGE = "Click to start";

	public static final int TICK_MS = 1000;
	private int GAME_SESSION_TIME = 30;

	private Marmot[][] marmotsList = new Marmot[FIELD_COLS][FIELD_ROWS];
	private Marmot currentMarmotCell;
	private Marmot pressedMarmotCell;

	private OrthographicCamera camera;
	private SpriteBatch batch;

	private BitmapFont timeCounterText;
	private BitmapFont startMenuText;
	private BitmapFont scoreCounterText;

	private Texture marmotHiddenImage;
	private Texture marmotPresentImage;
	private Texture marmotHitImage;

	private Vector3 touchPoint;

	private int timeCount = GAME_SESSION_TIME;

	private int sessionScore = 0;

	private boolean isGameSessionOnPause;
	private boolean isGameStarted;

	private Sound introMusic;
	private Sound kickMarmotSound;
	private Sound finishGameSound;

	private boolean soundPlaying;
	private MyTimer myTimer;

	@Override
	public void create() {
		camera = new OrthographicCamera();
		camera.setToOrtho(false, CAMERA_WIDTH, CAMERA_HEIGHT);
		batch = new SpriteBatch();

		touchPoint = new Vector3();

		createGameImages();
		createGameField();
		createGameText();
		createSounds();
	}

	private void createSounds() {
		introMusic = Gdx.audio.newSound(Gdx.files
				.internal(INTRO_SOUND_FILE_NAME));
		kickMarmotSound = Gdx.audio.newSound(Gdx.files
				.internal(KICK_MARMOT_SOUND_FILE_NAME));
		finishGameSound = Gdx.audio.newSound(Gdx.files
				.internal(FINISH_GAME_SOUND_FILE_NAME));
	}

	private void createGameImages() {
		marmotHiddenImage = new Texture(
				Gdx.files.internal(MARMOT_EMPTY_IMAGE_FILE_NAME));
		marmotPresentImage = new Texture(
				Gdx.files.internal(MARMOT_PRESENT_IMAGE_FILE_NAME));
		marmotHitImage = new Texture(
				Gdx.files.internal(MARMOT_HIT_IMAGE_FILE_NAME));
	}

	private void createGameText() {
		timeCounterText = GameUtils.createBitmapFont(1f, Color.BLACK);
		startMenuText = GameUtils.createBitmapFont(2f, Color.BLUE);
		scoreCounterText = GameUtils.createBitmapFont(1f, Color.BLACK);
	}

	private void createGameField() {
		for (int i = 0; i < FIELD_COLS; i++) {
			for (int j = 0; j < FIELD_ROWS; j++) {
				marmotsList[i][j] = new Marmot(i, j);
			}
		}

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	private void drawField() {
		for (int i = 0; i < FIELD_ROWS; i++) {
			for (int j = 0; j < FIELD_COLS; j++) {
				Marmot myMarmot = marmotsList[i][j];
				if (myMarmot == currentMarmotCell
						&& myMarmot == pressedMarmotCell) {
					batch.draw(marmotHitImage, myMarmot.getRectangle().x,
							myMarmot.getRectangle().y);
				} else if (myMarmot == currentMarmotCell) {
					batch.draw(marmotPresentImage, myMarmot.getRectangle().x,
							myMarmot.getRectangle().y);
				} else {
					batch.draw(marmotHiddenImage, myMarmot.getRectangle().x,
							myMarmot.getRectangle().y);
				}

			}
		}
	}

	@Override
	public void render() {
		Color color = GameUtils.colorFromHex(0xB0BC22);
		Gdx.gl.glClearColor(color.r, color.g, color.b, color.a);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT); // OpenGL code to clear the
													// screen
		camera.update();

		batch.setProjectionMatrix(camera.combined);
		batch.begin();

		timeCounterText.draw(
				batch,
				TIME_COUNTER_TEXT_MESSAGE + timeCount,
				CAMERA_WIDTH
						- timeCounterText.getBounds(TIME_COUNTER_TEXT_MESSAGE
								+ "      ").width,
				CAMERA_HEIGHT
						- timeCounterText.getBounds(TIME_COUNTER_TEXT_MESSAGE
								+ "  ").height);

		scoreCounterText.draw(
				batch,
				SCORE_COUNTER_TEXT_MESSAGE + sessionScore,
				10,
				CAMERA_HEIGHT
						- timeCounterText.getBounds(SCORE_COUNTER_TEXT_MESSAGE
								+ "   ").height);

		if (timeCount >= GAME_SESSION_TIME || timeCount <= 0) {
			startMenuText.draw(batch, START_MENU_TEXT_MESSAGE, CAMERA_WIDTH / 2
					- timeCounterText.getBounds(START_MENU_TEXT_MESSAGE).width
					/ 2 - 30, CAMERA_HEIGHT / 2);
		}

		if (isGameStarted) {
			if (myTimer == null) {
				myTimer = new MyTimer(Marmot.randomGenerator.nextInt(3));
				myTimer.start();
			}

			if (timeCount < 5) {
				if (!soundPlaying) {
					soundPlaying = true;
					finishGameSound.play();
				}
			}

			if (myTimer != null && myTimer.hasCompleted()) {
				myTimer = null;
				if (!isGameSessionOnPause) {
					currentMarmotCell = Marmot.getRandomMarmotCell(marmotsList);
					pressedMarmotCell = null;
					isGameSessionOnPause = true;
				} else {
					isGameSessionOnPause = false;
					currentMarmotCell = null;
				}

			}

			drawField();

		}

		batch.end();

		camera.unproject(touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0));

		/** Simple technique to detect user input on the touch screen **/
		if (Gdx.input.isTouched()) {

			if (!isGameStarted) {
				introMusic.play();
				// startTime = TimeUtils.millis();
				sessionScore = 0;
				soundPlaying = false;
				timeCount = GAME_SESSION_TIME;
				isGameStarted = true;

				final Timer timer = new Timer();

				timer.schedule(new TimerTask() {
					@Override
					public void run() {
						timeCount--;
						if (timeCount <= 0) {
							isGameStarted = false;
							timer.cancel();
						}

					}
				}, 0, TICK_MS);

			} else {
				checkField();
			}

		}
	}

	private void checkField() {
		for (int i = 0; i < FIELD_ROWS; i++) {
			for (int j = 0; j < FIELD_COLS; j++) {
				Marmot myMarmot = marmotsList[i][j];
				if (myMarmot.getRectangle()
						.contains(touchPoint.x, touchPoint.y)) {
					Log.d(MARMOT_GAME_TAG, "Point in rectangle!");
                    if (pressedMarmotCell == myMarmot) {
                        //ignore, it is already hit
                        Log.d(MARMOT_GAME_TAG, "Dont' hit it twice");
                        return;
                    }
					pressedMarmotCell = myMarmot;

					if (currentMarmotCell == pressedMarmotCell) {
						kickMarmotSound.play();
						Log.d(MARMOT_GAME_TAG, "Hit!");
						sessionScore = sessionScore + 10;
					}
				}
			}
		}
	}

	@Override
	public void resize(int arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

}