package controlling;

/**
 * <p>
 * The <code>{@link PulseParams}</code> class puts certain parameters of a
 * generated pulse into an object. These parameters are <i>maximum value</i>
 * and <i>maximum position</i>, the <i>minimum value</i> and <i>minimum
 * position</i> and the <i>left</i> and <i>right branch position</i>
 * respectivly. The latter means the position of the points where the voltage
 * exceeds a certain threshold.
 * 
 * <p>
 * To get this pulse parameters you need to implement a method which returns a
 * <code>{@link PulseParams}</code> object. See
 * <code>{@link RDEController#getPulseParams(int)}</code> for instance.
 * 
 * @author Mario Krapp
 * @author Stefan Zeller
 * 
 */
public class PulseParams {

	/**
	 * The maximum value of the pulse profile.
	 */
	private double maximum_;

	/**
	 * The minimum value of the pulse profile.
	 */
	private double minimum_;

	/**
	 * The position where the maximum is located.
	 */
	private int maxPosition_;

	/**
	 * The position where the minimum is located.
	 */
	private int minPosition_;

	/**
	 * The left position (from maximum's point of view) where the voltage
	 * exceeds a threshold.
	 */
	private int leftBranchPos_;

	/**
	 * The right position (from maximum's point of view) where the voltage
	 * exceeds a threshold.
	 */
	private int rightBranchPos_;

	/**
	 * Creates a <code>{@link PulseParams}</code> object, which contains
	 * nothing more than its parameters.
	 * 
	 * @param maximum
	 *            The maximum value of the pulse profile.
	 * @param minimum
	 *            The minimum value of the pulse profile.
	 * @param maxPosition
	 *            The position where the maximum is located.
	 * @param minPosition
	 *            The position where the minimum is located.
	 * @param leftBranchPos
	 *            The left position (from maximum's point of view) where the
	 *            voltage exceeds a threshold.
	 * @param rightBranchPos
	 *            The right position (from maximum's point of view) where the
	 *            voltage exceeds a threshold.
	 */
	public PulseParams(double maximum, double minimum, int maxPosition,
			int minPosition, int leftBranchPos, int rightBranchPos) {
		super();
		this.maximum_ = maximum;
		this.minimum_ = minimum;
		this.maxPosition_ = maxPosition;
		this.minPosition_ = minPosition;
		this.leftBranchPos_ = leftBranchPos;
		this.rightBranchPos_ = rightBranchPos;
	}

	/**
	 * @return The left position where the voltage exceeds a threshold.
	 */
	public int getLeftBranchPos() {
		return leftBranchPos_;
	}

	/**
	 * @return The maximum value of the pulse profile.
	 */
	public double getMaximum() {
		return maximum_;
	}

	/**
	 * @return The position where the maximum is located.
	 */
	public int getMaxPosition() {
		return maxPosition_;
	}

	/**
	 * @return The position where the minimum is located.
	 */
	public int getMinPosition() {
		return minPosition_;
	}

	/**
	 * @return The minimum value of the pulse profile.
	 */
	public double getMinimum() {
		return minimum_;
	}

	/**
	 * @return The right position where the voltage exceeds a threshold.
	 */
	public int getRightBranchPos() {
		return rightBranchPos_;
	}

}
