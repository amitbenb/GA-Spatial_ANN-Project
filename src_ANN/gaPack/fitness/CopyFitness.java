package gaPack.fitness;

import gaPack.ANN_Individual;

public class CopyFitness implements ANN_Fitness
{

	@Override
	public void generateSuites(ANN_Individual ind)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public double calculateFitness(ANN_Individual ind)
	{
		final int NUMBER_OF_TESTS = 50; 
		double fit = 0.0;
		for (int i = 0; i < NUMBER_OF_TESTS; i++)
		{
			double[] input = new double[ind.getAnn().getNumOfInputs()];
			generateRandomInput(input);
			ind.runNetwork(input);
			double[] output= ind.getAnn().getOutputs();
			fit += similarityBonus(input,output);
		}

		return fit/NUMBER_OF_TESTS;
	}

	private double similarityBonus(double[] input, double[] output)
	{
		double fit = 0.0;
		int len = Math.min(input.length, output.length);
		
		for (int i = 0; i < len; i++)
		{
			double dist = Math.abs(input[i]-output[i]);

//			System.out.print(output[i] + " ");
			
			if (dist < 0.5)
				fit += (1 - dist);
			
		}
		
//		System.out.println();
		
		return fit;
	}
	
	private void generateRandomInput(double[] input)
	{
		for (int i = 0; i < input.length; i++)
		{
			input[i] = (Math.random()<0.5)?(-1):(1);
		}		
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
