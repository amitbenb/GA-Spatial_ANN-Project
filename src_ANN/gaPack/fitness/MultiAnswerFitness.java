package gaPack.fitness;

import ann.Spatial_ANN_Builder;
import gaPack.ANN_Individual;

public class MultiAnswerFitness extends TestSuiteAnnFitness
{

	@Override
	protected double suiteFitnessBonus(double[] input, double[] perfectOutput,
			double[] actualOutput)
	{
		// TODO Auto-generated method stub
		if (perfectOutput.length != actualOutput.length)
			throw new RuntimeException("Length Mismatch error in MultiAnswerFitness.suiteFitnessBonus");
		
		double fit = 0.0;
		double perfect1s = 0.0, actual1s = 0.0; 
		double perfectMinus1s = 0.0, actualMinus1s = 0.0; 
		
		// Calculate basic Bonus.
		for (int i = 0; i < perfectOutput.length; i++)
		{
			if (perfectOutput[i] == 1)
			{
				perfect1s++;
				if (actualOutput[i] > 0.5)
					actual1s++;
			}
			else if (perfectOutput[i] == -1)
			{
				perfectMinus1s++;
				if (actualOutput[i] < -0.5)
					actualMinus1s++;
			}
			else;
		}
		
		fit = (actual1s/perfect1s + actualMinus1s/perfectMinus1s) / 2;

		
		// Check if answer is correct.
		boolean[] correctRow = new boolean[this.NUMBER_OF_ROWS], chosenRow = new boolean[this.NUMBER_OF_ROWS];
		int[] voteArray = new int[this.NUMBER_OF_ROWS];
		int maxVote = 0;


		for (int i = 0; i < this.NUMBER_OF_ROWS; i++)
		{

			if (perfectOutput[i * this.NUMBER_OF_COLS] == 1)
			correctRow[i] = true;
			
			for (int j = 0; j < perfectOutput.length / this.NUMBER_OF_ROWS; j++)
			{
				if (actualOutput[i * this.NUMBER_OF_COLS + j] > 0.5)
					voteArray[i] += 1;
			}

			if (voteArray[i] > maxVote)
			{
				maxVote = voteArray[i];
				chosenRow = new boolean[this.NUMBER_OF_ROWS];
				chosenRow[i] = true;
			}
			else if (voteArray[i] == maxVote)
			{
				chosenRow[i] = true;
			}
		}
		
		// fit <= 1 after this.
		// But better if output gives right answer as a whole.
		fit = fit * (1 - getCorrectTestWeight());

		boolean answerCorrectFlag = true;
		for (int i = 0; answerCorrectFlag && i < chosenRow.length; i++)
		{
			if (chosenRow[i] && !correctRow[i])
				answerCorrectFlag = false;
		}
		
		if (answerCorrectFlag)
		{
			fit = fit + getCorrectTestWeight();
		}
		
		return fit;
	}

	@Override
	/**
	 * Checking the bunus for a certain test.
	 * 
	 * @param input
	 *            Test input
	 * @param perfectOutput
	 *            Test perfect output
	 * @param actualOutput
	 *            Test actual output
	 * @param extraData
	 *            will hold the win margin (in [0]) of choice in actual output,
	 *            and maximum row signal (in [1])
	 * @return
	 */
	protected double suiteBenchmarkBonus(double[] input, double[] perfectOutput,
			double[] actualOutput, int[] extraData)
	{
		// TODO Auto-generated method stub
		if (perfectOutput.length != actualOutput.length)
			throw new RuntimeException(
					"Length Mismatch error in ReadingFitness.suiteFitnessBonus");

		// Only use this for statistics not normal benchmarking. In normal
		// benchmarking value gets lost and that's OK.
		if (extraData == null)
			extraData = new int[2];

		double fit = 0.0;
		boolean[] correctRow = new boolean[this.NUMBER_OF_ROWS], chosenRow = new boolean[this.NUMBER_OF_ROWS];
		int[] voteArray = new int[this.NUMBER_OF_ROWS];
		int maxVote = 0;


		for (int i = 0; i < this.NUMBER_OF_ROWS; i++)
		{
			if (perfectOutput[i * this.NUMBER_OF_COLS] == 1)
				correctRow[i] = true;

			for (int j = 0; j < perfectOutput.length / this.NUMBER_OF_ROWS; j++)
			{
				if (actualOutput[i * this.NUMBER_OF_COLS + j] > 0.5)
					voteArray[i] += 1;
			}

			if (voteArray[i] > maxVote)
			{
				extraData[0] = voteArray[i] - maxVote; 
				extraData[1] = voteArray[i];
				maxVote = voteArray[i];
				chosenRow = new boolean[this.NUMBER_OF_ROWS];
				chosenRow[i] = true;
			}
			else if (voteArray[i] == maxVote)
			{
				chosenRow[i] = true;
			}

		}

		boolean answerCorrectFlag = true;
		for (int i = 0; answerCorrectFlag && i < chosenRow.length; i++)
		{
			if (chosenRow[i] && !correctRow[i])
				answerCorrectFlag = false;
		}
		
		if (answerCorrectFlag)
		{
			fit = 1.0;
		}

		return fit;
	}

	@Override
	protected double analyzeBenchmark(ANN_Individual ind, String[] analysis)
	{
		double fit = 0.0;
		int marginSum = 0 , maxSignalSum = 0;
		int marginSumAll = 0 , maxSignalSumAll = 0;
		
		// Start analysis Strings
		for (int i = 0; i < analysis.length; i++)
		{
			analysis[i] = new String();
		}
		
		for (int i = 0; i < benchSuite.size(); i++)
		{
			double[] input = benchSuite.get(i);
			double[] perfectOutput = benchSuiteOutputs.get(i);
			ind.buildNetwork(ind.getBuilder());
			ind.runNetwork(input);
			double[] actualOutput = ind.getAnn().getOutputs();
			
			int[] signalDataArr = new int[2];

			double bonus = suiteBenchmarkBonus(input, perfectOutput, actualOutput, signalDataArr);
			fit += bonus;
			if (bonus == 1.0)
			{
				marginSum += signalDataArr[0];
				maxSignalSum += signalDataArr[1];
			}
			marginSumAll += signalDataArr[0];
			maxSignalSumAll += signalDataArr[1];
			
			analysis[1] = analysis[1] + "Test #" + i + " input:\n";
			analysis[1] = analysis[1] + Spatial_ANN_Builder.printArrayFixed(input,Spatial_ANN_Builder.m_inGridSize);
			analysis[1] = analysis[1] + "\n";

			analysis[1] = analysis[1] + "Test #" + i + " output:\n";
			analysis[1] = analysis[1] + Spatial_ANN_Builder.printArrayFixed(actualOutput,Spatial_ANN_Builder.m_outGridSize);
			analysis[1] = analysis[1] + "\n";
			
			analysis[1] = analysis[1] + "Test #" + i + " expected output:\n";
			analysis[1] = analysis[1] + Spatial_ANN_Builder.printArrayFixed(perfectOutput,Spatial_ANN_Builder.m_outGridSize);
			analysis[1] = analysis[1] + "\n";
		}
		
		double numOfcorrectOutputs = fit;
		fit = fit/benchSuite.size();
		
		analysis[0] = analysis[0] + "Best individual Benchmark score: " + (int) (100000 * fit) / 100.0 + "\n";
		analysis[0] = analysis[0] + "Number of inner nodes: " + ind.getAnn().getNeuronNumber() + "\n";
		analysis[0] = analysis[0] + "Number of links: " + ind.getAnn().getLinkNumber() + "\n";
		
		analysis[0] = analysis[0] + "Max signal average (correct only): " + maxSignalSum / numOfcorrectOutputs + "\n"; 
		analysis[0] = analysis[0] + "Max signal average (all): " + maxSignalSumAll / (double)benchSuite.size() + "\n"; 
		analysis[0] = analysis[0] + "Margin average (correct only): " + marginSum / numOfcorrectOutputs + "\n"; 
		analysis[0] = analysis[0] + "Margin average (all): " + marginSumAll / (double)benchSuite.size() + "\n"; 

		return (int) (100000 * fit) / 100.0;
	}

	@Override
	public String generateFitnessDataLine(ANN_Individual ind)
	{
		String retVal = new String("");

		double fit = 0.0;
		int marginSum = 0 , maxSignalSum = 0;
		int marginSumAll = 0 , maxSignalSumAll = 0;
		
	
		for (int i = 0; i < benchSuite.size(); i++)
		{
			double[] input = benchSuite.get(i);
			double[] perfectOutput = benchSuiteOutputs.get(i);
			ind.buildNetwork(ind.getBuilder());
			ind.runNetwork(input);
			double[] actualOutput = ind.getAnn().getOutputs();
			
			int[] signalDataArr = new int[2];

			double bonus = suiteBenchmarkBonus(input, perfectOutput, actualOutput, signalDataArr);
			fit += bonus;
			if (bonus == 1.0)
			{
				marginSum += signalDataArr[0];
				maxSignalSum += signalDataArr[1];
			}
			marginSumAll += signalDataArr[0];
			maxSignalSumAll += signalDataArr[1];
			
		}
		
		double numOfcorrectOutputs = fit;
		fit = fit/benchSuite.size();
		
		retVal = retVal + (int) (100000 * fit) / 100.0 + "\t"; // Benchmark
		retVal = retVal + ind.getAnn().getNeuronNumber() + "\t"; // Inner Nodes
		retVal = retVal + ind.getAnn().getLinkNumber() + "\t"; // Link number

		retVal = retVal + maxSignalSum / numOfcorrectOutputs + "\t"; // Max signal average (correct only)
		retVal = retVal + maxSignalSumAll / (double)benchSuite.size() + "\t"; // Max signal average (all)
		retVal = retVal + marginSum / numOfcorrectOutputs + "\t"; // Margin average (correct only)
		retVal = retVal + marginSumAll / (double)benchSuite.size() + "\n"; // Margin average (all)

		return retVal;
	}

	@Override
	public String generateFitnessDataTableHeader(ANN_Individual best)
	{
		String retVal = new String();
		retVal =
				"Benchmark" + "\t" + "Inner node #" + "\t" + "Link #" + "\t"
						+ "Max Signal avg (correct)" + "\t" + "Max Signal avg (all)" + "\t"
						+ "Margine Average (correct)" + "\t" + "Margine Average (all)" + "\n";
		return retVal; 
	}

}
