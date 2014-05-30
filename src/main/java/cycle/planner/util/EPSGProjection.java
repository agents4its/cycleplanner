package cycle.planner.util;

import org.apache.log4j.Logger;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.referencing.CRS;
import org.geotools.referencing.operation.DefaultMathTransformFactory;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

import eu.superhub.wp5.wp5common.GPSLocation;
import eu.superhub.wp5.wp5common.Location;

public class EPSGProjection {

	private static final Logger log = Logger.getLogger(EPSGProjection.class);
	private final int SOURCE_SRID = 4326;
	private final int targetSrid;
	private MathTransform transform;
	private GeometryFactory geometryFactory;

	public EPSGProjection(int targetSrid) throws NoSuchAuthorityCodeException, FactoryException, TransformException {

		super();
		this.targetSrid = targetSrid;
		geometryFactory = JTSFactoryFinder.getGeometryFactory();
		CoordinateReferenceSystem sourceCRS;
		CoordinateReferenceSystem targetCRS;
		DefaultMathTransformFactory defaultMathTransformFactory = new DefaultMathTransformFactory();

		switch (this.targetSrid) {
		case 2065:
			// transformation for Prague
			transform = defaultMathTransformFactory
					.createFromWKT("CONCAT_MT[PARAM_MT[\"Ellipsoid_To_Geocentric\", PARAMETER[\"dim\", 2], PARAMETER[\"semi_major\", 6378137.0], PARAMETER[\"semi_minor\", 6356752.314245179]], PARAM_MT[\"Affine\", PARAMETER[\"num_row\", 4], PARAMETER[\"num_col\", 4], PARAMETER[\"elt_0_0\", 0.9999964393029201], PARAMETER[\"elt_0_1\", 0.00002550614336127664], PARAMETER[\"elt_0_2\", -0.00000769334768418135], PARAMETER[\"elt_0_3\", -570.7965929492847], PARAMETER[\"elt_1_0\", -0.00002550577049649799], PARAMETER[\"elt_1_1\", 0.9999964387749789], PARAMETER[\"elt_1_2\", 0.00002423109773107734], PARAMETER[\"elt_1_3\", -85.69635026124624], PARAMETER[\"elt_2_0\", 0.00000769458375324277], PARAMETER[\"elt_2_1\", -0.00002423070524576312], PARAMETER[\"elt_2_2\", 0.9999964393663378], PARAMETER[\"elt_2_3\", -462.8006676357079]], PARAM_MT[\"Geocentric_To_Ellipsoid\", PARAMETER[\"dim\", 2], PARAMETER[\"semi_major\", 6377397.155], PARAMETER[\"semi_minor\", 6356078.962818189]], PARAM_MT[\"Affine\", PARAMETER[\"num_row\", 3], PARAMETER[\"num_col\", 3], PARAMETER[\"elt_0_2\", 17.666666666666668]], PARAM_MT[\"Krovak\", PARAMETER[\"semi_major\", 6377397.155], PARAMETER[\"semi_minor\", 6356078.962818189], PARAMETER[\"latitude_of_center\", 49.5], PARAMETER[\"longitude_of_center\", 42.5], PARAMETER[\"azimuth\", 30.288139722222223], PARAMETER[\"pseudo_standard_parallel_1\", 78.5], PARAMETER[\"scale_factor\", 0.9999], PARAMETER[\"false_easting\", 0.0], PARAMETER[\"false_northing\", 0.0]], PARAM_MT[\"Affine\", PARAMETER[\"num_row\", 3], PARAMETER[\"num_col\", 3], PARAMETER[\"elt_0_0\", -1.0], PARAMETER[\"elt_1_1\", -1.0]]]");
			break;

		case 2392:
			// transformation for Helsinki
			transform = defaultMathTransformFactory
					.createFromWKT("CONCAT_MT[PARAM_MT[\"Ellipsoid_To_Geocentric\", PARAMETER[\"dim\", 2], PARAMETER[\"semi_major\", 6378137.0], PARAMETER[\"semi_minor\", 6356752.314245179]], PARAM_MT[\"Affine\", PARAMETER[\"num_row\", 4], PARAMETER[\"num_col\", 4], PARAMETER[\"elt_0_0\", 0.9999985039549376], PARAMETER[\"elt_0_1\", -0.00000667098733689448], PARAMETER[\"elt_0_2\", -0.00000167275997079853], PARAMETER[\"elt_0_3\", 96.06110274723028], PARAMETER[\"elt_1_0\", 0.00000667106519966996], PARAMETER[\"elt_1_1\", 0.9999985034159683], PARAMETER[\"elt_1_2\", 0.00002327585883764965], PARAMETER[\"elt_1_3\", 82.4313513810777], PARAMETER[\"elt_2_0\", 0.00000167244942245345], PARAMETER[\"elt_2_1\", -0.00002327588115366278], PARAMETER[\"elt_2_2\", 0.9999985034576734], PARAMETER[\"elt_2_3\", 121.75105986598678]], PARAM_MT[\"Geocentric_To_Ellipsoid\", PARAMETER[\"dim\", 2], PARAMETER[\"semi_major\", 6378388.0], PARAMETER[\"semi_minor\", 6356911.9461279465]], PARAM_MT[\"Transverse_Mercator\", PARAMETER[\"semi_major\", 6378388.0], PARAMETER[\"semi_minor\", 6356911.9461279465], PARAMETER[\"central_meridian\", 24.0], PARAMETER[\"latitude_of_origin\", 0.0], PARAMETER[\"scale_factor\", 1.0], PARAMETER[\"false_easting\", 2500000.0], PARAMETER[\"false_northing\", 0.0]]]");
			break;

		case 28992:
			// transformation for Terneuzen
			transform = defaultMathTransformFactory
					.createFromWKT("CONCAT_MT[PARAM_MT[\"Ellipsoid_To_Geocentric\", PARAMETER[\"dim\", 2], PARAMETER[\"semi_major\", 6378137.0], PARAMETER[\"semi_minor\", 6356752.314245179]], PARAM_MT[\"Affine\", PARAMETER[\"num_row\", 4], PARAMETER[\"num_col\", 4], PARAMETER[\"elt_0_0\", 0.9999959187315419], PARAMETER[\"elt_0_1\", -0.00000906766634627204], PARAMETER[\"elt_0_2\", -0.00000170037517423925], PARAMETER[\"elt_0_3\", -565.2333478609597], PARAMETER[\"elt_1_0\", 0.00000906765963822141], PARAMETER[\"elt_1_1\", 0.9999959187305425], PARAMETER[\"elt_1_2\", -0.00000197250736834444], PARAMETER[\"elt_1_3\", -50.012702763008114], PARAMETER[\"elt_2_0\", 0.00000170041094616975], PARAMETER[\"elt_2_1\", 0.00000197247653103614], PARAMETER[\"elt_2_2\", 0.9999959188098742], PARAMETER[\"elt_2_3\", -465.65715933716746]], PARAM_MT[\"Geocentric_To_Ellipsoid\", PARAMETER[\"dim\", 2], PARAMETER[\"semi_major\", 6377397.155], PARAMETER[\"semi_minor\", 6356078.962818189]], PARAM_MT[\"Oblique_Stereographic\", PARAMETER[\"semi_major\", 6377397.155], PARAMETER[\"semi_minor\", 6356078.962818189], PARAMETER[\"central_meridian\", 5.387638888888891], PARAMETER[\"latitude_of_origin\", 52.15616055555556], PARAMETER[\"scale_factor\", 0.9999079], PARAMETER[\"false_easting\", 155000.0], PARAMETER[\"false_northing\", 463000.0]]]");
			break;

		case 3003:
			// transformation for Milan
			transform = defaultMathTransformFactory
					.createFromWKT("CONCAT_MT[PARAM_MT[\"Ellipsoid_To_Geocentric\", PARAMETER[\"dim\", 2], PARAMETER[\"semi_major\", 6378137.0], PARAMETER[\"semi_minor\", 6356752.314245179]], PARAM_MT[\"Affine\", PARAMETER[\"num_row\", 4], PARAMETER[\"num_col\", 4], PARAMETER[\"elt_0_0\", 1.0000116799244423], PARAMETER[\"elt_0_1\", 0.00000346154353902656], PARAMETER[\"elt_0_2\", 0.00001414219655099033], PARAMETER[\"elt_0_3\", 104.10152584966808], PARAMETER[\"elt_1_0\", -0.0000034616766888089], PARAMETER[\"elt_1_1\", 1.0000116801022803], PARAMETER[\"elt_1_2\", 0.00000470754687304821], PARAMETER[\"elt_1_3\", 49.1002597371927], PARAMETER[\"elt_2_0\", -0.00001414216395964834], PARAMETER[\"elt_2_1\", -0.00000470764478133306], PARAMETER[\"elt_2_2\", 1.0000116799142638], PARAMETER[\"elt_2_3\", 9.89841228652425]], PARAM_MT[\"Geocentric_To_Ellipsoid\", PARAMETER[\"dim\", 2], PARAMETER[\"semi_major\", 6378388.0], PARAMETER[\"semi_minor\", 6356911.9461279465]], PARAM_MT[\"Transverse_Mercator\", PARAMETER[\"semi_major\", 6378388.0], PARAMETER[\"semi_minor\", 6356911.9461279465], PARAMETER[\"central_meridian\", 9.0], PARAMETER[\"latitude_of_origin\", 0.0], PARAMETER[\"scale_factor\", 0.9996], PARAMETER[\"false_easting\", 1500000.0], PARAMETER[\"false_northing\", 0.0]]]");
			break;

		case 2062:
			// transformation for Barcelona
			transform = defaultMathTransformFactory
					.createFromWKT("CONCAT_MT[PARAM_MT[\"Molodenski\", PARAMETER[\"dim\", 2], PARAMETER[\"dx\", 0.0], PARAMETER[\"dy\", 0.0], PARAMETER[\"dz\", 0.0], PARAMETER[\"src_semi_major\", 6378137.0], PARAMETER[\"src_semi_minor\", 6356752.314245179], PARAMETER[\"tgt_semi_major\", 6378298.3], PARAMETER[\"tgt_semi_minor\", 6356657.142669561]], PARAM_MT[\"Affine\", PARAMETER[\"num_row\", 3], PARAMETER[\"num_col\", 3], PARAMETER[\"elt_0_2\", 3.68793888888889]], PARAM_MT[\"Lambert_Conformal_Conic_1SP\", PARAMETER[\"semi_major\", 6378298.3], PARAMETER[\"semi_minor\", 6356657.142669561], PARAMETER[\"central_meridian\", 0.0], PARAMETER[\"latitude_of_origin\", 40.0], PARAMETER[\"scale_factor\", 0.9988085293], PARAMETER[\"false_easting\", 600000.0], PARAMETER[\"false_northing\", 600000.0]]]");
			break;

		default:
			sourceCRS = CRS.decode("EPSG:" + this.SOURCE_SRID, true);
			targetCRS = CRS.decode("EPSG:" + this.targetSrid, true);
			transform = CRS.findMathTransform(sourceCRS, targetCRS);
			log.debug(this.targetSrid + ":::" + transform.toWKT());
			break;
		}
	}

	public Location getProjectedLocation(Location location) throws MismatchedDimensionException, TransformException {

		Coordinate coordinate = new Coordinate(location.getLongitude(), location.getLatitude());
		Point point = geometryFactory.createPoint(coordinate);

		Point pointProjected = (Point) JTS.transform(point, transform);
		Location locationProjected = new GPSLocation(location.getLatitudeE6(), location.getLongitudeE6(),
				pointProjected.getCoordinate().y, pointProjected.getCoordinate().x);

		// log.debug(coordinate);
		// log.debug(point);
		// log.debug(pointProjected);

		return locationProjected;
	}

	public GPSLocation getProjectedGPSLocation(GPSLocation location) {

		GPSLocation gpsLocationProjected = null;

		try {
			Location locationProjected = this.getProjectedLocation(location);
			gpsLocationProjected = new GPSLocation(locationProjected.getLatitudeE6(),
					locationProjected.getLongitudeE6(), locationProjected.getProjectedLatitude(),
					locationProjected.getProjectedLongitude());
		} catch (MismatchedDimensionException | TransformException e) {
			gpsLocationProjected = location;
			log.warn("location " + location + " could not be converted from 4326 to " + targetSrid);
			e.printStackTrace();
		}

		return gpsLocationProjected;
	}

	@Override
	public String toString() {
		return "EPSGProjection [SOURCE_SRID=" + SOURCE_SRID + ", targetSrid=" + targetSrid + "]";
	}
}
