package pixelshade.geniusloci.model;

/**
 * Created by pixelshade on 8.9.2015.
 */
public class GhostEntry {
    public String name;
    public String content;
    public Location location;

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
        this.location = new Location(longitude,latitude);
    }
}
