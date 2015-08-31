package ann;

//TODO Get weights and and factor and threshold encoded.

import runs.Utility;
import ann.SpatialAnn.SpatialNeuron;
import gaPack.ANN_Individual.ANN_Genome_Atom;

public class Spatial_ANN_Builder implements ANN_Builder
{
	public static int neuronLimit = 100;
	public static int linkLimit = 2000;
	
	// m_annGridSize needs to be of maximal dimensionality.
	public static int[] m_inGridSize, m_annGridSize, m_outGridSize;
	
	// Offset maximum for some cases (splitting etc).
	final public int SMALL_MOVE_LIMIT = 3;
	public static int NUMBER_OF_READ_ENCODINGS = 31;
	public static GeneticAnnIncoder ANN_BUILD_INCODER = new GeneticAnnIncoder();
	
	
	public SpatialAnn build(ANN_Genome_Atom[] genome)
	{
		SpatialAnn ann =
				new SpatialAnn(m_inGridSize, m_outGridSize, linkLimit, neuronLimit,
						m_annGridSize);		
		
//		m_oldIncoder = new OldGeneticAnnIncoder();
//		m_incoder = new GeneticAnnIncoder();
		
//		System.out.println("Building ANN");

		// Run on all genes.
		for (int i = 0; i < genome.length; i++)
		{
			ANN_Genome_Atom gene = genome[i];
			int readMode = gene.getReadMode();
			int opcode = gene.getOpCode();
//			int[] offset = gene.getLocationOffsetCopy();
			
			final GeneticAnnIncoder.ReadEncodingWeightVector readVector = Spatial_ANN_Builder.ANN_BUILD_INCODER.getReadVector();
			final GeneticAnnIncoder.WriteEncodingWeightVector writeVector = Spatial_ANN_Builder.ANN_BUILD_INCODER.getWriteVector();
//			final NeuronReadEncodingInfo neuronInfo = m_incoder.getNeuronInfo();

//			readMode = readVector.fixReadMode(readMode);
			opcode = writeVector.fixOpCode(opcode);
			
//			if(readMode<0)
//				System.out.println("Boop!");
			
			// new node is a special opcode. Independent of neuron. 
			if (writeVector.isNewNodeEncoding(opcode))
			{
				int [] location = gene.getLocationOffsetCopy(); // For consistency
				normalizeLocation(location, null, Integer.MAX_VALUE);

				// No genes to decide neuron threshold and factor.
				// Initializing to default value of 1 for now.
				ann.addYoungNode(location, gene.getThreshold(), gene.getFactor(), gene.getNewReadMode());
				
			}
		

			// For this gene run on all nodes.
			for (SpatialAnn.SpatialNeuron n : ann)
			{
				
//				if(!n.getYoungFlag() && oldReadModeMatch(readMode,n.getReadMode()))
				if(!n.getYoungFlag() && newReadModeMatch(readMode,n))
				{
//					System.out.println("BOOP!");
					n.setYoungFlag(true);
					
					if (n.getAwakeFlag() == true)
					{
						// Actions performed by awake neurons
						if (writeVector.isMoveEncoding(opcode))
						{
							int [] location = gene.getLocationOffsetCopy(); 

							normalizeLocation(location, n.getLocation(), SMALL_MOVE_LIMIT);
							
							ann.moveNeuron(n.getLocation(), location);
						}
//						if(writeVector.isUpDownSplitEncoding(opcode))
//						{
//							splitNeuron(ann,n,gene, n.UP_CODE, n.DOWN_CODE);
//							System.out.println("This split shouldn't happen");
//						}
//						else if(writeVector.isDownUpSplitEncoding(opcode))
//						{
//							splitNeuron(ann, n, gene, n.DOWN_CODE, n.UP_CODE);
//							System.out.println("This split shouldn't happen");
//						}
//						else if (writeVector.isLeftRightSplitEncoding(opcode))
//						{
//							splitNeuron(ann, n, gene, n.LEFT_CODE, n.RIGHT_CODE);
//							System.out.println("This split shouldn't happen");
//						}
//						else if (writeVector.isRightLeftSplitEncoding(opcode))
//						{
//							splitNeuron(ann, n, gene, n.RIGHT_CODE, n.LEFT_CODE);
//							System.out.println("This split shouldn't happen");
//						}
						else if (writeVector.isConnectEncoding(opcode))
						{
							int [] location = gene.getLocationOffsetCopy();
							
							normalizeLocation(location, n.getLocation(), Integer.MAX_VALUE);
							
							ann.connectNeurons(n.getLocation(), location, gene.getWeight());
						}
						else if (writeVector.isConnectOutputEncoding(opcode))
						{
							int [] location = gene.getLocationOffsetCopy();

							ann.connectNeuronToOutput(n.getLocation(), location, gene.getWeight());
						}
						else if (writeVector.isConnectInputEncoding(opcode))
						{
							int [] location = gene.getLocationOffsetCopy();
							
							ann.connectNeuronToInput(n.getLocation(), location, gene.getWeight());
						}
						else if (writeVector.isConnectAllOutputEncoding(opcode))
						{
							ann.connectNeuronToAllOutput(n.getLocation(), gene.getWeight());
						}
						else if (writeVector.isConnectAllInputEncoding(opcode))
						{
							ann.connectNeuronToAllInput(n.getLocation(), gene.getWeight());
						}
						else if (writeVector.isMutateThresholdFactorEncoding(opcode))
						{
							n.setThreshold(gene.getThreshold()); 
							n.setFactor(gene.getFactor()); 
						}
						else if(writeVector.isSplitEncoding(opcode))
						{
							splitNeuron(ann,n,gene, n.getReadMode(), gene.getNewReadMode());
						}
						else if(writeVector.isPowerSplitEncoding(opcode))
						{
							// Currently the same as split.
							splitNeuronWithEdges(ann,n,gene, n.getReadMode(), gene.getNewReadMode());
						}
						else if(writeVector.isSleepEncoding(opcode))
						{
							n.setAwakeFlag(false);
						}
						else if(writeVector.isDieEncoding(opcode))
						{
							ann.removeNeuron(n.getLocation());
						}
					}
					else //(n.getAwakeFlag() == false) 
					{
						// Actions performed by sleeping neurons
						if(writeVector.isAwakenEncoding(opcode))
						{
							n.setAwakeFlag(true);
						}
					
					}

				}
			}
			// SAME IDEA: Age neurons marked as NEW here, Before next gene is read.
			for (SpatialAnn.SpatialNeuron n : ann)
			{
				n.setYoungFlag(false);
			}

		}
		
		ann.cleanupBadLinks();
		
//		m_modes = new int[];
		
//		// Setup modes array
//		for (int i = 0; i < m_neurons.length; i++)
//		{
//			//neural network is empty.
//			m_neurons[i] = null;
//			m_networkMap[i]=false;
//		}
		

		
		//TODO: Finish this when SpatialANN is fixed.
		
		return ann;
	}

	private void splitNeuron(SpatialAnn ann, SpatialNeuron neuron, ANN_Genome_Atom gene,
			int readMode1, int readMode2)
	{
		int [] location1 = gene.getLocationOffsetCopy(); 
		int [] location2 = gene.getLocationOffsetCopy();
//		ann.normailizeLocation(location1,m_annGridSize);
//		ann.normailizeLocation(location2,m_annGridSize);
		negateArray(location2);
		
		normalizeLocation(location1, neuron.getLocation(), SMALL_MOVE_LIMIT);
		normalizeLocation(location2, neuron.getLocation(), SMALL_MOVE_LIMIT);
		
		if(Utility.isSameIntArray(location1,location2))
			return;
		
		ann.moveNeuron(neuron.getLocation(), location2);
		neuron.setReadMode(readMode1);
		ann.addYoungNode(location1, neuron.getThreshold(), neuron.getFactor(), readMode2);
		
	}

	private void splitNeuronWithEdges(SpatialAnn ann, SpatialNeuron neuron, ANN_Genome_Atom gene,
			int readMode1, int readMode2)
	{
		int [] location1 = gene.getLocationOffsetCopy(); 
		int [] location2 = gene.getLocationOffsetCopy();
//		ann.normailizeLocation(location1,m_annGridSize);
//		ann.normailizeLocation(location2,m_annGridSize);
		negateArray(location2);
		
		normalizeLocation(location1, neuron.getLocation(), SMALL_MOVE_LIMIT);
		normalizeLocation(location2, neuron.getLocation(), SMALL_MOVE_LIMIT);
		
		if(Utility.isSameIntArray(location1,location2))
			return;
		
		ann.moveNeuron(neuron.getLocation(), location2);
		neuron.setReadMode(readMode1);
		ann.addYoungNode(location1, neuron.getThreshold(), neuron.getFactor(), readMode2);
		
		ann.copyAllEdgesFromNeuron(neuron,location1);
	}

	private void negateArray(int[] location)
	{
		for (int i = 0; i < location.length; i++)
		{
			location[i] *= (-1);
		}		
	}
	
	/**
	 * Algorithm translates 'location' to an actual location in the ANN grid. If
	 * 'baseLocation' is not null, start from it instead of [0,0...]
	 * 
	 * @param location
	 *            Location to be Normalized.
	 * @param baseLocation
	 *            Base location to use as starting point (or null)
	 * @param offsetLimit
	 *            Limit on size of step.
	 */
	private void normalizeLocation(int[] location, int[] baseLocation, int offsetLimit)
	{
		for (int i = 0; i < m_annGridSize.length && i < location.length; i++)
		{
			if(baseLocation == null)
			{
				// No base location.
				// Simple normalization.
				
				// This is modulu operation that returns positive reminder.
				int devisor = Math.min(m_annGridSize[i], offsetLimit);
				location[i] = location[i] % devisor;
				if(location[i] < 0)
					location[i] += devisor;
				
//				if (location[i]<0)
//				{
//					System.out.println(original + "\n" + m_annGridSize[i] + "\n" + offsetLimit);
//					throw new RuntimeException();
//				}
			}			
			else //if (baseLocation != null)
			{
				// complicated normalization location[i] is an offset.
				
				// negFlag == -1 for location[i]<0
				int negFlag = (location[i]>=0)?(1):(-1); 

				// location[i] now positive
				location[i]*=negFlag;

				// This is modulu operation that returns positive reminder.
				int devisor = Math.min(m_annGridSize[i], offsetLimit);
				location[i] = location[i] % devisor;
				if(location[i] < 0)
					location[i] += devisor;

				// location[i] possibly negative again.
				location[i]*=negFlag;
				
				if (i < baseLocation.length)
					location[i] += baseLocation[i];
				
				if(location[i] < 0)
					location[i] += devisor;
			}
			
			
			
		}
		
		return;
	}

	private boolean newReadModeMatch(int geneReadMode, SpatialAnn.SpatialNeuron n)
	{
		boolean retVal = false;

		// Easier to judge heap relations if range begins in 1.
		geneReadMode = geneReadMode % n.getNumOfReadModes() + 1;
		int neuronReadMode = n.getReadMode() % n.getNumOfReadModes() + 1;
		
		int minMode = Math.min(geneReadMode,neuronReadMode);
		int maxMode = Math.max(geneReadMode,neuronReadMode);

		//Checking if min is an ancestor of max.
		for (; minMode < maxMode; maxMode = maxMode / 2);
		if (minMode == maxMode)
			retVal = true;
		
		return retVal;
	}

/*	
	private boolean oldReadModeMatch(int geneReadMode, int neuronReadMode)
	{
		boolean retValue = false;
		
		if (m_oldIncoder.getReadVector().isAllEncoding(geneReadMode))
			retValue = true;
		else if (m_oldIncoder.getNeuronInfo().isReadEncodingAll(neuronReadMode))
			retValue = true;
		else if (m_oldIncoder.getNeuronInfo().isReadEncodingUp(neuronReadMode))
		{
			if(m_oldIncoder.getReadVector().isUpEncoding(geneReadMode)
					|| m_oldIncoder.getReadVector().isUpOrLeftEncoding(geneReadMode)
					|| m_oldIncoder.getReadVector().isUpOrRightEncoding(geneReadMode))
				retValue = true;
		}
		else if (m_oldIncoder.getNeuronInfo().isReadEncodingDown(neuronReadMode))
		{
			if(m_oldIncoder.getReadVector().isDownEncoding(geneReadMode)
					|| m_oldIncoder.getReadVector().isDownOrLeftEncoding(geneReadMode)
					|| m_oldIncoder.getReadVector().isDownOrRightEncoding(geneReadMode))
				retValue = true;
		}
		else if (m_oldIncoder.getNeuronInfo().isReadEncodingLeft(neuronReadMode))
		{
			if(m_oldIncoder.getReadVector().isLeftEncoding(geneReadMode)
					|| m_oldIncoder.getReadVector().isUpOrLeftEncoding(geneReadMode)
					|| m_oldIncoder.getReadVector().isDownOrLeftEncoding(geneReadMode))
				retValue = true;
		}
		else if (m_oldIncoder.getNeuronInfo().isReadEncodingRight(neuronReadMode))
		{
			if(m_oldIncoder.getReadVector().isRightEncoding(geneReadMode)
					|| m_oldIncoder.getReadVector().isUpOrRightEncoding(geneReadMode)
					|| m_oldIncoder.getReadVector().isDownOrRightEncoding(geneReadMode))
				retValue = true;
		}
		
		return retValue;
	}
/**/

	@Override
	public int getDimentionality()
	{
		return m_annGridSize.length;
	}

	public static String printArrayFixed(double[] arr, int[] gridSize)
	{
		String retVal = new String();
		
		switch (gridSize.length)
		{
		case 1:
			for (int i = 0; i < arr.length; i++)
				retVal = retVal + printInOrOutAtom(arr[i]);
			retVal = retVal + "\n";
			break;
		case 2:
			for (int i = 0; i < gridSize[0]; i++)
			{
				for (int j = 0; j < gridSize[1]; j++)
					retVal = retVal + printInOrOutAtom(arr[i * gridSize[1] + j]);
				retVal = retVal + "\n";
			}
			break;
		case 3:
			for (int i = 0; i < gridSize[0]; i++)
			{
				for (int j = 0; j < gridSize[1]; j++)
				{
					for (int k = 0; k < gridSize[2]; k++)
						retVal = retVal + printInOrOutAtom(arr[(i * gridSize[1] + j)*gridSize[2] + k]);
					retVal = retVal + "\n";
				}
				retVal = retVal + "\n";
			}
			break;

		default:
			retVal = retVal + "I cannot handle " + gridSize.length + "dimensions. Sorry \n";
			break;
		} 
		
		return retVal;
	}

	private static String printInOrOutAtom(double x)
	{
		String retVal = new String();
		if (x<-0.5)
			retVal = " -1";
		else if (x>0.5)
			retVal = "  1";
		else 
			retVal = "  0";
			
		return retVal;
	}

	public static String generateEncoderInfo()
	{
//		String retVal = new String(m_incoder.toString());
		return ANN_BUILD_INCODER.toString();
	}

}


