package pixelshade.geniusloci.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pixelshade on 10.9.2015.
 */
public class Location {
    public String type = "Point";
    public List<Double> coordinates;

    public Location(double longtitude, double latitude){
        coordinates = new ArrayList<>();
        coordinates.add(longtitude);
        coordinates.add(latitude);
    }
}
