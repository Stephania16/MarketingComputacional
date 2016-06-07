package sa;

/** Clase abstract que representa el algoritmo SA */
public abstract class SimulatedAnnealingAlgorithm {

	protected static double Start_TEMP = 1000;
	protected static double TEMPERATURE = Start_TEMP;
	protected static double coolingRate = 0.006;

	public Object solve_SA(Object BestObjFound) throws Exception {

		Object bestObject = BestObjFound;
		Object currency_object = BestObjFound;

		TEMPERATURE = Start_TEMP;
		while (TEMPERATURE > 1) {

			Object new_obj = changeObject(currency_object);

			int old_energy = getFitness(currency_object);
			int new_energy = getFitness(new_obj);

			// CALCULATE THE ACCEPTED FUNCTION
			if (acceptanceProbability(old_energy, new_energy) > Math.random()) {
				currency_object = new_obj;

				// ACTUALIZE THE BEST
				if (getFitness(currency_object) > getFitness(bestObject))
					bestObject = currency_object;
			}

			// Cool system
			TEMPERATURE *= 1 - coolingRate;
		}
		return bestObject;
	}

	protected double acceptanceProbability(int OLD_fitness, int NEW_fitness) {
		double ret;
		if (NEW_fitness > OLD_fitness)
			ret = 1;
		else
			ret = Math.exp((NEW_fitness - OLD_fitness) / TEMPERATURE);

		return ret;
	}

	abstract public Object changeObject(Object new_obj);

	abstract public int getFitness(Object origin) throws Exception;
}
