package models;

import java.util.ArrayList;

public class Company {
	private String companyID;
	private String companyName;
	private int numberEmployee;
	private ArrayList<String> employees;
	private ArrayList<String> projects;
	private String headquarter;
	private String industrialSector;

	public String getCompanyID() {
		return companyID;
	}
	public void setCompanyID(String companyID) {
		this.companyID = companyID;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public int getNumberEmployee() {
		return numberEmployee;
	}
	public void setNumberEmployee(int numberEmployee) {
		this.numberEmployee = numberEmployee;
	}

	public ArrayList<String> getEmployees() {
		return employees;
	}
	public void setEmployees(ArrayList<String> employees) {
		this.employees = employees;
	}
	public ArrayList<String> getProjects() {
		return projects;
	}
	public void setProjects(ArrayList<String> projects) {
		this.projects = projects;
	}
	public String getHeadquarter() {
		return headquarter;
	}
	public void setHeadquarter(String headquarter) {
		this.headquarter = headquarter;
	}
	public String getIndustrialSector() {
		return industrialSector;
	}
	public void setIndustrialSector(String industrialSector) {
		this.industrialSector = industrialSector;
	}
}
