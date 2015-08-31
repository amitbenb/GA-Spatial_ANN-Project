package inputGenerators;

import java.io.FileWriter;

public class Generator
{

	static int numOfTests = 50;
	static double[] weightOfTests = {1.0};
	static int[] inGridSize = {5,6};
	public static int[] outGridSize = {10,5};
	
	static String mainDir = new String("Z:\\ANN_Exp\\");
	static FileWriter outF;
	
	public static String generateRandomNumberInput(int numberRepresented)
	{
		final double CHOSEN_MARKER = -50.0;
		String retVal = new String();
	
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


}
