package moduls;

import static utils.MyLogger.getLogger;
import static utils.Parser.getDouble;
import static utils.Parser.getInteger;
import static utils.Printer.print;
import static utils.PropertiesManager.loadProperties;
import static utils.PropertiesManager.storeProperties;
import static utils.Writer.write;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import controlling.RDEController;

public class TravellingWave {

	private static Logger logger = getLogger(TravellingWave.class
			.getSimpleName(), Level.ALL);

	private static RDEController rc = new RDEController("bla.config",
			"rde.config");

	private final static double STRENGTH = getDouble(rc.getProperties()
			.getProperty("strength"));
	private final static int WIDTH = getInteger(rc.getProperties().getProperty(
			"width"));
	private final static int SHOWSTEP = getInteger(rc.getProperties()
			.getProperty("show_step"));
	private final static int GRIDSTEP = getInteger(rc.getProperties()
			.getProperty("gridstep"));
	private final static double THRESHOLD = getDouble(rc.getProperties()
			.getProperty("threshold"));

	// Get some important quantities
	private final static double DT = rc.getRde().getDt();
	private final static int DIMX = rc.getRde().getDimX();
	private final static int DIMY = rc.getRde().getDimY();

	private final static String FOLDERNAME = rc.getRde().getModel().getClass()
			.getSimpleName()
			+ "." + DIMX + "." + DIMY;
	private final static File STOPFILE = new File(FOLDERNAME + "/stop");

	private StringBuilder sb = new StringBuilder();
	private DecimalFormat df = new DecimalFormat();

	public TravellingWave() {
		df.setMinimumIntegerDigits(6);
		df.setMaximumFractionDigits(0);
		// Create Folder
		File f = new File(FOLDERNAME);
		f.mkdir();
	}

	// Danke an Martin für diese Idee
	private boolean stopMe() {
		return STOPFILE.exists();
	}

	private void spiralWave() {
		Properties p = loadProperties("rde.config");
		p.setProperty("dimension", "" + 1);
		storeProperties(p, "rde.config");
		RDEController r = new RDEController("bla.config", "rde.config");
		r.stimulate(0, 0, STRENGTH);
		while (r.getData(0, (int) 5 * DIMX / 6, 0) < THRESHOLD) {
			r.doTimeSteps(1);
		}
		for (int f = 0; f < rc.getRde().getModel().getFieldCount(); f++) {
			for (int y = 0; y < DIMY / 2; y++) {
				for (int x = 0; x < DIMX; x++) {
					rc.getRde().getData()[rc.getRde().getActArray()][f][x][y] = r
							.getData(f, x, 0);
				}
			}
		}
		p.setProperty("dimension", "" + 2);
		storeProperties(p, "rde.config");
		logger.info("Spiral wave initiated.");
		writeData();
		while (!stopMe()) {
			rc.doTimeSteps(SHOWSTEP);
			writeData();
			logger.info("Data written for time: "
					+ df.format(rc.getTimeElapsed()) + "s.");
		}
	}

	private void planarWave(double period) {
		for (int y = 0; y < DIMY; y++) {
			rc.stimulate(0, y, STRENGTH);
		}
		double steps = period / DT;
		while (!stopMe()) {
			while (steps > 0) {
				rc.doTimeSteps(1);
				if ((int) steps % SHOWSTEP == 0) {
					writeData();
					logger.info("Data written for time: "
							+ df.format(rc.getTimeElapsed()) + "s.");
				}
				steps--;
			}
			steps = period / DT;
			for (int y = 0; y < DIMY; y++) {
				rc.stimulate(0, y, STRENGTH);
			}
		}
	}
	
	private void stimulatePoint(int posX, int posY) {
		for (int x = posX; x < posX + WIDTH; x++) {
			for (int y = posY; y <= posY + WIDTH; y++) {
				rc.stimulate(x, y, STRENGTH);
			}
		}
	}

	private void targetWave(double period) {
		int posX = (int) (rc.getRde().getDimX() / 3);
		int posY = (int) (rc.getRde().getDimY() / 3);
		stimulatePoint(posX, posY);
		int steps = (int) (period / DT);
		while (!stopMe()) {
			while (steps > 0) {
				rc.doTimeSteps(1);
				if (steps % SHOWSTEP == 0) {
					writeData();
					logger.info("Data written for time: "
							+ df.format(rc.getTimeElapsed()) + "s.");
				}
				steps--;
			}
			steps = (int) (period / DT);
			stimulatePoint(posX, posY);
		}
	}

	private void writeData() {
		for (int y = 0; y < DIMY; y += GRIDSTEP) {
			for (int x = 0; x < DIMX; x += GRIDSTEP) {
				sb.append(rc.getData(0, x, y) + "\t");
			}
			sb.append("\n");
		}
		write(FOLDERNAME + "/" + df.format(rc.getTimeElapsed()) + "_test.dat",
				sb.toString());
		sb.setLength(0);
	}

	public static void main(String[] args) {
		TravellingWave tw = new TravellingWave();
		if (args.length > 0) {
			if (args[0].equals("spiral")) {
				tw.spiralWave();
			} else if (args[0].equals("planar")) {
				tw.planarWave(getDouble(args[1]));
			} else if (args[0].equals("target")) {
				tw.targetWave(getDouble(args[1]));
			}
		} else {
			print("Usage:\n\tjava -jar TravellingWave.jar [options]");
			print("");
			print("Options are: spiral, planar <period>, target <period>");
			print("<period> has to be the second argument for target and planar!");
		}

	}

}
