package gaPack.fitness;

import java.io.FileWriter;

import popPack.indPack.GA_Individual;
import gaPack.ANN_Individual;

public interface ANN_Fitness
{
	abstract public void generateSuites(ANN_Individual ind);
	abstract public double calculateFitness(ANN_Individual ind);
	abstract public double calculateBenchmark(ANN_Individual ind);

	abstract public String generateFitnessDataText(ANN_Individual ind);
	abstract public String generateFitnessDataLine(ANN_Individual ind);
	abstract public String generateFitnessDataTableHeader(ANN_Individual best);
}
