package exec;

import java.util.ArrayList;

import objects.Rocket;

public abstract class ProcessArrayListFunction {
	int progress;
	
	public abstract void process(ArrayList<Rocket> arr);
}
