package ann;

import popPack.BasicBuilder;
import gaPack.ANN_Individual.ANN_Genome_Atom;

public interface ANN_Builder extends BasicBuilder
{
	public static int neuronInputsSizeLimit = 200;
	public static int neuronOutputsSizeLimit = neuronInputsSizeLimit;
	
//	public static int inputNumber = 16;
//	public static int outputNumber = 10;
	
	abstract public SpatialAnn build(ANN_Genome_Atom[] genome);

	abstract public int getDimentionality();

}
