package ann;

import java.util.Iterator;

import runs.Utility;

/**
 * Derived ANN class. Under construction.
 * 
 * @author amitbenb
 *
 */
public class SpatialAnn extends BasicAnn implements Iterable<SpatialAnn.SpatialNeuron>
{

	private int m_linkNumberLimit = 1000; // It's in A
	
	private int m_inputNumber = 0, m_outputNumber = 0, m_neuronCurrentNumber = 0,
			m_neuronGridSize = 0, m_neuronLimit = 0;
	
	private int m_numOfRounds = 1; // Number of rounds on ann. 
	
	private SpatialNeuron[] m_inputNeurons,m_neurons,m_outputNeurons;		// For neurons.
	private int[] m_inGridSize, m_annGridSize, m_outGridSize; // Grid sizes
	private boolean[] m_networkMap; // indicate with neurons exist (!=null) in m_neurons[] 

//	private int locks = 0;

	public class SpatialIterator implements Iterator<SpatialNeuron>
	{
		
		int m_idx = 0;
		
		public SpatialIterator()
		{
			for (m_idx = 0; m_idx < m_neurons.length && m_neurons[m_idx]==null; m_idx++);
		}
		
		@Override
		public boolean hasNext()
		{
			return m_idx < m_neurons.length;
		}

		@Override
		public SpatialNeuron next()
		{
			int i = m_idx;
			if (m_idx < m_neurons.length)
			{
				m_idx++;
				for (; m_idx < m_neurons.length && m_neurons[m_idx] == null; m_idx++)
					;
				return m_neurons[i];
			}
			return null;
		}

		@Override
		public void remove()
		{
			throw new RuntimeException("Remove not implemented for SpatialIterator");		
		}
		
	}
	
	public class SpatialNeuron extends BasicAnn.ArtificialNeuron
	{
		public int[] m_location = null;
		
//		public final int UP_CODE = 1; 
//		public final int DOWN_CODE = 2;
//		public final int LEFT_CODE = 3; 
//		public final int RIGHT_CODE = 4;
		

		/* Read encodings
		 * 0 - All
		 * 1 - Up
		 * 2 - down
		 * 3 - Left
		 * 4 - Right
		 */

//		int m_readMode = 0; // 0 is always all;
		// read mode here is natural number. Out side it's in the [1...m_numOfReadModes] range.
		int m_readMode = Utility.generateRandomNaturalInteger(Utility.LARGE_NUMBER); // 0 is always all;
		boolean m_youngFlag = false;
		boolean m_awake = true;
		
		public SpatialNeuron(double threshold,double factor,int[] location, int readMode, boolean youngFlag)
		{
			super(threshold, factor);
			setLocation(location);
			setReadMode(readMode);
			setYoungFlag(youngFlag);

		}
		
		public int getReadMode()
		{
			return m_readMode;
		}
		
		public int getNumOfReadModes()
		{
			return Spatial_ANN_Builder.NUMBER_OF_READ_ENCODINGS;
		}
		
		public int[] getLocation()
		{
			return m_location;
		}
		
		public boolean getYoungFlag()
		{
			return m_youngFlag;
		}
		
		public boolean getAwakeFlag()
		{
			return m_awake;
		}
		

		// Setters
		
		public void setReadMode(int readMode)
		{
//			m_readMode = readMode  % this.m_numOfReadModes;
			m_readMode = readMode;
		}
		
		protected void setLocation(int[] location)
		{
			if(location != null)
			{
				m_location = new int[location.length];
				
				for (int i = 0; i < location.length; i++)
				{
					setLocation(i,location[i]);					
				}
			}
		}

		private void setLocation(int idx, int value)
		{
			idx = idx % m_location.length;
			m_location[idx] = value % m_annGridSize[idx];
		}
		
		public void setYoungFlag(boolean youngFlag)
		{
			m_youngFlag = youngFlag;
		}
		
		public void setAwakeFlag(boolean awake)
		{
			this.m_awake = awake;
		}

		

//		public boolean isReadEncodingAll()
//		{
//			return this.m_readMode == 0;
//		}
//		
//		public boolean isReadEncodingUp()
//		{
//			return this.m_readMode == UP_CODE;
//		}
//		
//		public boolean isReadEncodingDown()
//		{
//			return this.m_readMode == DOWN_CODE;
//		}
//		
//		public boolean isReadEncodingLeft()
//		{
//			return this.m_readMode == LEFT_CODE;
//		}
//		
//		public boolean isReadEncodingRight()
//		{
//			return this.m_readMode == RIGHT_CODE;
//		}
		
	}
	
	
	public SpatialAnn(int[] inGridSize, int[] outGridSize, int linkLimit, int neuronLimit, int[] annGridSize)
	{
		setupGridsSizes(inGridSize,outGridSize,annGridSize);

		// Making sure operand values are workable.
//		if(m_inputNumber <= 0 || m_outputNumber <= 0 || linkLimit<=0 || neuronLimit<m_inputNumber+m_outputNumber)
//			return;
		if(m_inputNumber <= 0 || m_outputNumber <= 0 || linkLimit<=0 || neuronLimit<=0)
			return;
		
		
		setNetworkActive(true);
		
		m_neuronCurrentNumber = 0;
		this.m_neuronLimit = neuronLimit;
		this.m_linkNumberLimit = linkLimit; // + m_inputNumber;
		
		setupAllNeurons();

	}

	private void setupAllNeurons()
	{
		this.outputs = new double[this.m_outputNumber];


		
		this.m_inputNeurons = new SpatialNeuron[m_inputNumber];
		this.m_outputNeurons = new SpatialNeuron[m_outputNumber];

		// Hidden Neurons.
		this.m_neurons = new SpatialNeuron[m_neuronGridSize];
		this.m_networkMap = new boolean[m_neuronGridSize]; 

		
		for (int i = 0; i < m_inputNumber; i++)
		{
			// Input neurons.
			int[] coords = new int[m_inGridSize.length];
			setCoords(m_inGridSize,coords, i);
			this.m_inputNeurons[i] = new SpatialNeuron(0, 100,coords,0, false);
		}
		
		// No hidden neurons at startup.
		// All spaces in grid are null.
		for (int i = 0; i < m_neurons.length; i++)
		{
			//neural network is empty.
			this.m_neurons[i] = null;
			this.m_networkMap[i]=false;
		}
		
		for (int i = 0; i < m_outputNeurons.length; i++)
		{
			// Output neurons.
			int[] coords = new int[m_outGridSize.length];
			setCoords(m_outGridSize,coords, i);
			this.m_outputNeurons[i] = new SpatialNeuron(0, 100,coords,0, false);
		}

	}

	/**
	 * Set the coordinates from idx to coords.
	 * 
	 * @param GridSize Size of grid
	 * @param coords coordinates to be extracted here
	 * @param idx index in the 1d representation of the grid.
	 */
	private void setCoords(int[] GridSize, int[] coords, int idx)
	{
		for (int i = coords.length - 1; i >= 0 ; i--)
		{
			coords[i] = idx % GridSize[i];
			idx = idx / GridSize[i];
		}
	}

	private boolean setupGridsSizes(int[] inGridSize, int[] outGridSize, int[] annGridSize)
	{
		boolean retVal = true;
		
		//Checking Grids are possible to work with.
		if(inGridSize.length>annGridSize.length || outGridSize.length>annGridSize.length)
			retVal=false;
		
		else
		{
			m_inGridSize = new int[inGridSize.length]; 
			m_outGridSize = new int[outGridSize.length]; 
			m_annGridSize = new int[annGridSize.length]; 
			intArrayCopy(inGridSize,m_inGridSize); 
			intArrayCopy(outGridSize,m_outGridSize); 
			intArrayCopy(annGridSize,m_annGridSize);
			
			// Calculate number of inputs.
			m_inputNumber = 1;
			for (int i = 0; i < inGridSize.length; i++)
			{
				m_inputNumber *= inGridSize[i];
			}
			
			// Calculate number of outputs.
			m_neuronGridSize = 1;
			for (int i = 0; i < annGridSize.length; i++)
			{
				m_neuronGridSize *= annGridSize[i];
			}
			
			// Calculate number of outputs.
			m_outputNumber = 1;
			for (int i = 0; i < outGridSize.length; i++)
			{
				m_outputNumber *= outGridSize[i];
			}
			
			// Deprecated (probably)
//			m_neuronNumber = m_inputNumber + m_outputNumber;
		}
		
		return retVal;
	}

	public void connectNeurons(int[] location1,int[] location2, double weight)
	{
		int idx1 = getIndexfromlocation(location1,m_annGridSize);
//		int idx2 = getIndexfromlocation(location1,m_annGridSize);
		int idx2 = getIndexfromlocation(location2,m_annGridSize);
		
		if(m_linkNumber < getLinkNumberLimit())
			m_neurons[idx1].addLink(m_neurons[idx2], weight);
	}
	

	/**
	 * Connect ann neuron to input neuron.
	 * Uses relative location to choose output neuron.
	 * 
	 * @param annLocation Location of ann neuron to connect.
	 * @param inLocation Location offset of input neuron to connect.
	 * @param weight Connection weight.
	 */
	public void connectNeuronToInput(int[] annLocation,int[] inLocation, double weight)
	{
		int idxANN = getIndexfromlocation(annLocation,m_annGridSize);

		// fixing idxIn to match a relative location depending on annLocation
		int[] realInLocation = new int[m_inGridSize.length];
		for (int i = 0; i < realInLocation.length; i++)
		{
			if (i < annLocation.length)
			{
				// factor is size ratio for dimension i 
				double factor =  (double)m_annGridSize[i] / m_inGridSize[i];
				
				// This isn't trivial but modular arithmetic will work even though
				// annLocation[i] may be a big or negative number.
				realInLocation[i] = (int)(inLocation[i] + annLocation[i] / factor);
			}
			else // input grid has more dimensions than ann grid
				realInLocation[i] = inLocation[i];
		}

		// Now we are ready to calculate input neuron location.
		int idxIn = getIndexfromlocation(realInLocation,m_inGridSize);
		
//		if(idxIn>=9)
//			idxIn = getIndexfromlocation(inLocation,m_inGridSize); 

		if(m_linkNumber < getLinkNumberLimit())
			m_inputNeurons[idxIn].addLink(m_neurons[idxANN], weight);
	}
	
	/**
	 * Connect ann neuron to all input neurons.
	 * 
	 * @param annLocation Location of ann neuron to connect.
	 * @param weight Connection weight.
	 */
	public void connectNeuronToAllInput(int[] annLocation, double weight)
	{
		int idxANN = getIndexfromlocation(annLocation,m_annGridSize);

		for (int idxIn = 0; idxIn < m_inputNeurons.length; idxIn++)
		{
			if(m_linkNumber < getLinkNumberLimit())
				m_inputNeurons[idxIn].addLink(m_neurons[idxANN], weight);
		}
		
	}
	
	/**
	 * Connect ann neuron to output neuron.
	 * Uses relative location to choose output neuron.
	 * 
	 * @param annLocation Location of ann neuron to connect.
	 * @param outLocation Location offset of output neuron to connect.
	 * @param weight Connection weight.
	 */
	public void connectNeuronToOutput(int[] annLocation,int[] outLocation, double weight)
	{
		int idxANN = getIndexfromlocation(annLocation,m_annGridSize);

		// fixing idxOut to match a relative location depending on annLocation
		// Also fixing possible problem with number of dimensions.
		int[] realOutLocation = new int[m_outGridSize.length];
		for (int i = 0; i < realOutLocation.length; i++)
		{
			if (i < annLocation.length)
			{
				// factor is size ratio for dimension i 
				double factor =  (double)m_annGridSize[i] / m_outGridSize[i];
				
				// Getting location for output neuron to be connected.
				// This isn't trivial but modular arithmetic will work even though
				// annLocation[i] may be a big or negative number.
				realOutLocation[i] = (int)(outLocation[i] + annLocation[i] / factor);
			}
			else // output grid has more dimensions than ann grid
				realOutLocation[i] = outLocation[i];
		}

		// Now we are ready to calculate output neuron location.
		int idxOut = getIndexfromlocation(realOutLocation,m_outGridSize);
	
//		if(idxOut>=9)
//			idxOut = getIndexfromlocation(outLocation,m_outGridSize);; 
		
		if(m_linkNumber < getLinkNumberLimit())
			m_neurons[idxANN].addLink(m_outputNeurons[idxOut], weight);
	}
	
	/**
	 * Connect ann neuron to all output neurons.
	 * 
	 * @param annLocation Location of ann neuron to connect.
	 * @param weight Connection weight.
	 */
	public void connectNeuronToAllOutput(int[] annLocation, double weight)
	{
		int idxANN = getIndexfromlocation(annLocation,m_annGridSize);

		for (int idxOut = 0; idxOut < m_outputNeurons.length; idxOut++)
		{
			if(m_linkNumber < getLinkNumberLimit())
				m_neurons[idxANN].addLink(m_outputNeurons[idxOut], weight);
		}
		
	}
	
	public void removeNeuron(int[] location)
	{
		int idx = getIndexfromlocation(location,m_annGridSize);
		
		if (m_networkMap[idx])
		{
			m_neuronCurrentNumber--;
			m_networkMap[idx]=false;
			m_neurons[idx]=null;
		}
	}

	public void moveNeuron(int[] oldLocation, int[] newLocation)
	{
		int idx1 = getIndexfromlocation(oldLocation,m_annGridSize);
		int idx2 = getIndexfromlocation(newLocation,m_annGridSize);

		// Make sure there's something to move.
		if (m_networkMap[idx1] && idx1 != idx2)
		{
			// Getting rid of existing neuron in newLocation, if there.
			if (m_networkMap[idx2])
				removeNeuron(newLocation);
			
			m_networkMap[idx1] = false;
			m_networkMap[idx2] = true;
			m_neurons[idx2] = m_neurons[idx1];
			m_neurons[idx1] = null;
			m_neurons[idx2].setLocation(newLocation);
		}
		
	}
	
	public void replaceNeurons(int[] oldLocation, int[] newLocation)
	{
		int idx1 = getIndexfromlocation(oldLocation,m_annGridSize);
		int idx2 = getIndexfromlocation(newLocation,m_annGridSize);
		
		// Case a: Move neuron. 
		if (m_networkMap[idx1] && !m_networkMap[idx2])
		{
			moveNeuron(oldLocation, newLocation);
		}
		// Case b: Move neuron opposite direction. 
		else if (!m_networkMap[idx1] && m_networkMap[idx2])
		{
			moveNeuron(newLocation, oldLocation);
		}
		// Case c: Both neurons are there. Replace neurons. 
		else if (m_networkMap[idx1] && m_networkMap[idx2])
		{
			SpatialNeuron n = m_neurons[idx2];
			m_neurons[idx2] = null;
			m_networkMap[idx2] = false;
			
			moveNeuron(oldLocation, newLocation);
			
			m_neurons[idx1] = n;
			m_networkMap[idx1] = true;
			n.setLocation(oldLocation);

		}
			
	}
	
	/**
	 * 
	 * @param location Location vector, not necessarily normalized. 
	 * @param gridSize
	 * @return
	 */
	private int getIndexfromlocation(int location[], int[] gridSize)
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
			
			idx *= gridSize[i];
			idx += temp;
		}
		
		return idx;
	}

	@Override
	public int getNumOfInputs()
	{
		if (isNetworkActive())
			return this.m_inputNumber;
		else
			return 0;
	}

	@Override
	public int getNumOfOutputs()
	{
		return m_outputNumber;
	}

	public int getNeuronNumber()
	{
		return m_neuronCurrentNumber;
	}

	public int getLinkNumber()
	{
		return m_linkNumber;			
	}

	public int getNeuronLimit()
	{
		return m_neuronLimit;
	}

	public int getLinkNumberLimit()
	{
		return m_linkNumberLimit;
	}
		
	public int getDimentionality()
	{
		return m_annGridSize.length;
	}

	public int getDimentionSize(int i)
	{
		return m_annGridSize[i];
	}
	
	public int getInDimentionSize(int i)
	{
		return m_inGridSize[i];
	}

	private int getNumOfRounds()
	{
		return m_numOfRounds;
	}

	public void setNumOfRounds(int numOfRounds)
	{
		m_numOfRounds = numOfRounds;
	}


	
//	@Override
//	public void addLink(int node1Idx, int node2Idx, double weight)
//	{
//		if (!isNetworkActive() || !isUnlocked())
//		{
//			lock();
//			return;
//		}
//
//		if(linkNumber>=linkNumberLimit)
//		{
//			setNetworkActive(false);
//			return;
//		}
//		
//		//else
//
//		node1Idx = node1Idx % getNeuronNumber();
//		node2Idx = (node2Idx % (getNeuronNumber() - getNumOfInputs()));
//
//		int idx1=0;
//
//		while(node1Idx>0)
//		{
//			if (neurons[idx1] != null)
//				node1Idx--;
//			idx1 = (idx1 + 1) % neurons.length;
//		}
//		while(neurons[idx1] == null)
//			idx1 = (idx1 + 1) % neurons.length;
//
//		int idx2 = getNumOfInputs(); // Not going to be an input neuron.
//		while(node2Idx>0)
//		{
//			if (neurons[idx2] != null)
//				node2Idx--;
//			idx2 = (idx2 + 1) % neurons.length;
//		}
//		while(neurons[idx2] == null)
//			idx2 = (idx2 + 1) % neurons.length;
//		
//		if(idx1>idx2)
//		{
//			int tmp=idx1;
//			idx2=idx1;
//			idx1=tmp;
//		}
//		
//		if(idx1!=idx2) // No self links.
//		{
//			boolean linkAlreadyExists = false;
////			for (Iterator<BasicAnn.Link> iterator = neurons[idx1].outlinks
////					.iterator(); !linkAlreadyExists && iterator.hasNext();)
//			for (int i=0; i<neurons[idx1].outlinks.length;i++)
//			{
////				BasicAnn.Link link = iterator.next();
//				if (neurons[idx1].outlinks[i] != null
//						&& neurons[idx1].outlinks[i].destination == neurons[idx2])
//				{
//					linkAlreadyExists = true;
//					neurons[idx1].outlinks[i].weight = weight;
//				}
//			}
//			if(!linkAlreadyExists)
//				for (int i=0; i<neurons[idx1].outlinks.length;i++)
//				{
//					if (neurons[idx1].outlinks[i] == null)
//					{
//						neurons[idx1].outlinks[i] =new Link(neurons[idx2], weight);
//						linkNumber++;
//						break;
//					}
//				}
//		}
//	}

	/**
	 * Add a node to network at coords.
	 * 
	 * @param coords
	 * @param threshold
	 * @param factor 
	 */
	public void addNode(int[] coords, double threshold, double factor)
	{
		int idx = getIndexfromlocation(coords, m_annGridSize);
		
		addNode(idx, coords, threshold, factor, 0, false); // Actually add the node.
	}

	/**
	 * Add a node to network at coords.
	 * 
	 * @param coords
	 * @param threshold
	 * @param factor
	 * @param readMode TODO
	 */
	public void addYoungNode(int[] coords, double threshold, double factor, int readMode)
	{
		int idx = getIndexfromlocation(coords, m_annGridSize);
		
		addNode(idx, coords, threshold, factor, readMode, true); // Actually add the node.
	}
	
	/**
	 * Make a copy of all edges touching source neuron in destination neuron.
	 * 
	 * @param sourceNeuron
	 * @param destinationLocation
	 */
	public void copyAllEdgesFromNeuron(SpatialNeuron sourceNeuron, int[] destinationLocation)
	{
		int destIdx = getIndexfromlocation(destinationLocation, m_annGridSize);
		
		if(m_networkMap[destIdx] == false)
		{
			return;
//			throw new RuntimeException("Exception in copyAllEdgesFromNeuron(). No neuron in destination.");
		}
		
		if(m_neurons[destIdx] == sourceNeuron)
			throw new RuntimeException("Exception in copyAllEdgesFromNeuron(). Destination neuron same as source neuron.");

		//else
		
		SpatialNeuron destNeuron = m_neurons[destIdx];
		
		for (int i = 0; i < sourceNeuron.outlinks.length; i++)
		{
			if (sourceNeuron.outlinks[i] != null)
			{
				if(m_linkNumber < getLinkNumberLimit())
					destNeuron.addLink(sourceNeuron.outlinks[i].destination, sourceNeuron.outlinks[i].weight);
			}
		}
		
		for(SpatialNeuron n: this)
		{
			for (int i = 0; i < n.outlinks.length; i++)
			{
				if (n.outlinks[i] != null && n.outlinks[i].destination == sourceNeuron)
				{
					if(m_linkNumber < getLinkNumberLimit())
						n.addLink(destNeuron, n.outlinks[i].weight);
				}
			}
			
		}
		
	}

	

	/**
	 * Add a node to network at m_neurons[idx].
	 * 
	 * @param idx
	 * @param threshold
	 * @param factor 
	 * @param readMode The Read mode of the new neuron. 
	 * @param isYoung Is this a young node (for mechanics of development) 
	 */
	private void addNode(int idx, int[] location, double threshold, double factor, int readMode, boolean isYoung)
	{
//		try
//		{
			if (!m_networkMap[idx] && m_neuronCurrentNumber < m_neuronLimit)
			{
				m_neurons[idx] = new SpatialNeuron(threshold, factor, location, readMode, isYoung);
				m_networkMap[idx] = true;
				m_neuronCurrentNumber++;
			}
//		}
//		catch (Exception e)
//		{
//			System.out.println(idx + " " + m_neurons.length);
//			e.printStackTrace();
//		}
	}

	@Override
	public void run(double[] inputs)
	{
		if(!isNetworkActive() || inputs.length < getNumOfInputs())
			return;
		
		//else

		cleanNetwork();

		int i = 0;
		// Setup inputs in input neurons.
		for (i = 0; i < getNumOfInputs(); i++)
		{
			m_inputNeurons[i].clean();
			m_inputNeurons[i].addInput(inputs[i], null);
		}

		for (i=0; i < m_inputNeurons.length; i++)
		{
			m_inputNeurons[i].run();
		}
		
		for (i=0; i < getNumOfRounds(); i++)
		{
			for (int j=0; j < m_neurons.length; j++)
			{
				if (m_networkMap[j])
				{
					m_neurons[j].run();
					m_neurons[j].clean();
				}
			}
		}
		// Setup outputs
//		this.outputs = new ArrayList<Double>();
		for (i=0; i < m_outputNeurons.length; i++)
		{
			m_outputNeurons[i].run();
			this.outputs[i] = m_outputNeurons[i].output;
		}

	}

	private void cleanNetwork()
	{
		for (int i = 0; i < m_neurons.length; i++)
		{
			if(m_networkMap[i])
				m_neurons[i].clean();
		}
		
		for (int i = 0; i < getOutputs().length; i++)
		{
			m_outputNeurons[i].clean();
		}

	}

//	@Override
//	public void lock()
//	{
//		this.locks=1;
//	}
//
//	@Override
//	public void unlock()
//	{
//		if (this.locks>0)
//			this.locks--;
//	}
//	
//	private boolean isUnlocked()
//	{
//		return (this.locks==0);
//	} 

	private void intArrayCopy(int[] oldArray, int[] newArray)
	{
		for (int i = 0; i < oldArray.length; i++)
		{
			newArray[i]=oldArray[i];
		}
		
	}

	@Override
	public Iterator<SpatialNeuron> iterator()
	{
		return new SpatialIterator();
	}

	@Override
	public void cleanupBadLinks()
	{
		for (SpatialAnn.SpatialNeuron n : this)
		{
			n.cleaupBadLinks();
		}
	}

//	public void normailizeLocation(int[] location, int[] gridSize)
//	{
//		for (int i = 0; i < location.length; i++)
//		{
//			location[i] = (location[i]<0)?((-1)*(location[i])):(location[i]); 
//			location[i] = location[i] %  gridSize[i];
//		}
//		
//	}

}
