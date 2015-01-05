package models;

public class Oregonator extends RDEModel {

	double Du;
	double Dv;

	public Oregonator(double Du, double Dv) {
		super(2, new double[] {Du, Dv});
		this.Du = Du;
		this.Dv = Dv;
	}

	private static final double eps = 1.0 / 0.08;
	private static final double phi = 0.0071;
	private static final double q = 0.005;
	private static final double f = 1.4;
	
	@Override
	public double[] function(double[][][] data, int x, int y) {
		double[] ret = new double[2];
		double u = data[0][x][y];
		double v = data[1][x][y];
		ret[0] = eps * (u - (u * u) - (f * v + phi) * (u - q) / (u + q));
		ret[1] = u - v;
		return ret;
	}

}
