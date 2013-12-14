package ru.levrun.libgdx_demo;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

public class GameUtils {
	
	/**
	 * Expects a hex value as integer and returns the appropriate Color object.
	 * @param hex Must be of the form 0xAARRGGBB
	 * @return the generated Color object
	 */
	public static Color colorFromHex(long hex) {
		float a = (hex & 0xFF000000L) >> 24;
		float r = (hex & 0xFF0000L) >> 16;
		float g = (hex & 0xFF00L) >> 8;
		float b = (hex & 0xFFL);

		return new Color(r / 255f, g / 255f, b / 255f, a / 255f);
	}
	
	public static BitmapFont createBitmapFont(float scale, Color color) {
		BitmapFont textBitmap = new BitmapFont();
		textBitmap.scale(scale);
		textBitmap.setColor(color);
		return textBitmap; 
	}
	
	

}
