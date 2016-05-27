package models;

import java.util.ArrayList;

public class Document {
	private String name;
	private String type;
	private String creationDate;
	private String path;
	private String createdBy;
	private String status;
	private String version;
	private ArrayList<String> listKeyword;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(String creationDate) {
		this.creationDate = creationDate;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public ArrayList<String> getListKeyword() {
		return listKeyword;
	}
	public void setListKeyword(ArrayList<String> listKeyword) {
		this.listKeyword = listKeyword;
	}
}
