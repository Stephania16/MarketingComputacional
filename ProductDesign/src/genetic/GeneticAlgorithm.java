package genetic;

import java.util.ArrayList;
import javax.swing.JTextArea;

import Problem.Problem;
import general.StoredData;

public class GeneticAlgorithm extends Problem {

	/* GA VARIABLES */
	static int CROSSOVER_PROB = 80; /* % of crossover */
    static int MUTATION_PROB = 1; /* % of mutation */
    static int NUM_GENERATIONS = 100; /* number of generations */

    ArrayList<Object> Population = new ArrayList<>();
    ArrayList<Integer> Fitness = new ArrayList<>();

    ArrayList<Object> Best = new ArrayList<>();
    ArrayList<Integer> BestFitness = new ArrayList<>();
    
   /* private static GeneticAlgorithmNEW GA = null;

    public static GeneticAlgorithmNEW getInstance(){
        if(GA == null)
            return new GeneticAlgorithmNEW();

        return GA;
    }*/
    private ArrayList<Object> tournament(ArrayList<Object> newPopulation, ArrayList<Integer> newFitness) {
        ArrayList<Object> nextGeneration = new ArrayList<>();
        for (int i = 0; i < newPopulation.size(); i++) {

            if (Fitness.get(i) >= newFitness.get(i))
                nextGeneration.add((Population.get(i)));
            else {

                nextGeneration.add((newPopulation.get(i)));
                Fitness.set(i, newFitness.get(i));// We update the fitness of the new individual

                int worstIndex = isBetweenBest(Fitness.get(i));
                if (worstIndex != -1) {
                    BestFitness.set(worstIndex, Fitness.get(i));
                    Best.set(worstIndex, newPopulation.get(i));
                }
            }
        }
        return nextGeneration;
    }

    @Override
    protected void startProblem(JTextArea jtA) throws Exception {
        solvePD_GA();
    }

    @Override
    protected Object solveProblem() throws Exception {
        Fitness = new ArrayList<>();
        Best = new ArrayList<>();
        BestFitness = new ArrayList<>();

        Population = createInitPopulation();

        for(int i = 0; i < Population.size(); i++)
            Fitness.add(getFitness(Population.get(i)));

        for(int i = 0; i < Fitness.size();i++){
            if(i < StoredData.number_Products){
                BestFitness.add(Fitness.get(i));
                Best.add(Population.get(i));
            }else {
                int worstIndex = isBetweenBest(Fitness.get(i));
                if (worstIndex != -1) {
                    BestFitness.set(worstIndex, Fitness.get(i));
                    Best.set(worstIndex, Population.get(i));
                }
            }
        }

        ArrayList<Object> NewPopulation;
        ArrayList<Integer> newFitness = new ArrayList<>();
        for(int i = 0; i < NUM_GENERATIONS; i++){
            NewPopulation = createNewPopulation(newFitness);
            Population = tournament(NewPopulation, newFitness);
        }
        return Best;
    }

    private int isBetweenBest(int fitness) {
        for (int i = 0; i < BestFitness.size(); i++) {
            if (fitness > BestFitness.get(i))
                return i;
        }
        return -1;
    }

    private ArrayList<Object> createNewPopulation(ArrayList<Integer> newFitness) throws Exception {
        newFitness.clear();

        int fitnessSum = computeFitnessSum();
        ArrayList<Object> newPopu = new ArrayList<>();
        int father, mother;
        Object son;

        for (int i = 0; i < Population.size(); i++) {
            father = chooseFather(fitnessSum);
            mother = chooseFather(fitnessSum);
            son = mutate(breed(Population.get(father), Population.get(mother)));

            newPopu.add(son);

            newFitness.add(getFitness(newPopu.get(i)));

        }

        return newPopu;
    }

    /**
     * Computing the sum of the fitness of all the population
     */
    private int computeFitnessSum() {
        int sum = 0;
        for (int i = 0; i < Fitness.size() - 1; i++) {
            sum += Fitness.get(i);
        }
        return sum;
    }

    /**
     * Chosing the father in a random way taking into account the fitness
     */
    private int chooseFather(double fitnessSum) {
        int fatherPos = 0;
        double rndVal = fitnessSum * Math.random();
        double accumulator = Fitness.get(fatherPos);
        while (rndVal > accumulator) {
            fatherPos += 1;
            accumulator += Fitness.get(fatherPos);
        }
        return fatherPos;
    }

	public int getCROSSOVER_PROB() {
		return CROSSOVER_PROB;
	}

	public int getMUTATION_PROB() {
		return MUTATION_PROB;
	}

	public int getNUM_GENERATIONS() {
		return NUM_GENERATIONS;
	}

	public void setCROSSOVER_PROB(int cROSSOVER_PROB) {
		CROSSOVER_PROB = cROSSOVER_PROB;
	}

	public void setMUTATION_PROB(int mUTATION_PROB) {
		MUTATION_PROB = mUTATION_PROB;
	}

	public void setNUM_GENERATIONS(int nUM_GENERATIONS) {
		NUM_GENERATIONS = nUM_GENERATIONS;
	}



    
}
