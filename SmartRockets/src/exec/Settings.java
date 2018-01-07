package exec;

import java.util.ArrayList;

import javax.management.RuntimeErrorException;

import objects.Obstacle;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;
import processing.core.PVector;

public class Settings {
	private final static Obstacle[] ITEMS = new Obstacle[2];
	private final static int ITEM_SZ = 150;
	private final static int ANTI_ALIASING_XD = 2;
	private static PApplet parent;
	
	private boolean expanded = false, animating = false;
	private float angle = 0, endAngle = 60;
	private int xOffset, yOffset, rotateBack = 1;
	private final int[] START = new int[2];
	private PImage icon, background;
	
	
	public Settings(PApplet parent) {
		Settings.parent = parent;
		icon = parent.loadImage("assets/ic_settings_black_48dp.png");
		xOffset = icon.width;
		yOffset = 0;
		START[0] = getX() + icon.width/2;
		START[1] = getY() + icon.height/2;

		int maxSzX, maxSzY,
			szX, szY;
		maxSzX = parent.width - xOffset * 2;
		maxSzY = parent.height - xOffset * 2;
		
		szX = ITEMS.length * ITEM_SZ;
		
		//System.out.println(szX + "   " + maxSzX);
		int rows = (int)(szX / maxSzX) + 1;
		if (rows * ITEM_SZ > maxSzY)
			throw new RuntimeException("need more screen lol");
		if (rows == 0)
			rows = 1;
		szY = rows * ITEM_SZ;

		szX *= ANTI_ALIASING_XD;
		szY *= ANTI_ALIASING_XD;
		
		background  = parent.createImage(szX, szY, PConstants.RGB); 
		
		int alpha = 127,
			pos,

			iWidth = (icon.width * ANTI_ALIASING_XD)/2,
			gradient = 5;
		float distance;
		
		background.loadPixels();
		for (int y = 0; y < szY; y++) {
			for (int x = 0; x < szX; x++) {
				pos = (szX - x - 1) + y * szX;
				
//				distance = (int) PVector.dist(new PVector(x, y), new PVector(0, 0));
//
//				if (distance < iWidth + gradient) {
//					alpha = (int) PApplet.map(distance, iWidth, iWidth + gradient, 0, 127);
//					set = true;
//				}
				alpha = 127;
				if (x < iWidth
						&& y < iWidth) {
					distance = PVector.dist(new PVector(x, y), new PVector(0,0));
					if (iWidth > distance) {
						alpha = 1;
					}
				} else if (x >= szX - iWidth 
						&& y < iWidth) {
					distance = PVector.dist(new PVector(x, y), new PVector(szX - iWidth, iWidth));
					if (iWidth < distance - 2) {
						alpha = 1;
					}
				} else if (x >= szX - iWidth 
						&& y > szY - iWidth) {
					distance = PVector.dist(new PVector(x, y), new PVector(szX - iWidth, szY - iWidth));
					if (iWidth < distance - 2) {
						alpha = 1;
					}
				} else if (x <= iWidth 
						&& y > szY - iWidth) {
					distance = PVector.dist(new PVector(x, y), new PVector(iWidth, szY - iWidth));
					if (iWidth < distance) {
						alpha = 1;
					}
				} 
					
				background.pixels[pos] = parent.color(0, 0, 0, alpha);
			}

		}
		
		for (int y = 0; y < 100; y++) {
			for (int x = 0; x < 100; x++) {
				pos = (szX - x - 1) + y * szX;
				
				//background.pixels[pos] = parent.color(255, 0, 0);
			}

		}
		background.updatePixels();

	}
	
	public void draw() {
		parent.pushStyle();
		parent.pushMatrix();
		parent.imageMode(PConstants.CENTER);
		parent.translate(START[0], START[1]);
		
		parent.pushStyle();
		parent.tint(255, 127);
		if (animating) {
			parent.pushMatrix();
			parent.rotate(-PApplet.radians(angle));
			angle += 1.5f * rotateBack;
			if (Math.abs(angle) >= endAngle || angle == 0) {
				angle = 0;
				animating = false;
				rotateBack *= -1;
			}
			parent.image(icon, 0, 0);
			parent.popMatrix();
		} else {
			parent.image(icon, 0, 0);
		}
		parent.popStyle();
		
		if (expanded) {
			//parent.fill(0, 127);
			//parent.rectMode(PConstants.CORNERS);
			//parent.rect(0, 0, -250, 250, 25);
			parent.imageMode(PConstants.CORNERS);
			parent.image(background, 0, 0, -background.width/ANTI_ALIASING_XD, background.height/ANTI_ALIASING_XD);
		}

		parent.popMatrix();
		parent.popStyle();
	}
	
	public int getX() {
		return parent.width - xOffset;
	}
	
	public int getX(int val) {
		return parent.width - xOffset - val;
	}
	
	public int getY() {
		return yOffset;
	}
	
	public int getY(int val) {
		return yOffset + val;
	}

	public boolean buttonClicked(int mouseX, int mouseY) {
		if (mouseX >= getX()
			&& mouseX <= getX() + icon.width
			&& mouseY >= getY()
			&& mouseY <= getY() + icon.height) {
			if (animating)
				rotateBack *= -1;
			else
				animating = true;
			
			expanded = !expanded;
			return true;
		}
		return false;
	}
}










