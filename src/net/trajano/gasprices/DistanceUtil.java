package net.trajano.gasprices;

public final class DistanceUtil {
	/**
	 * Radius of the earth in km. Value obtained from Google.
	 */
	public static final double RADIUS_OF_THE_EARTH_IN_KM = 6378.1;

	/**
	 * Calculate the distance between two points on the earth. This uses the
	 * ‘haversine’ formula to calculate the great-circle distance between two
	 * points – that is, the shortest distance over the earth’s surface – giving
	 * an ‘as-the-crow-flies’ distance between the points (ignoring any hills,
	 * of course!).
	 * 
	 * @see http://www.movable-type.co.uk/scripts/latlong.html
	 * @param lat1
	 * @param long1
	 * @param lat2
	 * @param long2
	 * @return
	 */
	public static double distance(final double lat1, final double long1,
			final double lat2, final double long2) {
		final double dLat = Math.toRadians(lat2 - lat1);
		final double dLon = Math.toRadians(long2 - long1);
		final double rlat1 = Math.toRadians(lat1);
		final double rlat2 = Math.toRadians(lat2);

		final double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
				+ Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(rlat1)
				* Math.cos(rlat2);
		final double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		return RADIUS_OF_THE_EARTH_IN_KM * c;
	}

	/**
	 * Prevent instantiation of utility class.
	 */
	private DistanceUtil() {
	}
}