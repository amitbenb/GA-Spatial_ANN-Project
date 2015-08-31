package ann;

/*
 * 
 */

public class OldGeneticAnnIncoder
{
	public class ReadEncodingWeightVector
	{

		/*
		 * Read Mode Dictionary the comma means OR:
		 * 0: All
		 * 1: Up,Left
		 * 2: Up,Right
		 * 3: Down, Left
		 * 4: Down, Right
		 * 5: Up
		 * 6: Down
		 * 7: Left
		 * 8: Right
		 * 
		 * 
		 */

		// These members control relative probability of different operations being
		// encoded when number is generated at random to serve as opcode
		public int m_allWeight = 10;
		public int m_upOrLeftWeight = 2;
		public int m_upOrRightWeight = 2;
		public int m_downOrLeftWeight = 2;
		public int m_downOrRightWeight = 2;
		public int m_upWeight = 2;
		public int m_downWeight = 2;
		public int m_leftWeight = 2;
		public int m_rightWeight = 2;
		
		
		// Ranges: XXXXMin <= XXXX < XXXXMax
		public int m_allMin = 0, m_allMax=0;
		public int m_upOrLeftMin = 0, m_upOrLeftMax = 0;
		public int m_upOrRightMin = 0, m_upOrRightMax = 0;
		public int m_downOrLeftMin = 0, m_downOrLeftMax = 0;
		public int m_downOrRightMin = 0, m_downOrRightMax = 0;
		public int m_upMin = 0, m_upMax = 0;
		public int m_downMin = 0, m_downMax = 0;
		public int m_leftMin = 0, m_leftMax = 0;
		public int m_rightMin = 0, m_rightMax = 0;

		int m_sumOfAllWeights = 0;
		
		public ReadEncodingWeightVector()
		{
			updateSumOfWeights();
			calculateMinMaxRanges();
		}

		private void updateSumOfWeights()
		{
			m_sumOfAllWeights =
					m_allWeight + m_upOrLeftWeight + m_upOrRightWeight + m_downOrLeftWeight
							+ m_downOrRightWeight + m_upWeight + m_downWeight + m_rightWeight
							+ m_leftWeight;
		}

		private void calculateMinMaxRanges()
		{
			m_allMin = 0;
			m_allMax = m_allMin + m_allWeight;
			
			m_upOrLeftMin = m_allMax;
			m_upOrLeftMax = m_upOrLeftMin + m_upOrLeftWeight;
			
			m_upOrRightMin = m_upOrLeftMax;
			m_upOrRightMax = m_upOrRightMin + m_upOrRightWeight;
			
			m_downOrLeftMin = m_upOrRightMax;
			m_downOrLeftMax = m_downOrLeftMin + m_downOrLeftWeight;
			
			m_downOrRightMin = m_downOrLeftMax;
			m_downOrRightMax = m_downOrRightMin + m_downOrRightWeight;
			
			m_upMin = m_downOrRightMax;
			m_upMax = m_upMin + m_upWeight;
			
			m_downMin = m_upMax;
			m_downMax = m_downMin + m_downWeight;
			
			m_leftMin = m_downMax;
			m_leftMax = m_leftMin + m_leftWeight;
			
			m_rightMin = m_leftMax;
			m_rightMax = m_rightMin + m_rightWeight;
		}
		
		public boolean isAllEncoding(int code)
		{
			return (m_allMin <= code && code < m_allMax);
		}
		
		public boolean isUpOrLeftEncoding(int code)
		{
			return (m_upOrLeftMin <= code && code < m_upOrLeftMax);
		}
		
		public boolean isUpOrRightEncoding(int code)
		{
			return (m_upOrRightMin <= code && code < m_upOrRightMax);
		}
		
		public boolean isDownOrLeftEncoding(int code)
		{
			return (m_downOrLeftMin <= code && code < m_downOrLeftMax);
		}
		
		public boolean isDownOrRightEncoding(int code)
		{
			return (m_downOrRightMin <= code && code < m_downOrRightMax);
		}
		
		public boolean isUpEncoding(int code)
		{
			return (m_upMin <= code && code < m_upMax);
		}
		
		public boolean isDownEncoding(int code)
		{
			return (m_downMin <= code && code < m_downMax);
		}
		
		public boolean isLeftEncoding(int code)
		{
			return (m_leftMin <= code && code < m_leftMax);
		}
		
		public boolean isRightEncoding(int code)
		{
			return (m_rightMin <= code && code < m_rightMax);
		}

		public int fixReadMode(int readMode)
		{
			return (readMode<0)?((-1)*readMode % m_sumOfAllWeights):(readMode % m_sumOfAllWeights);
		}
		
		
		
		
	}
	
	public class WriteEncodingWeightVector
	{
		
		/*
		 * OpCode Dictionary:
		 * 0: New node
		 * 1: Split Up/down
		 * 2: Split down/Up
		 * 3: Split Left/Right
		 * 4: Split Right/Left
		 * 5: Move
		 * 6: Connect
		 * 7: Connect output
		 * 8: Connect input
		 * 9: Mutate threshold
		 * 10: Split (nonspecific)
		 * 11: Sleep
		 * 12: Wake up
		 * 13: Die
		 * 
		 */

		// These members control relative probability of different operations being
		// encoded when number is generated at random to serve as opcode
		public int m_newNodeWeight = 10;

		public int m_upDownSplitWeight = 0;
		public int m_downUpSplitWeight = 0;
		public int m_leftRightSplitWeight = 0;
		public int m_rightLeftSplitWeight = 0;
		
		public int m_moveWeight = 2;
		public int m_connectWeight = 4;
		public int m_connectOutputWeight = 4; 
		public int m_connectInputWeight = 4;
		public int m_mutateThreshFactorWeight = 2;
		public int m_splitWeight = 10;
		public int m_sleepWeight = 4;
		public int m_awakenWeight = 4;
		public int m_dieWeight = 2;
		
		// Ranges: XXXXMin <= XXXX < XXXXMax
		public int m_newNodeMin = 0, m_newNodeMax=0;
		public int m_upDownSplitMin = 0, m_upDownSplitMax = 0;
		public int m_downUpSplitMin = 0, m_downUpSplitMax = 0;
		public int m_leftRightSplitMin = 0, m_leftRightSplitMax = 0;
		public int m_rightLeftSplitMin = 0, m_rightLeftSplitMax = 0;
		public int m_moveMin = 0, m_moveMax = 0;
		public int m_connectMin = 0, m_connectMax = 0;
		public int m_connectOutputMin = 0, m_connectOutputMax = 0;
		public int m_connectInputMin = 0, m_connectInputMax = 0;
		public int m_mutateThreshFactMin = 0, m_mutateThreshFactMax = 0;
		
		public int m_splitMin = 0, m_splitMax = 0;
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
					m_newNodeWeight + m_upDownSplitWeight + m_downUpSplitWeight
							+ m_leftRightSplitWeight + m_rightLeftSplitWeight + m_moveWeight
							+ m_connectWeight + m_connectOutputWeight + m_connectInputWeight
							+ m_mutateThreshFactorWeight + m_splitWeight + m_sleepWeight
							+ m_awakenWeight + m_dieWeight;
	}

		private void calculateMinMaxRanges()
		{
			m_newNodeMin = 0;
			m_newNodeMax = m_newNodeMin + m_newNodeWeight;
			
			m_upDownSplitMin = m_newNodeMax;
			m_upDownSplitMax = m_upDownSplitMin + m_upDownSplitWeight;
			
			m_downUpSplitMin = m_upDownSplitMax;
			m_downUpSplitMax = m_downUpSplitMin + m_downUpSplitWeight;
			
			m_leftRightSplitMin = m_downUpSplitMax;
			m_leftRightSplitMax = m_leftRightSplitMin + m_leftRightSplitWeight;
			
			m_rightLeftSplitMin = m_leftRightSplitMax;
			m_rightLeftSplitMax = m_rightLeftSplitMin + m_rightLeftSplitWeight;
			
			m_moveMin = m_rightLeftSplitMax;
			m_moveMax = m_moveMin + m_moveWeight;
			
			m_connectMin = m_moveMax;
			m_connectMax = m_connectMin + m_connectWeight;
			
			m_connectOutputMin = m_connectMax;
			m_connectOutputMax = m_connectOutputMin + m_connectOutputWeight;
			
			m_connectInputMin = m_connectOutputMax;
			m_connectInputMax = m_connectInputMin + m_connectInputWeight;
			
			m_mutateThreshFactMin = m_connectInputMax;
			m_mutateThreshFactMax = m_mutateThreshFactMin + m_mutateThreshFactorWeight;
			
			m_splitMin = m_mutateThreshFactMax;
			m_splitMax = m_splitMin + m_splitWeight;
			
			m_sleepMin = m_splitMax;
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
		
		public boolean isUpDownSplitEncoding(int code)
		{
			return (m_upDownSplitMin <= code && code < m_upDownSplitMax);
		}
		
		public boolean isDownUpSplitEncoding(int code)
		{
			return (m_downUpSplitMin <= code && code < m_downUpSplitMax);
		}
		
		public boolean isLeftRightSplitEncoding(int code)
		{
			return (m_leftRightSplitMin <= code && code < m_leftRightSplitMax);
		}
		
		public boolean isRightLeftSplitEncoding(int code)
		{
			return (m_rightLeftSplitMin <= code && code < m_rightLeftSplitMax);
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
		
		public boolean isMutateThresholdFactorEncoding(int code)
		{
			return (m_mutateThreshFactMin <= code && code < m_mutateThreshFactMax);
		}
		
		public boolean isSplitEncoding(int code)
		{
			return (m_splitMin <= code && code < m_splitMax);
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
	
	public class NeuronReadEncodingInfo
	{
		/* Read encodings
		 * 0 - All
		 * 1 - Up
		 * 2 - down
		 * 3 - Left
		 * 4 - Right
		 */
		public boolean isReadEncodingAll(int code)
		{
			return code == 0;
		}
		
		public boolean isReadEncodingUp(int code)
		{
			return code == 1;
		}
		
		public boolean isReadEncodingDown(int code)
		{
			return code == 2;
		}
		
		public boolean isReadEncodingLeft(int code)
		{
			return code == 3;
		}
		
		public boolean isReadEncodingRight(int code)
		{
			return code == 4;
		}
		
		
	}
	
	private ReadEncodingWeightVector m_readEncWeights;
	private WriteEncodingWeightVector m_writeEncWeights;
	private NeuronReadEncodingInfo m_neuronEncInfo;
	
	public OldGeneticAnnIncoder()
	{
		m_readEncWeights = new ReadEncodingWeightVector();
		m_readEncWeights.calculateMinMaxRanges();
		
		m_writeEncWeights = new WriteEncodingWeightVector();
		m_writeEncWeights.calculateMinMaxRanges();
		
		m_neuronEncInfo = new NeuronReadEncodingInfo();
		
	}
	
	public ReadEncodingWeightVector getReadVector()
	{
		return m_readEncWeights;
	}

	
	public WriteEncodingWeightVector getWriteVector()
	{
		return m_writeEncWeights;
	}

	public NeuronReadEncodingInfo getNeuronInfo()
	{
		return m_neuronEncInfo;
	}

	


}

