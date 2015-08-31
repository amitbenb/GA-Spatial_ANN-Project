//TODO This does not work. Do not use.
package inputGenerators;

import java.io.FileWriter;

public class OrdinalityGeneratorBroken
{
	// TODO Not just continuous blobs at the moment. Does not differentiate
	// between continuous and discrete. deFix? Add option?	

//	static int minNumber = 2, maxNumber = 7, firstBigNumber = 5;
//	static int numOfNumbers = maxNumber -  minNumber + 1;
	
	public static void main(String[] args) throws Exception
	{
		FileWriter outF = Generator.outF;
		outF = new FileWriter(Generator.mainDir + "TempSize.input");
		
		outF.write("Number of input dimensions\n");
		outF.write(Generator.inGridSize.length + "\n");
		if (Generator.inGridSize.length != 2)
		{
			outF.close();
			throw new RuntimeException("inGridSize.length != 2. Cannot handle this");
		}

		if (Generator.inGridSize[0] > Generator.inGridSize[1])
		{
			outF.close();
			throw new RuntimeException("inGridSize has more rows than columns. Ordinal input impossible.");
		}

		
		outF.write("Number of output dimensions\n");
		outF.write(Generator.outGridSize.length + "\n");
		if (Generator.outGridSize.length != 2)
		{
			outF.close();
			throw new RuntimeException("outGridSize.length != 2. Cannot handle this");
		}

		outF.write("Input grid size\n");
		for (int i = 0; i < Generator.inGridSize.length; i++)
			outF.write(Generator.inGridSize[i] + " ");
		outF.write("\n");

		outF.write("Output grid size\n");
		for (int i = 0; i < Generator.inGridSize.length; i++)
			outF.write(Generator.outGridSize[i] + " ");
		outF.write("\n");
		
		outF.write("Number of inputs\n");
		outF.write(Generator.numOfTests + "\n");
		
		outF.write("\n");
		
		
		for (int i = 0; i < Generator.numOfTests; i++)
		{
			boolean ordinalFlag = (i%2==0)?(false):(true);
			outF.write("Input number " + i + ":\n");
			outF.write(i + "\n\n");
			
//			outF.write(generateRandomOrdinalityInput(ordinalFlag) + "\n");
			
			outF.write("Output #" +  i + ":\n\n");

			outF.write(generateOrdinalityOutput(ordinalFlag) + "\n");
			
			outF.write("Weight of test\n");
			outF.write(Generator.weightOfTests[i % Generator.weightOfTests.length] + "\n\n");
		}
		

		outF.write("END\n");
		outF.write(-10 + "\n");
		
		


		outF.close();
	}

	public static String generateOrdinalityOutput(boolean ordinalFlag)
	{
		String retVal = new String();

		for (int i = 0; i < Generator.outGridSize[0]; i++)
		{
			for (int j = 0; j < Generator.outGridSize[1]; j++)
			{
				if (!ordinalFlag)
					retVal = retVal + " -1";
				else
					retVal = retVal + "  1";
			}
			retVal = retVal + "\n";
		}
		
		return retVal;
	}

/*
	//TODO
	private static String generateRandomOrdinalityInput(boolean ordinalFlag)
	{
		if(ordinalFlag)
			return generateRandomOrdinalInput();
		else
			return generateRandomNonOrdinalInput();
		
	}

	//TODO
	private static String generateRandomNonOrdinalInput()
	{
		// TODO Auto-generated method stub
		final double CHOSEN_MARKER = -50.0;
		String retVal = new String();
	
		double[] randValuesRows = new double[Generator.inGridSize[0]];

		for (int i = 0; i < randValuesRows.length; i++)
		{
			randValuesRows[i] = Math.random();
		}

		double[][] randValues = new double[Generator.inGridSize[0]][];
		for (int i = 0; i < randValues.length; i++)
		{
			randValues[i] = new double[Generator.inGridSize[1]];
			
			for (int j = 0; j < randValues[i].length; j++)
			{
				randValues[i][j] = Math.random();
			}
		}
		
		for (int i = 0; i < numberRepresented; i++)
		{
			int maxRow = 0, maxCol = 0;
			for (int j = 0; j < randValues.length; j++)
			{
				for (int k = 0; k < randValues[j].length; k++)
				{
					if (randValues[j][k] > randValues[maxRow][maxCol])
					{
						maxRow = j;
						maxCol = k;
					}
				}
				
			}
	
			// Mark a chosen square.
			randValues[maxRow][maxCol] = CHOSEN_MARKER;
		}
		
		for (int i = 0; i < Generator.inGridSize[0]; i++)
		{
			for (int j = 0; j < Generator.inGridSize[1]; j++)
			{
				if (randValues[i][j] == CHOSEN_MARKER)
					retVal = retVal + "  1";
				else
					retVal = retVal + " -1";
			}
			retVal = retVal + "\n";
		}
	
		return retVal;
	}

	// TODO Auto-generated method stub
	private static String generateRandomOrdinalInput()
	{
		final double CHOSEN_MARKER = -50.0;
		String retVal = new String();
	
		double[] randValuesRows = new double[Generator.inGridSize[1]];

		for (int i = 0; i < randValuesRows.length; i++)
		{
			randValuesRows[i] = Math.random();
		}
		
		boolean[] chosenLengths[Generator.inGridSize[1]]

		double[][] randValues = new double[Generator.inGridSize[0]][];
		for (int i = 0; i < randValues.length; i++)
		{
			randValues[i] = new double[Generator.inGridSize[1]];
			
			for (int j = 0; j < randValues[i].length; j++)
			{
				randValues[i][j] = Math.random();
			}
		}
		
		for (int i = 0; i < numberRepresented; i++)
		{
			int maxRow = 0, maxCol = 0;
			for (int j = 0; j < randValues.length; j++)
			{
				for (int k = 0; k < randValues[j].length; k++)
				{
					if (randValues[j][k] > randValues[maxRow][maxCol])
					{
						maxRow = j;
						maxCol = k;
					}
				}
				
			}
	
			// Mark a chosen square.
			randValues[maxRow][maxCol] = CHOSEN_MARKER;
		}
		
		for (int i = 0; i < Generator.inGridSize[0]; i++)
		{
			for (int j = 0; j < Generator.inGridSize[1]; j++)
			{
				if (randValues[i][j] == CHOSEN_MARKER)
					retVal = retVal + "  1";
				else
					retVal = retVal + " -1";
			}
			retVal = retVal + "\n";
		}
	
		return retVal;
	}
/**/
//	public static String generateSizeOutput(int numberRepresented)
//	{
//		String retVal = new String();
//
//		for (int i = 0; i < Generator.outGridSize[0]; i++)
//		{
//			for (int j = 0; j < Generator.outGridSize[1]; j++)
//			{
//				if (numberRepresented < firstBigNumber)
//					retVal = retVal + " -1";
//				else
//					retVal = retVal + "  1";
//			}
//			retVal = retVal + "\n";
//		}
//		
//		return retVal;
//	}

}
