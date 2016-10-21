package com.redhat.training.view;

import java.io.IOException;
import java.net.InetAddress;

import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.sasl.RealmCallback;
import javax.servlet.http.HttpServletRequest;

import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.as.controller.client.OperationBuilder;
import org.jboss.as.controller.client.helpers.ClientConstants;
import org.jboss.dmr.ModelNode;

@RequestScoped
@Named
public class Version {

	public String getVersion(){
		//first attempt to connect to localhost and por 9990
		try{
			return runCliCommand("localhost", 9990);
		}catch(Exception e){
			//not available on localhost:9990. Trying 19990
			try{
				return runCliCommand("localhost", 19990);
			}catch(Exception a){
				//not available on localhost:9990. Trying domain...
				try{
					return runCliCommand("172.25.250.254", 9990);
				}catch(Exception b){
					return "JBoss Version not found";
				}
			}
		}

		
		
	}
	
	private ModelControllerClient createClient (final InetAddress host, final int port, final String username, final char[] password, final String securityRealmName)
    {
       final CallbackHandler callbackHandler = new CallbackHandler() {
               public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException
                 {
                     for (Callback current : callbacks) {
                        if (current instanceof NameCallback) {
                           NameCallback ncb = (NameCallback) current;
                           System.out.println("nttncb.setName() = "+new String(password));
                           ncb.setName(new String(password));
                          } else if (current instanceof PasswordCallback) {
                           PasswordCallback pcb = (PasswordCallback) current;
                           System.out.println("nttpcb.setPassword() = "+username);
                           pcb.setPassword(username.toCharArray());
                          } else if (current instanceof RealmCallback) {
                           RealmCallback rcb = (RealmCallback) current;
                           System.out.println("nttrcb.getDefaulttest() = "+rcb.getDefaultText());
                           rcb.setText(securityRealmName);
                          } else {
                           throw new UnsupportedCallbackException(current);
                      }
                  }
             }
           };
        return ModelControllerClient.Factory.create(host, port, callbackHandler);
     }
	
	public String runCliCommand(String host, Integer port) throws Exception{
		
		  ModelNode request = new ModelNode();
		  request.get(ClientConstants.OP).set("read-resource");
		  request.get("recursive").set(false);
		  ModelControllerClient client = createClient(InetAddress.getByName(host), port, "jbossadm", "JBoss@RedHat123".toCharArray(), "ManagementRealm");
		  ModelNode responce = client.execute(new OperationBuilder(request).build());
		  ModelNode productName = responce.get(ClientConstants.RESULT).get("product-name");
		  ModelNode version = responce.get(ClientConstants.RESULT).get("product-version");
		  if (version.isDefined() && productName.isDefined()) {
		      return productName.asString()+" - "+version.asString();
		  }else{
			  throw new Exception("Not Found");
		  }

		
//		try (ModelControllerClient client = ModelControllerClient.Factory.create(ip, port)) {  
//			final ModelNode op = Operations.createOperation("product-info", new ModelNode().setEmptyList());
//			final ModelNode result = client.execute(op);
//			if (Operations.isSuccessfulOutcome(result)) {
//				final ModelNode model = Operations.readResult(result);
//				String productName = null;
//				String productVersion = null;
//
//				List<Property> properties = model.asPropertyList();
//				for (Property p : properties) {
//					if (p.getName().equals("summary")) {
//						ModelNode summary = p.getValue();
//						if (summary.hasDefined("product-name")) {
//							productName = summary.get("product-name").asString();
//						} else {
//							productName = "EAP";
//						}
//
//						
//						if (summary.hasDefined("product-version")) {
//							productVersion = summary.get("product-version").asString();
//						}
//						break;
//					}
//
//				}
//				return productName + " - "+ productVersion;
//			} else {
//				return "JBoss Version not found";
//			}
//        }
	}
	
   

	public String getServerName(){
		return ((HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest()).getServerName();
	}
	
	public String getServerPort(){
		return String.valueOf(((HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest()).getServerPort());
	}
	
        /*
         * Those return information based on System Properties initialized by EAP
         */

        public String getJbossNodeName() {
                return System.getProperty("jboss.node.name");
        }


        public String getJbossHostName() {
                return System.getProperty("jboss.host.name");
        }

        public String getJbossQualifiedHostName() {
                return System.getProperty("jboss.qualified.host.name");
        }


	
}
