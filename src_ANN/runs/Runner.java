package runs;

import gaPack.ANN_Individual;
import gaPack.ANN_Population;
import gaPack.fitness.ANN_Fitness;
import gaPack.fitness.TestSuiteAnnFitness;

import java.io.*;
import java.util.Scanner;

import popPack.Base_Runner;
import popPack.indPack.*;
import ann.ANN_Builder;
import ann.GeneticAnnIncoder;
import ann.Spatial_ANN_Builder;

public class Runner extends Base_Runner
{
//	public static Runner[] runningStages;

//	public ANN_Fitness fitnessObj;
	
	
	public void runEvoCycle(int expNumber, int stageNumber) throws IOException 
	{
		
		String fileIdentifier = Base_Runner.getFileIdentifier(expNumber,stageNumber);

		ANN_Builder b = new Spatial_ANN_Builder();
		ANN_Population p = new ANN_Population(this, b);
		
		String fitOutFilePath = new String(this.popDirFullPath + "FitOut" + fileIdentifier + ".txt");
		String runInfoFilePath = new String(this.popDirFullPath + "SmallOut" + fileIdentifier + ".txt");
		String ExperimentDataFilePath = new String(this.popDirFullPath + "ExpData_" + stageNumber + ".txt");
		

		if (Base_Runner.DEBUG_OUTPUT)
			System.out.println("Evo cycle identifier: " + fileIdentifier);
		if (Base_Runner.DEBUG_OUTPUT)
			System.out.print("Gen " + 0);
		p.evaluation();
		//			System.out.print(p.getBestIndividual().getFitness() + "\t");
		//			System.out.println(p.getAvgFitness() + " ");
		
		if (Base_Runner.DEBUG_OUTPUT)
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
			if (Base_Runner.DEBUG_OUTPUT)
			{
				System.out.print("Gen " + gen);
			}
			p.selection();
			p.procreation();
			p.evaluation();
			
//			System.out.println("\n" + p.getBestIndividual().getGenome().length);

			if (Base_Runner.DEBUG_OUTPUT)
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
				if (Base_Runner.DEBUG_OUTPUT)
					System.out.println("Fitness condition met! Stage terminated!");
				stageStillActive = false;
			}
			if(this.BENCHMARK_STOP_FLAG && p.getBenchmarkScore() >= this.BENCHMARK_STOP_THRESHOLD)
			{
				if (Base_Runner.DEBUG_OUTPUT)
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
		Base_Runner.savePop = p;
		Base_Runner.RandomizeArray(p.getPop());
		
		// If enabled population restarts
//		Base_Runner.savePop = null;
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
		Base_Runner.DEBUG_OUTPUT = inF.nextBoolean(); 
		inF.nextLine();	// Clear line
		
		inF.nextLine();	// Getting rid of non-data line.
		Base_Runner.NUMBER_OF_EXPERIMENTS = inF.nextInt(); // Not useful at the moment
		inF.nextLine();	// Clear line
		
		inF.nextLine();	// Getting rid of non-data line.
		Base_Runner.NUMBER_OF_STAGES = inF.nextInt(); // Not useful at the moment
		inF.nextLine();	// Clear line
		
		Base_Runner.runningStages = new Runner[Base_Runner.NUMBER_OF_STAGES];
		
		inF.nextLine();	// Getting rid of non-data line.
		for (int i = 0; i < Base_Runner.runningStages.length;)
		{
//			System.out.println(i);
			int numOfSameStages = inF.nextInt();
			String popPathTemp = new String(inF.next());
			inF.nextLine();	// ClearLine
			for (int j = i; i < j + numOfSameStages; i++)
			{
				Base_Runner.runningStages[i] = new Runner();
				Base_Runner r_i = Base_Runner.runningStages[i];
				r_i.popDirPartPath = new String(popPathTemp);
				r_i.popDirFullPath = new String(Base_Runner.mainDir + r_i.popDirPartPath);
			}
		}
		
		// Evolutionary parameters.
		inF.nextLine();	// Getting rid of non-data line.
		for (int i = 0; i < Base_Runner.runningStages.length;)
		{
//			System.out.println("boop" + i);
			int numOfSameStages = inF.nextInt();
			String evoFileName = new String(inF.next()); 
			inF.nextLine();	// ClearLine
			for (int j = i; i < j + numOfSameStages; i++)
			{
				runningStages[i].collectEvoParameters(evoFileName, i);
			}
		}
		
		// Fitness and output parameters.
		inF.nextLine();	// Getting rid of non-data line.
		for (int i = 0; i < Base_Runner.runningStages.length;)
		{
			int numOfSameStages = inF.nextInt();
			String fitFileName = new String(inF.next());
			inF.nextLine();	// ClearLine
			for (int j = i; i < j + numOfSameStages; i++)
			{
				runningStages[i].collectFitParameters(fitFileName, i);
			}
		}

		// ANN parameters.
		inF.nextLine();	// Getting rid of non-data line.
		String annFileName = new String(inF.nextLine()); 
		collectAnnParameters(annFileName);
		
		inF.close();
		
	}


	public void collectFitParameters(String fileName, int idx) throws Exception
	{
		// TODO Auto-generated method stub
		
		Scanner inF = new Scanner(new File(mainDir + fileName));

		Base_Runner r = Base_Runner.runningStages[idx];
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

	

}
