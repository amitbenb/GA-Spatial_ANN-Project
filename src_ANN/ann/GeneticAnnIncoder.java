package ann;

import java.util.Scanner;

public class GeneticAnnIncoder
{
	public class ReadEncodingWeightVector
	{
		
	}
	
	public class WriteEncodingWeightVector
	{
		
		/*
		 * Dictionary:
		 * 0: New node
		 * 1: Move
		 * 2: Connect
		 * 3: Connect output
		 * 4: Connect input
		 * 5: Connect all output
		 * 6: Connect all input
		 * 7: Mutate threshold
		 * 8: Split (nonspecific)
		 * 9: Power-Split 
		 * 10: Sleep
		 * 11: Wake up
		 * 12: Die
		 * 
		 */

		// These members control relative probability of different operations being
		// encoded when number is generated at random to serve as opcode
		public int m_newNodeWeight = 10;
		public int m_moveWeight = 2;
		public int m_connectWeight = 4;
		public int m_connectOutputWeight = 4; 
		public int m_connectInputWeight = 4;
		public int m_connectAllOutputWeight = 0;  
		public int m_connectAllInputWeight = 4;  
		public int m_mutateThreshFactorWeight = 2;
		public int m_splitWeight = 8;
		public int m_powerSplitWeight = 8;
		public int m_sleepWeight = 4;
		public int m_awakenWeight = 4;
		public int m_dieWeight = 2;
		
		// Ranges: XXXXMin <= XXXX < XXXXMax
		public int m_newNodeMin = 0, m_newNodeMax=0;
		public int m_moveMin = 0, m_moveMax = 0;
		public int m_connectMin = 0, m_connectMax = 0;
		public int m_connectOutputMin = 0, m_connectOutputMax = 0;
		public int m_connectInputMin = 0, m_connectInputMax = 0;
		public int m_connectAllOutputMin = 0, m_connectAllOutputMax = 0;
		public int m_connectAllInputMin = 0, m_connectAllInputMax = 0;
		public int m_mutateThreshFactMin = 0, m_mutateThreshFactMax = 0;
		
		public int m_splitMin = 0, m_splitMax = 0;
		public int m_powerSplitMin = 0, m_powerSplitMax = 0;
		public int m_sleepMin = 0, m_sleepMax = 0;
		public int m_awakenMin = 0, m_awakenMax = 0;
		public int m_dieMin = 0, m_dieMax = 0;

		
		public int m_sumOfAllWeights = 0;
		
		public WriteEncodingWeightVector()
		{
			updateSumOfWeights();
			calculateMinMaxRanges();
		}
		
		private void updateSumOfWeights()
		{
			m_sumOfAllWeights =
					m_newNodeWeight + m_moveWeight + m_connectWeight + m_connectOutputWeight
							+ m_connectInputWeight + m_connectAllOutputWeight
							+ m_connectAllInputWeight + m_mutateThreshFactorWeight
							+ m_splitWeight + m_powerSplitWeight + m_sleepWeight
							+ m_awakenWeight + m_dieWeight;
	}

		private void calculateMinMaxRanges()
		{
			m_newNodeMin = 0;
			m_newNodeMax = m_newNodeMin + m_newNodeWeight;
			
			m_moveMin = m_newNodeMax;
			m_moveMax = m_moveMin + m_moveWeight;
			
			m_connectMin = m_moveMax;
			m_connectMax = m_connectMin + m_connectWeight;
			
			m_connectOutputMin = m_connectMax;
			m_connectOutputMax = m_connectOutputMin + m_connectOutputWeight;
			
			m_connectInputMin = m_connectOutputMax;
			m_connectInputMax = m_connectInputMin + m_connectInputWeight;
			
			m_connectAllOutputMin = m_connectInputMax;
			m_connectAllOutputMax = m_connectAllOutputMin + m_connectAllOutputWeight;
			
			m_connectAllInputMin = m_connectAllOutputMax;
			m_connectAllInputMax = m_connectAllInputMin + m_connectAllInputWeight;
			
			m_mutateThreshFactMin = m_connectAllInputMax;
			m_mutateThreshFactMax = m_mutateThreshFactMin + m_mutateThreshFactorWeight;
			
			m_splitMin = m_mutateThreshFactMax;
			m_splitMax = m_splitMin + m_splitWeight;
			
			m_powerSplitMin = m_splitMax;
			m_powerSplitMax = m_powerSplitMin + m_powerSplitWeight;
			
			m_sleepMin = m_powerSplitMax;
			m_sleepMax = m_sleepMin + m_sleepWeight;
			
			m_awakenMin = m_sleepMax;
			m_awakenMax = m_awakenMin + m_awakenWeight;
			
			m_dieMin = m_awakenMax;
			m_dieMax = m_dieMin + m_dieWeight;
		}
		
		public boolean isNewNodeEncoding(int code)
		{
			return (m_newNodeMin <= code && code < m_newNodeMax);
		}
		
		public boolean isMoveEncoding(int code)
		{
			return (m_moveMin <= code && code < m_moveMax);
		}
		
		public boolean isConnectEncoding(int code)
		{
			return (m_connectMin <= code && code < m_connectMax);
		}
		
		public boolean isConnectOutputEncoding(int code)
		{
			return (m_connectOutputMin <= code && code < m_connectOutputMax);
		}
		
		public boolean isConnectInputEncoding(int code)
		{
			return (m_connectInputMin <= code && code < m_connectInputMax);
		}
		
		public boolean isConnectAllOutputEncoding(int code)
		{
			return (m_connectAllOutputMin <= code && code < m_connectAllOutputMax);
		}
		
		public boolean isConnectAllInputEncoding(int code)
		{
			return (m_connectAllInputMin <= code && code < m_connectAllInputMax);
		}
		
		public boolean isMutateThresholdFactorEncoding(int code)
		{
			return (m_mutateThreshFactMin <= code && code < m_mutateThreshFactMax);
		}
		
		public boolean isSplitEncoding(int code)
		{
			return (m_splitMin <= code && code < m_splitMax);
		}
		
		public boolean isPowerSplitEncoding(int code)
		{
			return (m_powerSplitMin <= code && code < m_powerSplitMax);
		}
		
		public boolean isSleepEncoding(int code)
		{
			return (m_sleepMin <= code && code < m_sleepMax);
		}
		
		public boolean isAwakenEncoding(int code)
		{
			return (m_awakenMin <= code && code < m_awakenMax);
		}
		
		public boolean isDieEncoding(int code)
		{
			return (m_dieMin <= code && code < m_dieMax);
		}
		

		public int fixOpCode(int opCode)
		{
			return (opCode<0)?((-1)*opCode % m_sumOfAllWeights):(opCode % m_sumOfAllWeights);
		}
		
	}

	private ReadEncodingWeightVector m_readEncWeights;
	private WriteEncodingWeightVector m_writeEncWeights;
//	private NeuronReadEncodingInfo m_neuronEncInfo;
	
	public GeneticAnnIncoder()
	{
		m_readEncWeights = new ReadEncodingWeightVector();
//		m_readEncWeights.calculateMinMaxRanges();
		
		m_writeEncWeights = new WriteEncodingWeightVector();
		m_writeEncWeights.calculateMinMaxRanges();
		
//		m_neuronEncInfo = new NeuronReadEncodingInfo();
		
	}
	
	public GeneticAnnIncoder(Scanner inF)
	{
		m_readEncWeights = new ReadEncodingWeightVector();
//		m_readEncWeights.calculateMinMaxRanges();
		
		m_writeEncWeights = new WriteEncodingWeightVector();
		
		WriteEncodingWeightVector wVect = this.m_writeEncWeights;

		inF.nextLine();	// Getting rid of non-data line.
		wVect.m_newNodeWeight = inF.nextInt();
		wVect.m_moveWeight = inF.nextInt();
		wVect.m_connectWeight = inF.nextInt();
		inF.nextLine();	// Clear line.

		inF.nextLine();	// Getting rid of non-data line.
		wVect.m_connectOutputWeight = inF.nextInt();
		wVect.m_connectInputWeight = inF.nextInt();
		wVect.m_connectAllOutputWeight = inF.nextInt();
		wVect.m_connectAllInputWeight = inF.nextInt();
		inF.nextLine();	// Clear line.
		
		inF.nextLine();	// Getting rid of non-data line.
		wVect.m_mutateThreshFactorWeight = inF.nextInt();
		inF.nextLine();	// Clear line.
		
		inF.nextLine();	// Getting rid of non-data line.
		wVect.m_splitWeight = inF.nextInt();
		wVect.m_powerSplitWeight = inF.nextInt();
		inF.nextLine();	// Clear line.

		inF.nextLine();	// Getting rid of non-data line.
		wVect.m_sleepWeight = inF.nextInt();
		wVect.m_awakenWeight = inF.nextInt();
		wVect.m_dieWeight = inF.nextInt();
		inF.nextLine();	// Clear line.

		m_writeEncWeights.calculateMinMaxRanges();
	}

	public ReadEncodingWeightVector getReadVector()
	{
		return m_readEncWeights;
	}

	
	public WriteEncodingWeightVector getWriteVector()
	{
		return m_writeEncWeights;
	}
	
	public String toString()
	{
		String retVal = new String();
		
		retVal = retVal + "New node: " + getWriteVector().m_newNodeWeight + "\t\t";
		retVal = retVal + "Move: " + getWriteVector().m_moveWeight + "\t\t";
		retVal = retVal + "Connect: " + getWriteVector().m_connectWeight + "\t\t";
		retVal = retVal + "Connect out: " + getWriteVector().m_connectOutputWeight + "\t\t";
		retVal = retVal + "Connect in: " + getWriteVector().m_connectInputWeight + "\t\t";
		retVal = retVal + "Connect all out: " + getWriteVector().m_connectAllOutputWeight + "\t\t";
		retVal = retVal + "Connect all in: " + getWriteVector().m_connectAllInputWeight + "\t\t";
		retVal = retVal + "Mutate threshold: " + getWriteVector().m_mutateThreshFactorWeight + "\t\t";
		retVal = retVal + "Primitive split: " + getWriteVector().m_splitWeight + "\t\t";
		retVal = retVal + "Power split: " + getWriteVector().m_powerSplitWeight + "\t\t";
		retVal = retVal + "Sleep: " + getWriteVector().m_sleepWeight + "\t\t";
		retVal = retVal + "Awaken: " + getWriteVector().m_awakenWeight + "\t\t";
		retVal = retVal + "Die: " + getWriteVector().m_dieWeight;

		return retVal;
	}

	

}
