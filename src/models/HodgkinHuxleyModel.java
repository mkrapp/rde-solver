package models;

public class HodgkinHuxleyModel extends RDEModel {

    double DV;

    double Dm;

    double Dh;

    double Dn;

    public HodgkinHuxleyModel(double DV, double Dm, double Dh, double Dn) {
	super(4, new double[] { DV, Dm, Dh, Dn });
	this.DV = DV;
	this.Dm = Dm;
	this.Dh = Dh;
	this.Dn = Dn;
    }

    private static final int C_m = 1;

    private static final int g_Na = 120;

    private static final int E_Na = 115;

    private static final int g_K = 36;

    private static final int E_K = -12;

    private static final double E_L = 10.6;

    private static final double g_L = 0.3;

    private static double I_ext = 0.0;

    public double[] function(double[][][] data, int x, int y) {
	double[] ret = new double[8];
	double V = data[0][x][y];
	double m = data[1][x][y];
	double h = data[2][x][y];
	double n = data[3][x][y];

	ret[0] = -1 / C_m * (I_Na(V, m, h) + I_K(V, n) + I_L(V) - I_ext);
	ret[1] = a_m(V) * (1 - m) - b_m(V) * m;
	ret[2] = a_h(V) * (1 - h) - b_h(V) * h;
	ret[3] = a_n(V) * (1 - n) - b_n(V) * n;

	return ret;
    }

    // Sodium Current
    private double I_Na(double V, double m, double h) {
	return g_Na * Math.pow(m, 3) * h * (V - E_Na);
    }

    private double a_m(double V) {
	return 0.1 * (25 - V) / (Math.exp(0.1 * (25 - V)) - 1);
    }

    private double b_m(double V) {
	return 4 * Math.exp(-V / 18);
    }

    private double a_h(double V) {
	return 0.07 * Math.exp(-V / 20);
    }

    private double b_h(double V) {
	return 1 / (Math.exp(0.1 * (30 - V)) + 1);
    }

    // Potassium Current
    private double I_K(double V, double n) {
	return g_K * Math.pow(n, 4) * (V - E_K);
    }

    private double a_n(double V) {
	return 0.01 * (10 - V) / (Math.exp(0.1 * (10 - V)) - 1);
    }

    private double b_n(double V) {
	return 0.125 * Math.exp(-V / 80);
    }

    // Leakage Current
    private double I_L(double V) {
	return g_L * (V - E_L);
    }

    public void setStimulus(double val) {
	I_ext = val;
    }

}
