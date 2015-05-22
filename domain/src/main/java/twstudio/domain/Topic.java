package twstudio.domain;

import java.util.ArrayList;
import java.util.List;

public class Topic {
	private int id;	
	private String name;
	private int portalId;
	
	private int parentTopicId;
	private int displayOrder;
	private List<Topic> topics = new ArrayList<Topic>();
	private List<Article> articles = new ArrayList<Article>();
			
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getPortalId() {
		return portalId;
	}
	public void setPortalId(int portalId) {
		this.portalId = portalId;
	}
	public int getParentTopicId() {
		return parentTopicId;
	}
	public void setParentTopicId(int parentTopicId) {
		this.parentTopicId = parentTopicId;
	}
	public int getDisplayOrder() {
		return displayOrder;
	}
	public void setDisplayOrder(int displayOrder) {
		this.displayOrder = displayOrder;
	}
	public List<Topic> getTopics() {
		return topics;
	}
	public List<Article> getArticles() {
		return articles;
	}
	
}
