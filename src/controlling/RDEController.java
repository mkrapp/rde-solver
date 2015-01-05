package controlling;

import static utils.MyLogger.getLogger;
import static utils.Parser.getDouble;
import static utils.PropertiesManager.loadProperties;

import java.awt.geom.Point2D;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import models.BeelerReuterModel;
import models.FentonKarmaModel;
import models.FitzHughNagumoModel;
import models.Heat;
import models.HodgkinHuxleyModel;
import models.KarmaModel;
import models.MinimalModelEndo;
import models.MinimalModelEpi;
import models.MinimalModelEpiTWS;
import models.MinimalModelM;
import models.Oregonator;
import models.RDEModel;
import rdeapprox.InitCondition;
import rdeapprox.RDESolver;

/**
 * 
 * A <code>{@link RDESolver}</code> object can solve a system of PDEs (as a
 * <code>{@link RDEModel}</code> object) numerically for one time step. The
 * <code>{@link RDEController}</code> class contains therefore helpful methods
 * to get use this object.
 * 
 * @author Mario Krapp
 * 
 */
public class RDEController {

	/**
	 * A <code>Logger</code> object for this class.
	 */
	private static Logger logger = getLogger(RDEController.class
			.getSimpleName(), Level.ALL);

	/**
	 * The <code>{@link RDESolver}</code> object which will be controlled.
	 */
	private RDESolver rde;

	/**
	 * The <code>{@link InitCondition}</code> object which defines the initial
	 * conditions.
	 */
	private InitCondition ic;

	private Properties p;

	/**
	 * The simulation time elapsed since the start of the numerical
	 * calculations.
	 */
	private double timeElapsed;

	/**
	 * A <code>boolean</code> value which defines pausing action.
	 */
	private boolean pause;

	private double timeStep;

	/**
	 * Creates a <code>{@link RDEController}</code> object which loads the
	 * parameters of a configuration file to
	 * <code>{@link RDEController#prop}</code>.
	 * 
	 * @param configFile
	 *                Name of configuration file.
	 * @param RDEConfigFile
	 *                Name of file which sets numerical parameters for RDESolver
	 * 
	 */
	public RDEController(String configFile, String RDEConfigFile) {
		// Load the property file
		p = loadProperties(configFile);
		// Read out all properties
		if (p.getProperty("model").equalsIgnoreCase("br")) {
			rde = new RDESolver(
					new BeelerReuterModel(1.0, 0, 0, 0, 0, 0, 0, 0),
					RDEConfigFile);
			ic = new InitCondition(rde);
			ic.steadyState(0, -84.57375612225653);
			ic.steadyState(1, 0.010981968723265758);
			ic.steadyState(2, 0.9877211754875601);
			ic.steadyState(3, 0.9748381389815388);
			ic.steadyState(4, 0.0029707246632091067);
			ic.steadyState(5, 0.9999813338933937);
			ic.steadyState(6, 0.005628650570534315);
			ic.steadyState(7, 1.7820072156200738E-7);
		} else if (p.getProperty("model").equalsIgnoreCase("fk")) {
			rde = new RDESolver(new FentonKarmaModel(0.1, 0, 0), RDEConfigFile);
			ic = new InitCondition(rde);
			ic.steadyState(0, 0);
			ic.steadyState(1, 1);
			ic.steadyState(2, 1);
		} else if (p.getProperty("model").equalsIgnoreCase("fhn")) {
			rde = new RDESolver(new FitzHughNagumoModel(1, 0), RDEConfigFile);
			ic = new InitCondition(rde);
			ic.steadyState(0, 0);
			ic.steadyState(1, 0);
		} else if (p.getProperty("model").equalsIgnoreCase("hh")) {
			rde = new RDESolver(new HodgkinHuxleyModel(1, 0, 0, 0),
					RDEConfigFile);
			ic = new InitCondition(rde);
			ic.steadyState(0, 2.775662655567501E-4);
			ic.steadyState(1, 0.05293421762086476);
			ic.steadyState(2, 0.5961110463468148);
			ic.steadyState(3, 0.3176811675797801);
		} else if (p.getProperty("model").equalsIgnoreCase("ka")) {
			rde = new RDESolver(new KarmaModel(1.1, 0), RDEConfigFile);
			ic = new InitCondition(rde);
			ic.steadyState(0, 0);
			ic.steadyState(1, 0);
		} else if (p.getProperty("model").equalsIgnoreCase("mm_epi")) {
			rde = new RDESolver(new MinimalModelEpi(0.1171, 0, 0, 0),
					RDEConfigFile);
			ic = new InitCondition(rde);
			ic.steadyState(0, 0);
			ic.steadyState(1, 1);
			ic.steadyState(2, 1);
			ic.steadyState(3, 0.02155304308028087);
		} else if (p.getProperty("model").equalsIgnoreCase("mm_endo")) {
			rde = new RDESolver(new MinimalModelEndo(0.1171, 0, 0, 0),
					RDEConfigFile);
			ic = new InitCondition(rde);
			ic.steadyState(0, 0);
			ic.steadyState(1, 1);
			ic.steadyState(2, 1);
			ic.steadyState(3, 0.02155304308028087);
		} else if (p.getProperty("model").equalsIgnoreCase("mm_m")) {
			rde = new RDESolver(new MinimalModelM(0.1171, 0, 0, 0),
					RDEConfigFile);
			ic = new InitCondition(rde);
			ic.steadyState(0, 0);
			ic.steadyState(1, 1);
			ic.steadyState(2, 1);
			ic.steadyState(3, 0.02155304308028087);
		} else if (p.getProperty("model").equalsIgnoreCase("ore")) {
			rde = new RDESolver(new Oregonator(1.0, 0), RDEConfigFile);
			ic = new InitCondition(rde);
			ic.steadyState(0, 0);
			ic.steadyState(1, 0);
		} else if (p.getProperty("model").equalsIgnoreCase("heat")) {
			rde = new RDESolver(new Heat(1.0), RDEConfigFile);
			ic = new InitCondition(rde);
			ic.steadyState(0, 0);
		} else if (p.getProperty("model").equalsIgnoreCase("tws")) {
			rde = new RDESolver(new MinimalModelEpiTWS(0,0,0,0,0), RDEConfigFile);
			ic = new InitCondition(rde);
			ic.steadyState(0, 0.0);
			ic.steadyState(1, -0.38);
			ic.steadyState(2, 0.2);
			ic.steadyState(3, 0.24);
			ic.steadyState(4, -0.06);
		}

		logger.info(rde.getModel().getClass().getSimpleName() + " is set!");
		timeStep = rde.getDt();
	}

	/**
	 * Solves the PDEs for severals steps. Thereby the elapsed time is taken and
	 * added to <code>{@link RDEController#timeElapsed}</code>. The
	 * calculation pauses if <code>{@link RDEController#pause}</code> is set
	 * to <code>true</code>.
	 * 
	 * @param steps
	 *                number of time steps for which the calculation is carried
	 *                on.
	 */
	public void doTimeSteps(double steps) {
		while (steps > 0) {
			rde.nextTimeStep();
			timeElapsed += timeStep;
			if (pause) {
				pauseLoop();
			}
			steps--;
		}
	}

	/**
	 * @return Value of elapsed simulation time.
	 */
	public double getTimeElapsed() {
		return timeElapsed;
	}

	/**
	 * @param timeElapsed
	 *                sets the elapsed simulation to a specific value (i.e to
	 *                0).
	 */
	public void setTimeElapsed(double timeElapsed) {
		this.timeElapsed = timeElapsed;
	}

	public double getData(int field, int x, int y) {
		return rde.getData()[rde.getActArray()][field][x][y];
	}
	
	public String allDataX(int field, int y) {
		StringBuilder sb = new StringBuilder();
		for (int x = 0; x < rde.getDimX(); x++) {
			sb.append(x + "\t" + rde.getData()[rde.getActArray()][field][x][y]);
			sb.append("\n");
		}
		return sb.toString();
	}

	public String allDataY(int field, int x) {
		StringBuilder sb = new StringBuilder();
		for (int y = 0; y < rde.getDimY(); y++) {
			sb.append(y + "\t" + rde.getData()[rde.getActArray()][field][x][y]);
			sb.append("\n");
		}
		return sb.toString();
	}

	/**
	 * @return the <code>{@link RDESolver}</code> object.
	 * 
	 * @see RDESolver
	 */
	public RDESolver getRde() {
		return this.rde;
	}

	/**
	 * Sets a new <code>{@link RDESolver}</code> object.
	 * 
	 * @param rde
	 *                the new <code>{@link RDESolver}</code> object.
	 * 
	 * @see RDESolver
	 */
	public void setRde(RDESolver rde) {
		this.rde = rde;
	}

	/**
	 * Sets the <code>boolean</code> <code>{@link RDEController#pause}</code>
	 * to an new value.
	 * 
	 * @param pause
	 *                new value of <code>{@link RDEController#pause}</code>.
	 */
	public void setPause(boolean pause) {
		this.pause = pause;
	}

	/**
	 * @return value of <code>{@link RDEController#pause}</code>.
	 */
	public boolean isPause() {
		return pause;
	}

	/**
	 * Pauses the current Thread.
	 */
	public void pauseLoop() {
		while (pause) {
			try {
				Thread.sleep(10);
			} catch (Exception ex) {
				logger.log(Level.WARNING, "Exception occurred", ex);
			}
		}
	}

	public void stimulate(int x, int y, double strength) {
		rde.getData()[rde.getActArray()][0][x][y] += strength;
	}

	public void stimulateLeft(int width, double strength) {
		for (int x = 0; x <= width; x++) {
			rde.getData()[rde.getActArray()][0][x][0] += strength;
		}
	}

	public void stimulateRight(int width, double strength) {
		int dimX = rde.getDimX();
		for (int x = dimX - 1 - width; x < dimX; x++) {
			rde.getData()[rde.getActArray()][0][x][0] += strength;
		}
	}

	public void setState(int x, int y, double value) {
		rde.getData()[rde.getActArray()][0][x][y] = value;
	}

	public boolean isAbove(int x, int y, double value) {
		return (getData(0, x, y) > value ? true : false);
	}

	/**
	 * Moves a generated one dimensional pulse to a certain position along the
	 * cable. Therefore a <code>{@link PulseParams}</code> object is created
	 * and the position of the pulse's maximum is taken as reference for moving
	 * it. So the maximum of the pulse will be moved to the defined position.
	 * 
	 * @param pos
	 *                position to where the pulse will be moved.
	 */
	public void movePulse(int pos) {
		logger.fine("Moving maximum of pulse to " + pos + "...");
		PulseParams pp = getPulseParams(0);
		while (pp.getMaxPosition() != pos) {
			doTimeSteps(1);
			pp = getPulseParams(0);
			if (pp.getMaximum() < getDouble(p, "threshold")) {
				logger.fine("No Pulse for moving available");
				break;
			}
		}
	}

	/**
	 * To get a collection of pulse parameters (<i>maximum, minimum, maximum's
	 * position, minimum's position, left branch's position, right branch's
	 * position</i>) it is necessary to derive these quantities first.
	 * 
	 * <p>
	 * The minimum and maximum with their positions are calulated by looping
	 * through grid.
	 * 
	 * <p>
	 * The left and right branch positon respectivly depend on the parameter
	 * <i>threshold</i> which can be set in the configuration file. These
	 * positions are defined as points where the voltage exceeds this threshold.
	 * 
	 * @param f
	 *                variable for which the pulse parameters will be
	 *                calculated.
	 * @return a new <code>{@link PulseParams}</code> object.
	 * 
	 * @see PulseParams
	 */
	public PulseParams getPulseParams(int f) {
		double val = getDouble(p, "threshold");
		if (rde.getDimension() == 1) {
			int dimX = rde.getDimX();
			int y = 0;
			double min = 0;
			double max = 0;
			int minpos = -1;
			int maxpos = -1;
			int leftBranch = -1;
			int rightBranch = -1;

			for (int x = 0; x < dimX; x++) {
				// Calculating Minimum/MinimumPosition
				if (rde.getData()[rde.getActArray()][f][x][y] > max) {
					max = rde.getData()[rde.getActArray()][f][x][y];
					maxpos = x;
				}
				// Calculating Maximum/MaximumPosition
				if (rde.getData()[rde.getActArray()][f][x][y] < min) {
					min = rde.getData()[rde.getActArray()][f][x][y];
					minpos = x;
				}

			}

			// for (int x = maxpos; x < maxpos + dimX; x++) {
			// if ((rde.getData()[rde.getActArray()][f][x % dimX][y] > val)
			// && (rde.getData()[rde.getActArray()][f][x % dimX][y] > rde
			// .getData()[rde.getActArray()][f][(x + 1) % dimX][y])) {
			// rightBranch = x % dimX;
			// }
			// if ((rde.getData()[rde.getActArray()][f][x % dimX][y] < val)
			// && (rde.getData()[rde.getActArray()][f][x % dimX][y] < rde
			// .getData()[rde.getActArray()][f][(x + 1) % dimX][y])) {
			// leftBranch = x % dimX;
			// }
			// }

			return new PulseParams(max, min, maxpos, minpos, leftBranch,
					rightBranch);
		} else {
			System.out.println("Supports only one dimension");
			return null;
		}
	}

	public Point2D.Double getMaximum(int field, int startX, int endX) {
		double max = 0.0;
		int maxpos = -1;
		for (int x = startX; x <= endX; x++) {
			if (rde.getData()[rde.getActArray()][field][x][0] > max) {
				max = rde.getData()[rde.getActArray()][field][x][0];
				maxpos = x;
			}
		}
		return new Point2D.Double(maxpos, max);
	}

	public Properties getProperties() {
		return p;
	}

}
