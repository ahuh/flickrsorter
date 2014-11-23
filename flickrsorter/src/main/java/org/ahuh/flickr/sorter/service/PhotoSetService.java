package org.ahuh.flickr.sorter.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.ahuh.flickr.sorter.bean.PhotoBean;
import org.ahuh.flickr.sorter.bean.PhotoSetBean;
import org.ahuh.flickr.sorter.constants.FlickrSorterConstants;
import org.ahuh.flickr.sorter.exception.AppException;
import org.ahuh.flickr.sorter.helper.DateHelper;
import org.apache.log4j.Logger;

import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.photos.Photo;
import com.flickr4java.flickr.photos.PhotoList;
import com.flickr4java.flickr.photosets.Photoset;
import com.flickr4java.flickr.photosets.Photosets;
import com.flickr4java.flickr.photosets.PhotosetsInterface;

/**
 * PhotoSet Service
 * @author Julien
 *
 */
public class PhotoSetService {
	
	/**
	 * Logger
	 */
	private static Logger log = Logger.getLogger(PhotoSetService.class);

	private Flickr flickr;
	private PhotosetsInterface psInterface;
	
	/**
	 * Constructor
	 * @param flicker
	 */
	public PhotoSetService(Flickr flickr) {		
		this.flickr = flickr;
		this.psInterface = flickr.getPhotosetsInterface();
	}
	
	/**
	 * Destroyer
	 */
	public void destroy() {
		this.flickr = null;
		this.psInterface = null;
	}
	
	/**
	 * Sort and Reorder all Photo Sets
	 * @param psBeanList
	 * @return
	 * @throws AppException
	 */
	public int sortAndReorderPhotoSets(List<PhotoSetBean> psBeanList) throws AppException {
		// Sort photosets
		psBeanList = sortPhotoSetBeanList(psBeanList);
		
		// Convert to ID array
		String[] photoSetIds = photoSetBeanListToIdArray(psBeanList);
		
		// Reorder photosets
		return reorderPhotoSets(photoSetIds);
	}
	
	/**
	 * Sort and Reorder all Photos in given Photo Set
	 * @param psBeanList
	 * @return
	 * @throws AppException
	 */
	public int sortAndReorderPhotos(PhotoSetBean psBean) throws AppException {		

		// List photos
		List<PhotoBean> pBeanList = listPhotos(psBean);
		
		// Sort photos
		pBeanList = sortPhotoBeanList(pBeanList);
		
		// Convert to ID string
		String photoIds = photoBeanListToString(pBeanList);
		
		// Reorder photos
		return reorderPhotos(psBean, photoIds);
	}
	
	/**
	 * List Photo Sets
	 * @return
	 * @throws AppException
	 */
	public List<PhotoSetBean> listPhotoSets() throws AppException {
		try {
			List<PhotoSetBean> output = new ArrayList<PhotoSetBean>();
			int i = 1;
			while (true) {
				Photosets photosets = psInterface.getList(null, 500, i, null);
				int page = photosets.getPage();
				int pages = photosets.getPages();
				for (Photoset ps : photosets.getPhotosets()) {
					output.add(new PhotoSetBean(ps.getId(), 
							ps.getTitle(), 
							DateHelper.convertJsonToJava(ps.getDateCreate()), 
							DateHelper.convertJsonToJava(ps.getDateUpdate())));
				}
				i++;
				if (page == pages) {
					break;
				}
			}
			return output;			
		}
		catch (Exception e) {
			String message = "Error while listing photo sets";
			log.error(message, e);
			throw new AppException(message, e);
		}
	}
	
	/**
	 * List Photos in the given PhotoSet
	 * @param photoSet
	 * @return
	 * @throws AppException
	 */
	public List<PhotoBean> listPhotos(PhotoSetBean photoSet) throws AppException {
		try {
			List<PhotoBean> output = new ArrayList<PhotoBean>();
			int i = 1;
			while (true) {
				Set<String> extras = new HashSet<String>();
				extras.add("date_upload");
				extras.add("date_taken");
				PhotoList<Photo> photoList = psInterface.getPhotos(photoSet.getId(), extras, 0, 500, i);
				int page = photoList.getPage();
				int pages = photoList.getPages();
				for (Photo p : photoList) {
					output.add(new PhotoBean(p.getId(),
							p.getTitle(), 
							p.getDateTaken(), 
							p.getDatePosted()));
				}
				i++;
				if (page == pages) {
					break;
				}
			}
			return output;			
		}
		catch (Exception e) {
			String message = "Error while listing photos in photo set " + photoSet.getId();
			log.error(message, e);
			throw new AppException(message, e);
		}
	}
	
	/**
	 * Sort Photo Sets Bean list
	 * @return
	 */
	public List<PhotoSetBean> sortPhotoSetBeanList(List<PhotoSetBean> psBeanList) {
		// TODO : implement param for sort order
		Collections.sort(psBeanList, PhotoSetBean.PhotoSetTitleDescComparator);
		return psBeanList;
	}
	
	
	/**
	 * Sort Photo Bean list
	 * @return
	 */
	public List<PhotoBean> sortPhotoBeanList(List<PhotoBean> pBeanList) {
		// TODO : implement param for sort order
		Collections.sort(pBeanList, PhotoBean.PhotoTitleAscComparator);
		return pBeanList;
	}
	
	/**
	 * Reorder Photo Sets in the specified order
	 * @param photoSetIds
	 * @return Number of Photo Sets reordered
	 * @throws AppException
	 */
	public int reorderPhotoSets(String[] photoSetIds) throws AppException {
		try {
			if (photoSetIds != null && photoSetIds.length > 0) {
				log.debug("Reorder PhotoSets");
				psInterface.orderSets(photoSetIds);
				return photoSetIds.length;
			}
			else {
				return 0;
			}
		}
		catch (Exception e) {
			String message = "Error while reordering photo sets";
			log.error(message, e);
			throw new AppException(message, e);
		}
	}
	
	/**
	 * Reorder Photo in the given PhotoSet in the specified order
	 * @param photoSet
	 * @param photoIds
	 * @return Number of Photo reordered
	 * @throws AppException
	 */
	public int reorderPhotos(PhotoSetBean photoSet, String photoIds) throws AppException {
		try {
			if (photoIds != null && !photoIds.isEmpty()) {
				log.debug("Reorder Photos in PhotoSet " + photoSet.getId() + " (" + photoSet.getTitle() + ")");
				psInterface.reorderPhotos(photoSet.getId(), photoIds);				
				return photoIds.split(FlickrSorterConstants.FLICKR_API_PHOTO_IDS_SEPARATOR).length;
			}
			else {
				return 0;
			}
		}
		catch (Exception e) {
			String message = "Error while reordering photo in photo set " + photoSet.getId();
			log.error(message, e);
			throw new AppException(message, e);
		}
	}

	/**
	 * Convert PhotoSet bean list to ID string array 
	 * @param input
	 * @return
	 */
	private String[] photoSetBeanListToIdArray(List<PhotoSetBean> input) {
		if (input != null) {
			String[] output = new String[input.size()];
			for (int i = 0 ; i < input.size() ; i++) {
				output[i] = input.get(i).getId();
			}
			return output;
		}
		return null;
	}
	
	/**
	 * Convert Photo bean list to string (comma separated)
	 * @param input
	 * @return
	 */
	private String photoBeanListToString(List<PhotoBean> input) {
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
