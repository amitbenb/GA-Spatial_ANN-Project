package ann;

/**
 * Basic class for ANNs made of artificial neurons that output values in the
 * (-1,1) range.
 * 
 * @author amitbenb
 * 
 */
public abstract class BasicAnn
{
	protected boolean networkActive = false;
	protected int m_linkNumber = 0;
	protected double[] outputs;		// For outputs.

	public class ArtificialNeuron
	{
		double m_threshold = 0;
		double m_factor = 1; // Higher factor means stronger reaction.

//		ArrayList<Double> inputs = new ArrayList<Double>();
		double[] inputs = new double[ANN_Builder.neuronInputsSizeLimit];
		ArtificialNeuron[] inputSources = new ArtificialNeuron[ANN_Builder.neuronInputsSizeLimit]; 		

		Double output = new Double(0);
		BasicAnn.Link[] outlinks = new BasicAnn.Link[ANN_Builder.neuronOutputsSizeLimit];
		
		/**
		 * 
		 * @param threshold
		 *            Threshold value for the neuron.
		 * @param factor
		 *            Factor for sigmoid function.
		 */
		public ArtificialNeuron(double threshold,double factor)
		{
			this.m_threshold = threshold;
			this.m_factor = factor;
		}
		
		public double getThreshold()
		{
			return m_threshold;			
		}

		public double getFactor()
		{
			return m_factor;			
		}

		public void setThreshold(double threshold)
		{
			m_threshold = threshold;			
		}

		public void setFactor(double factor)
		{
			m_factor = factor;			
		}

		public void run()
		{
			calculate();
			transmit();
		}
		
		/**
		 * Calculates neuron output.
		 */
		public void calculate()
		{
			double calcVal = 0;
			for (int i = 0; i < this.inputs.length; i++)
			{
				calcVal += this.inputs[i];
			}
			
			this.output = neuronFunction(calcVal);
		}
		
		/**
		 * Transmit output through outlinks.
		 */
		public void transmit()
		{
			for (int i = 0; i<outlinks.length; i++)
			{
//				BasicAnn.Link link = iterator.next();
				if(outlinks[i]!=null)
				{
//					if(outlinks[i].destination == null)
//						System.out.println("NULL!!!");
					// Unfortunately. Neurons get connected to nothing.
//					if(outlinks[i].destination != null)
						outlinks[i].destination.addInput(this.output * outlinks[i].weight, this);
				}
			}
		}

		/**
		 * 
		 * @param inputValue Value of input
		 * @param source Source neuron of input (for override)
		 */
		protected void addInput(double inputValue, ArtificialNeuron source)
		{
			for (int i = 0; i < this.inputs.length; i++)
			{
				if (this.inputSources[i] == source || this.inputSources[i] == null || this.inputs[i]==0)
				{
					this.inputs[i] = inputValue;
					this.inputSources[i] = source;
					break;
				}
			}
		}

		public Double neuronFunction(double calcVal)
		{
			return Math.tanh(m_factor * (calcVal - m_threshold));
		}

		public void clean()
		{
			for (int i = 0; i < this.inputs.length; i++)
			{
				this.inputs[i] = 0;
			}
			
		}
		
		public void cleaupBadLinks()
		{
			for (int i = 0; i < outlinks.length && outlinks[i] != null; i++)
			{
				if (outlinks[i].destination == null)
				{
					// Remove bad link.
					for (int j = i; j < outlinks.length - 1; j++)
					{
						outlinks[j] = outlinks[j+1];
					}
					outlinks[outlinks.length - 1] = null;
					m_linkNumber--;
				}
			}
			
		}
		

		
		public void addLink(ArtificialNeuron destination, double weight)
		{
			int i=0;
//			try
//			{
			if (destination!=null)
			{
				for (i = 0; i < outlinks.length && outlinks[i] != null; i++)
				{
					if (outlinks[i].destination == destination)
					{
						outlinks[i].weight = weight;
						break;
					}
				}
			}
//			}
//			catch (Exception e)
//			{
//				System.out.println(i + " " + outlinks.length);
//				throw new RuntimeException();
//			}
			
			if(i < outlinks.length)
			{
				if (outlinks[i] == null)
				{
					outlinks[i] = new Link(destination, weight);
					m_linkNumber++;
				}
			}
		}
	}

	public class Link
	{
		ArtificialNeuron destination;
		double weight;
	
		public Link(ArtificialNeuron destination, double weight)
		{
//			this.source = source;
			this.destination = destination;
			this.weight = weight;
		}
	}

	public boolean isNetworkActive()
	{
		return networkActive;
	}
	
	public double[] getOutputs()
	{
		return outputs;
	}
	
	abstract public int getNumOfInputs();

	abstract public int getNumOfOutputs();
	
	protected void setNetworkActive(boolean networkActive)
	{
		this.networkActive = networkActive;
	}
	
	abstract public void cleanupBadLinks();
	

	abstract public void run(double[] inputs);


}
