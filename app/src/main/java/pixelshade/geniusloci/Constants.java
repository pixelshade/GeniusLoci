package pixelshade.geniusloci;

/**
 * Created by AKiniyalocts on 2/23/15.
 */
public class Constants {
    /*
      Logging flag
     */
    public static final boolean LOGGING = true;

    /*
      Your imgur client id. You need this to upload to imgur.

      More here: https://api.imgur.com/
     */
    // TODO ADD CLIENT ID AND SECRET
    public static final String MY_IMGUR_CLIENT_ID = "6f8db26a3dfcb37";
    public static final String MY_IMGUR_CLIENT_SECRET = "e424653455ea1d2a9578ba03e0b72b370fef3f9d";

    /*
      Redirect URL for android.
     */
    public static final String MY_IMGUR_REDIRECT_URL = "http://www.nocturnal.cf";
    public static final String SERVER_URL = "http://nocturnal.cf:1337/";

    /*
      Client Auth
     */
    public static String getClientAuth() {
        return "Client-ID " + MY_IMGUR_CLIENT_ID;
    }

}
