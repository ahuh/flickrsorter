package org.ahuh.flickr.sorter.interfaces;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.Response;
import com.flickr4java.flickr.Transport;

/**
 * Custom Interface for flickr.collections.* methods
 */
public class CustomCollectionsInterface {
	
	private static final String METHOD_EDIT_SETS = "flickr.collections.editSets";
	
	/**
	 * Logger
	 */
	private static Logger log = Logger.getLogger(CustomCollectionsInterface.class);
	
	private final String apiKey;
	private final String sharedSecret;
	private final Transport transportAPI;
	
	/**
	 * Constructor
	 * @param apiKey
	 * @param sharedSecret
	 * @param transportAPI
	 */
	public CustomCollectionsInterface(String apiKey, String sharedSecret, Transport transportAPI) {
		this.apiKey = apiKey;
		this.sharedSecret = sharedSecret;
		this.transportAPI = transportAPI;
	}
		
	/**
	 * Add or reorder the sets in a collection.
	 * @param collectionId
	 * @param photosetIds
	 * @param doRemove
	 * @throws FlickrException
	 */
	public void editSets(String collectionId, String photosetIds, int doRemove) throws FlickrException {
		
		//		method=flickr.collections.editSets
		//				&do_remove=(NOT REQUIRED, observed "0")
		//				&collection_id=(REQUIRED)
		//				&photoset_ids=(REQUIRED, COMMA LIST)

		
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("method", METHOD_EDIT_SETS);
		if (collectionId != null) {
			parameters.put("collection_id", collectionId);
		}
		if (photosetIds != null) {
			parameters.put("photoset_ids", photosetIds);
		}
		if (doRemove >= 0) {
			parameters.put("do_remove", String.valueOf(doRemove));
		}
		
		Response response = transportAPI.get(transportAPI.getPath(), parameters, apiKey, sharedSecret);
		if (response.isError()) {
			throw new FlickrException(response.getErrorCode(), response.getErrorMessage());
		}
	}
}