package inputGenerators.multiVoteGenerators;


import inputGenerators.Generator;
import inputGenerators.SizeGenerator;

public class SizeGeneratorMulti extends SizeGenerator
{
	// TODO Not just continuous blobs at the moment. Does not differentiate
	// between continuous and discrete. deFix? Add option?	

	
	public static void main(String[] args) throws Exception
	{
		SizeGenerator.minNumber = 1; SizeGenerator.maxNumber = 10; SizeGenerator.firstBigNumber = 6;
		SizeGenerator.numOfNumbers = SizeGenerator.maxNumber -  SizeGenerator.minNumber + 1;
		
		SizeGenerator gen = new SizeGeneratorMulti();
		
		gen.mainWork();
	}

	public String generateSizeOutput(int numberRepresented)
	{
		// TODO Auto-generated method stub
		String retVal = new String();

		for (int i = 0; i < Generator.outGridSize[0]; i++)
		{
			for (int j = 0; j < Generator.outGridSize[1]; j++)
			{
				if (numberRepresented < firstBigNumber)
					if (i < Generator.outGridSize[0]/2)
						retVal = retVal + "  1";
					else
						retVal = retVal + " -1";
				else
					if (i >= Generator.outGridSize[0]/2)
						retVal = retVal + "  1";
					else
						retVal = retVal + " -1";

			}
			retVal = retVal + "\n";
		}

		return retVal;
	}

}
