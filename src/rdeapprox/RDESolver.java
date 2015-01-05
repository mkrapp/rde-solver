package rdeapprox;

import static utils.Parser.getDouble;
import static utils.Parser.getInteger;
import static utils.PropertiesManager.loadProperties;

import java.awt.Rectangle;
import java.util.Arrays;
import java.util.Properties;
import java.util.Random;

import models.RDEModel;

/**
 * <p>
 * The <code>{@link RDESolver}</code> presents a class which is able to solve
 * <b>Nonlinear Partial Differential Equations</b>. The currently implemented
 * method is the <b>Euler Forward</b> method (Estimated error in order of
 * <i>O(k+h<sup>2</sup>)</i> where <i>k</i> is the time and <i>h</i> the
 * spatial step). The <code>{@link RDESolver}</code> uses a four-dimensional
 * <code>Array</code> where the first field contains two states storing
 * current and old values. The second field contains the different variables
 * provided by the <code>{@link RDEModel}</code> class. The third and fourth
 * fields represent the x- and y-position of the data value.
 * 
 * <p>
 * <code>{@link RDESolver}</code> uses the <code>Properties</code> class
 * which contains the parameter sets for solving the nonlinear PDEs
 * 
 * @author Stefan Zeller
 * @author Mario Krapp
 * 
 */
public class RDESolver {

    /**
     * Sets the fields for Euler forward methods which calculates the new values
     * from the old ones
     */
    private static final int EULER_FORWARD_MEMORY = 2;

    /**
     * The number of data points for the x-direction.
     */
    private int dimX;

    /**
     * The number of data points for the y-direction.
     */
    private int dimY;

    /**
     * The time step.
     */
    private double dt;

    /**
     * The spatial step.
     */
    private double dh;

    /**
     * The boundary condition for solving the PDEs. Normaly <b>noflux</b>
     * (Neumann) <b>zero</b> (Dirichlet) or <b>periodic</b> boundary
     * conditions.
     */
    private int bc;

    private final static int ZERO = 0;

    private final static int NOFLUX = 1;

    private final static int PERIODIC = 2;

    /**
     * The <code>Array</code> storing the values for all variables at each
     * grid point with old and new data.
     */
    private double[][][][] data; // [memory][fields][x][y]

    /**
     * The current memory field of th array. Values are <b>0</b> or <b>1</b>
     * corresponding to <i>EULER_FORWARD_MEMORY</i>.
     */
    private int act, old;

    /**
     * The model which contains the nonlinear PDEs to be solved.
     */
    private RDEModel model;

    /**
     * The dimension for which the PDEs are to be solved. Values are <b>0</b>,
     * <b>1</b> or <b>2</b>.
     */
    private int dimension;

    private int fieldCount;

    private Rectangle[] rect;

    /**
     * Creates an new <code>{@link RDESolver}</code> object with the
     * parameters given by <code>Properties</code> and the equations submitted
     * by <code>{@link RDEModel}</code>. Afterwards the <code>Array</code>
     * <i>{@link RDESolver#data}</i> will be initialized.
     * 
     * @param p
     *                the properties which contain the parameters for solving
     *                the PDEs given by <code>{@link RDEModel}</code>.
     * @param model
     *                contain the nonlinear PDEs to be solved.
     */
    public RDESolver(RDEModel model, String RDEConfigFile) {
	this.model = model;
	fieldCount = model.getFieldCount();
	Properties p = loadProperties(RDEConfigFile);

	dt = getDouble(p, "time_step");
	dimension = getInteger(p, "dimension");

	if (dimension != 0) {
	    if (p.getProperty("boundary_condition").equals("zero")) {
		bc = ZERO;
	    } else if (p.getProperty("boundary_condition").equals("noflux")) {
		bc = NOFLUX;
	    } else if (p.getProperty("boundary_condition").equals("periodic")) {
		bc = PERIODIC;
	    }
	}

	// Settings for the chosen dimension
	if (dimension == 2) {
	    dimX = getInteger(p, "x_dimension");
	    dimY = getInteger(p, "y_dimension");
	    dh = getDouble(p, "spatial_step");
	    rect = randomRect(100, 5);
	}
	if (dimension == 1) {
	    dimX = getInteger(p, "x_dimension");
	    dimY = 1;
	    dh = getDouble(p, "spatial_step");
	}
	if (dimension == 0) {
	    dimX = 1;
	    dimY = 1;
	}

	// initialize array
	data = new double[EULER_FORWARD_MEMORY][fieldCount][dimX][dimY];

    }

    /**
     * Calculates one time step using the Euler forward method, dependent on the
     * boundary conditions, given by <code>Properties</code>.
     */
    public void nextTimeStep() {
	// TODO mir fällt auf, dass zu oft nach der Dimension geprüft wird. in
	// laplace() kann man das besser lösen. Dasselbe gilt für die
	// Randbedingungen. Das würde den Code sehr verkürzen. Also nur noch im
	// Lplace nach "dimension" testen

	// switch index
	act = (act == 0) ? 1 : 0;
	// set actual index to old index
	old = (act == 0) ? 1 : 0;

	int y = 0;
	int x = 0;
	int f = 0;
	double[] newValues = model.function(data[old], x, y);

	switch (dimension) {
	case 0:
	    // calculate new values
	    // loop through dynamical variables
	    for (f = 0; f < fieldCount; f++) {
		// old value
		double oldValues = data[old][f][x][y];
		data[act][f][x][y] = oldValues + dt * newValues[f];
	    }
	    break;
	case 1:
	    switch (bc) {
	    case 0:
		zeroBC();
		break;
	    case 1:
		noFluxBC();
		break;
	    case 2:
		periodicBC();
		break;
	    }
	    y = 0;
	    for (x = 1; x < dimX - 1; x++) {
		// calculate new values
		newValues = model.function(data[old], x, y);
		// loop through dynamical variables
		for (f = 0; f < fieldCount; f++) {
		    // old value
		    double oldValues = data[old][f][x][y];
		    // diffusion coefficient for current dynamical variable
		    double diffCoeff = model.diffConsts[f];
		    // calc laplace
		    double laplace = 0;
		    if (diffCoeff != 0) {
			laplace = laplace(0, data[old][f][x - 1][y],
				data[old][f][x][y], data[old][f][x + 1][y], 0);
		    }

		    data[act][f][x][y] = oldValues + dt
			    * (diffCoeff * laplace + newValues[f]);
		}
	    }
	    break;
	case 2:
	    switch (bc) {
	    case 0:
		zeroBC();
		break;
	    case 1:
		noFluxBC();
		break;
	    case 2:
		periodicBC();
		break;
	    }
	    for (x = 1; x < dimX - 1; x++) {
		for (y = 1; y < dimY - 1; y++) {
		    // calculate new values
		    newValues = model.function(data[old], x, y);
		    // loop through dynamical variables
		    for (f = 0; f < fieldCount; f++) {
			// old value
			double oldValues = data[old][f][x][y];
			// diffusion coefficient for current dynamical variable
			double diffCoeff = model.diffConsts[f];
			// calc laplace
			double laplace = 0;
			if (diffCoeff != 0) {
			    laplace = laplace(data[old][f][x][y - 1],
				    data[old][f][x - 1][y], data[old][f][x][y],
				    data[old][f][x + 1][y],
				    data[old][f][x][y + 1]);
			}

			data[act][f][x][y] = oldValues + dt
				* (diffCoeff * laplace + newValues[f]);

		    }
		}
	    }
	    break;

	}

	// for (int i = 0; i < rect.length; i++) {
	// rectangularObstacle(rect[i]);
	// }

    }

    /**
     * Sets the boundray conditions to <b>periodic</b>.
     */
    private void periodicBC() {

	int oldIdx = (act == 0) ? 1 : 0;
	int x = -1;
	int y = -1;
	int f = -1;
	double[] newFieldValues = null;
	double old = -1;
	double diffCoeff = -1;
	double laplace = -1;

	switch (dimension) {
	case 1:
	    /*
	     * left boundary
	     */
	    x = 0;
	    y = 0;
	    // calculate new values
	    newFieldValues = model.function(data[oldIdx], x, y);
	    // loop through dynamical variables
	    for (f = 0; f < fieldCount; f++) {
		// old value
		old = data[oldIdx][f][x][y];
		// diffusion coefficient for current dynamical variable
		diffCoeff = model.diffConsts[f];
		// calc laplace
		laplace = 0;
		if (diffCoeff != 0) {
		    laplace = laplace(0, data[oldIdx][f][dimX - 1][y],
			    data[oldIdx][f][x][y], data[oldIdx][f][x + 1][y], 0);
		}
		data[act][f][x][y] = old + dt
			* (diffCoeff * laplace + newFieldValues[f]);
	    }
	    /*
	     * right boundary
	     */
	    x = dimX - 1;
	    // calculate new values
	    newFieldValues = model.function(data[oldIdx], x, y);
	    // loop through dynamical variables
	    for (f = 0; f < fieldCount; f++) {
		// old value
		old = data[oldIdx][f][x][y];
		// diffusion coefficient for current dynamical variable
		diffCoeff = model.diffConsts[f];
		// calc laplace
		laplace = 0;
		if (diffCoeff != 0) {
		    laplace = laplace(0, data[oldIdx][f][x - 1][y],
			    data[oldIdx][f][x][y], data[oldIdx][f][0][y], 0);
		}
		data[act][f][x][y] = old + dt
			* (diffCoeff * laplace + newFieldValues[f]);
	    }
	    break;
	case 2:

	    /*
	     * the corners
	     */
	    // upper left
	    x = 0;
	    y = 0;
	    newFieldValues = model.function(data[oldIdx], x, y);
	    for (f = 0; f < fieldCount; f++) {
		// old value
		old = data[oldIdx][f][x][y];
		// diffusion coefficient for current dynamical variable
		diffCoeff = model.diffConsts[f];
		// calc laplace
		laplace = 0;
		if (diffCoeff != 0) {
		    laplace = laplace(data[oldIdx][f][0][dimY - 1],
			    data[oldIdx][f][dimX - 1][0],
			    data[oldIdx][f][x][y], data[oldIdx][f][x + 1][y],
			    data[oldIdx][f][x][y + 1]);
		}
		data[act][f][x][y] = old + dt
			* (diffCoeff * laplace + newFieldValues[f]);
	    }
	    // upper right
	    x = dimX - 1;
	    y = 0;
	    newFieldValues = model.function(data[oldIdx], x, y);
	    for (f = 0; f < fieldCount; f++) {
		// old value
		old = data[oldIdx][f][x][y];
		// diffusion coefficient for current dynamical variable
		diffCoeff = model.diffConsts[f];
		// calc laplace
		laplace = 0;
		if (diffCoeff != 0) {
		    laplace = laplace(data[oldIdx][f][dimX - 1][dimY - 1],
			    data[oldIdx][f][x - 1][y], data[oldIdx][f][x][y],
			    data[oldIdx][f][0][0], data[oldIdx][f][x][y + 1]);
		}
		data[act][f][x][y] = old + dt
			* (diffCoeff * laplace + newFieldValues[f]);
	    }
	    // lower right
	    x = dimX - 1;
	    y = dimY - 1;
	    newFieldValues = model.function(data[oldIdx], x, y);
	    for (f = 0; f < fieldCount; f++) {
		// old value
		old = data[oldIdx][f][x][y];
		// diffusion coefficient for current dynamical variable
		diffCoeff = model.diffConsts[f];
		// calc laplace
		laplace = 0;
		if (diffCoeff != 0) {
		    laplace = laplace(data[oldIdx][f][x][y - 1],
			    data[oldIdx][f][x - 1][y], data[oldIdx][f][x][y],
			    data[oldIdx][f][0][dimY - 1],
			    data[oldIdx][f][dimX - 1][0]);
		}
		data[act][f][x][y] = old + dt
			* (diffCoeff * laplace + newFieldValues[f]);
	    }
	    // lower left
	    x = 0;
	    y = dimY - 1;
	    newFieldValues = model.function(data[oldIdx], x, y);
	    for (f = 0; f < fieldCount; f++) {
		// old value
		old = data[oldIdx][f][x][y];
		// diffusion coefficient for current dynamical variable
		diffCoeff = model.diffConsts[f];
		// calc laplace
		laplace = 0;
		if (diffCoeff != 0) {
		    laplace = laplace(data[oldIdx][f][x][y - 1],
			    data[oldIdx][f][dimX - 1][dimY - 1],
			    data[oldIdx][f][x][y], data[oldIdx][f][x + 1][y],
			    data[oldIdx][f][0][0]);
		}
		data[act][f][x][y] = old + dt
			* (diffCoeff * laplace + newFieldValues[f]);
	    }
	    /*
	     * left boundary
	     */
	    x = 0;
	    for (y = 1; y < dimY - 1; y++) {
		// calculate new values
		newFieldValues = model.function(data[oldIdx], x, y);
		// loop through dynamical variables
		for (f = 0; f < fieldCount; f++) {
		    // old value
		    old = data[oldIdx][f][x][y];
		    // diffusion coefficient for current dynamical variable
		    diffCoeff = model.diffConsts[f];
		    // calc laplace
		    laplace = 0;
		    if (diffCoeff != 0) {
			laplace = laplace(data[oldIdx][f][x][y - 1],
				data[oldIdx][f][dimX - 1][y],
				data[oldIdx][f][x][y],
				data[oldIdx][f][x + 1][y],
				data[oldIdx][f][x][y + 1]);
		    }
		    data[act][f][x][y] = old + dt
			    * (diffCoeff * laplace + newFieldValues[f]);
		}
	    }
	    /*
	     * right boundary
	     */
	    x = dimX - 1;
	    for (y = 1; y < dimY - 1; y++) {
		// calculate new values
		newFieldValues = model.function(data[oldIdx], x, y);
		// loop through dynamical variables
		for (f = 0; f < fieldCount; f++) {
		    // old value
		    old = data[oldIdx][f][x][y];
		    // diffusion coefficient for current dynamical variable
		    diffCoeff = model.diffConsts[f];
		    // calc laplace
		    laplace = 0;
		    if (diffCoeff != 0) {
			laplace = laplace(data[oldIdx][f][x][y - 1],
				data[oldIdx][f][x - 1][y],
				data[oldIdx][f][x][y], data[oldIdx][f][0][y],
				data[oldIdx][f][x][y + 1]);
		    }
		    data[act][f][x][y] = old + dt
			    * (diffCoeff * laplace + newFieldValues[f]);
		}
	    }
	    /*
	     * lower boundary
	     */
	    y = dimY - 1;
	    for (x = 1; x < dimX - 1; x++) {
		// calculate new values
		newFieldValues = model.function(data[oldIdx], x, y);
		// loop through dynamical variables
		for (f = 0; f < fieldCount; f++) {
		    // old value
		    old = data[oldIdx][f][x][y];
		    // diffusion coefficient for current dynamical variable
		    diffCoeff = model.diffConsts[f];
		    // calc laplace
		    laplace = 0;
		    if (diffCoeff != 0) {
			laplace = laplace(data[oldIdx][f][x][y - 1],
				data[oldIdx][f][x - 1][y],
				data[oldIdx][f][x][y],
				data[oldIdx][f][x + 1][y],
				data[oldIdx][f][x][0]);
		    }
		    data[act][f][x][y] = old + dt
			    * (diffCoeff * laplace + newFieldValues[f]);
		}
	    }
	    /*
	     * upper boundary
	     */
	    y = 0;
	    for (x = 1; x < dimX - 1; x++) {
		// calculate new values
		newFieldValues = model.function(data[oldIdx], x, y);
		// loop through dynamical variables
		for (f = 0; f < fieldCount; f++) {
		    // old value
		    old = data[oldIdx][f][x][y];
		    // diffusion coefficient for current dynamical variable
		    diffCoeff = model.diffConsts[f];
		    // calc laplace
		    laplace = 0;
		    if (diffCoeff != 0) {
			laplace = laplace(data[oldIdx][f][x][dimY - 1],
				data[oldIdx][f][x - 1][y],
				data[oldIdx][f][x][y],
				data[oldIdx][f][x + 1][y],
				data[oldIdx][f][x][y + 1]);
		    }
		    data[act][f][x][y] = old + dt
			    * (diffCoeff * laplace + newFieldValues[f]);
		}
	    }
	    break;
	}
    }

    /**
     * Sets the boundray conditions to <b>zero</b>.
     */
    private void zeroBC() {

	int oldIdx = (act == 0) ? 1 : 0;
	int x = -1;
	int y = -1;
	int f = -1;
	double[] newFieldValues = null;
	double old = -1;
	double diffCoeff = -1;
	double laplace = -1;

	switch (dimension) {
	case 1:
	    /*
	     * left boundary
	     */
	    x = 0;
	    y = 0;
	    // calculate new values
	    newFieldValues = model.function(data[oldIdx], x, y);
	    // loop through dynamical variables
	    for (f = 0; f < fieldCount; f++) {
		// old value
		old = data[oldIdx][f][x][y];
		// diffusion coefficient for current dynamical variable
		diffCoeff = model.diffConsts[f];
		// calc laplace
		laplace = 0;
		if (diffCoeff != 0) {
		    laplace = laplace(0, 0, data[oldIdx][f][x][y],
			    data[oldIdx][f][x + 1][y], 0);
		}
		data[act][f][x][y] = old + dt
			* (diffCoeff * laplace + newFieldValues[f]);
	    }
	    /*
	     * right boundary
	     */
	    x = dimX - 1;
	    // calculate new values
	    newFieldValues = model.function(data[oldIdx], x, y);
	    // loop through dynamical variables
	    for (f = 0; f < fieldCount; f++) {
		// old value
		old = data[oldIdx][f][x][y];
		// diffusion coefficient for current dynamical variable
		diffCoeff = model.diffConsts[f];
		// calc laplace
		laplace = 0;
		if (diffCoeff != 0) {
		    laplace = laplace(0, data[oldIdx][f][x - 1][y],
			    data[oldIdx][f][x][y], 0, 0);
		}
		data[act][f][x][y] = old + dt
			* (diffCoeff * laplace + newFieldValues[f]);
	    }
	    break;
	case 2:
	    /*
	     * the corners
	     */
	    // upper left
	    x = 0;
	    y = 0;
	    // calculate new values
	    newFieldValues = model.function(data[oldIdx], x, y);
	    for (f = 0; f < fieldCount; f++) {
		// old value
		old = data[oldIdx][f][x][y];
		// diffusion coefficient for current dynamical variable
		diffCoeff = model.diffConsts[f];
		// calc laplace
		laplace = 0;
		if (diffCoeff != 0) {
		    laplace = laplace(0, 0, data[oldIdx][f][x][y],
			    data[oldIdx][f][x + 1][y],
			    data[oldIdx][f][x][y + 1]);
		}
		data[act][f][x][y] = old + dt
			* (diffCoeff * laplace + newFieldValues[f]);
	    }
	    // upper right
	    x = dimX - 1;
	    y = 0;
	    newFieldValues = model.function(data[oldIdx], x, y);
	    for (f = 0; f < fieldCount; f++) {
		// old value
		old = data[oldIdx][f][x][y];
		// diffusion coefficient for current dynamical variable
		diffCoeff = model.diffConsts[f];
		// calc laplace
		laplace = 0;
		if (diffCoeff != 0) {
		    laplace = laplace(0, data[oldIdx][f][x - 1][y],
			    data[oldIdx][f][x][y], 0, data[oldIdx][f][x][y + 1]);
		}
		data[act][f][x][y] = old + dt
			* (diffCoeff * laplace + newFieldValues[f]);
	    }
	    // lower right
	    x = dimX - 1;
	    y = dimY - 1;
	    newFieldValues = model.function(data[oldIdx], x, y);
	    for (f = 0; f < fieldCount; f++) {
		// old value
		old = data[oldIdx][f][x][y];
		// diffusion coefficient for current dynamical variable
		diffCoeff = model.diffConsts[f];
		// calc laplace
		laplace = 0;
		if (diffCoeff != 0) {
		    laplace = laplace(data[oldIdx][f][x][y - 1],
			    data[oldIdx][f][x - 1][y], data[oldIdx][f][x][y],
			    0, 0);
		}
		data[act][f][x][y] = old + dt
			* (diffCoeff * laplace + newFieldValues[f]);
	    }
	    // lower left
	    x = 0;
	    y = dimY - 1;
	    newFieldValues = model.function(data[oldIdx], x, y);
	    for (f = 0; f < fieldCount; f++) {
		// old value
		old = data[oldIdx][f][x][y];
		// diffusion coefficient for current dynamical variable
		diffCoeff = model.diffConsts[f];
		// calc laplace
		laplace = 0;
		if (diffCoeff != 0) {
		    laplace = laplace(data[oldIdx][f][x][y - 1], 0,
			    data[oldIdx][f][x][y], data[oldIdx][f][x + 1][y], 0);
		}
		data[act][f][x][y] = old + dt
			* (diffCoeff * laplace + newFieldValues[f]);
	    }
	    /*
	     * left boundary
	     */
	    x = 0;
	    for (y = 1; y < dimY - 1; y++) {
		// calculate new values
		newFieldValues = model.function(data[oldIdx], x, y);
		// loop through dynamical variables
		for (f = 0; f < fieldCount; f++) {
		    // old value
		    old = data[oldIdx][f][x][y];
		    // diffusion coefficient for current dynamical variable
		    diffCoeff = model.diffConsts[f];
		    // calc laplace
		    laplace = 0;
		    if (diffCoeff != 0) {
			laplace = laplace(data[oldIdx][f][x][y - 1], 0,
				data[oldIdx][f][x][y],
				data[oldIdx][f][x + 1][y],
				data[oldIdx][f][x][y + 1]);
		    }
		    data[act][f][x][y] = old + dt
			    * (diffCoeff * laplace + newFieldValues[f]);
		}
	    }
	    /*
	     * right boundary
	     */
	    x = dimX - 1;
	    for (y = 1; y < dimY - 1; y++) {
		// calculate new values
		newFieldValues = model.function(data[oldIdx], x, y);
		// loop through dynamical variables
		for (f = 0; f < fieldCount; f++) {
		    // old value
		    old = data[oldIdx][f][x][y];
		    // diffusion coefficient for current dynamical variable
		    diffCoeff = model.diffConsts[f];
		    // calc laplace
		    laplace = 0;
		    if (diffCoeff != 0) {
			laplace = laplace(data[oldIdx][f][x][y - 1],
				data[oldIdx][f][x - 1][y],
				data[oldIdx][f][x][y], 0,
				data[oldIdx][f][x][y + 1]);
		    }
		    data[act][f][x][y] = old + dt
			    * (diffCoeff * laplace + newFieldValues[f]);
		}
	    }
	    /*
	     * lower boundary
	     */
	    y = dimY - 1;
	    for (x = 1; x < dimX - 1; x++) {
		// calculate new values
		newFieldValues = model.function(data[oldIdx], x, y);
		// loop through dynamical variables
		for (f = 0; f < fieldCount; f++) {
		    // old value
		    old = data[oldIdx][f][x][y];
		    // diffusion coefficient for current dynamical variable
		    diffCoeff = model.diffConsts[f];
		    // calc laplace
		    laplace = 0;
		    if (diffCoeff != 0) {
			laplace = laplace(data[oldIdx][f][x][y - 1],
				data[oldIdx][f][x - 1][y],
				data[oldIdx][f][x][y],
				data[oldIdx][f][x + 1][y], 0);
		    }
		    data[act][f][x][y] = old + dt
			    * (diffCoeff * laplace + newFieldValues[f]);
		}
	    }
	    /*
	     * upper boundary
	     */
	    y = 0;
	    for (x = 1; x < dimX - 1; x++) {
		// calculate new values
		newFieldValues = model.function(data[oldIdx], x, y);
		// loop through dynamical variables
		for (f = 0; f < fieldCount; f++) {
		    // old value
		    old = data[oldIdx][f][x][y];
		    // diffusion coefficient for current dynamical variable
		    diffCoeff = model.diffConsts[f];
		    // calc laplace
		    laplace = 0;
		    if (diffCoeff != 0) {
			laplace = laplace(0, data[oldIdx][f][x - 1][y],
				data[oldIdx][f][x][y],
				data[oldIdx][f][x + 1][y],
				data[oldIdx][f][x][y + 1]);
		    }
		    data[act][f][x][y] = old + dt
			    * (diffCoeff * laplace + newFieldValues[f]);
		}
	    }
	    break;
	}

    }

    /**
     * Sets the boundray conditions to <b>noflux</b>.
     */
    private void noFluxBC() {

	int oldIdx = (act == 0) ? 1 : 0;
	int x = -1;
	int y = -1;
	int f = -1;
	double[] newFieldValues = null;
	double old = -1;
	double diffCoeff = -1;
	double laplace = -1;

	switch (dimension) {
	case 1:
	    /*
	     * left boundary
	     */
	    x = 0;
	    y = 0;
	    // calculate new values
	    newFieldValues = model.function(data[oldIdx], x, y);
	    // loop through dynamical variables
	    for (f = 0; f < fieldCount; f++) {
		// old value
		old = data[oldIdx][f][x][y];
		// diffusion coefficient for current dynamical variable
		diffCoeff = model.diffConsts[f];
		// calc laplace
		laplace = 0;
		if (diffCoeff != 0) {
		    laplace = laplace(0, data[oldIdx][f][x][y],
			    data[oldIdx][f][x][y], data[oldIdx][f][x + 1][y], 0);
		}
		data[act][f][x][y] = old + dt
			* (diffCoeff * laplace + newFieldValues[f]);
	    }
	    /*
	     * right boundary
	     */
	    x = dimX - 1;
	    // calculate new values
	    newFieldValues = model.function(data[oldIdx], x, y);
	    // loop through dynamical variables
	    for (f = 0; f < fieldCount; f++) {
		// old value
		old = data[oldIdx][f][x][y];
		// diffusion coefficient for current dynamical variable
		diffCoeff = model.diffConsts[f];
		// calc laplace
		laplace = 0;
		if (diffCoeff != 0) {
		    laplace = laplace(0, data[oldIdx][f][x - 1][y],
			    data[oldIdx][f][x][y], data[oldIdx][f][x][y], 0);
		}
		data[act][f][x][y] = old + dt
			* (diffCoeff * laplace + newFieldValues[f]);
	    }
	    break;
	case 2:
	    /*
	     * the corners
	     */
	    // upper left
	    x = 0;
	    y = 0;
	    // calculate new values
	    newFieldValues = model.function(data[oldIdx], x, y);
	    for (f = 0; f < fieldCount; f++) {
		// old value
		old = data[oldIdx][f][x][y];
		// diffusion coefficient for current dynamical variable
		diffCoeff = model.diffConsts[f];
		// calc laplace
		laplace = 0;
		if (diffCoeff != 0) {
		    laplace = laplace(data[oldIdx][f][x][y],
			    data[oldIdx][f][x][y], data[oldIdx][f][x][y],
			    data[oldIdx][f][x + 1][y],
			    data[oldIdx][f][x][y + 1]);
		}
		data[act][f][x][y] = old + dt
			* (diffCoeff * laplace + newFieldValues[f]);
	    }
	    // upper right
	    x = dimX - 1;
	    y = 0;
	    newFieldValues = model.function(data[oldIdx], x, y);
	    for (f = 0; f < fieldCount; f++) {
		// old value
		old = data[oldIdx][f][x][y];
		// diffusion coefficient for current dynamical variable
		diffCoeff = model.diffConsts[f];
		// calc laplace
		laplace = 0;
		if (diffCoeff != 0) {
		    laplace = laplace(
			    data[oldIdx][f][x][y], // ---
			    data[oldIdx][f][x - 1][y], data[oldIdx][f][x][y],
			    data[oldIdx][f][x][y], // ---
			    data[oldIdx][f][x][y + 1]);
		}
		data[act][f][x][y] = old + dt
			* (diffCoeff * laplace + newFieldValues[f]);
	    }
	    // lower right
	    x = dimX - 1;
	    y = dimY - 1;
	    newFieldValues = model.function(data[oldIdx], x, y);
	    for (f = 0; f < fieldCount; f++) {
		// old value
		old = data[oldIdx][f][x][y];
		// diffusion coefficient for current dynamical variable
		diffCoeff = model.diffConsts[f];
		// calc laplace
		laplace = 0;
		if (diffCoeff != 0) {
		    laplace = laplace(data[oldIdx][f][x][y - 1],
			    data[oldIdx][f][x - 1][y], data[oldIdx][f][x][y],
			    data[oldIdx][f][x][y], // ---
			    data[oldIdx][f][x][y]); // ---
		}
		data[act][f][x][y] = old + dt
			* (diffCoeff * laplace + newFieldValues[f]);
	    }
	    // lower left
	    x = 0;
	    y = dimY - 1;
	    newFieldValues = model.function(data[oldIdx], x, y);
	    for (f = 0; f < fieldCount; f++) {
		// old value
		old = data[oldIdx][f][x][y];
		// diffusion coefficient for current dynamical variable
		diffCoeff = model.diffConsts[f];
		// calc laplace
		laplace = 0;
		if (diffCoeff != 0) {
		    laplace = laplace(
			    data[oldIdx][f][x][y - 1],
			    data[oldIdx][f][x][y], // ---
			    data[oldIdx][f][x][y], data[oldIdx][f][x + 1][y],
			    data[oldIdx][f][x][y]); // ---
		}
		data[act][f][x][y] = old + dt
			* (diffCoeff * laplace + newFieldValues[f]);
	    }
	    /*
	     * left boundary
	     */
	    x = 0;
	    for (y = 1; y < dimY - 1; y++) {
		// calculate new values
		newFieldValues = model.function(data[oldIdx], x, y);
		// loop through dynamical variables
		for (f = 0; f < fieldCount; f++) {
		    // old value
		    old = data[oldIdx][f][x][y];
		    // diffusion coefficient for current dynamical variable
		    diffCoeff = model.diffConsts[f];
		    // calc laplace
		    laplace = 0;
		    if (diffCoeff != 0) {
			laplace = laplace(
				data[oldIdx][f][x][y - 1],
				data[oldIdx][f][x][y], // ---
				data[oldIdx][f][x][y],
				data[oldIdx][f][x + 1][y],
				data[oldIdx][f][x][y + 1]);
		    }
		    data[act][f][x][y] = old + dt
			    * (diffCoeff * laplace + newFieldValues[f]);
		}
	    }
	    /*
	     * right boundary
	     */
	    x = dimX - 1;
	    for (y = 1; y < dimY - 1; y++) {
		// calculate new values
		newFieldValues = model.function(data[oldIdx], x, y);
		// loop through dynamical variables
		for (f = 0; f < fieldCount; f++) {
		    // old value
		    old = data[oldIdx][f][x][y];
		    // diffusion coefficient for current dynamical variable
		    diffCoeff = model.diffConsts[f];
		    // calc laplace
		    laplace = 0;
		    if (diffCoeff != 0) {
			laplace = laplace(data[oldIdx][f][x][y - 1],
				data[oldIdx][f][x - 1][y],
				data[oldIdx][f][x][y], data[oldIdx][f][x][y], // ---
				data[oldIdx][f][x][y + 1]);
		    }
		    data[act][f][x][y] = old + dt
			    * (diffCoeff * laplace + newFieldValues[f]);
		}
	    }
	    /*
	     * lower boundary
	     */
	    y = dimY - 1;
	    for (x = 1; x < dimX - 1; x++) {
		// calculate new values
		newFieldValues = model.function(data[oldIdx], x, y);
		// loop through dynamical variables
		for (f = 0; f < fieldCount; f++) {
		    // old value
		    old = data[oldIdx][f][x][y];
		    // diffusion coefficient for current dynamical variable
		    diffCoeff = model.diffConsts[f];
		    // calc laplace
		    laplace = 0;
		    if (diffCoeff != 0) {
			laplace = laplace(data[oldIdx][f][x][y - 1],
				data[oldIdx][f][x - 1][y],
				data[oldIdx][f][x][y],
				data[oldIdx][f][x + 1][y],
				data[oldIdx][f][x][y]); // ---
		    }
		    data[act][f][x][y] = old + dt
			    * (diffCoeff * laplace + newFieldValues[f]);
		}
	    }
	    /*
	     * upper boundary
	     */
	    y = 0;
	    for (x = 1; x < dimX - 1; x++) {
		// calculate new values
		newFieldValues = model.function(data[oldIdx], x, y);
		// loop through dynamical variables
		for (f = 0; f < fieldCount; f++) {
		    // old value
		    old = data[oldIdx][f][x][y];
		    // diffusion coefficient for current dynamical variable
		    diffCoeff = model.diffConsts[f];
		    // calc laplace
		    laplace = 0;
		    if (diffCoeff != 0) {
			laplace = laplace(
				data[oldIdx][f][x][y], // ---
				data[oldIdx][f][x - 1][y],
				data[oldIdx][f][x][y],
				data[oldIdx][f][x + 1][y],
				data[oldIdx][f][x][y + 1]);
		    }
		    data[act][f][x][y] = old + dt
			    * (diffCoeff * laplace + newFieldValues[f]);
		}
	    }
	}
    }

    /**
     * Calculates the laplacian for the current grid point <i>(x,y)</i>.
     * Capable for one or two dimensions. In one dimension the upper and lower
     * neighbour has to be set to <b>0</b>.
     * 
     * @param xym
     *                lower neighbour of <i>(x,y)</i>.
     * @param xmy
     *                left neighbour of <i>(x,y)</i>.
     * @param xy
     *                <i>(x,y)</i> itself.
     * @param xpy
     *                right neighbour of <i>(x,y)</i>.
     * @param xyp
     *                upper neighbour of <i>(x,y)</i>.
     * @return
     */
    private double laplace(double xym, double xmy, double xy, double xpy,
	    double xyp) {
	if (dimension == 1) {
	    return (xmy + xpy + xym + xyp - 2 * xy) / (dh * dh);
	}
	if (dimension == 2) {
	    return (xmy + xpy + xym + xyp - 4 * xy) / (dh * dh);
	} else
	    return 0;
    }

    /**
     * 
     * @return current index of the calculation.
     */
    public int getActArray() {
	return act;
    }

    /**
     * @return old index of the calculation.
     */
    // private int getOldIdx() {
    // return (actArray == 0) ? 1 : 0;
    // }
    /**
     * Switches the index of current calculation.
     */
    // private void switchIdx() {
    // actArray = getOldIdx();
    // }
    /**
     * @return value of <code>{@link RDESolver#dimension}</code>.
     */
    public int getDimension() {
	return dimension;
    }

    /**
     * @return value of <code>{@link RDESolver#dt}</code>.
     */
    public double getDt() {
	return dt;
    }

    /**
     * @return value of <code>{@link RDESolver#dh}</code>.
     */
    public double getDh() {
	return dh;
    }

    /**
     * @return value of <code>{@link RDESolver#dimX}</code>.
     */
    public int getDimX() {
	return dimX;
    }

    /**
     * Sets the number of grid points in x direction to a new value. If the new
     * value is larger than the old one the <code>Array</code>
     * <code>{@link RDESolver#data}</code>
     * is filled up with the last data points.
     * 
     * @param newDimX
     *                the new number of grid points.
     */
    public void setDimX(int newDimX) {
	int oldDimX = getDimX();
	this.dimX = newDimX;
	double[][][][] data2 = new double[EULER_FORWARD_MEMORY][model
		.getFieldCount()][newDimX][dimY];
	for (int m = 0; m < EULER_FORWARD_MEMORY; m++) {
	    for (int f = 0; f < fieldCount; f++) {
		data2[m][f] = Arrays.copyOf(getData()[m][f], newDimX);
	    }
	}
	if (oldDimX < newDimX) {
	    for (int m = 0; m < EULER_FORWARD_MEMORY; m++) {
		for (int f = 0; f < fieldCount; f++) {
		    for (int x = oldDimX; x < newDimX; x++) {
			data2[m][f][x] = data[m][f][oldDimX];
		    }
		}
	    }
	}
	setData(data2);
    }

    /**
     * @return value of <code>{@link RDESolver#dimY}</code>.
     */
    public int getDimY() {
	return dimY;
    }

    /**
     * Sets the number of grid points in y direction to a new value. If the new
     * value is larger than the old one the
     * <code>Array</code> <code>{@link RDESolver#data}</code> is filled up
     * with the last data points.
     * 
     * @param newDimY
     *                the new number of grid points.
     */
    public void setDimY(int newDimY) {
	int oldDimY = getDimY();
	this.dimY = newDimY;
	double[][][][] data2 = new double[EULER_FORWARD_MEMORY][model
		.getFieldCount()][dimX][newDimY];
	for (int m = 0; m < EULER_FORWARD_MEMORY; m++) {
	    for (int f = 0; f < fieldCount; f++) {
		for (int x = 0; x < dimX; x++) {
		    data2[m][f][x] = Arrays.copyOf(getData()[m][f][x], newDimY);
		}
	    }
	}
	if (oldDimY < newDimY) {
	    for (int m = 0; m < EULER_FORWARD_MEMORY; m++) {
		for (int f = 0; f < fieldCount; f++) {
		    for (int x = 0; x < dimX; x++) {
			for (int y = oldDimY; y < newDimY; y++) {
			    data2[m][f][x][y] = data[m][f][x][oldDimY];
			}
		    }
		}
	    }
	}
	setData(data2);
    }

    /**
     * @return an <code>Array</code> of <code>{@link RDESolver#data}</code>.
     */
    public double[][][][] getData() {
	return data;
    }

    /**
     * Sets a new <code>Array</code> of <code>{@link RDESolver#data}</code>.
     * 
     * @param data
     *                the new <code>Array</code>.
     */
    public void setData(double[][][][] data) {
	this.data = data;
    }

    /**
     * @return the model with the PDEs.
     */
    public RDEModel getModel() {
	return model;
    }

    /**
     * @return the coundary condition as <code>String</code>.
     */
    public int getBoundaryCondition() {
	return bc;
    }

    /**
     * Sets the new boundary conditions.
     * 
     * @param boundaryCondition
     *                the new boundary conditions. Values are <b>noflux</b>,
     *                <b>zero</b> or <b>periodic</b>
     */
    public void setBoundaryCondition(int boundaryCondition) {
	this.bc = boundaryCondition;
    }

    private void rectangularObstacle(Rectangle rect) {

	/*
	 * the corners
	 */
	// upper left
	int oldIdx = (act == 0) ? 1 : 0;
	int x = (int) rect.getMinX();
	int y = (int) rect.getMinY();
	double[] newFieldValues = model.function(data[oldIdx], x, y);
	for (int f = 0; f < fieldCount; f++) {
	    // old value
	    double old = data[oldIdx][f][x][y];
	    // diffusion coefficient for current dynamical variable
	    double diffCoeff = model.diffConsts[f];
	    // calc laplace
	    double laplace = 0;
	    if (diffCoeff != 0) {
		laplace = laplace(
			data[oldIdx][f][x][y], // ---
			data[oldIdx][f][x][y], // ---
			data[oldIdx][f][x][y], data[oldIdx][f][x + 1][y],
			data[oldIdx][f][x][y + 1]);
	    }
	    data[act][f][x][y] = old + dt
		    * (diffCoeff * laplace + newFieldValues[f]);
	}
	// upper right
	x = (int) rect.getMaxX();
	y = (int) rect.getMinY();
	newFieldValues = model.function(data[oldIdx], x, y);
	for (int f = 0; f < fieldCount; f++) {
	    // old value
	    double old = data[oldIdx][f][x][y];
	    // diffusion coefficient for current dynamical variable
	    double diffCoeff = model.diffConsts[f];
	    // calc laplace
	    double laplace = 0;
	    if (diffCoeff != 0) {
		laplace = laplace(
			data[oldIdx][f][x][y], // ---
			data[oldIdx][f][x - 1][y], data[oldIdx][f][x][y],
			data[oldIdx][f][x][y], // ---
			data[oldIdx][f][x][y + 1]);
	    }
	    data[act][f][x][y] = old + dt
		    * (diffCoeff * laplace + newFieldValues[f]);
	}
	// lower right
	x = (int) rect.getMaxX();
	y = (int) rect.getMaxY();
	newFieldValues = model.function(data[oldIdx], x, y);
	for (int f = 0; f < fieldCount; f++) {
	    // old value
	    double old = data[oldIdx][f][x][y];
	    // diffusion coefficient for current dynamical variable
	    double diffCoeff = model.diffConsts[f];
	    // calc laplace
	    double laplace = 0;
	    if (diffCoeff != 0) {
		laplace = laplace(data[oldIdx][f][x][y - 1],
			data[oldIdx][f][x - 1][y], data[oldIdx][f][x][y],
			data[oldIdx][f][x][y], // ---
			data[oldIdx][f][x][y]); // ---
	    }
	    data[act][f][x][y] = old + dt
		    * (diffCoeff * laplace + newFieldValues[f]);
	}
	// lower left
	x = (int) rect.getMinX();
	y = (int) rect.getMaxY();
	newFieldValues = model.function(data[oldIdx], x, y);
	for (int f = 0; f < fieldCount; f++) {
	    // old value
	    double old = data[oldIdx][f][x][y];
	    // diffusion coefficient for current dynamical variable
	    double diffCoeff = model.diffConsts[f];
	    // calc laplace
	    double laplace = 0;
	    if (diffCoeff != 0) {
		laplace = laplace(
			data[oldIdx][f][x][y - 1],
			data[oldIdx][f][x][y], // ---
			data[oldIdx][f][x][y], data[oldIdx][f][x + 1][y],
			data[oldIdx][f][x][y]); // ---
	    }
	    data[act][f][x][y] = old + dt
		    * (diffCoeff * laplace + newFieldValues[f]);
	}
	/*
	 * left boundary
	 */
	x = (int) rect.getMinX();
	for (y = (int) rect.getMinY() + 1; y < (int) rect.getMaxY(); y++) {
	    // calculate new values
	    newFieldValues = model.function(data[oldIdx], x, y);
	    // loop through dynamical variables
	    for (int f = 0; f < fieldCount; f++) {
		// old value
		double old = data[oldIdx][f][x][y];
		// diffusion coefficient for current dynamical variable
		double diffCoeff = model.diffConsts[f];
		// calc laplace
		double laplace = 0;
		if (diffCoeff != 0) {
		    laplace = laplace(
			    data[oldIdx][f][x][y - 1],
			    data[oldIdx][f][x][y], // ---
			    data[oldIdx][f][x][y], data[oldIdx][f][x + 1][y],
			    data[oldIdx][f][x][y + 1]);
		}
		data[act][f][x][y] = old + dt
			* (diffCoeff * laplace + newFieldValues[f]);
	    }
	}
	/*
	 * right boundary
	 */
	x = (int) rect.getMaxX();
	for (y = (int) rect.getMinY() + 1; y < (int) rect.getMaxY(); y++) {
	    // calculate new values
	    newFieldValues = model.function(data[oldIdx], x, y);
	    // loop through dynamical variables
	    for (int f = 0; f < fieldCount; f++) {
		// old value
		double old = data[oldIdx][f][x][y];
		// diffusion coefficient for current dynamical variable
		double diffCoeff = model.diffConsts[f];
		// calc laplace
		double laplace = 0;
		if (diffCoeff != 0) {
		    laplace = laplace(data[oldIdx][f][x][y - 1],
			    data[oldIdx][f][x - 1][y], data[oldIdx][f][x][y],
			    data[oldIdx][f][x][y], // ---
			    data[oldIdx][f][x][y + 1]);
		}
		data[act][f][x][y] = old + dt
			* (diffCoeff * laplace + newFieldValues[f]);
	    }
	}
	/*
	 * lower boundary
	 */
	y = (int) rect.getMaxY();
	for (x = (int) rect.getMinX() + 1; x < (int) rect.getMaxX(); x++) {
	    // calculate new values
	    newFieldValues = model.function(data[oldIdx], x, y);
	    // loop through dynamical variables
	    for (int f = 0; f < fieldCount; f++) {
		// old value
		double old = data[oldIdx][f][x][y];
		// diffusion coefficient for current dynamical variable
		double diffCoeff = model.diffConsts[f];
		// calc laplace
		double laplace = 0;
		if (diffCoeff != 0) {
		    laplace = laplace(data[oldIdx][f][x][y - 1],
			    data[oldIdx][f][x - 1][y], data[oldIdx][f][x][y],
			    data[oldIdx][f][x + 1][y], data[oldIdx][f][x][y]); // ---
		}
		data[act][f][x][y] = old + dt
			* (diffCoeff * laplace + newFieldValues[f]);
	    }
	}
	/*
	 * upper boundary
	 */
	y = (int) rect.getMinY();
	for (x = (int) rect.getMinX() + 1; x < (int) rect.getMaxX(); x++) {
	    // calculate new values
	    newFieldValues = model.function(data[oldIdx], x, y);
	    // loop through dynamical variables
	    for (int f = 0; f < fieldCount; f++) {
		// old value
		double old = data[oldIdx][f][x][y];
		// diffusion coefficient for current dynamical variable
		double diffCoeff = model.diffConsts[f];
		// calc laplace
		double laplace = 0;
		if (diffCoeff != 0) {
		    laplace = laplace(
			    data[oldIdx][f][x][y], // ---
			    data[oldIdx][f][x - 1][y], data[oldIdx][f][x][y],
			    data[oldIdx][f][x + 1][y],
			    data[oldIdx][f][x][y + 1]);
		}
		data[act][f][x][y] = old + dt
			* (diffCoeff * laplace + newFieldValues[f]);
	    }
	}
    }

    private Rectangle[] randomRect(int number, int size) {
	Rectangle[] rect = new Rectangle[number];
	Random r = new Random();
	for (int i = 0; i < number; i++) {
	    rect[i] = new Rectangle(r.nextInt(dimX - size), r.nextInt(dimY
		    - size), size, size);
	}
	return rect;
    }

}
