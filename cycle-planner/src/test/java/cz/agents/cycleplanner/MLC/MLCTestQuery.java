package cz.agents.cycleplanner.MLC;

public class MLCTestQuery {
	
		private long origin;
		private long destination;

		public MLCTestQuery(long origin, long destination) {
			super();
			this.origin = origin;
			this.destination = destination;
		}

		public long getOrigin() {
			return origin;
		}

		public long getDestination() {
			return destination;
		}
	
}
