package org.ahuh.flickr.sorter.bean;

import java.util.List;

/**
 * Collection Bean
 * @author Julien
 *
 */
public class CollectionBean {
	
	private String id;
	private String title;
	private List<PhotoSetBean> psBeanList;
	
	/**
	 * Constructor	
	 * @param id
	 * @param title
	 * @param psBeanList
	 */
	public CollectionBean(String id, String title, List<PhotoSetBean> psBeanList) {
		super();
		this.id = id;
		this.title = title;
		this.psBeanList = psBeanList;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the psBeanList
	 */
	public List<PhotoSetBean> getPsBeanList() {
		return psBeanList;
	}

	/**
	 * @param psBeanList the psBeanList to set
	 */
	public void setPsBeanList(List<PhotoSetBean> psBeanList) {
		this.psBeanList = psBeanList;
	}
	
	
}
