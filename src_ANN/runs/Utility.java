package runs;

public class Utility
{

	public static final int LARGE_NUMBER = 100000;


	public static int generateRandomNaturalInteger(int numRange)
	{
		return (int) ((Math.random() * numRange));
	}

	public static int generateRandomInteger(int numRange)
	{
		return (int) ((Math.random() * numRange) - (numRange / 2));
	}

	public static boolean isSameIntArray(int[] location1, int[] location2)
	{
		boolean retValue = true;
		
		if (location1 == null || location2 == null)
			retValue = false;

		if (retValue && location1.length != location2.length)
			retValue = false;

		for (int i = 0; i < location1.length && retValue; i++)
		{
			if (location1[i] != location2[i])
				retValue = false;
		}
		
		return retValue;
	}

}
