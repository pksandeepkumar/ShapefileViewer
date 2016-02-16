package com.texus.shapefileviewer.utility;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class PolygoneInsideChecker {

	private double PI = 3.14159;
	private double TWOPI = 3.14159 * 2;
	Context context;

    ArrayList<LatLng> points;
    LatLng pointToCheck;

	public PolygoneInsideChecker(Context context,ArrayList<LatLng> points, LatLng pointToCheck) {
		this.context = context;
		this.points = points;
	}

	public boolean checkPointsInside() {

		if (insidePolygon(points, pointToCheck)) {
			Toast.makeText(context, "You are inside the perimeter of village", Toast.LENGTH_LONG)
					.show();
			return true;
		} else {
			Toast.makeText(context, "You are outside the perimeter of village", Toast.LENGTH_LONG)
					.show();
			return false;
		}
	}


    public class GeoPoints {

        public double latitude = 0;
        public double longitude = 0;

    }

	public boolean insidePolygon(ArrayList<LatLng> points, LatLng p) {
		int n = points.size();
		Log.d("points",""+n);
		int i;
		double angle = 0;
		GeoPoints p1 = new GeoPoints();
		GeoPoints p2 = new GeoPoints();
		for (i = 0; i < n-1; i++) {

			p1.latitude = points.get(i).latitude - p.latitude;
			p1.longitude = points.get(i).longitude - p.longitude;
			p2.latitude = points.get((i + 1) % n).latitude - p.latitude;
			p2.longitude = points.get((i + 1) % n).longitude - p.longitude;
			angle += Angle2D(p1.latitude, p1.longitude, p2.latitude,
					p2.longitude);
		}
		if (Math.abs(angle) < PI)
			return (false);
		else
			return (true);
	}

	/*
	 * Return the angle between two vectors on a plane The angle is from vector
	 * 1 to vector 2, positive anti clockwise The result is between -pi -> pi
	 */
	double Angle2D(double x1, double y1, double x2, double y2) {
		double dtheta, theta1, theta2;

		theta1 = Math.atan2(y1, x1);
		theta2 = Math.atan2(y2, x2);
		dtheta = theta2 - theta1;

		while (dtheta > PI)
			dtheta -= TWOPI;
		while (dtheta < -PI)
			dtheta += TWOPI;

		return (dtheta);
	}

}
