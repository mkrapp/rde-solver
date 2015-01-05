package models;

public class KarmaModel extends RDEModel {

	double DE; 
	double Dn;
	
	// Table II gamma = 1.1
	//From Fenton Karma Chaos4_461.pdf
	private static final double tau_E = 2.5;

	private static final double tau_n = 250;

	private static final double E_star = 1.5415;

	private static final double E_n = 1;

	private static final double Re = 0.8; // 0.5 ... 1.4

	private static final int M = 6; // 4 ... 10
	
	private static double I_ext;
	
	public KarmaModel(double DE, double Dn) {
		super(2, new double[] {DE, Dn});
		this.DE = DE;
		this.Dn = Dn;
	}

	@Override
	public double[] function(double[][][] data, int x, int y) {
		double[] ret = new double[2];
		double E = data[0][x][y];
		double n = data[1][x][y];
		// eq. 1
		ret[0] = 1 / tau_E * f(E, n);
		// eq. 2
		ret[1] = 1 / tau_n * g(E, n);
		return ret;
	}

	// eq. 3
	public double f(double E, double n) {
		return -E + (E_star - D(n)) * h(E) + I_ext;
	}

	// eq. 4
	public double g(double E, double n) {
		return R(n) * Heaviside(E - E_n) - (1 - Heaviside(E - E_n)) * n;
	}

	// Eq 7
	public double R(double n) {
		return (1 - (1 - Math.exp(-Re)) * n) / (1 - Math.exp(-Re));
	}

	public double D(double n) {
		return Math.pow(n, M);
	}

	public double h(double E) {
		return (1 - Math.tanh(E - E_n)) * Math.pow(E, 2) / 2;
	}

	public int Heaviside(double x) {
		int ret = 0;
		if (x > 0) {
			ret = 1;
		}
		if (x <= 0) {
			ret = 0;
		}
		return ret;
	}
	
	public void setStimulus(double val) {
		I_ext = val;
	}
}