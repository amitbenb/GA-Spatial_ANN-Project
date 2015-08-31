package gaPack.fitness;

import gaPack.ANN_Individual;

public class RandomFitness implements ANN_Fitness
{

	@Override
	public void generateSuites(ANN_Individual ind)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public double calculateFitness(ANN_Individual ind)
	{
		ind.runNetwork();
		return Math.random();
	}

	public double calculateBenchmark(ANN_Individual ind)
	{
		return calculateFitness(ind);		
	}

	@Override
	public String generateFitnessDataText(ANN_Individual ind)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public String generateFitnessDataLine(ANN_Individual ind)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String generateFitnessDataTableHeader(ANN_Individual best)
	{
		// TODO Auto-generated method stub
		return null;
	}

}
