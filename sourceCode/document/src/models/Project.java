package models;

import java.util.ArrayList;

public class Project {
	private String projectID;
	private String projectName;
	private String Status;
	private String projectManager;
	private ArrayList<String> projectMembers;
	private ArrayList<String> involvedCompanies;
	
	public String getProjectID() {
		return projectID;
	}
	public void setProjectID(String projectID) {
		this.projectID = projectID;
	}
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public String getStatus() {
		return Status;
	}
	public void setStatus(String status) {
		Status = status;
	}

	public String getProjectManager() {
		return projectManager;
	}
	public void setProjectManager(String projectManager) {
		this.projectManager = projectManager;
	}
	public ArrayList<String> getProjectMembers() {
		return projectMembers;
	}
	public void setProjectMembers(ArrayList<String> projectMembers) {
		this.projectMembers = projectMembers;
	}
	public ArrayList<String> getInvolvedCompanies() {
		return involvedCompanies;
	}
	public void setInvolvedCompanies(ArrayList<String> involvedCompanies) {
		this.involvedCompanies = involvedCompanies;
	}

}
