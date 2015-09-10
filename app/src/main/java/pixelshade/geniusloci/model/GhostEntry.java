package pixelshade.geniusloci.model;

import org.json.JSONObject;

/**
 * Created by pixelshade on 8.9.2015.
 */
public class GhostEntry {
    public String name;
    public String content;
    public Coordinates coordinates;

    public String getName() {
        return name;
    }

    public String getContent() {
        return content;
    }

    public GhostEntry(){}

    public GhostEntry(String name, String content, double longitude, double latitude){
        this.name = name;
        this.content = content;
        this.coordinates = new Coordinates(longitude,latitude);
    }
}
