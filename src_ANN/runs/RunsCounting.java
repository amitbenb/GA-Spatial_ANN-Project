package runs;

import java.io.FileWriter;

import popPack.Base_Runner;

public class RunsCounting
{
	public static void main(String[] args)
	{
		try
		{
			mainWork(args);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException();
		}
		
//		System.out.println(1/3.0);
		
//		System.out.println((-35395) % 20);
	}

	private static void mainWork(String[] args) throws Exception
	{
		FileWriter fitOut = Runner.fitOut;
		if(args.length > 0)
			Runner.mainDir = new String(args[0]);
		else
			Runner.mainDir = new String("Z:\\ANN_Exp\\Counting\\");


		if(args.length > 1)
			Runner.ParameterFilePath = new String(Runner.mainDir + args[1]);
		else
			Runner.ParameterFilePath = new String(Runner.mainDir + "parameters.txt");

		Runner.collectParameters(Runner.ParameterFilePath);

		//		if(1<2)
		//			return;

		for (int i = 0; i < Runner.NUMBER_OF_EXPERIMENTS; i++)
		{
			Base_Runner[] r = Base_Runner.runningStages;

			for (int j = 0; j < r.length; j++)
			{
				((Runner)r[j]).runEvoCycle(i,j);
			}
			
			// For next experiment clear memory.
			Runner.savePop = null;
		}
	}
}
