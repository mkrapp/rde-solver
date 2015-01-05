package models;

import rdeapprox.RDESolver;

/**
 * The abstract <code>{@link RDEModel}</code> class defines the method which
 * must be implemeted in the different PDEs. They are necessary to determine
 * numerical solutions using the <code>{@link RDESolver}</code> class.
 * 
 * @author Mario Krapp
 * @author Stefan Zeller
 * 
 */
public abstract class RDEModel {

	/**
	 * Number of coupled PDEs which shall be solved numerically.
	 */
	public int fieldCount;

	/**
	 * An <code>Array</code> object of the diffusion constants for the
	 * different PDEs.
	 */
	public double[] diffConsts;

	/**
	 * Creates a new <code>{@link RDEModel}</code> object which contains the
	 * system of PDEs with their different diffusion constants.
	 * 
	 * @param fieldCount
	 *            Number of coupled equations.
	 * @param diffConsts
	 *            <code>Array</code> of the diffusion constants.
	 */
	public RDEModel(int fieldCount, double[] diffConsts) {
		this.fieldCount = fieldCount;
		this.diffConsts = diffConsts;
	}

	/**
	 * @return Number of coupled equations.
	 */
	public int getFieldCount() {
		return fieldCount;
	}

	/**
	 * @return <code>Array</code> of the diffusion constants.
	 */
	public double[] getDiffConsts() {
		return diffConsts;
	}

	/**
	 * The system of coupled PDEs which shall be solved numerically on a grid
	 * with length <i>x</i> and width <i>y</i>.
	 * 
	 * @param data
	 *            <code>Array</code> of data for each variable at each point
	 *            <i>(x,y)</i>.
	 * @param x
	 *            x-coordinate of point <i>(x,y)</i>.
	 * @param y
	 *            y-coordinate point <i>(x,y)</i>.
	 * @return function <code>Array</code> for each variable containing the
	 *         equation for each grid point <i>(x,y)</i>.
	 */
	public abstract double[] function(double[][][] data, int x, int y);

}