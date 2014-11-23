import java.awt.Desktop;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import javax.swing.JOptionPane;

import org.junit.Test;
import org.scribe.model.Token;
import org.scribe.model.Verifier;

import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.REST;
import com.flickr4java.flickr.RequestContext;
import com.flickr4java.flickr.auth.Auth;
import com.flickr4java.flickr.auth.AuthInterface;
import com.flickr4java.flickr.auth.Permission;
import com.flickr4java.flickr.collections.Collection;
import com.flickr4java.flickr.collections.CollectionsInterface;
import com.flickr4java.flickr.people.User;
import com.flickr4java.flickr.photosets.Photosets;
import com.flickr4java.flickr.photosets.PhotosetsInterface;
import com.flickr4java.flickr.test.TestInterface;


public class FlickrTest {
	
	//@Test
	public void test0() throws Exception {
		/*PropertiesHelper.configureLogger();
		String authToken = PropertiesHelper.getAuthToken();
		
		PropertiesHelper.setAuthToken("aaaa");
		
		PropertiesHelper.savePropertiesToFile();*/
		
		/*PropertiesConfiguration pc = new PropertiesConfiguration("FlickrSorter.properties");
		pc.setProperty("auth.token.secret", "aaaa");
		pc.save();*/
		
		return;
	}

	@Test
	public void test1() throws Exception {
		
		// FlickrSorter API and Shared Secret
		String apiKey = "5ad8b6ae3a72a87ca9b5a11de70a25d3";
		String sharedSecret = "1c86183e92373db0";
		Flickr flickr = new Flickr(apiKey, sharedSecret, new REST());
		
		// Authentication token (dynamically retrieve from Flickr OAuth 1st time, store it in properties after)
		String authToken = "72157649005252838-6d62858c0df789a1";
		String authTokenSecret = "631f055a2243323e";
		
		// 1st time
		Auth auth = firstAuthenticate(flickr);
		authToken = auth.getToken();
		authTokenSecret = auth.getTokenSecret();
		
		// Other times
		/*Auth auth = new Auth();
		auth.setToken(authToken);
		auth.setTokenSecret(authTokenSecret);*/
		
		// Set authentication token in request context for all calls to Flickr API
		RequestContext.getRequestContext().setAuth(auth);
		//flickr.setAuth(auth); => DOES NOT WORK !
		
		
		// Calls to Flickr API
		try {
			TestInterface testInterface = flickr.getTestInterface();
			User user = testInterface.login();
		}
		catch (FlickrException ex) {
			if (ex.getErrorCode().equals("98")) {
				// Invalid auth token exception
				// => regen token
				return;
			}
		}		
		
		CollectionsInterface collectionInterface = flickr.getCollectionsInterface();
		List<Collection> cols = collectionInterface.getTree(null, null);
		
		PhotosetsInterface psInterface = flickr.getPhotosetsInterface();
		Photosets photosets = psInterface.getList(null);
		
		return;
	}
	
	public Auth firstAuthenticate(Flickr flickr) throws MalformedURLException, FlickrException {
		AuthInterface authInterface = flickr.getAuthInterface();
		Token requestToken = authInterface.getRequestToken();
		String url = authInterface.getAuthorizationUrl(requestToken, Permission.WRITE);		
		openWebpage(new URL(url));

		String inputVerifierValue = JOptionPane.showInputDialog("Please input verifier value: ");
		
		Verifier verifier = new Verifier(inputVerifierValue);
		Token accessToken = authInterface.getAccessToken(requestToken, verifier);
		
		Auth auth = authInterface.checkToken(accessToken);
		
		return auth;
	}
	
	public static void openWebpage(URI uri) {
	    Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
	    if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
	        try {
	            desktop.browse(uri);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	}

	public static void openWebpage(URL url) {
	    try {
	        openWebpage(url.toURI());
	    } catch (URISyntaxException e) {
	        e.printStackTrace();
	    }
	}
}