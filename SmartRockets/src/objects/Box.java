package objects;

import processing.core.PConstants;

public class Box extends Obstacle {
	private float width, height;
	
	public Box(float x, float y, float width, float height) {
		super(x, y);
		this.width = width;
		this.height = height;
	}
	
	@Override
	public void draw() {
		
		parent.pushStyle();
		parent.fill(fill[0], fill[1], fill[2]);
		parent.rectMode(PConstants.CENTER);
		parent.rect(getX(), getY(), width, height);
		parent.popStyle();
	}

	@Override
	public boolean collision(Rocket r) {
		int x = (int) (getX() - width/2),
			y = (int) (getY() - height/2);
		if (r.getPos().x > x && r.getPos().x <= x + width
			&& r.getPos().y > y && r.getPos().y <= y + height) {
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return "Box [width=" + width + ", height=" + height + ", getPos()=" + getPos() + "]";
	}

}
