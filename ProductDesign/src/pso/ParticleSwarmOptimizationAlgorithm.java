package pso;

import java.util.ArrayList;
import java.util.Random;

import javax.swing.JTextArea;

import Problem.Problem;

public class ParticleSwarmOptimizationAlgorithm extends Problem{
    //private static ParticleSwarmOptimizationAlgorithm PSOinstance = null;

    private ArrayList<Integer> ParticleBestWSC = new ArrayList<>(); /* Stores the best wsc found */
    private ArrayList<Object> BestObjects = new ArrayList<>(); /* Stores the best wsc found */
    private ArrayList<ArrayList<Double>> Velocity = new ArrayList<>();

    private ArrayList<Object> SWARM = new ArrayList<>();
    private ArrayList<Integer> Fitness = new ArrayList<>();
    private Object Best = null;
    private Integer BestFitness = 0;
    private int MAX_ITERATION = 60;
    private int SWARM_SIZE = 20;

    double W_UPPERBOUND = 1;
    double W_LOWERBOUND = 0;
    double C1 = 2.0;
    double C2 = 2.0;
    double VEL_LOW = -2;
    double VEL_HIGH = 2;

  /*  public static ParticleSwarmOptimizationAlgorithm getInstance() {

        if (PSOinstance == null)
            PSOinstance = new ParticleSwarmOptimizationAlgorithm();

        return PSOinstance;
    }*/

    protected void startProblem(JTextArea jtA) throws Exception {
        initializePSOproblem();
    }

    protected Object solveProblem() throws Exception {

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

//            // STEP 2 - UPDATE GENERAL BEST
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

                    //STEP 3 - UPDATE VELOCITY
                    double vel = (w * Velocity.get(k).get(p)) +
                            (r1 * C1) * (getLocationValue(BestObjects.get(k), p) - getLocationValue(obj, p)) +
                            (r2 * C2) * (getLocationValue(Best, p) - getLocationValue(obj, p));

//                    product.getVelocity().put(TotalAttributes.get(p), vel);
                    Velocity.get(k).set(p, vel);


                    //STEP 4 - UPDATE LOCATION
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


    private void updateFitness() throws Exception {
        for (int i = 0; i < SWARM.size(); i++) {
            int ParticleFitness = getFitness(SWARM.get(i));
            if (ParticleFitness > Fitness.get(i)) {
                Fitness.set(i, ParticleFitness);
            }
        }
    }

	public int getSWARM_SIZE() {
		return SWARM_SIZE;
	}

	public double getW_UPPERBOUND() {
		return W_UPPERBOUND;
	}

	public double getW_LOWERBOUND() {
		return W_LOWERBOUND;
	}

	public double getC1() {
		return C1;
	}

	public double getC2() {
		return C2;
	}

	public double getVEL_LOW() {
		return VEL_LOW;
	}

	public double getVEL_HIGH() {
		return VEL_HIGH;
	}

	public void setSWARM_SIZE(int sWARM_SIZE) {
		SWARM_SIZE = sWARM_SIZE;
	}

	public void setW_UPPERBOUND(double w_UPPERBOUND) {
		W_UPPERBOUND = w_UPPERBOUND;
	}

	public void setW_LOWERBOUND(double w_LOWERBOUND) {
		W_LOWERBOUND = w_LOWERBOUND;
	}

	public void setC1(double c1) {
		C1 = c1;
	}

	public void setC2(double c2) {
		C2 = c2;
	}

	public void setVEL_LOW(double vEL_LOW) {
		VEL_LOW = vEL_LOW;
	}

	public void setVEL_HIGH(double vEL_HIGH) {
		VEL_HIGH = vEL_HIGH;
	}

	public int getMAX_ITERATION() {
		return MAX_ITERATION;
	}

	public void setMAX_ITERATION(int mAX_ITERATION) {
		MAX_ITERATION = mAX_ITERATION;
	}
    
    

}
