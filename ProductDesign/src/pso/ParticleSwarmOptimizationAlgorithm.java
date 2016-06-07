package pso;

import java.util.ArrayList;
import java.util.Random;

/**
 * Clase abstracta que representa el algoritmo PSO
 */
public abstract class ParticleSwarmOptimizationAlgorithm {

	private ArrayList<Integer> ParticleBestWSC = new ArrayList<>();
	private ArrayList<Object> BestObjects = new ArrayList<>();
	private ArrayList<ArrayList<Double>> Velocity = new ArrayList<>();

	private ArrayList<Object> SWARM = new ArrayList<>();
	private Object Best = null;
	private Integer BestFitness = 0;
	protected static int MAX_ITERATION = 60;

	protected static int SWARM_SIZE = 20;

	protected static double W_UPPERBOUND = 1;
	protected static double W_LOWERBOUND = 0;
	protected static double C1 = 2.0;
	protected static double C2 = 2.0;
	protected static double VEL_LOW = -2;
	protected static double VEL_HIGH = 2;

	public ArrayList<Integer> Fitness = new ArrayList<>();

	public ArrayList<Object> solvePSOAlgorithm() throws Exception {

		Velocity = new ArrayList<>();
		Fitness = new ArrayList<>();
		BestObjects = new ArrayList<>();
		ParticleBestWSC = new ArrayList<>();

		Best = null;
		BestFitness = 0;

		SWARM = createInitSwarm();

		for (int i = 0; i < SWARM.size(); i++) {
			ArrayList<Double> vel = new ArrayList<>();
			for (int j = 0; j < getDimensions(); j++)
				vel.add(((VEL_HIGH - VEL_LOW) * Math.random()) + VEL_LOW);
			Velocity.add(vel);
		}

		for (int i = 0; i < SWARM.size(); i++) {
			Fitness.add(getFitness(SWARM.get(i)));
			ParticleBestWSC.add(Fitness.get(i));
			BestObjects.add(SWARM.get(i));
		}

		double w;
		for (int i = 0; i < MAX_ITERATION; i++) {

			// STEP 1 - UPDATE PARTICLE'S BEST
			for (int j = 0; j < SWARM_SIZE; j++) {
				if (Fitness.get(j) > ParticleBestWSC.get(j)) {
					ParticleBestWSC.set(j, Fitness.get(j));
					BestObjects.set(j, SWARM.get(j));
				}
			}

			// // STEP 2 - UPDATE GENERAL BEST
			for (int j = 0; j < Fitness.size(); j++) {
				if (Fitness.get(j) > BestFitness) {
					BestFitness = Fitness.get(j);
					Best = SWARM.get(j);
				}
			}

			w = W_UPPERBOUND - (((double) i) / MAX_ITERATION) * (W_UPPERBOUND - W_LOWERBOUND);

			for (int k = 0; k < SWARM_SIZE; k++) {

				Random generator = new Random();

				double r1 = generator.nextDouble();
				double r2 = generator.nextDouble();

				Object obj = SWARM.get(k);

				for (int p = 0; p < Velocity.get(k).size(); p++) {

					// STEP 3 - UPDATE VELOCITY
					double vel = (w * Velocity.get(k).get(p))
							+ (r1 * C1) * (getLocationValue(BestObjects.get(k), p) - getLocationValue(obj, p))
							+ (r2 * C2) * (getLocationValue(Best, p) - getLocationValue(obj, p));

					// product.getVelocity().put(TotalAttributes.get(p), vel);
					Velocity.get(k).set(p, vel);

					// STEP 4 - UPDATE LOCATION
					int new_value_for_location = (int) (getLocationValue(obj, p) + Velocity.get(k).get(p));
					updateLocation(obj, p, new_value_for_location);
				}
			}

			updateFitness();
		}

		// STEP 1 - UPDATE PARTICLE'S BEST
		for (int j = 0; j < SWARM_SIZE; j++) {
			if (Fitness.get(j) > ParticleBestWSC.get(j)) {
				ParticleBestWSC.set(j, Fitness.get(j));
				BestObjects.set(j, SWARM.get(j));
			}
		}

		return BestObjects;
	}

	protected abstract int getDimensions();

	private void updateFitness() throws Exception {
		for (int i = 0; i < SWARM.size(); i++) {
			int ParticleFitness = getFitness(SWARM.get(i));
			if (ParticleFitness > Fitness.get(i)) {
				Fitness.set(i, ParticleFitness);
			}
		}
	}

	public abstract ArrayList<Object> createInitSwarm() throws Exception;

	protected abstract void updateLocation(Object obj, int p, int new_value_for_location);

	protected abstract Integer getLocationValue(Object obj, int dimen);

	protected abstract Integer getFitness(Object object) throws Exception;
}
