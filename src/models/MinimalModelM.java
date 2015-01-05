package models;

public class MinimalModelM extends RDEModel {
	
	double Du;
	double Dv;
	double Dw;
	double Ds;
	
	public MinimalModelM(double Du, double Dv, double Dw, double Ds) {
		super(4, new double[] {Du, Dv, Dw, Ds});
		this.Du = Du;
		this.Dv = Dv;
		this.Dw = Dw;
		this.Ds = Ds;
	}
	
	/*
	 * Mathematical modeling and spectral simulation of ... 
	 * from Alfonso Bueno Orovio p.34 Params for EPI
	 * 
	 */
//	parameters for all three cell types
	private static final double u_m = 0.3;
	private static final double u_p = 0.13;
	private static final double t_v_plus = 1.45;
	private static final double t_s1 = 2.7342;
	private static final double k_s = 2.0994;
	private static final double u_s = 0.9087;
	
//	parameters for the midmyoucardial cells
	private static final double u_u = 1.61;
	private static final double u_q = 0.1;
	private static final double u_r = 0.005;
	private static final double t_v1_minus = 80;
	private static final double t_v2_minus = 1.45;
	private static final double t_w1_minus = 70;
	private static final double t_w2_minus = 8;
	private static final double k_w_minus = 200;
	private static final double u_w_minus = 0.016;
	private static final double t_w_plus = 280;
	private static final double t_fi = 0.078;
	private static final double t_o1 = 410;
	private static final double t_o2 = 7;
	private static final double t_so1 = 91;
	private static final double t_so2 = 0.8;
	private static final double k_so = 2.1;
	private static final double u_so = 0.6;
	private static final double t_s2 = 4;
	private static final double t_si = 3.3849;
	private static final double t_w_inf = 0.01;
	private static final double w_inf_star = 0.5;
	
	private static double I_ext = 0;

	public double[] function(double[][][] data, int x, int y) {
		double[] ret = new double[4];
		double u = data[0][x][y];
		double v = data[1][x][y];
		double w = data[2][x][y];
		double s = data[3][x][y];
		int m = (u < u_m) ? 0 : 1;
		int p = (u < u_p) ? 0 : 1;
		int q = (u < u_q) ? 0 : 1;
		int r = (u < u_r) ? 0 : 1;
		int v_inf = (u < u_q) ? 1 : 0;
		
		ret[0] = - (- v * m * (u - u_m) * (u_u - u) / t_fi + u * (1 - p) / ((1 - r) * t_o1 + r * t_o2) + p / (t_so1 + (t_so2 - t_so1) * (1 + java.lang.StrictMath.tanh(k_so * (u - u_so))) / 2) - p * w * s / t_si - I_ext);
		ret[1] = (1 - m) * (v_inf - v) / ((1 - q) * t_v1_minus + q * t_v2_minus) - m * v / t_v_plus;
//		Difference in Bueno-Orovio2007 and Bueno-Orovio2007a 
		ret[2] = (1 - p) * (((1 - r) * (1 - u / t_w_inf) + r * w_inf_star) - w) / (t_w1_minus + (t_w2_minus - t_w1_minus) * (1 + java.lang.StrictMath.tanh(k_w_minus * (u - u_w_minus))) / 2) - p * w / t_w_plus;
		ret[3] = ((1 + java.lang.StrictMath.tanh(k_s * (u - u_s))) / 2 - s) / ((1 - p) * t_s1 + p * t_s2);
		return ret;
	}
}
