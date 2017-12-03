package objects;

import exec.Population;
import exec.SmartRockets;
import processing.core.PApplet;

public class Rocket {
	private static final int SPEED_LIMIT = 3;
	private static PApplet parent;
	private static Vector2D target;

	private DNA dna;
	private CalculateFitness calcFitness;
	private Population population; // population to which the rocket belongs to

	private Vector2D pos = new Vector2D(parent.width/2, parent.height - 20),
			acc = new Vector2D(),
			vel = new Vector2D();

	private boolean crashed = false, finished = false, crashedOnObstacle = false;
	private Double fitness = 1d;
	private Integer finishTime, passedObstacleTime;
	private int radius = 5;



	public Rocket(Population p) {
		this.population = p;
		calcFitness = population.getPopulationFitnessFunction();
		setDna(new DNA(population.getFill()));
	}

	public Rocket(Rocket r) {
		this.setDna(r.getDna().copy());
	}

	public Rocket(Population p, DNA dna) {
		this.population = p;
		calcFitness = population.getPopulationFitnessFunction();
		this.setDna(dna);
	}

	public void applyForce(Vector2D force) {
		this.acc.add(force);
	}

	public void calcFitness() {
		if (calcFitness == null)
			throw new NullPointerException("calcFitness == null");

		setFitness(calcFitness.calc(this));
	}

	public void update() {

		if (!this.hasFinished() && !hasCrashed()) {
			float d = PApplet.dist(this.getPos().x, this.getPos().y, target.x, target.y);

			if (getPos().y <= SmartRockets.ry) {
				if (getPassedObstacleTime() == null) 
					setPassedObstacleTime(SmartRockets.getCounter());
				if (!population.passedTheObstacle)
					population.passedTheObstacle = true;
			}

			if (d < SmartRockets.TARGET_R/2) {
				this.setFinished(true);
				//this.pos = target.copy();
				setFinishTime(SmartRockets.getCounter());
				//population.targetPos = this.pos.copy();
				population.setSuccess();
			}
			
			//TODO generic object array
			
			// Rocket hit the barrier
			if (this.getPos().x > SmartRockets.rx && this.getPos().x < SmartRockets.rx + SmartRockets.rw 
					&& this.getPos().y > SmartRockets.ry && this.getPos().y < SmartRockets.ry + SmartRockets.rh) {

				this.setCrashed(true);
				setCrashedOnObstacle(true);
			}

			// Rocket has hit left or right of window
			if (this.getPos().x > parent.width || this.getPos().x < 0) {
				this.setCrashed(true);
			}
			// Rocket has hit top or bottom of window
			if (this.getPos().y > parent.height || this.getPos().y < 0) {
				this.setCrashed(true);
			}


			//applies the random vectors defined in dna to consecutive frames of rocket
			this.applyForce(this.getDna().genes.get(SmartRockets.getCounter() >= getDna().genes.size() ? getDna().genes.size() - 1 : SmartRockets.getCounter()));

			// if rocket has not got to goal and not crashed then update physics engine
			if (!this.hasFinished() && !this.hasCrashed()) {
				this.vel.add(this.acc);
				this.getPos().add(this.vel);
				this.acc.mult(0);
				this.vel.limit(SPEED_LIMIT);
				
				if (getPos().x > parent.width) {
					getPos().x = parent.width;
					this.setCrashed(true);
				}
				if (getPos().x < 0) {
					getPos().x = 0;
					this.setCrashed(true);
				}
				if (getPos().y > parent.height) {
					getPos().y = parent.height;
					this.setCrashed(true);
				}
				if (getPos().y < 0) {
					getPos().y = 0;
					this.setCrashed(true);
				}
			}


		}
	}

	public void show() {
		drawCircle((int) getPos().x, (int) getPos().y, radius);
	}

	public void drawCircle(int x0, int y0, int radius) {
		int endPoint = radius - 3,
			x, y, dx, dy, err;
		
		while (radius != endPoint) {
			x = radius - 1;
			y = 0;
			dx = 1;
			dy = 1;
			err = dx - (radius << 1);

			while (x >= y) {
				putpixel(x0 + x, y0 + y);
				putpixel(x0 + y, y0 + x);
				putpixel(x0 - y, y0 + x);
				putpixel(x0 - x, y0 + y);
				putpixel(x0 - x, y0 - y);
				putpixel(x0 - y, y0 - x);
				putpixel(x0 + y, y0 - x);
				putpixel(x0 + x, y0 - y);

				if (err <= 0)
				{
					y++;
					err += dy;
					dy += 2;
				}
				if (err > 0)
				{
					x--;
					dx += 2;
					err += dx - (radius << 1);
				}
			}
			radius--;
		}
    }

	private void putpixel(int x, int y) {
		try {
			parent.pixels[x + y * parent.width] =  population.getFill().getRGB();
		} catch (ArrayIndexOutOfBoundsException e) {
			//if a part of a circle is outside of the canvas -> do nothing
		}
	}
	
	public static void setParent(PApplet p) {
		parent = p;
	}	

	public static void setTarget(Vector2D target) {
		Rocket.target = target;
	}

	public Population getPopulation() {
		return population;
	}

	private void setCrashed(boolean crashed) {
		this.crashed = crashed;
	}

	public boolean hasCrashed() {
		return crashed;
	}

	public DNA getDna() {
		return dna;
	}

	public void setDna(DNA dna) {
		this.dna = dna;
	}

	public boolean hasCrashedOnObstacle() {
		return crashedOnObstacle;
	}

	private void setCrashedOnObstacle(boolean crashedOnObstacle) {
		this.crashedOnObstacle = crashedOnObstacle;
	}

	public Double getFitness() {
		return fitness;
	}

	public void setFitness(Double fitness) {
		this.fitness = fitness;
	}

	public Integer getFinishTime() {
		return finishTime;
	}

	private void setFinishTime(Integer finishTime) {
		this.finishTime = finishTime;
	}

	public Integer getPassedObstacleTime() {
		return passedObstacleTime;
	}

	private void setPassedObstacleTime(Integer passedObstacleTime) {
		this.passedObstacleTime = passedObstacleTime;
	}

	public boolean hasFinished() {
		return finished;
	}

	private void setFinished(boolean finished) {
		this.finished = finished;
	}

	public Vector2D getPos() {
		return pos;
	}

	public static PApplet getParent() {
		return parent;
	}
}








