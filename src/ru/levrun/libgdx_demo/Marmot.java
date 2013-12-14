package ru.levrun.libgdx_demo;

import java.util.Random;

import com.badlogic.gdx.math.Rectangle;

public class Marmot {
	
	public static Random randomGenerator = new Random();
	
	private Rectangle cell;
	private int x;
	private int y;
	
	public Marmot(int x, int y) {
		this.x = x;
		this.y = y;
		cell = new Rectangle();
		cell.width = Game.CELL_WEIGHT;
		cell.height = Game.CELL_HEIGHT;
		cell.x = cell.width + x * Game.CELL_WEIGHT - 80;
		cell.y = cell.height + y * Game.CELL_HEIGHT + 100;
	}
	
	public Rectangle getRectangle() {
		return this.cell;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public static Marmot getRandomMarmotCell(Marmot[][] field) {
		int i = randomGenerator.nextInt(Game.FIELD_COLS);
		int j = randomGenerator.nextInt(Game.FIELD_ROWS);
		
		return field[i][j];
	}
	
	
	

}
