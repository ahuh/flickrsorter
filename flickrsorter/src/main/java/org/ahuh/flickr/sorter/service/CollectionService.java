package org.ahuh.flickr.sorter.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.ahuh.flickr.sorter.bean.CollectionBean;
import org.ahuh.flickr.sorter.bean.PhotoSetBean;
import org.ahuh.flickr.sorter.constants.FlickrSorterConstants;
import org.ahuh.flickr.sorter.exception.AppException;
import org.ahuh.flickr.sorter.interfaces.CustomCollectionsInterface;
import org.apache.log4j.Logger;

import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.collections.Collection;
import com.flickr4java.flickr.collections.CollectionsInterface;
import com.flickr4java.flickr.photosets.Photoset;

/**
 * Collection Service
 * @author Julien
 *
 */
public class CollectionService {
	
	/**
	 * Logger
	 */
	private static Logger log = Logger.getLogger(CollectionService.class);

	private Flickr flickr;
	private CollectionsInterface colInterface;
	private CustomCollectionsInterface customColInterface;
	
	/**
	 * Constructor
	 * @param flicker
	 */
	public CollectionService(Flickr flickr) {		
		this.flickr = flickr;
		this.colInterface = flickr.getCollectionsInterface();
		this.customColInterface = new CustomCollectionsInterface(flickr.getApiKey(),
																flickr.getSharedSecret(),
																flickr.getTransport());
	}
	
	/**
	 * Destroyer
	 */
	public void destroy() {
		this.flickr = null;
		this.colInterface = null;
		this.customColInterface = null;
	}
		
	/**
	 * List Collections
	 * @return
	 * @throws AppException
	 */
	public List<CollectionBean> listCollections() throws AppException {
		try {
			List<CollectionBean> output = new ArrayList<CollectionBean>();
			List<Collection> colList = colInterface.getTree(null, null);
			for (Collection col : colList) {
				
				List<PhotoSetBean> psBeanList = new ArrayList<PhotoSetBean>();
				for (Photoset ps : col.getPhotosets()) {
					// Note: PhotoSet Dates not implemented in getTree
					psBeanList.add(new PhotoSetBean(ps.getId(), 
									ps.getTitle(),
									null,
									null));
				}
				
				output.add(new CollectionBean(col.getId(), 
						col.getTitle(), 
						psBeanList));
			}
			return output;
		}
		catch (Exception e) {
			String message = "Error while listing collections";
			log.error(message, e);
			throw new AppException(message, e);
		}
	}
	
	/**
	 * Sort and Reorder Photo Sets in Collection bean
	 * @param colBean
	 * @return
	 * @throws AppException
	 */
	public int sortAndReorderPhotoSets(CollectionBean colBean) throws AppException {
		// Sort photosets
		List<PhotoSetBean> psBeanList = colBean.getPsBeanList();
		psBeanList = sortPhotoSetBeanList(psBeanList);
		
		// Convert to string (comma separated)
		String photosetIds = photoSetBeanListToString(psBeanList);
		
		// Reorder photosets in collection
		return reorderPhotoSets(colBean, photosetIds);
	}
	
	/**
	 * Sort Photo Sets Bean list
	 * @return
	 */
	public List<PhotoSetBean> sortPhotoSetBeanList(List<PhotoSetBean> psBeanList) {
		// TODO : implement param for sort order
		Collections.sort(psBeanList, PhotoSetBean.PhotoSetTitleAscComparator);
		return psBeanList;
	}
	
	/**
	 * Reorder PhotoSets in the given Collection in the specified order
	 * @param photoSet
	 * @param photoIds
	 * @return Number of PhotoSets reordered
	 * @throws AppException
	 */
	public int reorderPhotoSets(CollectionBean colBean, String photosetIds) throws AppException {
		try {
			if (photosetIds != null && !photosetIds.isEmpty()) {
				log.debug("Reorder PhotoSets in Collection " + colBean.getId() + " (" + colBean.getTitle() + ")");
				customColInterface.editSets(colBean.getId(), photosetIds, 0);				
				return photosetIds.split(FlickrSorterConstants.FLICKR_API_PHOTO_IDS_SEPARATOR).length;
			}
			else {
				return 0;
			}
		}
		catch (Exception e) {
			String message = "Error while reordering photosets in collection " + colBean.getId();
			log.error(message, e);
			throw new AppException(message, e);
		}
	}
	
	/**
	 * Convert PhotoSet bean list to string (comma separated)
	 * @param input
	 * @return
	 */
	private String photoSetBeanListToString(List<PhotoSetBean> input) {
		String output = "";
		if (input != null) {
			for (int i = 0 ; i < input.size() ; i++) {
				if (i > 0) {
					output += FlickrSorterConstants.FLICKR_API_PHOTO_IDS_SEPARATOR;
				}
				output += input.get(i).getId();	
			}
		}
		return output;
	}
}
