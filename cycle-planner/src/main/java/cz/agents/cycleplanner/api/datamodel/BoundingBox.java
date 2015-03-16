package cz.agents.cycleplanner.api.datamodel;

public class BoundingBox {

	private final int leftE6;
	private final int topE6;
	private final int rightE6;
	private final int bottomE6;

	@SuppressWarnings("unused")
	private BoundingBox() {
		this.leftE6 = Integer.MAX_VALUE;
		this.topE6 = Integer.MAX_VALUE;
		this.rightE6 = Integer.MAX_VALUE;
		this.bottomE6 = Integer.MAX_VALUE;
	}

	public BoundingBox(int leftE6, int topE6, int rightE6, int bottomE6) {
		super();
		this.leftE6 = leftE6;
		this.topE6 = topE6;
		this.rightE6 = rightE6;
		this.bottomE6 = bottomE6;
	}

	public int getLeftE6() {
		return leftE6;
	}

	public int getTopE6() {
		return topE6;
	}

	public int getRightE6() {
		return rightE6;
	}

	public int getBottomE6() {
		return bottomE6;
	}

	@Override
	public String toString() {
		return "BoundingBox [leftE6=" + leftE6 + ", topE6=" + topE6 + ", rightE6=" + rightE6 + ", bottomE6=" + bottomE6
				+ "]";
	}
}
