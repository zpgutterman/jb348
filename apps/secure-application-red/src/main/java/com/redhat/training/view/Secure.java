package com.redhat.training.view;

import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

@RequestScoped
@Named
public class Secure {

    private String principal;
    private String user;
    private String admin;
    private boolean isAdmin;
	
	public String getPrincipal(){
		return ((HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest()).getUserPrincipal().getName();
	}
	
	public String getUser(){
		return ((HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest()).getRemoteUser();
	}
	
	
    public String getAdmin(){
        boolean isAdmin = isAdmin();
        System.out.println("######### ADMIN? ######## "+isAdmin);
        if (isAdmin)
            return "YES";
		else
            return "NO";
	}

	public boolean isAdmin() {
		return ((HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest()).isUserInRole("admin");
	}

	
	
}
