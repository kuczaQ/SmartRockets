package exec;

import objects.Rocket;

public class Worker extends Thread {
	public Rocket[] toProcess;
	public ProcessingFunction func;

	public Worker(ProcessingFunction func, Rocket... r) {
		this.func = func;
		toProcess = r;
	}

	@Override
	public void run() {
		func.process(toProcess);
	}
}