package gaPack.fitness;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

import popPack.indPack.GA_Individual;
import ann.Spatial_ANN_Builder;
import runs.Runner;
import gaPack.ANN_Individual;

public abstract class TestSuiteAnnFitness implements ANN_Fitness
{

	protected int m_NumOfFitnessInFiles = 0;
	protected String[] fitnessInFilePaths;
	
	protected int m_NumOfBenchmarkInFiles = 0;
	protected String[] benchmarkInFilePaths;
	
	public int NUMBER_OF_ROWS = 0;
	public int NUMBER_OF_COLS = 0;

	ArrayList<double[]> learningSuite = new ArrayList<double[]>();
	ArrayList<double[]> learningSuiteOutputs = new ArrayList<double[]>();
	ArrayList<Double> learningSuiteWeights = new ArrayList<Double>();
	
	ArrayList<double[]> benchSuite = new ArrayList<double[]>();
	ArrayList<double[]> benchSuiteOutputs = new ArrayList<double[]>();
	ArrayList<Double> benchSuiteWeights = new ArrayList<Double>();

	private double m_correctTestWeight;

	@Override
	public void generateSuites(ANN_Individual ind)
	{
		if (learningSuite.size() > 0)
			return;
		
		this.NUMBER_OF_ROWS = Spatial_ANN_Builder.m_outGridSize[0];
		if (Spatial_ANN_Builder.m_outGridSize.length > 1)
			this.NUMBER_OF_COLS = Spatial_ANN_Builder.m_outGridSize[1];
		
		
		for (int i = 0; i < fitnessInFilePaths.length; i++)
		{
			try
			{
//				inFilePaths = null;
				Scanner inF = new Scanner(new File(fitnessInFilePaths[i]));
				
				generateSuitesFromFile(inF, false);
			}
			catch (Exception e)
			{
				System.err.println("Error in TestSuiteAnnFitness/GenerateSuites");
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
		
//		System.out.println(benchmarkInFilePaths.length);

		for (int i = 0; i < benchmarkInFilePaths.length; i++)
		{
			try
			{
//				inFilePaths = null;
				Scanner inF = new Scanner(new File(benchmarkInFilePaths[i]));
				
				generateSuitesFromFile(inF, true);
			}
			catch (Exception e)
			{
				System.err.println("Error in ReadingFitness/GenerateSuites");
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
		
	}


	/**
	 * extracts fitness or benchmark tests from a file.
	 * 
	 * @param inF The input file.
	 * @param benchmarkFlag true if benchmark tests false if fitness tests.
	 */
	protected void generateSuitesFromFile(Scanner inF, boolean benchmarkFlag)
	{
		inF.nextLine(); // Getting rid of non-data line.
		int numOfInDimensions = inF.nextInt();
		inF.nextLine();	// Clear line

		inF.nextLine(); // Getting rid of non-data line.
		int numOfOutDimensions = inF.nextInt();
		inF.nextLine();	// Clear line

		inF.nextLine(); // Getting rid of non-data line.
		int sizeOfInput = 1;
		for (int i = 0; i < numOfInDimensions; i++)
		{
			sizeOfInput *= inF.nextInt();
		}
		inF.nextLine();	// Clear line

		inF.nextLine(); // Getting rid of non-data line.
		int sizeOfOutput = 1;
		for (int i = 0; i < numOfOutDimensions; i++)
		{
			sizeOfOutput *= inF.nextInt();
		}
//		System.out.println(sizeOfOutput);
		inF.nextLine();	// Clear line

		inF.nextLine(); // Getting rid of non-data line.
		int numOfTests = inF.nextInt();
		inF.nextLine();	// Clear line

		for (int i = 0; i < numOfTests; i++)
		{
			inF.nextLine();	// Clear empty line
			inF.nextLine(); // Getting rid of non-data line.
			inF.nextLine(); // Getting index line (not used at the moment).
			double[] input = new double[sizeOfInput];
			for (int j = 0; j < input.length; j++)
			{
				input[j] = inF.nextInt();
//				System.out.println(j);
			}
			inF.nextLine();	// Clear line
			if(!benchmarkFlag)
				learningSuite.add(input);
			else
				benchSuite.add(input);
			
			inF.nextLine();	// Clear empty line
			inF.nextLine(); // Getting rid of non-data line.
			double[] output = new double[sizeOfOutput];
			for (int j = 0; j < output.length; j++)
			{
				output[j] = inF.nextInt();
			}
			inF.nextLine();	// Clear line
			if(!benchmarkFlag)
				learningSuiteOutputs.add(output);
			else
				benchSuiteOutputs.add(output);
			
			inF.nextLine();	// Clear empty line
			inF.nextLine(); // Getting rid of non-data line.
			if(!benchmarkFlag)
				learningSuiteWeights.add(inF.nextDouble());
			else
			{
				benchSuiteWeights.add(inF.nextDouble());
			}
			inF.nextLine();	// Clear line

			
//			System.out.println("BBB");
		}
//		System.out.println("Beep");
		
	}



	@Override
	public double calculateFitness(ANN_Individual ind)
	{
		double fit = 0.0;
		int[] outGridSize = Spatial_ANN_Builder.m_outGridSize;
		
		int numberOfOutputs = 1;
		for (int i = 0; i < outGridSize.length; i++)
		{
			numberOfOutputs *= outGridSize[i];
		}
		
		// Behviour profile for diversity measures.
//		int[] behaviorProfile = new int[learningSuite.size()];
		int[] behaviorProfile = new int[learningSuite.size() * numberOfOutputs];
		
		for (int i = 0; i < learningSuite.size(); i++)
		{
			double[] input = learningSuite.get(i);
			double[] perfectOutput = learningSuiteOutputs.get(i);
			ind.runNetwork(input);
			double[] actualOutput = ind.getAnn().getOutputs(); 

			// Old unweighted verison
// 			fit += suiteFitnessBonus(input, perfectOutput, actualOutput); 

			// New weighted version
			fit += suiteFitnessBonus(input, perfectOutput, actualOutput) * learningSuiteWeights.get(i);
			
			
//			behaviorProfile[i] = hashOutput(actualOutput);
			for (int j = 0; j < actualOutput.length; j++)
			{
				int value = 0;
				if (actualOutput[j] > 0.5)
					value = 1;
				else if (actualOutput[j] < -0.5)
					value = -1;
				behaviorProfile[i*actualOutput.length + j] = value; 
			}
		}
		
		ind.setMarkerArray(behaviorProfile);
		
		// Old unweighted verison
//		fit = fit/learningSuite.size();

		// New weighted version
		fit = fit/weightSum(learningSuiteWeights);

		return (int) (100000 * fit) / 100.0;
//		return fit;
	}

	private double weightSum(ArrayList<Double> weights)
	{
		double sum = 0.0;
		
		for(int i=0; i < weights.size(); i++)
			sum += weights.get(i);
		return sum;
	}


	abstract protected double suiteFitnessBonus(double[] input, double[] perfectOutput,
			double[] actualOutput);

	@Override
	public double calculateBenchmark(ANN_Individual ind)
	{
		double fit = 0.0;
		int[] outGridSize = Spatial_ANN_Builder.m_outGridSize;
		
//		System.out.println(benchSuite.size());
		
		for (int i = 0; i < benchSuite.size(); i++)
		{
			double[] input = benchSuite.get(i);
			double[] perfectOutput = benchSuiteOutputs.get(i);
			ind.runNetwork(input);
			double[] actualOutput = ind.getAnn().getOutputs(); 
			
//			for (int j = 0; j < this.NUMBER_OF_ROWS; j++)
//			{
//				for (int j2 = 0; j2 < actualOutput.length/this.NUMBER_OF_ROWS; j2++)
//				{
//					System.out.print(perfectOutput[j * this.NUMBER_OF_ROWS + j2] + " ");
//				}
//				System.out.println();
//			}
//			System.out.println();

			// Old unweighted verison
//			fit += suiteBenchmarkBonus(input, perfectOutput, actualOutput, null);

			// New weighted version
			fit += suiteBenchmarkBonus(input, perfectOutput, actualOutput, null) * benchSuiteWeights.get(i);
		}
		
		// Old unweighted verison
//		fit = fit/benchSuite.size();

		// New weighted version
		fit = fit/weightSum(benchSuiteWeights);
		
		return (int)(100000 * fit) / 100.0;
	}

	abstract protected double suiteBenchmarkBonus(double[] input, double[] perfectOutput,
			double[] actualOutput, int[] extraData);

	@Override
	public String generateFitnessDataText(GA_Individual ind_)
	{
		ANN_Individual ind = (ANN_Individual)ind_; 
		String retVal = new String("");
		
		// 0 for short version
		// 1 for full output profile 
		String[] analysis = new String[2]; 
		
		// To be safe and not damage ind.
		ANN_Individual ind2 = (ANN_Individual)ind.selfReplicate(); 
		
		analyzeBenchmark(ind2, analysis);
		
		// TODO Auto-generated method stub
		
		for (int i = 0; i < analysis.length; i++)
		{
			retVal = retVal + analysis[i] + "\n";
		}
		return retVal;
	}

	abstract protected double analyzeBenchmark(ANN_Individual ind2, String[] analysis);

	public String[] getFitnessInFilePaths()
	{
		return fitnessInFilePaths;
	}

	public int getNumOfFitnessInFiles()
	{
		return this.m_NumOfFitnessInFiles;
	}

	public int getNumOfBenchmarkInFiles()
	{
		return this.m_NumOfBenchmarkInFiles;
	}

	public void setNumOfFitnessInFiles(int num)
	{
		this.m_NumOfFitnessInFiles  = num;
	}

	public void setNumOfBenchmarkInFiles(int num)
	{
		this.m_NumOfBenchmarkInFiles  = num;
	}

	public void extractFitnessFileNames(Scanner inF)
	{
		fitnessInFilePaths = new String[getNumOfFitnessInFiles()];
		
		for (int i = 0; i < fitnessInFilePaths.length; i++)
		{
			fitnessInFilePaths[i] = new String(Runner.mainDir + inF.nextLine());
		}
		
	}

	public void extractBenchmarkFileNames(Scanner inF)
	{
		benchmarkInFilePaths = new String[getNumOfBenchmarkInFiles()];
		
		for (int i = 0; i < benchmarkInFilePaths.length; i++)
		{
			benchmarkInFilePaths[i] = new String(Runner.mainDir + inF.nextLine());
		}
		
	}


	/**
	 * 
	 * @return weight in fitness of weight being correct.
	 */
	public double getCorrectTestWeight()
	{
		return m_correctTestWeight;
	}

	public void setCorrectTestWeight(double weight)
	{
		if(weight < 0)
			weight = - weight;
		if(weight > 1)
			weight = weight - (int)weight;
		
		m_correctTestWeight = weight;
	}

}
