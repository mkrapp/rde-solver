package models;

public class BeelerReuterModel extends RDEModel {

    double DV;

    double Dm;

    double Dh;

    double Dj;

    double Dd;

    double Df;

    double Dx1;

    double DCa;

    public BeelerReuterModel(double DV, double Dm, double Dh, double Dj,
	    double Dd, double Df, double Dx1, double DCa) {
	super(8, new double[] { DV, Dm, Dh, Dj, Dd, Df, Dx1, DCa });
	this.DV = DV;
	this.Dm = Dm;
	this.Dh = Dh;
	this.Dj = Dj;
	this.Dd = Dd;
	this.Df = Df;
	this.Dx1 = Dx1;
	this.DCa = DCa;
    }

    private static final int C_m = 1;

    private static final int E_Na = 50;

    private static final double g_NaC = 0.003;

    private static final int g_Na = 4;

    private static final double g_s = 0.09;

    private static double I_ext = 0;

    @Override
    public double[] function(double[][][] data, int x, int y) {
	double[] ret = new double[8];
	double V = data[0][x][y];
	double m = data[1][x][y];
	double h = data[2][x][y];
	double j = data[3][x][y];
	double d = data[4][x][y];
	double f = data[5][x][y];
	double x1 = data[6][x][y];
	double Cai = data[7][x][y];

	ret[0] = -1
		/ C_m
		* (I_Na(V, m, h, j) + I_s(V, d, f, Cai) + I_K1(V) + I_x1(V, x1) - I_ext);
	ret[1] = a_m(V) * (1 - m) - b_m(V) * m;
	ret[2] = a_h(V) * (1 - h) - b_h(V) * h;
	ret[3] = a_j(V) * (1 - j) - b_j(V) * j;
	ret[4] = a_d(V) * (1 - d) - b_d(V) * d;
	ret[5] = a_f(V) * (1 - f) - b_f(V) * f;
	ret[6] = a_x1(V) * (1 - x1) - b_x1(V) * x1;
	ret[7] = -1 * 10E-7 * I_s(V, d, f, Cai) + 0.07 * (1 * 10E-7 - Cai);

	return ret;
    };

    // Fast Inward Current
    private double I_Na(double V, double m, double h, double j) {
	return (g_Na * Math.pow(m, 3) * h * j + g_NaC) * (V - E_Na);
    }

    private double a_m(double V) {
	return -(V + 47) / (Math.exp(-0.1 * (V + 47)) - 1);
    }

    private double b_m(double V) {
	return 40 * Math.exp(-0.056 * (V + 72));
    }

    private double a_h(double V) {
	return 0.126 * Math.exp(-0.25 * (V + 77));
    }

    private double b_h(double V) {
	return 1.7 / (Math.exp(-0.082 * (V + 22.5)) + 1);
    }

    private double a_j(double V) {
	return (0.055 * Math.exp(-0.25 * (V + 78)))
		/ (Math.exp(-0.2 * (V + 78)) + 1);
    }

    private double b_j(double V) {
	return 0.3 / (Math.exp(-0.1 * (V + 32)) + 1);
    }

    // Slow Inward Current
    private double I_s(double V, double d, double f, double Cai) {
	return g_s * d * f * (V - E_s(Cai));
    }

    private double E_s(double Cai) {
	return -82.3 - 13.0287 * Math.log(Cai);
    }

    private double a_d(double V) {
	return (0.095 * Math.exp(-0.01 * (V - 5)))
		/ (Math.exp(-0.072 * (V - 5)) + 1);
    }

    private double b_d(double V) {
	return (0.07 * Math.exp(-0.017 * (V + 44)))
		/ (Math.exp(0.05 * (V + 44)) + 1);
    }

    private double a_f(double V) {
	return (0.012 * Math.exp(-0.008 * (V + 28)))
		/ (Math.exp(0.15 * (V + 28)) + 1);
    }

    private double b_f(double V) {
	return (0.0065 * Math.exp(-0.02 * (V + 30)))
		/ (Math.exp(-0.2 * (V + 30)) + 1);
    }

    // Time-independent Potassium Current
    private double I_K1(double V) {
	return 1.4
		* ((Math.exp(0.04 * (V + 85)) - 1) / (Math.exp(0.08 * (V + 53)) + Math
			.exp(0.04 * (V + 53)))) + 0.07 * (V + 23)
		/ (1 - Math.exp(-0.04 * (V + 23)));
    }

    // Time-dependent Outward Current
    private double I_x1(double V, double x1) {
	return 0.8 * x1 * (Math.exp(0.04 * (V + 77)) - 1)
		/ (Math.exp(0.04 * (V + 35)));
    }

    private double a_x1(double V) {
	return (0.0005 * Math.exp(0.083 * (V + 50)))
		/ (Math.exp(0.057 * (V + 50)) + 1);
    }

    private double b_x1(double V) {
	return (0.0013 * Math.exp(-0.06 * (V + 20)))
		/ (Math.exp(-0.04 * (V + 20)) + 1);
    }

    public void setStimulus(double val) {
	I_ext = val;
    }

}
