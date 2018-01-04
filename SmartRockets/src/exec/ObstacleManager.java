package exec;

import java.util.ArrayList;

import objects.Obstacle;
import objects.Rocket;
import objects.Vector2D;

public class ObstacleManager {
	private ArrayList<Obstacle> obstacles = new ArrayList<Obstacle>();
	private Obstacle activeObstacle = null;
	
	public void addObstacle(Obstacle o) {
		obstacles.add(o);
	}
	
	public void draw() {
		for (Obstacle o : obstacles) {
			o.update();
			o.draw();
		}
	}
	
	public void reset() {
		obstacles.clear();
	}
	
	public void mouseClicked(int x, int y) {
		if (activeObstacle != null) {
			activeObstacle.setActive(false);
			activeObstacle.setAnimated(false);
			activeObstacle = null;
		} else {
			for (int a = obstacles.size() - 1; a >= 0; a--) {
				Obstacle o = obstacles.get(a);
				if (o.collision(new Rocket(x, y, 5))) {
					o.setActive(true);
					activeObstacle = o;
					break;
				}
			}
		}
	}
	
	public boolean collision(Rocket r) {
		for (Obstacle o : obstacles) {
			if (o.collision(r)) {
				return true;
			}
		}

		return false;
	}
}
