package inputGenerators;

import java.io.FileWriter;

public class CountingGenerator
{
	// TODO Not just continuous blobs at the moment. Does not differentiate
	// between continuous and discrete. deFix? Add option?	

	static int minInputNumber = 0, maxInputNumber = 9;
	static int minOutputNumber = 0, maxOutputNumber = 9;
	static int numOfNumbers = maxInputNumber -  minInputNumber + 1;
	
	public static void main(String[] args) throws Exception
	{
		FileWriter outF = Generator.outF;
		outF = new FileWriter(Generator.mainDir + "TempCounting.input");
		
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
			int NumberRepresented = (i % numOfNumbers + minInputNumber);
			outF.write("Input of size " + NumberRepresented + ":\n");
			outF.write(i + "\n\n");
			
			outF.write(Generator.generateRandomNumberInput(NumberRepresented) + "\n");
			
			outF.write("Output #" +  i + ":\n\n");

			outF.write(generateCountingOutput(NumberRepresented) + "\n");
			
			outF.write("Weight of test\n");
			outF.write(Generator.weightOfTests[i % Generator.weightOfTests.length] + "\n\n");
		}
		

		outF.write("END\n");
		outF.write(-10 + "\n");
		
		


		outF.close();
	}

	public static String generateCountingOutput(int numberRepresented)
	{
		String retVal = new String();

		for (int i = 0; i < Generator.outGridSize[0]; i++)
		{
			for (int j = 0; j < Generator.outGridSize[1]; j++)
			{
				if (numberRepresented - minOutputNumber == i)
					retVal = retVal + "  1";
				else
					retVal = retVal + " -1";
			}
			retVal = retVal + "\n";
		}
		
		return retVal;
	}
}
