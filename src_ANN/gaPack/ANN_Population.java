package gaPack;


import gaPack.fitness.ANN_Fitness;
import ann.ANN_Builder;
import ann.Spatial_ANN_Builder;
import popPack.GA_Population;
import popPack.indPack.GA_Individual;
import runs.Runner;

public class ANN_Population extends GA_Population
{
	public static boolean debugFlag = true;

	ANN_Individual[] m_archive;

	ANN_Builder m_builder = new Spatial_ANN_Builder();
	ANN_Fitness m_fitness = null;
	
	
	// C'tor
	/**
	 * 
	 * @param popSize
	 * @param archiveSize
	 * @param xoProb
	 * @param mutProb
	 * @param eliteRatio TODO
	 * @param b
	 * @param f TODO
	 */
//	public ANN_Population(int popSize, int archiveSize, double xoProb, double mutProb,
//			double eliteRatio, ANN_Builder b, ANN_Fitness f)
//	{
//		m_pop = new ANN_Individual[popSize];
////		m_archive = new ANN_Individual[m_archiveSize];
////		m_archiveBenchmarkScores = new double[archiveSize];
//		m_xoProb = xoProb;
//		m_mutProb = mutProb;
//		m_eliteRatio = eliteRatio;
//		m_builder = b;
//		m_fitness = f;
//		
//		ANN_Individual.dimentionality = b.getDimentionality();
//		
//		for (int i = 0; i < m_pop.length; i++)
//		{
//			m_pop[i] = new ANN_Individual(ANN_Individual.init_genome_size, getBuilder(), getFitnessObj());
//		}
//	}
	
	public ANN_Population(Runner r, ANN_Builder b)
	{
		m_pop = new ANN_Individual[r.SIZE_OF_POPULATION];
//		m_archive = new ANN_Individual[m_archiveSize];
//		m_archiveBenchmarkScores = new double[archiveSize];
		m_xoProb = r.CROSSOVER_PROB;
		m_mutProb = r.MUTATION_PROB;
		m_eliteRatio = r.ELITE_RATIO;
		m_fitness = r.fitnessObj;
		tournamentSize = r.TOURNAMENT_SIZE;
		tournamentWinnerNum = r.NUMBER_OF_TOUR_WINNERS;
		
		m_crowdingFlag = r.USE_CROWDING_FLAG;
		m_neighborDistanceRatio = r.MAXIMAL_NEIGHBOR_DISTANCE_RATIO;
		m_maxNumOfNeighbors = r.MAXIMUM_NEIGHBOR_NUMBER;
		
		m_builder = b;
		
		ANN_Individual.dimentionality = b.getDimentionality();

		if(Runner.savePop == null)
		{
			for (int i = 0; i < m_pop.length; i++)
			{
				m_pop[i] = new ANN_Individual(ANN_Individual.init_genome_size, getBuilder(), getFitnessObj());
			}
		}
		else //(Runner.savePop == p)
		{
			ANN_Population p = Runner.savePop;
			for (int i = 0; i < m_pop.length; i++)
			{
				m_pop[i] = p.getIndividual(i % p.getPopSize()).selfReplicate();

				ANN_Individual ind = (ANN_Individual)m_pop[i];
				ind.setFitnessObj(getFitnessObj());
				ind.setBuilder(getBuilder());
			}
		}

	}
	
	public ANN_Builder getBuilder()
	{
		return m_builder;
	}

	public ANN_Fitness getFitnessObj()
	{
		return m_fitness;
	}

	// Setters.
	public void setBuilder(ANN_Builder b)
	{
		m_builder = b;
	}

	public void setFitnessObj(ANN_Fitness fit)
	{
		m_fitness = fit;
	}

	public void evaluation()
	{
		if(getPopSize()>0)
		{
//			if(getFitnessObj() == null)
//				System.out.println("Boop!");
			getFitnessObj().generateSuites((ANN_Individual)getIndividual(0));
			super.evaluation();
			
		}
	}

	public String generateBestInfo()
	{
		String retVal = new String("");
		GA_Individual best = getBestEverIndividual();
		
		retVal = getFitnessObj().generateFitnessDataText((ANN_Individual)best);
		return retVal;
	}

	public String generateBestDataLine()
	{
		String retVal = new String("");
		GA_Individual best = getBestEverIndividual();
		
		retVal = getFitnessObj().generateFitnessDataLine((ANN_Individual)best);
		return retVal;
	}

	public String generateDataTableHeader()
	{
		String retVal = new String("");
		GA_Individual best = getBestEverIndividual();
		
		retVal = getFitnessObj().generateFitnessDataTableHeader((ANN_Individual)best);
		return retVal;
	}

/*
	public void procreation()
	{
		int i,j;
		GA_Individual[] new_pop = new GA_Individual[getPopSize()]; 
		
		for(i=0; i<getPopSize(); i++)
		{
			new_pop[i] = null;
		}
		
		// XO loop.
		int firstInd = (int)(Math.random() * getPopSize());
		for(i=0; i<getPopSize(); i++)
		{
			int ind1 =  (i+firstInd)%getPopSize();							// first to cross over.
			int ind2 =  (int)(Math.floor(Math.random() * m_pop.length));	// second to cross over.

			// Find your first candidate (that wasn't used yet).
			for(;i<getPopSize() && getIndividual(ind1%getPopSize())==null; i++, ind1++);

			ind1 = ind1 % getPopSize();		// Update candidate.
			if (i == getPopSize()) 
				break;						// No more individuals
			else
			{
				new_pop[ind1] = getIndividual(ind1);
				setInd(null,ind1);
			}

			// Find your second candidate.
			j = (int)(Math.floor(Math.random() * m_pop.length));
			for (j = 0; j<getPopSize() && getIndividual(ind2%getPopSize())==null; j++, ind2++);

			ind2 = ind2 % getPopSize();		// Update candidate.
			if (j == getPopSize()) 
				break;						// No more individuals
			else
			{
				new_pop[ind2] = getIndividual(ind2);
				setInd(null,ind2);
			}

			// For debugging. 
			//System.out.print("" + no1 + no2 + "\n");
			//System.out.print(m_pop[no1].getFitness() + " " + m_pop[no2].getFitness() + "\n");

			if (Math.random() < m_xoProb)	// XO happens in this probability.
			{
				
//				if(GP_Constants001.useSelectiveXO)
//				{
//					if (new_pop[ind1].getFitness() > new_pop[ind2].getFitness())
//						GP_Individual.genericCrossover(new_pop[ind1].selfReplicate(), new_pop[ind2]);
////						GP_Individual.genericCrossover(new_pop[ind1], new_pop[ind2]);
//					else if (ind1 != ind2)	// So cycle in tree isn't created by self-crossover.
//						GP_Individual.genericCrossover(new_pop[ind1], new_pop[ind2]);
//				}
//				else if (ind1 != ind2)	// So cycle in tree isn't created by self-crossover.
//					GP_Individual.genericCrossover(new_pop[ind1], new_pop[ind2]);
				
				// No Selective XO
				GA_Individual.crossover(new_pop[ind1], new_pop[ind2]);

			}
		}
		
		// Mutation loop.
		for(i=0; i<getPopSize(); i++)
		{
			new_pop[i].mutate(m_mutProb);
		}
		
		setPopulation(new_pop);
	}
/**/	

	


//	public int[] func()
//	{
//		int[] arr = new int[3];
//		arr[0] = 77;
//		return arr;
//	}
//	

}

