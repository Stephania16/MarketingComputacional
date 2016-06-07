package minimax;

import java.util.ArrayList;

/** Clase abstracta que representa al algoritmo minimax */
public abstract class MinimaxAlgorithm {

	protected static int mPrevTurns = 5; // Number of previous turns to compute
											// (tp)
	protected static int mNTurns = 5; // Number of turns to play (tf)
	protected static int mNPlayers = 2; // Number of turns to play (tf)

	protected static int MAX_DEPTH_0 = 3; // Maximun depth of the minimax
	protected static int MAX_DEPTH_1 = 2; // Maximun depth of the minimax

	private int MY_PLAYER = 0;

	private ArrayList<ArrayList<Integer>> mFitnessAcumulated;

	public void playMinimaxAlgorithm() throws Exception {
		mFitnessAcumulated = new ArrayList<>();
		for (int i = 0; i < mNPlayers; i++)
			mFitnessAcumulated.add(new ArrayList<Integer>());

		for (int i = 0; i < mNTurns; i++) {
			for (int j = 0; j < mNPlayers; j++) {
				changeProduct(j);
				updateCustGathered();
			}
		}
	}

	public void changeProduct(int playerIndex) throws Exception {
		int depth;

		if (playerIndex == MY_PLAYER)
			depth = MAX_DEPTH_0;
		else
			depth = MAX_DEPTH_1;

		ArrayList<Object> listObjects = new ArrayList<>();
		for (int i = 0; i < mNPlayers; i++)
			listObjects.add(getObject(i));

		StrAB ab = alphaBetaInit(listObjects, playerIndex, depth, Integer.MIN_VALUE, Integer.MAX_VALUE);
		setSolution(playerIndex, ab.getDimension(), ab.getSolution());

	}

	private StrAB alphaBetaInit(ArrayList<Object> listObjects, int playerIndex, int depth, int alpha, int beta)
			throws Exception {

		ArrayList<StrAB> abList = new ArrayList<>();
		StrAB ab = new StrAB();
		int acumulatedFitness;
		boolean repetedChild = false; // To prune repeated childs

		for (int dimension = 0; dimension < getDimens(); dimension++) {
			for (int SolutionSpaceIndex = 0; SolutionSpaceIndex < getSolutionsSpace(dimension); SolutionSpaceIndex++) {
				if (isPosibleToChange(playerIndex, dimension, SolutionSpaceIndex)) {
					if (getSolution(playerIndex, dimension) != SolutionSpaceIndex || !repetedChild) {

						if (getSolution(playerIndex, dimension) == SolutionSpaceIndex)
							repetedChild = true;

						// Computing Childs
						ArrayList<Object> childs = new ArrayList<>();
						for (int i = 0; i < listObjects.size(); i++)
							childs.add(listObjects.get(i));

						changeChild(childs.get(playerIndex), dimension, SolutionSpaceIndex);

						acumulatedFitness = getFitness(childs.get(playerIndex), 0);

						ab.setAlphaBeta(alphaBeta(childs, acumulatedFitness, playerIndex, (playerIndex + 1) % 2,
								depth - 1, alpha, beta, false));
						ab.setDimension(dimension);
						ab.setSolution(SolutionSpaceIndex);

						abList.add(ab);
						alpha = Math.max(alpha, ab.getAlphaBeta());
					}
				}
			}
		}
		return bestMovement(abList, alpha);
	}

	private int alphaBeta(ArrayList<Object> listObjects, int acumulatedFitness, int playerIndex, int prodIndex,
			int depth, int alpha, int beta, boolean maximizingPlayer) throws Exception {

		boolean exitFor;
		int fitness;
		boolean repeatedChild = false; // To prune repeated childs

		// It is a terminal node
		if (depth == 0)
			return acumulatedFitness;

		for (int dimension = 0; dimension < getDimens(); dimension++) {
			exitFor = false;
			for (int SolutionSpaceIndex = 0; SolutionSpaceIndex < getSolutionsSpace(dimension); SolutionSpaceIndex++) {
				if (isPosibleToChange(playerIndex, dimension, SolutionSpaceIndex)) {
					if (getSolution(playerIndex, dimension) != SolutionSpaceIndex || !repeatedChild) {

						if (getSolution(playerIndex, dimension) == SolutionSpaceIndex)
							repeatedChild = true;

						// Computing Childs
						ArrayList<Object> childs = new ArrayList<>();
						for (int i = 0; i < listObjects.size(); i++)
							childs.add(listObjects.get(i));

						changeChild(childs.get(playerIndex), dimension, SolutionSpaceIndex);

						fitness = getFitness(childs.get(playerIndex), 0);

						if (maximizingPlayer) {
							alpha = Math.max(alpha, alphaBeta(childs, acumulatedFitness + fitness, playerIndex,
									(prodIndex + 1) % 2, depth - 1, alpha, beta, false));

							if (beta < alpha) {
								exitFor = true;
								break;
							}
						} else {
							beta = Math.max(beta, alphaBeta(childs, acumulatedFitness + fitness, playerIndex,
									(prodIndex + 1) % 2, depth - 1, alpha, beta, true));

							if (beta < alpha) {
								exitFor = true;
								break;
							}
						}
					}
				}
			}
			if (exitFor)
				break;
		}

		if (maximizingPlayer)
			return alpha;
		else
			return beta;
	}

	private StrAB bestMovement(ArrayList<StrAB> abList, int best) {
		StrAB ab = new StrAB();

		for (int i = 0; i < abList.size(); i++) {
			if (abList.get(i).getAlphaBeta() == best) {
				ab.setSolution(abList.get(i).getSolution());
				ab.setDimension(abList.get(i).getDimension());
				ab.setAlphaBeta(abList.get(i).getAlphaBeta());
			}
		}
		return ab;
	}

	private void updateCustGathered() throws Exception {

		for (int i = 0; i < mNPlayers; i++) {
			int wsc = getFitness(getObject(i), i);

			if (mFitnessAcumulated.get(i).size() == mPrevTurns * 2) {
				mFitnessAcumulated.get(i).remove(0);
			}

			mFitnessAcumulated.get(i).add(wsc);
			setFitnessAcumulated(i, mFitnessAcumulated.get(i));
		}
	}

	protected abstract void setFitnessAcumulated(int i, ArrayList<Integer> integers);

	public abstract Object getObject(int producerIndex);

	public abstract Integer getFitness(Object object, int index) throws Exception;

	public abstract int getDimens();

	protected abstract int getSolutionsSpace(int dimen);

	protected abstract int getSolution(int playerIndex, int dimension);

	protected abstract boolean isPosibleToChange(int playerIndex, int dimension, int solution);

	protected abstract void changeChild(Object o, int dimension, int solutionSpaceIndex);

	protected abstract void setSolution(int playerIndex, int dimension, int solution);

}
