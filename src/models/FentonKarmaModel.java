package models;

public class FentonKarmaModel extends RDEModel {

	double Du;

	double Dv;

	double Dw;

	// Set 1 Fenton2002
//	private static final double t_v_plus = 3.33;
//	private static final double t_v1_minus = 19.6;
//	private static final double t_v2_minus = 1000;
//	private static final double t_w_plus = 667;
//	private static final double t_w_minus = 11;
////	private static final double t_d = 0.41;
//	private static final double t_d = 0.25;
//	private static final double t_0 = 8.3;
//	private static final double t_r = 50;
//	private static final double t_si = 45;
////	private static final double k1 = 1;
//	private static final double k1 = 10;
//	private static final double V_c_si = 0.85;
//	private static final double V_c = 0.13;
//	private static final double V_v = 0.055;
//	// Zusätzliche Parameter für Cherry2004 - Ausdruck der Gleichungen
//	private static final double k2 = 0;
//	private static final double V_r = V_c;
//	private static final double V_fi = V_c;

	// Set 6 Fenton2002
	//	private static final double t_v_plus = 3.33;
	//	private static final double t_v1_minus = 9;
	//	private static final double t_v2_minus = 8;
	//	private static final double t_w_plus = 250;
	//	private static final double t_w_minus = 60;
	//	private static final double t_d = 0.395;
	//	private static final double t_0 = 9;
	//	private static final double t_r = 33.33;
	//	private static final double t_si = 29;
	//	private static final double k1 = 15;
	//	private static final double V_c_si = 0.5;
	//	private static final double V_c = 0.13;
	//	private static final double V_v = 0.04;

	// Set BR Fenton1998
	//     private static final double t_v_plus = 3.33;
	//     private static final double t_v1_minus = 1250;
	//     private static final double t_v2_minus = 19.6;
	//     private static final double t_w_plus = 870;
	//     private static final double t_w_minus = 41;
	//     private static final double t_d = 0.25;
	//     private static final double t_0 = 12.5;
	//     private static final double t_r = 33;
	//     private static final double t_si = 30;
	//     private static final double k1 = 10;
	//     private static final double V_c_si = 0.85;
	//     private static final double V_c = 0.13;
	//     private static final double V_v = 0.04;

	// Model 1 Cherry2004
	//	private static final double t_v_plus = 10;
	//	private static final double t_v1_minus = 350;
	//	private static final double t_v2_minus = 80;
	//	private static final double t_w_plus = 562;
	//	private static final double t_w_minus = 48.5;
	//	private static final double t_d = 0.15;
	//	private static final double t_0 = 1.5;
	//	private static final double t_r = 12.5;
	//	private static final double t_si = 10;
	//	private static final double k1 = 15;
	//	private static final double k2 = 0;
	//	private static final double V_c_si = 0.2;
	//	private static final double V_c = 0.25;
	//	private static final double V_r = 0.16;
	//	private static final double V_v = 0.001;
	//	private static final double V_fi = 0.15;

	//	Model 2 Cherry2004
		private static final double t_v_plus = 10;
		private static final double t_v1_minus = 100;
		private static final double t_v2_minus = 20;
		private static final double t_w_plus = 800;
		private static final double t_w_minus = 45;
		private static final double t_d = 0.15;
		private static final double t_0 = 1.5;
		private static final double t_r = 31;
		private static final double t_si = 26.5;
		private static final double k1 = 10;
		private static final double k2 = 1;
		private static final double V_c_si = 0.7;
		private static final double V_c = 0.25;
		private static final double V_r = 0.6;
		private static final double V_v = 0.05;
		private static final double V_fi = 0.11;

	public FentonKarmaModel(double Du, double Dv, double Dw) {
		super(3, new double[] { Du, Dv, Dw });
		this.Du = Du;
		this.Dv = Dv;
		this.Dw = Dw;
	}

	public double[] function(double[][][] data, int x, int y) {
		double[] ret = new double[3];
		double V = data[0][x][y];
		double v = data[1][x][y];
		double w = data[2][x][y];

		int p = (V >= V_c) ? 1 : 0;
		int q = (V >= V_v) ? 1 : 0;
		int r = (V >= V_r) ? 1 : 0;

		ret[0] = - (  
				// I_fi
				- v * p * (V - V_fi) * (1 - V) / t_d
				// I_so
				+ V * (1 - r) * (1 - v * k2) / t_0 + r / t_r
				 // I_si
				- w * (1 + Math.tanh(k1 * (V - V_c_si))) / (2 * t_si)
				);
		ret[1] = (1 - p) * (1 - v) / ((1 - q) * t_v1_minus + q * t_v2_minus)
				- p * v / t_v_plus;
		ret[2] = (1 - p) * (1 - w) / t_w_minus - p * w / t_w_plus;
		return ret;
	}

}
