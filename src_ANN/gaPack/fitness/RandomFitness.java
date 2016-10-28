package gaPack.fitness;

import popPack.indPack.GA_Individual;
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
	public String generateFitnessDataText(GA_Individual ind_)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public String generateFitnessDataLine(GA_Individual ind_)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String generateFitnessDataTableHeader()
	{
		// TODO Auto-generated method stub
		return null;
	}

}
