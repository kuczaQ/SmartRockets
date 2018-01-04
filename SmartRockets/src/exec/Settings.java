package exec;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;

public class Settings {
	private static PApplet parent;
	private boolean expanded = false, animating = false;
	private float angle = 0, endAngle = 60;
	private int xOffset, yOffset, rotateBack = 1;
	private PImage icon;
	
	
	public Settings(PApplet parent) {
		Settings.parent = parent;
		icon = parent.loadImage("assets/ic_settings_black_48dp.png");
		xOffset = icon.width;
		yOffset = 0;
	}
	
	public void draw() {
		parent.pushStyle();
		parent.pushMatrix();
		parent.imageMode(PConstants.CENTER);
		parent.translate(getX() + icon.width/2, getY() + icon.height/2);
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
		
		if (expanded) {
			parent.fill(0, 127);
			parent.rectMode(PConstants.CORNERS);
			parent.rect(0, 0, -250, 250, 25);
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










