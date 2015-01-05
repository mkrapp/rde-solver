package rdeapprox;

/**
 * The <code>{@link InitCondition}</code> class provides methods to set
 * initial condition for the <code>{@link RDESolver}</code> object.
 * 
 * <p>
 * For the steady state or other constant values for the initial conditions use
 * <code>{@link InitCondition#steadyState(int, double)}</code>.
 * 
 * <p>
 * For initializing a target pattern use
 * <code>{@link InitCondition#targetPattern(int, int, int, int, double)}</code>.
 * 
 * @author Mario Krapp
 */
public class InitCondition {

    /**
         * The <code>{@link RDESolver}</code> object for which the initial
         * conditions will be set.
         */
    private RDESolver rde;
    
    private double[][][][] data;

    /**
         * Creates an new <code>{@link InitCondition}</code> object.
         * 
         * @param rde
         *                The <code>{@link RDESolver}</code> object for which
         *                the initial conditions will be set.
         */
    public InitCondition(RDESolver rde) {
	this.rde = rde;
	data = rde.getData();
    }

    /**
         * <p>
         * Creates a traget pattern at point <i>(posX,posY)</i>.
         * 
         * <p>
         * For one dimensional calculation the value for <code>posY</code> can
         * be chosen arbitrarily and does not affect calculation.
         * 
         * <p>
         * The same holds for zero dimensional calculations. Here
         * <code>posX</code> and <code>posY</code> can be chosen
         * arbitrarily.
         * 
         * @param posX
         *                Position of target pattern in x-direction.
         * @param posy
         *                Position of target pattern in y-direction.
         * @param rad
         *                Radius of the target pattern.
         * @param field
         *                Variable for which the target pattern shall be set.
         * @param value
         *                Value of the target pattern.
         */
    public void targetPattern(int posX, int posY, int rad, int field,
	    double value) {
	int dimension = rde.getDimension();
	if (posX < rad) {
	    posX = rad;
	}
	if (posY < rad) {
	    posY = rad;
	}
	if (dimension == 2) {
	    for (int i = posX - rad; i < posX + rad; i++) {
		for (int j = posY - rad; j < posY + rad; j++) {
		    data[rde.getActArray()][field][i][j] = value;
		}
	    }
	}
	if (dimension == 1) {
	    for (int i = posX - rad; i < posX + rad; i++)
		data[rde.getActArray()][field][i][0] = value;
	}
	if (dimension == 0) {
	    data[rde.getActArray()][field][0][0] = value;
	}
    }

    /**
         * @param width
         *                The width of the planar wave.
         * @param length
         *                The Length of the planar wave.
         * @param offsetX
         *                The Offset of the wave width in x-direction
         * @param offsetY
         *                The Offset of the wave length in y-direction
         * @param field
         *                Variable for which the target pattern shall be set.
         * @param value
         *                Value of the planar wave.
         */
    public void planarWave(int width, int length, int offsetX, int offsetY,
	    int field, double value) {
	int dimension = rde.getDimension();
	if (dimension == 0) {
	    targetPattern(0, 0, 0, field, value);
	} else if (dimension == 1) {
	    for (int i = offsetX; i < offsetX + width; i++) {
		data[rde.getActArray()][field][i][0] = value;
	    }
	} else if (dimension == 2) {
	    for (int i = offsetX; i < offsetX + width; i++) {
		for (int j = 0; j < length; j++) {
		    data[rde.getActArray()][field][i][j] = value;
		}
	    }
	}
    }

    /**
         * Set the complete data of the <code>{@link RDESolver}</code> object
         * for a specified variable to the steady state or to an arbitrary
         * constant value.
         * 
         * @param field
         *                Variable for which the value has to be set.
         * @param value
         *                Value which has to be set.
         */
    public void steadyState(int field, double value) {
		int dimension = rde.getDimension();
		int act = rde.getActArray();
		int dimX = -1;
		int dimY = -1;
		if (dimension == 0) {
			data[act][field][0][0] = value;
		} else if (dimension == 1) {
			dimX = rde.getDimX();
			for (int i = 0; i < dimX; i++) {
				data[act][field][i][0] = value;
			}
		} else if (dimension == 2) {
			dimX = rde.getDimX();
			dimY = rde.getDimY();
			for (int i = 0; i < dimX; i++) {
				for (int j = 0; j < dimY; j++) {
					data[act][field][i][j] = value;
				}
			}
		}
	}
}
