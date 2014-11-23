package org.ahuh.flickr.sorter.service;

import java.net.URL;

import org.ahuh.flickr.sorter.constants.FlickrSorterConstants;
import org.ahuh.flickr.sorter.exception.AppException;
import org.ahuh.flickr.sorter.helper.PropertiesHelper;
import org.apache.log4j.Logger;
import org.scribe.model.Token;
import org.scribe.model.Verifier;

import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.RequestContext;
import com.flickr4java.flickr.auth.Auth;
import com.flickr4java.flickr.auth.AuthInterface;
import com.flickr4java.flickr.auth.Permission;
import com.flickr4java.flickr.people.User;
import com.flickr4java.flickr.test.TestInterface;

/**
 * Authentication Service
 * @author Julien
 *
 */
public class AuthenticationService {
	
	/**
	 * Logger
	 */
	private static Logger log = Logger.getLogger(AuthenticationService.class);

	private Flickr flickr;
	private TestInterface testInterface;
	private AuthInterface authInterface;
	private Token requestToken;
	private Token accessToken;
	
	/**
	 * Constructor
	 * @param flicker
	 */
	public AuthenticationService(Flickr flickr) {		
		this.flickr = flickr;
		this.testInterface = flickr.getTestInterface();
		this.authInterface = flickr.getAuthInterface();
	}
	
	/**
	 * Destroyer
	 */
	public void destroy() {
		this.flickr = null;
		this.testInterface = null;
		this.authInterface = null;
		this.requestToken = null;
		this.accessToken = null;
	}
		
	/**
	 * Set the authentication token in the request context for all Flickr REST API calls
	 * @param authToken
	 * @param authTokenSecret
	 */
	public static void setAuthTokenInContext(String authToken, String authTokenSecret) {
		Auth auth = new Auth();
		auth.setToken(authToken);
		auth.setTokenSecret(authTokenSecret);
		RequestContext.getRequestContext().setAuth(auth);
	}
	
	/**
	 * Check authentication to Flickr API
	 * @return true : authentication OK ; false : new authentication token required
	 * @throws Exception 
	 */
	public boolean checkAuth() throws AppException {
		try {
			String authToken = PropertiesHelper.getAuthToken();
			String authTokenSecret = PropertiesHelper.getAuthTokenSecret();
			
			if (authToken.isEmpty() || authTokenSecret.isEmpty()) {
				// No authentication token in properties
				return false;
			}
			else {
				// Put access token in context
				setAuthTokenInContext(authToken, authTokenSecret);
				
				// Test login
				return testLogin();
			}
		}
		catch (Exception e) {
			String message = "Error while checking authentication";
			log.error(message, e);
			throw new AppException(message, e);
		}
	}
	
	/**
	 * Authenticate to Flickr API - Phase 1: create request token and get URL to authorization page
	 * @return URL of the authorization page
	 * @throws AppException
	 */
	public URL authenticatePhase1() throws AppException {
		try {
			this.requestToken = authInterface.getRequestToken();
			log.debug("Get Authorization Url");
			String url = authInterface.getAuthorizationUrl(requestToken, Permission.WRITE);		
			return new URL(url);
		}
		catch (Exception e) {
			String message = "Error during authentication phase 1";
			log.error(message, e);
			throw new AppException(message, e);
		}
	}
	
	/**
	 * Authenticate to Flickr API - Phase 3: verify code, get access token, and test authorization
	 * @param verifyCode
	 * @throws AppException
	 */
	public void authenticatePhase3(String verifyCode) throws AppException {
		try {
			// Verify code and get access token
			Verifier verifier = new Verifier(verifyCode);
			log.debug("Get Access Token");
			this.accessToken = authInterface.getAccessToken(requestToken, verifier);
			
			// Check access token
			log.debug("Check Token");
			Auth auth = authInterface.checkToken(accessToken);
			String authToken = auth.getToken();
			String authTokenSecret = auth.getTokenSecret();
			
			// Put access token in context
			setAuthTokenInContext(authToken, authTokenSecret);
			
			// Test
			if (!testLogin()) {
				String message = "Authentication token is not valid";
				log.error(message);
				throw new AppException(message);
			}
			
			// Store token and token secret to properties
			PropertiesHelper.saveAuthToken(authToken, authTokenSecret);
		}
		catch (AppException e) {
			throw e;
		}
		catch (Exception e) {
			String message = "Error during authentication phase 3";
			log.error(message, e);
			throw new AppException(message, e);
		}
	}
	
	/**
	 * Test login to Flickr API
	 * @return true : authentication OK ; false : invalid authentication token
	 * @throws AppException
	 */
	private boolean testLogin() throws AppException {
		try {
			log.debug("Test Login");
			User user = testInterface.login();
			return true;
		}
		catch (FlickrException e) {
			if (e.getErrorCode().equals(FlickrSorterConstants.FLICKR_EXCEPTION_INVALID_AUTH_TOKEN)) {
				// Invalid auth token exception
				return false;
			}			
			String message = "Error while testing login to Flickr API";
			log.error(message, e);
			throw new AppException(message, e);
		}		
	}
}
