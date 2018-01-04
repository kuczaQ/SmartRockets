package objects;

import processing.core.PApplet;
import processing.core.PConstants;

public class Circle extends Obstacle {
	private float radius;
	
	public Circle(float x, float y, float radius) {
		super(x, y);
		this.radius = radius*2;
	}
	
	@Override
	public void draw() {
		parent.pushStyle();
		parent.ellipseMode(PConstants.CENTER);
		parent.noStroke();
		parent.fill(fill[0], fill[1], fill[2]);
		parent.ellipse(getX(), getY(), radius, radius);
		parent.popStyle();
	}

	@Override
	public boolean collision(Rocket r) {
		if (PApplet.dist(getX(), getY(), r.getPos().x, r.getPos().y)<= radius/2) {
			return true;
		}
		return false;
	}
}
