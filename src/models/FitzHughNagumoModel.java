package models;

/**
 * @author mario
 *
 */
public class FitzHughNagumoModel extends RDEModel {

	double Du;
	double Dv;
	

	public FitzHughNagumoModel(double Du, double Dv) {
		//call Papa
		super(2, new double[] {Du, Dv});
		this.Du = Du;
		this.Dv = Dv;
	}
	
	private static final double a = 0.02;
	private static final double b = 0.25;
	private static final double eps  = 0.003;
	private static double I_ext = 0.0;
	
	@Override
	public double[] function(double[][][] data, int x, int y) {
		double[] ret = new double[2];
		double v = data[0][x][y];
		double w = data[1][x][y];
		ret[0] = - v * (v - 1) * (v - a) - w + I_ext;
		ret[1] = eps * (v - b * w); 
		return ret;
	}
	
}
