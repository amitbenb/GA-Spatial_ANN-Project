package gaPack.fitness;

import popPack.indPack.GA_Individual;
import gaPack.ANN_Individual;

public class OrdinalFitness implements ANN_Fitness
{
	boolean ASCENDING_ALLOWED = false, DESCENDING_ALLOWED = true;
	boolean CONSTANT_SUITE = true;
	boolean INPUTS_SEPERATED = true;
	
	int NUMBER_OF_BLOCKS = 2; // This is changed from the outside.
	
	final int NUMBER_OF_TESTS = 200;
	final int NUMBER_OF_BENCHMARK_TESTS = 1000;

	double[][] learningSuite = null;
	double[][] benchmarkSuite = null;

	@Override
	public void generateSuites(ANN_Individual ind)
	{
		// If no need for new suite.
		if (CONSTANT_SUITE && learningSuite != null)
			return;
		
		// Setup number of blocks to be number of rows.
		NUMBER_OF_BLOCKS = Math.max(ind.getAnn().getInDimentionSize(0), 2);
//		System.out.println(NUMBER_OF_BLOCKS);
		
//		if (learningSuite == null)
		learningSuite = new double[NUMBER_OF_TESTS][];
		for (int i = 0; i < learningSuite.length; i++)
		{
			learningSuite[i] = new double[ind.getAnn().getNumOfInputs()];
			
			generateInput(learningSuite[i]);
			
			// Fix bad inputs.
			if(NUMBER_OF_BLOCKS > 2 && !legalTest(learningSuite[i]))
				i--;
//			else
//			{
//				if(isAscending(learningSuite[i]))
//				{
//					for (int j = 0; j < learningSuite[i].length; j++)
//					{
//						System.out.print(learningSuite[i][j] + 1 + " ");
//						if (j % 10 == 9)
//							System.out.println();
//					}
//					System.out.println();
//				}
//			}
			
		}
		
		if (benchmarkSuite != null)
			return;
		
		benchmarkSuite = new double[NUMBER_OF_BENCHMARK_TESTS][];
		for (int i = 0; i < benchmarkSuite.length; i++)
		{
			benchmarkSuite[i] = new double[ind.getAnn().getNumOfInputs()];
			
			generateInput(benchmarkSuite[i]);
			
			// Fix bad inputs.
			if(NUMBER_OF_BLOCKS > 2 && !legalTest(benchmarkSuite[i]))
				i--;
		}
		
	}
	
	private boolean legalTest(double[] test)
	{
		boolean retVal = true;
		
		if (retVal && !ASCENDING_ALLOWED)
			retVal = !isAscending(test);
		if (retVal && !DESCENDING_ALLOWED)
			retVal = !isDescending(test);

		return retVal;
	}

	private boolean isAscending(double[] input)
	{
		boolean retVal = true;
		int len = input.length/NUMBER_OF_BLOCKS;
		
		int[] sums = new int[NUMBER_OF_BLOCKS];
		
		for (int i = 0; retVal && i < NUMBER_OF_BLOCKS; i++)
		{
			for (int j = 0; j < len; j++)
			{
				if (input[j + i * len] == 1)
					sums[i]++;
					
			}

			if (i > 0 && sums[i] <= sums[i - 1])
				retVal = false;
		}
		
		return retVal;
	}

	private boolean isDescending(double[] input)
	{
		boolean retVal = true;
		int len = input.length/NUMBER_OF_BLOCKS;
		
		int[] sums = new int[NUMBER_OF_BLOCKS];
		
		for (int i = 0; retVal && i < NUMBER_OF_BLOCKS; i++)
		{
			for (int j = 0; j < len; j++)
			{
				if (input[j + i * len] == 1)
					sums[i]++;
					
			}

			if (i > 0 && sums[i] >= sums[i - 1])
				retVal = false;
		}
		
		return retVal;
	}

	@Override
	public double calculateFitness(ANN_Individual ind)
	{
		double fit = 0.0;
		
		for (int i = 0; i < this.NUMBER_OF_TESTS; i++)
		{
			double bonus = 0; // Will hold the bonus for this test.
			double[] input = learningSuite[i];
			
			ind.runNetwork(input);
			double[] output= ind.getAnn().getOutputs();
			
			if (isOrdered(input))
				bonus = orderedBonus(output);
			else 
				bonus = unorderedBonus(output);
			
			if (bonus < 0.5) // failed
				bonus *= 0.1; // Lower bonus
//				bonus *= 1/2; // Lower bonus
//				bonus = 0;

			if (bonus > 0.65) // definitive majority
				bonus = 1;
//			else if (bonus > 0.55) //strong majority
//				bonus = Math.sqrt(bonus);

			fit += bonus;
			
//			if (output[0] > 0.5)
//				fit += 0.25;
//			if (output[1] < -0.5)
//				fit += 0.25;
//			if (output[2] < -0.5)
//				fit += 0.25;
//			if (output[3] > 0.5)
//				fit += 0.25;
		}
		
		fit = fit/this.NUMBER_OF_TESTS;
		
		return (int) (100000 * fit) / 100.0;
//		return (int)(1000*fit);
	}

	private boolean isOrdered(double[] input)
	{
		boolean retVal = false;
		if(NUMBER_OF_BLOCKS == 2)
			retVal = isOrdered2Blocks(input);
		else
			retVal = isOrderedMultiBlocks(input);
		
		return retVal;
	}

	private boolean isOrdered2Blocks(double[] input)
	{
		int count1 = 0, count2 = 0;
		for (int i = 0; i < input.length / 2; i++)
		{
			if (input[i] == 1)
				count1++;
			if (input[i + (input.length / 2)] == 1)
				count2++;
		}
		
		return count1 <= count2;
	}

	private boolean isOrderedMultiBlocks(double[] input)
	{
		boolean retVal = false;
		if (!retVal && ASCENDING_ALLOWED)
			retVal = isAscending(input);
		if (!retVal && DESCENDING_ALLOWED)
			retVal = isDescending(input);

		return retVal;
	}

	private double orderedBonus(double[] output)
	{
		// This thing does not work right at the moment.
//		if(this.ASCENDING_ALLOWED && this.DESCENDING_ALLOWED)
//			return orderedBonusBidirectional(output);
		
		return orderedBonusUnidirectional(output);
	}

	private double orderedBonusUnidirectional(double[] output)
	{
		double positiveOutputs = 0;
		
		for (int i = 0; i < output.length; i++)
		{
			if (output[i] > 0.5)
				positiveOutputs += 1;
		}
		
		
		return positiveOutputs/output.length;
	}

	/**
	 * If Ascending and Descending are both allowed, output is divided into two
	 * segments: One for ascending, and one for descending.
	 * 
	 * @param output
	 * @return
	 */
	private double orderedBonusBidirectional(double[] output)
	{
		double positiveOutputs1 = 0;
		double positiveOutputs2 = 0;
		
		for (int i = 0; i < output.length/2; i++)
		{
			if (output[i] > 0.5)
				positiveOutputs1 += 1;
			if (output[i + output.length / 2] > 0.5)
				positiveOutputs2 += 1;
		}

		double retVal = 2*Math.max(positiveOutputs1, positiveOutputs2);
		
		return retVal/output.length;
	}

	
	private double unorderedBonus(double[] output)
	{
		double negativeOutputs = 0;
		
		for (int i = 0; i < output.length; i++)
		{
			if (output[i] < -0.5)
				negativeOutputs += 1;
		}
		
		
		return negativeOutputs/output.length;
	}

	private void generateInput(double[] input)
	{
		if(INPUTS_SEPERATED)
			generateRandomSeparatedInput(input);
		else
			generateRandomInput(input);
	}
	
	/**
	 * 
	 * @param input Will contain input such that
	 * 				each row is of format
	 * 				(1,1...1,-1,-1...-1)
	 */
	private void generateRandomSeparatedInput(double[] input)
	{
		int len = input.length/NUMBER_OF_BLOCKS;
		int[] x = new int[NUMBER_OF_BLOCKS];
		
		for (int i = 0; i < x.length; i++)
		{
			x[i] = (int)(Math.random()*(len+1)); 
		}

		// making sure x is not sorted at this point;
		if(NUMBER_OF_BLOCKS > 2)
		{
			if (isSortedInt(x))
			{
				generateRandomSeparatedInput(input);
				return;
			}
		}

		
		// Make ~half of inputs sorted.
		if (NUMBER_OF_BLOCKS > 2 && Math.random() < 0.5)
		{
			for (int i = 0; i < x.length; i++)
			{
				x[i] = (int)(Math.random()*(len+1));
				
				// Make x[i] be different.
				for (int j = 0; j < i; j++)
				{
					if(x[i]==x[j])
					{
						i--;
						break;
					}
				}
			}
			
			int factor = 0;
			if (this.ASCENDING_ALLOWED && this.ASCENDING_ALLOWED)
			{
				factor = (Math.random()<0.5)?(1):(-1);
			}
			else if (this.ASCENDING_ALLOWED)
			{
				factor = 1;
			}
			else if (this.DESCENDING_ALLOWED)
			{
				factor = -1;
			}
			else // (!this.ASCENDING_ALLOWED && !this.DESCENDING_ALLOWED)
			{
				throw new RuntimeException(
						"Parameter setup error !this.ASCENDING_ALLOWED && !this.DESCENDING_ALLOWED");
			}
				
			qSort(x, null, factor);
		}
		
		for (int i = 0; i < NUMBER_OF_BLOCKS; i++)
		{
			for (int j = 0; j < len; j++)
			{
				input[j + i * len] = (j < x[i]) ? (1) : (-1);
			}
		}
	}

	/**
	 * 
	 * @param x Integer array.
	 * @return true if sorted in allowed order.
	 */
	private boolean isSortedInt(int[] x)
	{
		boolean ascendingFlag = false, descendingFlag = false;
		
		if(ASCENDING_ALLOWED)
		{
			ascendingFlag = true;
			
			for (int i = 0; i < x.length - 1 && ascendingFlag; i++)
			{
				if(x[i] >= x[i+1])
					ascendingFlag = false;
			}
		}
		
		if(DESCENDING_ALLOWED)
		{
			descendingFlag = true;
			
			for (int i = 0; i < x.length - 1 && descendingFlag; i++)
			{
				if(x[i] <= x[i+1])
					descendingFlag = false;
			}
		}
		
		return ascendingFlag || descendingFlag;
	}

	/**
	 * 
	 * 
	 * @param input Will contain input, ordered with 50% probability.
	 */
	private void generateRandomInput(double[] input)
	{
		generateRandomSeparatedInput(input);

//		for (int i = 0; i < input.length; i++)
//		{
//			input[i] = (Math.random()<0.5)?(-1):(1);
//		}
		
		// Make ~half of inputs sorted.
		int len = input.length/NUMBER_OF_BLOCKS;

		for (int i = 0; i < NUMBER_OF_BLOCKS; i++)
			for (int j = 0; j < len; j++)
			{
				// k is the index j-th input in row goes to.
				int k = j + (int)(Math.random() * (len-j));
				
				
				double tmp = input[(i * NUMBER_OF_BLOCKS) + j];
				input[(i * NUMBER_OF_BLOCKS) + j] = input[(i * NUMBER_OF_BLOCKS) + k];
				input[(i * NUMBER_OF_BLOCKS) + k] = tmp;
			}
	}

	/**
	 * 
	 * @param arr Array to be sorted
	 * @param arr2 TODO
	 * @param factor Allows sorting in reverse order if negative
	 */
	private void qSort(int[] arr, double[] arr2, int factor)
	{
		//TODO: Improve sorting function.
		// Bubble-sort, awful.
		int tmp = 0;
		for (int i = 0; i < arr.length - 1; i++)
		{
			for (int j = 0; j < arr.length - i - 1; j++)
			{
				if (arr[j] * factor > arr[j + 1] * factor)
				{
					tmp = arr[j];
					arr[j] = arr[j + 1];
					arr[j + 1] = tmp;
					
					if (arr2 != null)
					{
						double[] tmpArr = new double[arr2.length/NUMBER_OF_BLOCKS];
						
						for (int k = 0; k < tmpArr.length; k++)
						{
							tmpArr[k] = arr2[j * NUMBER_OF_BLOCKS + k];
							arr2[j * NUMBER_OF_BLOCKS + k] = arr2[(j + 1) * NUMBER_OF_BLOCKS + k];
							arr2[j + 1] = tmpArr[k];
						}
					}
					
				}
			}
			
		}
	}
	
//	private void differentiateArray(int[] x)
//	{
//		// Differentiate
//		for (int i = 0; i < x.length - 1; i++)
//		{
//			if (x[i]==x[i+1])
//			{
//				for (int j = i + 1; j < x.length; j++)
//				{
//					x[j+1]++;
//				}
//			}
//		}
//		
//	}

	
	public double calculateBenchmark(ANN_Individual ind)
	{
		double bench = 0.0;
		
		for (int i = 0; i < this.NUMBER_OF_BENCHMARK_TESTS; i++)
		{
			double bonus = 0; // Will hold the bonus for this test.
			double[] input = benchmarkSuite[i];
			
			ind.runNetwork(input);
			double[] output= ind.getAnn().getOutputs();
			
			if (isOrdered(input))
				bonus = orderedBonus(output);
			else 
				bonus = unorderedBonus(output);
			
			if (bonus > 0.5) // Majority rules
				bonus = 1;
			else 
				bonus = 0;

			bench += bonus;
			
		}
		
		bench = bench/this.NUMBER_OF_BENCHMARK_TESTS;
		
		return (int)(1000*bench);
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
