package gaPack;

import gaPack.fitness.*;
import ann.*;
import popPack.BasicBuilder;
import popPack.indPack.GA_Individual;
import runs.Utility;

public class ANN_Individual extends GA_Individual
{
	ANN_Builder m_builder;
	ANN_Fitness m_fitness;

	public static int init_genome_size = 60;
	public static int dimentionality = 0; // Decided from outside by builder info.
	// Soft limits for initial link weight and node threshold.
	public static double WEIGHT_LIMIT = 10.0; 
	public static double THRESHOLD_LIMIT = 20.0;
	
	private SpatialAnn m_ann = null;
	
	public class ANN_Genome_Atom extends GA_Atom
	{
		// Lets you know which neurons read this gene.
		private int m_readMode = Utility.generateRandomNaturalInteger(Utility.LARGE_NUMBER);

		private int m_newReadMode = Utility.generateRandomNaturalInteger(Utility.LARGE_NUMBER);

		private int m_opCode = Utility.generateRandomNaturalInteger(Utility.LARGE_NUMBER);

		// Double use. Gene for weight and Factor.
		private double m_weight = generateRandomFloat(WEIGHT_LIMIT);

		private double m_threshold = generateRandomFloat(THRESHOLD_LIMIT);

		private int[] m_locationOffset;
		
		public ANN_Genome_Atom(int dimentionality)
		{
			m_locationOffset = new int[dimentionality];
			for (int i = 0; i < m_locationOffset.length; i++)
			{
				setLocationOffset(i, Utility.generateRandomInteger(Utility.LARGE_NUMBER));
			}
		}
		
		public ANN_Genome_Atom(ANN_Genome_Atom a)
		{
			m_readMode = a.m_readMode;
			m_newReadMode = a.m_newReadMode;
			m_opCode = a.m_opCode;
			m_weight = a.m_weight;
			m_threshold = a.m_threshold;
			
			m_locationOffset = new int[a.m_locationOffset.length];
			for (int i = 0; i < m_locationOffset.length; i++)
			{
				setLocationOffset(i, a.getLocationOffset(i));
			}
		}
		
		private double generateRandomFloat(double limit)
		{
			// Favor lower numbers.
			double retVal = Math.pow(Math.random(), 2) * limit;
			
			if (Math.random() < 0.5)
				retVal *= (-1);

			
			return retVal;
		}

		public void mutateAtom()
		{
			int NumberOfNonspatialItems = 5;
			
			int i = (int)(Math.random() * (NumberOfNonspatialItems + m_locationOffset.length));
			
			if (i == 0)
				setReadMode(Utility.generateRandomNaturalInteger(Utility.LARGE_NUMBER));
			else if (i == 1)
				setOpCode(Utility.generateRandomNaturalInteger(Utility.LARGE_NUMBER));
			else if (i == 2)
				setNewReadMode(Utility.generateRandomNaturalInteger(Utility.LARGE_NUMBER));
			else if (i == 3)
				setWeight(getWeight() + ((Math.random() / 5) - 0.1));
			else if (i == 4)
				setThreshold(getThreshold() + ((Math.random() / 5) - 0.1));
			else 
				setLocationOffset(i-NumberOfNonspatialItems, Utility.generateRandomInteger(Utility.LARGE_NUMBER));
		}

		public int getReadMode()
		{
			return m_readMode;
		}

		public void setReadMode(int readMode)
		{
			this.m_readMode = readMode;
		}

		public int getNewReadMode()
		{
			return m_newReadMode;
		}

		public void setNewReadMode(int readMode)
		{
			this.m_newReadMode = readMode;
		}

		public int getOpCode()
		{
			return m_opCode;
		}

		public void setOpCode(int opCode)
		{
			this.m_opCode = opCode;
		}

		public double getWeight()
		{
			return m_weight;
		}

		public void setWeight(double weight)
		{
			this.m_weight = weight;
		}

		public double getFactor()
		{
			// Yes. This is intentional. Double use.
			return m_weight; 
		}

		public void setFactor(double factor)
		{
			// Yes. This is intentional. Double use.
			this.m_weight = factor;
		}

		public double getThreshold()
		{
			return m_threshold;
		}

		public void setThreshold(double m_threshold)
		{
			this.m_threshold = m_threshold;
		}

		public int[] getLocationOffset()
		{
			return m_locationOffset;
		}

		public int[] getLocationOffsetCopy()
		{
			int[] location = new int[getLocationOffset().length];
			intArrayCopy(m_locationOffset, location);
			return location;
		}

		public int getLocationOffset(int dim)
		{
			return m_locationOffset[dim % m_locationOffset.length];
		}

		public void setLocationOffset(int dim, int locationOffset)
		{
			this.m_locationOffset[dim % m_locationOffset.length] = locationOffset;
		}
		
		@Override
		public GA_Atom selfReplicate()
		{
			ANN_Genome_Atom a = new ANN_Genome_Atom(this);

			return a;
		}
	}

//	public ANN_Individual()
//	{
//		this(init_genome_size,new Spatial_ANN_Builder(), new RandomFitness());
//	}

//	public ANN_Individual(int genomeSize)
//	{
//		this(genomeSize,new Spatial_ANN_Builder(), new RandomFitness());
//	}

	public ANN_Individual(int genomeSize, ANN_Builder b, ANN_Fitness fit)
	{
		super();
		m_genome = new ANN_Genome_Atom[genomeSize];
		setBuilder(b);
		setFitnessObj(fit);
		
		for (int i = 0; i < genomeSize; i++)
		{
			m_genome[i] = new ANN_Genome_Atom(dimentionality);
		}
		
//		buildNetwork(b);
		
//		System.out.print(m_genome.length + " ");
//		System.out.print(m_ann.getNeuronNumber()+ "\n");
	}

	public ANN_Individual(ANN_Individual ind)
	{
		super(ind);
		m_genome = new ANN_Genome_Atom[ind.getGenomeSize()];
		
		setBuilder(ind.getBuilder());
		setFitnessObj(ind.getFitnessObj());
		setStaticFitness(ind.getStaticFitness());
		setDynamicFitness(ind.getDynamicFitness());
		
		for (int i = 0; i < ind.getGenomeSize(); i++)
		{
			m_genome[i] = new ANN_Genome_Atom((ANN_Genome_Atom)ind.m_genome[i]);
		}

//		buildNetwork(getBuilder());
	}
	
	public int getGenomeSize()
	{
		return m_genome.length;
	}
	
	public SpatialAnn getAnn()
	{
		return m_ann;
	}
	
	public ANN_Builder getBuilder()
	{
		return m_builder;
	}


	public ANN_Fitness getFitnessObj()
	{
		return m_fitness;
	}

	// Setters
	
	public void setStaticFitness(double fit)
	{
		m_staticFitness = fit;
		
	}

	public void setBuilder(ANN_Builder b)
	{
		m_builder = b;
	}

	public void setFitnessObj(ANN_Fitness fit)
	{
		m_fitness = fit;
	}

	private void intArrayCopy(int[] oldArray, int[] newArray)
	{
		for (int i = 0; i < oldArray.length; i++)
		{
			newArray[i]=oldArray[i];
		}
		
	}

	// Needed to find a bug.
	@Override
	public void buildPhenotype()
	{
		buildNetwork(getBuilder());
	}

	public void buildNetwork(ANN_Builder b)
	{
		m_ann = b.build((ANN_Genome_Atom[])getGenome());
	}

	// For debugging
	public void runNetwork()
	{
		double[] inputs = new double[getAnn().getNumOfInputs()];
		
		runNetwork(inputs);
	}
	
	public void runNetwork(double[] inputs)
	{
		if (getAnn()!=null)
		{
//			if (ANN_Population.debugFlag)
//			{
//				System.out.println("Bleep");
//				ANN_Population.debugFlag = false;
//			}
			getAnn().run(inputs);
		}
		else
			throw new RuntimeException();
	}
	
	@Override
	public GA_Individual selfReplicate()
	{
		GA_Individual I = new ANN_Individual(this);
		
		return I;
	}

	@Override
	public void calculateFitness()
	{
//		ANN_Fitness fit = new OrdinalFitness();
		buildNetwork(getBuilder());
		setStaticFitness(getFitnessObj().calculateFitness(this));
	}

	public double calculateBenchmark()
	{
		return getFitnessObj().calculateBenchmark(this);
	}

	@Override
	public void development(BasicBuilder b)
	{
		
		if (b.getClass().isInstance(getBuilder()))
			buildNetwork((ANN_Builder)b);
		else
			throw new RuntimeException("Bad builder in type ANN_Individual");
	}
}
