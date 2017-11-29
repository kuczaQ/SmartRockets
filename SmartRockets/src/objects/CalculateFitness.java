package objects;

import processing.core.PVector;

public interface CalculateFitness {
	public double calc(Rocket r, Boolean success, Population pop);
}
