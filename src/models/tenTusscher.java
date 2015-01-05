package models;

public class tenTusscher extends RDEModel {

	double DV; // D = 0.00154

	double R = 8.3143;

	double T = 310;

	double F = 96.4867;

	int C_m = 2;

	double S = 0.2;

	int rho = 162;

	int V_C = 16404;

	int V_SR = 1094;

	double K_O = 5.4;

	int Na_O = 140;

	int Ca_O = 2;

	double G_Na = 14.838;

	double G_K1 = 5.405;

	double G_to = 0.294;

	double G_Kr = 0.096;

	double G_Ks = 0.245;

	double p_KNa = 0.03;

	double G_CaL = 1.75E-4;

	int k_NaCa = 1000;

	double gam = 0.35;

	double K_mCa = 1.38;

	double K_mNai = 87.5;

	double k_sat = 0.1;

	double alpha = 2.5;

	double P_NaK = 1.362;

	int K_mK = 1;

	int K_mNa = 40;

	double G_pK = 0.0146;

	double G_pCa = 0.025;

	double K_pCa = 0.0005;

	double G_bNa = 0.00029;

	double G_bCa = 0.000592;

	double V_maxup = 0.000425;

	double K_up = 0.00025;

	double a_rel = 16.464;

	double b_rel = 0.25;

	double c_rel = 8.232;

	double V_leak = 0.00008;

	double Buf_c = 0.15;

	double K_bufc = 0.001;

	int Buf_sr = 10;

	double K_bufsr = 0.3;

	double I_ext, I_ax = 0.0;

	// double V;

	// double m, h, j, r, s, x_r1, x_r2, d, f, f_Ca, x_s;

	// double Na_i, K_i, Ca_i;

	public tenTusscher(double DV) {
		// call Papa
		super(1, new double[] { DV });
		this.DV = DV;
	}

	public double[] function(double[][][] data, int x, int y) {
		double[] ret = new double[17];

		double V = data[0][x][y];
		double Na_i = data[1][x][y];
		double Ca_i = data[2][x][y];
		double Ca_sr = data[3][x][y];
		double K_i = data[4][x][y];

		// Fast Na+ Current
		double a_m = 1 / Math.pow((1 + Math.exp((-56.86 - V) / 9.03)), 2);
		double b_m = 0.1 / (1 + Math.exp((V + 35) / 5)) + 0.1
				/ (1 + Math.exp((V - 50) / 200));
//		double m_inf = 1 / Math.pow((1 + Math.exp((-56.86 - V) / 9.03)), 2);
		double m = data[5][x][y];
		double a_h = 0;
		double b_h = 0;
		if (V >= -40) {
			a_h = 0;
			b_h = 0.77 / (0.13 * (1 + Math.exp(-(V + 10.66) / 11.1)));
		} else {
			a_h = 0.057 * Math.exp(-(V + 80) / 6.8);
			b_h = 2.7 * Math.exp(0.079 * V) + 3.1 * 10E5 * Math.exp(0.3485 * V);
		}
		double h = data[6][x][y];
		double a_j = 0;
		double b_j = 0;
		if (V >= -40) {
			a_j = 0;
			b_j = 0.6 * Math.exp(0.057 * V)
					/ (1 + Math.exp(-0.1378 * (V + 40.14)));
		} else {
			a_j = (-2.5428 * 10E4 * Math.exp(0.2444 * V) - 6.948 * 10E-6 * Math
					.exp(-0.04391 * V))
					* (V + 37.78) / (1 + Math.exp(0.311 * (V + 79.23)));
			b_j = 0.02424 * Math.exp(-0.01052 * V)
					/ (1 + Math.exp(-0.1378 * (V + 40.14)));
		}
		double j = data[7][x][y];

		// Transient Outward Current
		double r = data[8][x][y];
		double s = data[9][x][y];

		// Rapid Delayed Rectifier Current
		double x_r1 = data[10][x][y];
		double x_r2 = data[11][x][y];
		
		// Slow Delayed Rectifier Current
		double x_s = data[12][x][y];
		
		// L-type Ca++ Current 
		double d = data[13][x][y];
		double f = data[14][x][y];
		double f_Ca = data[15][x][y];
		
		// Calcium Dynamics
		double g = data[16][x][y];
		// Transmembrane Voltage Dynamics
		ret[0] = -(I_ion(V, m, h, j, r, s, x_r1, x_r2, x_s, d, f, f_Ca, Ca_i,
				Na_i, K_i) + I_ext)
				/ C_m; // dV/dt

		// Intracellular Ion Dynamics
		ret[1] = -(I_Na(V, m, h, j, Na_i) + I_bNa(V, Na_i) + 3 * I_NaK(V, Na_i) + 3 * I_NaCa(
				V, Na_i, Ca_i))
				/ (V_C * F); // dNa_i/dt
		ret[2] = -(I_CaL(V, d, f, f_Ca, Ca_i) + I_bCa(V, Ca_i) + I_pCa(Ca_i) - 2 * I_NaCa(
				V, Na_i, Ca_i))
				/ (2 * V_C * F)
				+ I_leak(Ca_sr, E_Ca(Ca_i))
				- I_up(Ca_i)
				+ I_rel(d, g, Ca_sr); // dCa_i/dt
		ret[3] = V_C
				/ V_SR
				* (-I_leak(Ca_sr, E_Ca(Ca_i)) + I_up(Ca_i) - I_rel(d, g, Ca_sr)); // dCa_sr/dt
		ret[4] = -(I_K1(V, K_i) + I_to(V, r, s, K_i) + I_Kr(V, x_r1, x_r2, K_i)
				+ I_pK(V, K_i) - 2 * I_NaK(V, Na_i) + I_pK(V, K_i) + I_ext - I_ax)
				/ (V_C * F); // dK_i/dt

		// TODO Equations for the Gating Variables
		ret[5] = a_m * (1 - m) - b_m * m; // m
		ret[6] = a_h * (1 - h) - b_h * h; // h
		ret[7] = a_j * (1 - j) - b_j * j; // j

		return ret;
	}

	public double I_ion(double V, double m, double h, double j, double r,
			double s, double x_r1, double x_r2, double x_s, double d, double f,
			double f_Ca, double Ca_i, double Na_i, double K_i) {
		return I_Na(V, m, h, j, Na_i) + I_K1(V, K_i) + I_to(V, r, s, K_i)
				+ I_Kr(V, x_r1, x_r2, K_i) + I_Ks(V, x_s, K_i, Na_i)
				+ I_CaL(V, d, f, f_Ca, Ca_i) + I_NaCa(V, Na_i, Ca_i)
				+ I_NaK(V, Na_i) + I_pCa(Ca_i) + I_pK(V, K_i) + I_bCa(V, Ca_i)
				+ I_bNa(V, Na_i);
	}

	public double I_Na(double V, double m, double h, double j, double Na_i) {
		return G_Na * Math.pow(m, 3) * h * j * (V - E_Na(Na_i));
	}

	public double I_K1(double V, double K_i) {
		return G_K1 * Math.sqrt(K_O / 5.4) * x_K1_infty(V, K_i)
				* (V - E_K(K_i));
	}

	public double I_to(double V, double r, double s, double K_i) {
		return G_to * r * s * (V - E_K(K_i));
	}

	public double I_Kr(double V, double x_r1, double x_r2, double K_i) {
		return G_Kr * Math.sqrt(K_O / 5.4) * x_r1 * x_r2 * (V - E_K(K_i));
	}

	public double I_CaL(double V, double d, double f, double f_Ca, double Ca_i) {
		return G_CaL * d * f * f_Ca * 4 * V * Math.pow(F, 2) / (R * T)
				* (Ca_i * Math.exp(2 * V * F / (R * T)) - 0.341 * Ca_O)
				/ (Math.exp(2 * V * F / (R * T)) - 1);
	}

	public double I_NaCa(double V, double Na_i, double Ca_i) {
		return k_NaCa
				* (Math.exp(gam * V * F / (R * T)) * Math.pow(Na_i, 3) * Ca_O - Math
						.exp((gam - 1) * V * F / (R * T))
						* Math.pow(Na_O, 3) * Ca_i * alpha)
				/ ((Math.pow(K_mNai, 3) + Math.pow(Na_O, 3)) * (K_mCa + Ca_O) * (1 + k_sat
						* Math.exp((gam - 1) * V * F / (R * T))));
	}

	public double I_NaK(double V, double Na_i) {
		return P_NaK
				* (K_O * Na_i)
				/ ((K_O + K_mK) * (Na_i + K_mNa) * (1 + 0.1245 * Math.exp(-0.1
						* V / (R * T)) + 0.0353 * Math.exp(-V * F / (R * T))));
	}

	public double I_pCa(double Ca_i) {
		return G_pCa * Ca_i / (K_pCa + Ca_i);
	}

	public double I_pK(double V, double K_i) {
		return G_pK * (V - E_K(K_i)) / (1 + Math.exp((25 - V) / 5.98));
	}

	public double I_bCa(double V, double Ca_i) {
		return G_bCa * (V - E_Ca(Ca_i));
	}

	public double I_bNa(double V, double Na_i) {
		return G_bNa * (V - E_Na(Na_i));
	}

	public double I_Ks(double V, double x_s, double K_i, double Na_i) {
		return G_Ks * Math.pow(x_s, 2) * (V - E_Ks(K_i, Na_i));
	}

	public double I_leak(double Ca_sr, double Ca_i) {
		return V_leak * (Ca_sr - Ca_i);
	}

	public double I_rel(double d, double g, double Ca_sr) {
		return (a_rel * (Ca_sr * Ca_sr) / (b_rel * b_rel + Ca_sr * Ca_sr) + c_rel)
				* d * g;
	}

	public double I_up(double Ca_i) {
		return V_maxup / (1 + Math.pow(K_up, 2) / Math.pow(Ca_i, 2));
	}

	public double E_Na(double Na_i) {
		return R * T / F * Math.log(Na_O / Na_i);
	}

	public double E_K(double K_i) {
		return R * T / F * Math.log(K_O / K_i);
	}

	public double E_Ca(double Ca_i) {
		return R * T / (2 * F) * Math.log(Ca_O / Ca_i);
	}

	public double E_Ks(double K_i, double Na_i) {
		return R * T / F
				* Math.log((K_O + p_KNa * Na_O) / (K_i + p_KNa * Na_i));
	}

	public double alpha_K1(double V, double K_i) {
		return 0.1 / (1 + Math.exp(0.06 * (V - E_K(K_i) - 200)));
	}

	public double beta_K1(double V, double K_i) {
		return (3 * Math.exp(0.0002 * (V - E_K(K_i) + 100)) + Math.exp(0.1 * (V
				- E_K(K_i) - 10)))
				/ (1 + Math.exp(-0.5 * (V - E_K(K_i))));
	}

	public double x_K1_infty(double V, double K_i) {
		return alpha_K1(V, K_i) / (alpha_K1(V, K_i) + beta_K1(V, K_i));
	}
	
	public double gateVar(double gate_infty, double gate_0, double tau) {
		return gate_infty - (gate_infty - gate_0) * Math.exp(-0.01/ tau);
	}
	
	public void setStimulus(double val) {
		I_ext = val;
	}
}

