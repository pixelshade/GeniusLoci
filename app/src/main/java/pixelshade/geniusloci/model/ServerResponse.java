package pixelshade.geniusloci.model;

/**
 * Created by pixelshade on 07/09/15.
 */
public class ServerResponse {
    public boolean success;
    public int status;
    public UploadedEntry data;

    public static class UploadedEntry{
        public String id;
        public String name;
        public String content;
        public String type;
        public boolean animated;
        public int width;
        public int height;
        public int size;
        public int views;
        public int bandwidth;
        public String vote;
        public boolean favorite;
        public String account_url;
        public String deletehash;
        public String link;

        @Override public String toString() {
            return "UploadedImage{" +
                    "id='" + id + '\'' +
                    ", title='" + name + '\'' +
                    ", description='" + content + '\'' +
                    ", type='" + type + '\'' +
                    ", animated=" + animated +
                    ", width=" + width +
                    ", height=" + height +
                    ", size=" + size +
                    ", views=" + views +
                    ", bandwidth=" + bandwidth +
                    ", vote='" + vote + '\'' +
                    ", favorite=" + favorite +
                    ", account_url='" + account_url + '\'' +
                    ", deletehash='" + deletehash + '\'' +
                    ", link='" + link + '\'' +
                    '}';
        }
    }

    @Override public String toString() {
        return "ImageResponse{" +
                "success=" + success +
                ", status=" + status +
                ", data=" + data.toString() +
                '}';
    }
}
