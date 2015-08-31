package inputGenerators;

import java.io.FileWriter;

public class SetSubitizingGenerator
{
	static int minInputNumber = 1, maxInputNumber = 13;
	static int minOutputNumber = 1, maxOutputNumber = 7;
	static int numOfNumbers = maxInputNumber -  minInputNumber + 1;
	static int[][] numberSets = {{1},{2},{3},{4},{5,6,7},{8,9,10},{11,12,13}};

	public static void main(String[] args) throws Exception
	{
		FileWriter outF = Generator.outF;
		outF = new FileWriter(Generator.mainDir + "TempSubitizing.input");
		
		outF.write("Number of input dimensions\n");
		outF.write(Generator.inGridSize.length + "\n");
		if (Generator.outGridSize.length != 2)
		{
			outF.close();
			throw new RuntimeException("inGridSize.length != 2. Cannot handle this");
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
			// Option 1: Each number gets equal number of appearances.
			int numberRepresented = (i % numOfNumbers + minInputNumber);
			
			// Option 2: Each number set gets equal number of appearances.
//			int numberRepresented = getRandomMember(numberSets[i % numberSets.length]);
			
			outF.write("Input of size " + numberRepresented + ":\n");
			outF.write(i + "\n\n");
			
			outF.write(Generator.generateRandomNumberInput(numberRepresented) + "\n");
			
			outF.write("Output #" +  i + ":\n\n");

			outF.write(generateSetSubitizingOutput(numberRepresented, getSetIndex(numberRepresented)) + "\n");
			
			outF.write("Weight of test\n");
			outF.write(Generator.weightOfTests[i % Generator.weightOfTests.length] + "\n\n");
		}

		outF.write("END\n");
		outF.write(-10 + "\n");
		
		


		outF.close();
	}

	public static String generateSetSubitizingOutput(int numberRepresented, int setIndex)
	{
		String retVal = new String();

		for (int i = 0; i < Generator.outGridSize[0]; i++)
		{
			for (int j = 0; j < Generator.outGridSize[1]; j++)
			{
				if (setIndex == i)
					retVal = retVal + "  1";
				else
					retVal = retVal + " -1";
			}
			retVal = retVal + "\n";
		}
		
		return retVal;
	}
	
	public static int getSetIndex(int numberRepresented)
	{
		int idx = -1;
		
		for (int i = 0; idx < 0  && i < numberSets.length; i++)
		{
			for (int j = 0; j < numberSets[i].length; j++)
			{
				if (numberRepresented == numberSets[i][j])
					idx = i;
			}
		} 
		
		
		return idx;
	}

	public static int getRandomMember(int[] set)
	{
		return set[(int) (Math.random() * Integer.MAX_VALUE) % set.length];
	}

}
