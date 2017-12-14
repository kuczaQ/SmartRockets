package exec;

import java.awt.Color;
import objects.CalculateFitness;

public class PopulationBlueprint {
	public final int POPULATION_SIZE;
	public final CalculateFitness CALCULATE_FITNESS;
	public final Color COLOR;
	
	public PopulationBlueprint(int popSize, CalculateFitness calc, Color color) {
		this.POPULATION_SIZE = popSize;
		this.CALCULATE_FITNESS = calc;	
		this.COLOR = color;
	}
}
