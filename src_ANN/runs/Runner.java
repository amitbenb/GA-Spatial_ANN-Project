package runs;

import gaPack.ANN_Individual;
import gaPack.ANN_Population;
import gaPack.fitness.ANN_Fitness;
import gaPack.fitness.TestSuiteAnnFitness;

import java.io.*;
import java.util.Scanner;

import popPack.indPack.*;
import ann.ANN_Builder;
import ann.GeneticAnnIncoder;
import ann.Spatial_ANN_Builder;

public class Runner
{
	public static boolean DEBUG_OUTPUT = true;

	public static FileWriter fitOut;
	public static FileWriter smallOut;
	public static FileWriter dataOut;
	
	public static String mainDir = new String("D:\\");
	public static String ParameterFilePath = new String("D:\\parameters.txt");
	
	public static ANN_Population savePop = null;
	
	public static int NUMBER_OF_EXPERIMENTS = 1; // Number of experiments.

	public static int NUMBER_OF_STAGES = 1;
	
	public static Runner[] runningStages;

	public String popDirFullPath = null;
	public String popDirPartPath = new String("pop\\");
	
	public double ELITE_RATIO;
	
	public int SIZE_OF_POPULATION = 400;
	public int NUMBER_OF_GENERATIONS = 200;
	
	public boolean FITNESS_STOP_FLAG = false;
	public boolean BENCHMARK_STOP_FLAG = false;
	public double FITNESS_STOP_THRESHOLD = 0.0;
	public double BENCHMARK_STOP_THRESHOLD = 0.0;

	public int TYPE_OF_MUTATION = 0;
	public double MUTATION_PROB = 0.02;
	public int TYPE_OF_CROSSOVER = 0;
	public double CROSSOVER_PROB = 0.8;
	
	public int SELECTION_TYPE = 1; // 1 is tournament Selection
	public int TOURNAMENT_SIZE = 2;
	public int NUMBER_OF_TOUR_WINNERS = 1;
	
	public boolean USE_CROWDING_FLAG = false; 
	public double MAXIMAL_NEIGHBOR_DISTANCE_RATIO = 0.4;
	public int MAXIMUM_NEIGHBOR_NUMBER = 10;

	public String FITNESS_CLASS_NAME;
	public ANN_Fitness fitnessObj;
	
	public double WEIGHT_OF_CORRECT_TEST = 0.8;

	
	public void runEvoCycle(int expNumber, int stageNumber) throws IOException 
	{
		
		String fileIdentifier = Runner.getFileIdentifier(expNumber,stageNumber);

		ANN_Builder b = new Spatial_ANN_Builder();
		ANN_Population p = new ANN_Population(this, b);
		
		String fitOutFilePath = new String(this.popDirFullPath + "FitOut" + fileIdentifier + ".txt");
		String runInfoFilePath = new String(this.popDirFullPath + "SmallOut" + fileIdentifier + ".txt");
		String ExperimentDataFilePath = new String(this.popDirFullPath + "ExpData_" + stageNumber + ".txt");
		

		if (Runner.DEBUG_OUTPUT)
			System.out.println("Evo cycle identifier: " + fileIdentifier);
		if (Runner.DEBUG_OUTPUT)
			System.out.print("Gen " + 0);
		p.evaluation();
		//			System.out.print(p.getBestIndividual().getFitness() + "\t");
		//			System.out.println(p.getAvgFitness() + " ");
		
		if (Runner.DEBUG_OUTPUT)
		{
			System.out.print(" (Best Fitness " + p.getBestIndividual().getFitness());
			System.out.println(" Benchmark score " + p.getBenchmarkScore() + ")");
		}

		fitOut = new FileWriter(fitOutFilePath);
		fitOut.write(0 + "\t" + p.getBestIndividual().getFitness() + "\t" + p.getApproxAvgFitness()
				+ "\t" + p.getBenchmarkScore() + "\n");
		fitOut.close();


		boolean stageStillActive = true;
		for (int gen = 1; gen <= this.NUMBER_OF_GENERATIONS && stageStillActive; gen++)
		{
			if (Runner.DEBUG_OUTPUT)
			{
				System.out.print("Gen " + gen);
			}
			p.selection();
			p.procreation();
			p.evaluation();
			
//			System.out.println("\n" + p.getBestIndividual().getGenome().length);

			if (Runner.DEBUG_OUTPUT)
			{
				System.out.print(" (Best Fitness " + p.getBestIndividual().getFitness());
				System.out.println(" Benchmark score " + p.getBenchmarkScore() + ")");
			}
			//				System.out.print(p.getBestIndividual().getFitness() + "\t");
			//				System.out.println(p.getAvgFitness() + " ");
			fitOut = new FileWriter(fitOutFilePath, true);
			fitOut.write(gen + "\t" + p.getBestIndividual().getFitness() + "\t" + p.getApproxAvgFitness()
					+ "\t" + p.getBenchmarkScore() + "\n");
			fitOut.close();
			
//			System.out.println(this.FITNESS_STOP_FLAG);
			
			if(this.FITNESS_STOP_FLAG && p.getBestIndividual().getFitness() >= this.FITNESS_STOP_THRESHOLD)
			{
				if (Runner.DEBUG_OUTPUT)
					System.out.println("Fitness condition met! Stage terminated!");
				stageStillActive = false;
			}
			if(this.BENCHMARK_STOP_FLAG && p.getBenchmarkScore() >= this.BENCHMARK_STOP_THRESHOLD)
			{
				if (Runner.DEBUG_OUTPUT)
					System.out.println("Benchmark condition met! Stage terminated!");
				stageStillActive = false;
			}
		}
		
		// For run info. TODO.
		smallOut = new FileWriter(runInfoFilePath);
		if (expNumber == 0)
		{
			// First Experiment. New file.
			dataOut = new FileWriter(ExperimentDataFilePath, false);
			dataOut.write(p.generateDataTableHeader()); 
		}
		else
			dataOut = new FileWriter(ExperimentDataFilePath, true);

		writeRunInfo(smallOut, dataOut, p);
		dataOut.close();
		smallOut.close();

		// Saving population for next runner.
		Runner.savePop = p;
		Runner.RandomizeArray(p.getPop());
		
		// If enabled population restarts
//		Runner.savePop = null;
	}
	
	private void writeRunInfo(FileWriter outF, FileWriter dataOutF, ANN_Population pop) throws IOException
	{

		// Evo Parameters
		
		outF.write("Population size: " + this.SIZE_OF_POPULATION + "\t\t");
		outF.write("Generations: " + this.NUMBER_OF_GENERATIONS + "\t\t");
		outF.write("Elitism ratio: " + this.ELITE_RATIO + "\n");
		
		outF.write("Mut prob: " + this.MUTATION_PROB + "\t\t");
		outF.write("XO prob: " + this.CROSSOVER_PROB + "\n");
		
		switch (this.SELECTION_TYPE)
		{
		case 1: // Tournament Selection.
			outF.write("Tournament Selection with tour params " + this.NUMBER_OF_TOUR_WINNERS + "/" + this.TOURNAMENT_SIZE +  "\n");
			break;

		default:
			outF.write("Unknown selection type. Probable error in parameter." +  "\n");
			break;
		}
		
		
		
		if (!this.USE_CROWDING_FLAG)
			outF.write("No diversity maintenance used" +  "\n");
		else
			outF.write("Crowding parameters: Max distance ratio: "
					+ this.MAXIMAL_NEIGHBOR_DISTANCE_RATIO + " Max number of neighbors: "
					+ this.MAXIMUM_NEIGHBOR_NUMBER + "\n");
		
		outF.write("\n");
		
		// Fitness Parameters
		
		outF.write("Fitness Object class: " + this.FITNESS_CLASS_NAME + "\n");
		
		if (this.fitnessObj instanceof TestSuiteAnnFitness)
		{
			outF.write("Fitness input files: " + "\n");

			TestSuiteAnnFitness fit = (TestSuiteAnnFitness)this.fitnessObj;
			String[] FilePaths = fit.getFitnessInFilePaths();
			
			for (int i = 0; i < FilePaths.length; i++)
			{
				outF.write("\t" + FilePaths[i] + "\n");
			}
			
		}
		
		
		outF.write("\n");
		
		// ANN parameters
		
		outF.write("Initial genome size: " + ANN_Individual.init_genome_size + "\t\t");
		outF.write("Neuron limit and edge limit: " + Spatial_ANN_Builder.neuronLimit + " " + Spatial_ANN_Builder.linkLimit + "\n");

		outF.write("Number of read encodings:\t" + Spatial_ANN_Builder.NUMBER_OF_READ_ENCODINGS + "\n");
		
		outF.write("Encoding weights info:\t");
		outF.write(Spatial_ANN_Builder.generateEncoderInfo());
		
		outF.write("Input # of dimensions: " + Spatial_ANN_Builder.m_inGridSize.length + "\t\t");
		outF.write("Input size: ");
		for (int i = 0; i < Spatial_ANN_Builder.m_inGridSize.length; i++)
		{
			outF.write(Spatial_ANN_Builder.m_inGridSize[i] + " ");
		}
		outF.write("\n");

		outF.write("Network # of dimensions: " + Spatial_ANN_Builder.m_annGridSize.length + "\t\t");
		outF.write("Network size: ");
		for (int i = 0; i < Spatial_ANN_Builder.m_annGridSize.length; i++)
		{
			outF.write(Spatial_ANN_Builder.m_annGridSize[i] + " ");
		}
		outF.write("\n");

		outF.write("Output # of dimensions: " + Spatial_ANN_Builder.m_outGridSize.length + "\t\t");
		outF.write("Output size: ");
		for (int i = 0; i < Spatial_ANN_Builder.m_outGridSize.length; i++)
		{
			outF.write(Spatial_ANN_Builder.m_outGridSize[i] + " ");
		}
		outF.write("\n\n");
		
//		this.fitnessObj.outInfo(outF);
		outF.write(pop.generateBestInfo());
		
		dataOutF.write(pop.generateBestDataLine());
	


		// TODO Continue this.
		
		// Put information about fitness/benchmark suite files, as well as
		// fitness object.
	}

	public static void collectParameters(String fileName) throws Exception
	{
		Scanner inF = new Scanner(new File(fileName));

//		System.out.println(inF.nextLine());
//		System.out.println(inF.nextDouble());
//		
//		if (1<2)
//			return;
		
		// Getting parameter files format code.
		inF.nextLine();	// Getting rid of non-data line.
		int formatCode = inF.nextInt(); 
		inF.nextLine();	// Clear line
//		System.out.println(formatCode);

		inF.nextLine();	// Getting rid of non-data line.
		Runner.DEBUG_OUTPUT = inF.nextBoolean(); 
		inF.nextLine();	// Clear line
		
		inF.nextLine();	// Getting rid of non-data line.
		Runner.NUMBER_OF_EXPERIMENTS = inF.nextInt(); // Not useful at the moment
		inF.nextLine();	// Clear line
		
		inF.nextLine();	// Getting rid of non-data line.
		Runner.NUMBER_OF_STAGES = inF.nextInt(); // Not useful at the moment
		inF.nextLine();	// Clear line
		
		Runner.runningStages = new Runner[Runner.NUMBER_OF_STAGES];
		
		inF.nextLine();	// Getting rid of non-data line.
		for (int i = 0; i < Runner.runningStages.length;)
		{
//			System.out.println(i);
			int numOfSameStages = inF.nextInt();
			String popPathTemp = new String(inF.next());
			inF.nextLine();	// ClearLine
			for (int j = i; i < j + numOfSameStages; i++)
			{
				Runner.runningStages[i] = new Runner();
				Runner r_i = Runner.runningStages[i];
				r_i.popDirPartPath = new String(popPathTemp);
				r_i.popDirFullPath = new String(Runner.mainDir + r_i.popDirPartPath);
			}
		}
		
		// Evolutionary parameters.
		inF.nextLine();	// Getting rid of non-data line.
		for (int i = 0; i < Runner.runningStages.length;)
		{
//			System.out.println("boop" + i);
			int numOfSameStages = inF.nextInt();
			String evoFileName = new String(inF.next()); 
			inF.nextLine();	// ClearLine
			for (int j = i; i < j + numOfSameStages; i++)
			{
				collectEvoParameters(evoFileName, i);
			}
		}
		
		// Fitness and output parameters.
		inF.nextLine();	// Getting rid of non-data line.
		for (int i = 0; i < Runner.runningStages.length;)
		{
			int numOfSameStages = inF.nextInt();
			String fitFileName = new String(inF.next());
			inF.nextLine();	// ClearLine
			for (int j = i; i < j + numOfSameStages; i++)
			{
				collectFitParameters(fitFileName, i);
			}
		}

		// ANN parameters.
		inF.nextLine();	// Getting rid of non-data line.
		String annFileName = new String(inF.nextLine()); 
		collectAnnParameters(annFileName);
		
	}


	private static void collectFitParameters(String fileName, int idx) throws Exception
	{
		// TODO Auto-generated method stub
		
		Scanner inF = new Scanner(new File(mainDir + fileName));

		Runner r = Runner.runningStages[idx];
		inF.nextLine(); // Getting rid of non-data line. 
		r.FITNESS_CLASS_NAME = new String(inF.nextLine());
		Class theClass = Class.forName(r.FITNESS_CLASS_NAME);
		theClass.newInstance();
		r.fitnessObj = (ANN_Fitness)theClass.newInstance();

		if (r.fitnessObj instanceof TestSuiteAnnFitness)
		{
			TestSuiteAnnFitness fit = (TestSuiteAnnFitness)r.fitnessObj;
			
			//TODO add input for correctTestWeight to file
			fit.setCorrectTestWeight(r.WEIGHT_OF_CORRECT_TEST);

			inF.nextLine(); // Getting rid of non-data line.
			fit.setNumOfFitnessInFiles(inF.nextInt());
			inF.nextLine();	// Clear line

			fit.extractFitnessFileNames(inF);
			
			inF.nextLine(); // Getting rid of non-data line.
			fit.setNumOfBenchmarkInFiles(inF.nextInt());
			inF.nextLine();	// Clear line

			fit.extractBenchmarkFileNames(inF);
			
		}

		inF.close();
	}


	private static void collectEvoParameters(String fileName, int idx)throws Exception
	{
		// TODO Auto-generated method stub

		Scanner inF = new Scanner(new File(mainDir + fileName));

		Runner r = Runner.runningStages[idx];

		inF.nextLine(); // Getting rid of non-data line.
		r.SIZE_OF_POPULATION = inF.nextInt();
		inF.nextLine();	// Clear line

		inF.nextLine(); // Getting rid of non-data line.
		r.NUMBER_OF_GENERATIONS = inF.nextInt();
		inF.nextLine();	// Clear line

		inF.nextLine(); // Getting rid of non-data line.
		r.FITNESS_STOP_FLAG = inF.nextBoolean();
		if (r.FITNESS_STOP_FLAG)
			r.FITNESS_STOP_THRESHOLD = inF.nextInt();
		inF.nextLine();	// Clear line
		
		inF.nextLine(); // Getting rid of non-data line.
		r.BENCHMARK_STOP_FLAG = inF.nextBoolean();
		if (r.BENCHMARK_STOP_FLAG)
			r.BENCHMARK_STOP_THRESHOLD = inF.nextInt();
		inF.nextLine();	// Clear line
		
		inF.nextLine();	// Getting rid of non-data line.
		r.ELITE_RATIO = inF.nextDouble();
		inF.nextLine();	// Clear line

		inF.nextLine();	// Getting rid of non-data line.
		r.TYPE_OF_MUTATION = inF.nextInt(); // Not useful at the moment
		r.MUTATION_PROB = inF.nextDouble(); 
		inF.nextLine();	// Clear line

		inF.nextLine();	// Getting rid of non-data line.
		r.TYPE_OF_CROSSOVER = inF.nextInt(); // Not useful at the moment
		r.CROSSOVER_PROB = inF.nextDouble();
		inF.nextLine();	// Clear line

		inF.nextLine();	// Getting rid of non-data line.
		r.SELECTION_TYPE = inF.nextInt();
		inF.nextLine();	// Clear line

		inF.nextLine();	// Getting rid of non-data line.
		r.TOURNAMENT_SIZE = inF.nextInt();
		r.NUMBER_OF_TOUR_WINNERS = inF.nextInt();
		inF.nextLine();	// Clear line

		inF.nextLine();	// Getting rid of non-data line.
		r.USE_CROWDING_FLAG = inF.nextBoolean();
		inF.nextLine();	// Clear line

		inF.nextLine();	// Getting rid of non-data line.
		r.MAXIMAL_NEIGHBOR_DISTANCE_RATIO = inF.nextDouble();
		inF.nextLine();	// Clear line

		inF.nextLine();	// Getting rid of non-data line.
		r.MAXIMUM_NEIGHBOR_NUMBER = inF.nextInt();
		inF.nextLine();	// Clear line

		inF.close();
	}


	private static void collectAnnParameters(String fileName)throws Exception
	{
		Scanner inF = new Scanner(new File(mainDir + fileName));
		
		inF.nextLine();	// Getting rid of non-data line.
		GA_Individual.init_genome_size = inF.nextInt();
		inF.nextLine();	// Clear line
		
		//TODO add parameter to limit genome size.

		inF.nextLine();	// Getting rid of non-data line.
		Spatial_ANN_Builder.neuronLimit = inF.nextInt();
		Spatial_ANN_Builder.linkLimit = inF.nextInt();
		inF.nextLine();	// Clear line

		inF.nextLine();	// Getting rid of non-data line.
		Spatial_ANN_Builder.m_inGridSize = new int[inF.nextInt()];
		for (int i = 0; i < Spatial_ANN_Builder.m_inGridSize.length; i++)
		{
			Spatial_ANN_Builder.m_inGridSize[i] = inF.nextInt();
		}
		inF.nextLine();	// Clear line
		
		inF.nextLine();	// Getting rid of non-data line.
		Spatial_ANN_Builder.m_annGridSize = new int[inF.nextInt()];
		for (int i = 0; i < Spatial_ANN_Builder.m_annGridSize.length; i++)
		{
			Spatial_ANN_Builder.m_annGridSize[i] = inF.nextInt();
		}
		inF.nextLine();	// Clear line
		
		inF.nextLine();	// Getting rid of non-data line.
		Spatial_ANN_Builder.m_outGridSize = new int[inF.nextInt()];
		for (int i = 0; i < Spatial_ANN_Builder.m_outGridSize.length; i++)
		{
			Spatial_ANN_Builder.m_outGridSize[i] = inF.nextInt();
		}
		inF.nextLine();	// Clear line
		
		inF.nextLine();	// Getting rid of non-data line.
		Spatial_ANN_Builder.NUMBER_OF_READ_ENCODINGS = inF.nextInt();
		inF.nextLine();	// Clear line

		inF.nextLine();	// Getting rid of non-data line.
		String encFileName = new String(inF.nextLine()); 
		collectEncParameters(encFileName);
		
		inF.close();

		
	}

	private static void collectEncParameters(String fileName)throws Exception
	{
		Scanner inF = new Scanner(new File(mainDir + fileName));
		
		GeneticAnnIncoder incoder = new GeneticAnnIncoder(inF);
		
		Spatial_ANN_Builder.ANN_BUILD_INCODER = incoder;
		
		inF.close();
	}

	/**
	 * Randomizing order of input array.
	 * 
	 * @param arr
	 */
	public static void RandomizeArray(Object[] arr)
	{
		for (int i = 0; i < arr.length - 1; i++)
		{
			int j = i + (int)(Math.random()*Integer.MAX_VALUE) % (arr.length - i - 1);
			
			// Swap
			Object tmp = arr[i];
			arr[i] = arr[j];
			arr[j] = tmp;
		}
	}

	static String getFileIdentifier(int expNumber, int stageNumber)
	{
		String retVal = new String("");
		
		// Only if multiple experiments add this counter;
		if(NUMBER_OF_EXPERIMENTS != 1)
		{
			retVal = retVal + "_";
			if (expNumber<100)
				retVal = retVal + "0";
			if (expNumber<10)
				retVal = retVal + "0";
			retVal = retVal + expNumber;
		}
	
		retVal = retVal + "_";
		if (stageNumber<100)
			retVal = retVal + "0";
		if (stageNumber<10)
			retVal = retVal + "0";
		retVal = retVal + stageNumber;
		
		return retVal;
	}

	

}
