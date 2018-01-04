package objects;

import processing.core.PApplet;

public abstract class Obstacle {
	protected static PApplet parent;
	private boolean active = false, animated = false;
	public void setActive(boolean active) {
		this.active = active;
	}

	private float animStep = 0;
	
	private Vector2D pos, posOG;
	protected int[] fill = {(int) parent.random(255), (int) parent.random(255), (int) parent.random(255)};
	public Obstacle() {
		pos = new Vector2D();
	}
	
	public Obstacle(float x, float y) {
		pos = new Vector2D(x, y);
		posOG = pos.copy();
	}
	
	public void update() {
		if (active) {
			if (!animated) {
				pos.x = PApplet.lerp(posOG.x, parent.mouseX, animStep);
				pos.y = PApplet.lerp(posOG.y, parent.mouseY, animStep);
				if (animStep > 1) {
					animated = true;
					animStep = 0;
					
				}
				System.out.println(animStep);
				animStep += 0.08;
			} else {
				pos.x = parent.mouseX;
				pos.y = parent.mouseY;
			}
		}
	}
	
	public abstract void draw();
	
	public abstract boolean collision(Rocket r);
	
	public float getX() {
		return pos.x;
	}
	
	public void setX(float x) {
		pos.x = x;
	}
	
	public float getY() {
		return pos.y;
	}
	
	public void setY(float y) {
		pos.y = y;
	}
	
	public Vector2D getPos() {
		return pos;
	}
	
	public void setPos(Vector2D pos) {
		this.pos = pos;
	}
	
	public void setPos(float x, float y) {
		this.pos.x = x;
		this.pos.y = y;
	}
	
	public static void setParent(PApplet p) {
		parent = p;
	}

	public void setAnimated(boolean animated) {
		posOG = pos.copy();
		this.animated = animated;
	}
}










