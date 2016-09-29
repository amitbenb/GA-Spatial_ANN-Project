package runs;

import java.io.FileWriter;

import popPack.Base_Runner;
import gaPack.*;
import ann.ANN_Builder;
import ann.Spatial_ANN_Builder;
import runs.Runner;

public class RunsOrdinality
{	
	public static void main(String[] args)
	{
//		int[] a1 = {9,9,2}, a2 = {10,10,3};
//		
//		System.out.println(getIndexfromlocation(a1, a2));
		
//		ANN_Individual ind = new ANN_Individual(ANN_Individual.init_genome_size);
//		
		try
		{
			mainWork(args);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException();
		}
		
//		System.out.println(1/3.0);
		
//		System.out.println((-35395) % 20);
	}

	private static void mainWork(String[] args) throws Exception
	{
		FileWriter fitOut = Runner.fitOut;
		if(args.length > 0)
			Runner.mainDir = new String(args[0]);
		else
			Runner.mainDir = new String("Z:\\ANN_Exp\\Ordinal\\");
		
		
		if(args.length > 1)
			Runner.ParameterFilePath = new String(Runner.mainDir + args[1]);
		else
			Runner.ParameterFilePath = new String(Runner.mainDir + "parameters.txt");
		
		Runner.collectParameters(Runner.ParameterFilePath);
		
//		String str = new String("java.lang.Integer");
//		
//		Class theClass = Class.forName(str);
//		Integer obj = (Integer)theClass.newInstance();
		
//		if (1<2)
//			return;
		
		Base_Runner[] r = Runner.runningStages;
		ANN_Builder b = new Spatial_ANN_Builder();
//		ANN_Population p =
//				new ANN_Population(r[0].SIZE_OF_POPULATION, 0, r[0].CROSSOVER_PROB,
//						r[0].MUTATION_PROB, 0, b, r[0].fitnessObj);
		ANN_Population p = new ANN_Population((Runner)r[0], b);

		if (Runner.DEBUG_OUTPUT)
			System.out.println("Gen " + 0);
		p.evaluation();
//		System.out.print(p.getBestIndividual().getFitness() + "\t");
//		System.out.println(p.getAvgFitness() + " ");
		fitOut = new FileWriter(Runner.mainDir + "fitOut.txt");
		fitOut.write(0 + "\t" + p.getBestIndividual().getFitness() + "\t" + p.getApproxAvgFitness()
				+ "\t" + p.getBenchmarkScore() + "\n");
		fitOut.close();
		
		for (int gen = 1; gen <= r[0].NUMBER_OF_GENERATIONS; gen++)
		{
			if (Runner.DEBUG_OUTPUT)
				System.out.println("Gen " + gen);
			p.selection();
			p.procreation();
			p.evaluation();

//			System.out.print(p.getBestIndividual().getFitness() + "\t");
//			System.out.println(p.getAvgFitness() + " ");
			fitOut = new FileWriter(Runner.mainDir + "fitOut.txt", true);
			fitOut.write(gen + "\t" + p.getBestIndividual().getFitness() + "\t" + p.getApproxAvgFitness()
					+ "\t" + p.getBenchmarkScore() + "\n");
			fitOut.close();
		}
	}

	public static int getIndexfromlocation(int location[], int[] gridSize)
	{
		int idx = location[0] % gridSize[0];
		if (idx<0)
			idx += gridSize[0];

		// find index of coordinates and set to idx;
		for (int i = 1; i < location.length; i++)
		{
			int temp = location[i] % gridSize[i];
			if (temp<0)
				temp += gridSize[i];
			
			idx *= gridSize[i-1];
			idx += temp;
		}
		
		return idx;
	}


}
