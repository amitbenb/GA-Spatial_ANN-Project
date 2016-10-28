package gaPack.fitness;

import popPack.Base_Fitness;
import popPack.indPack.GA_Individual;
import gaPack.ANN_Individual;

public interface ANN_Fitness extends Base_Fitness
{
	abstract public void generateSuites(ANN_Individual ind);
	abstract public double calculateFitness(ANN_Individual ind);
	abstract public double calculateBenchmark(ANN_Individual ind);

}
