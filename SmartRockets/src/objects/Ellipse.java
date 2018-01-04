package objects;

public class Ellipse extends Obstacle {
	private float width, height;
	
	public Ellipse(float x, float y, float width, float height) {
		super(x, y);
		this.width = width;
		this.height = height;
	}
	
	@Override
	public void draw() {
		parent.pushStyle();
		parent.fill(fill[0], fill[1], fill[2]);
		parent.ellipse(getX(), getY(), width, height);
		parent.popStyle();
	}

	@Override
	public boolean collision(Rocket r) {
		// TODO Auto-generated method stub
		return false;
	}

}
