package models;

/**
 * @author mario
 *
 */
public class Heat extends RDEModel {

	double Dc;

	public Heat(double Dc) {
		//call Papa
		super(1, new double[] {Dc});
		this.Dc = Dc;
	}
	
	@Override
	public double[] function(double[][][] data, int x, int y) {
		double[] ret = new double[1];
		ret[0] = 0;
		return ret;
	}
	
}
