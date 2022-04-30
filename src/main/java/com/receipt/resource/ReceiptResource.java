package com.receipt.resource;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;


import com.receipt.emailutility.SendUtility;
import com.receipt.generate.ReceiptGenerator;

/**
 * Root resource (exposed at "myresource" path)
 */
@Path("receipt")
public class ReceiptResource {

    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @return String that will be returned as a text/plain response.
     * @throws URISyntaxException 
     */
   
	@POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Path("/setDetails")
    public Response getIt(@Context HttpServletRequest httpServletRequest, MultivaluedMap<String, String> data) throws URISyntaxException {
        
		System.out.println("data : "+data);
    	String name = data.get("name").get(0);
    	String mobileNumber = data.get("mobilenumber").get(0);
    	String pan = data.get("pan").get(0);
    	String amount = data.get("amount").get(0);
    	String transactiondate = data.get("transactiondate").get(0);
    	String email = data.get("email").get(0);
    	String volunteername = data.get("volunteername").get(0);
    	String volunteeremail = data.get("volunteeremail").get(0);
    	String scheme ="Scholorship";
    	try {
    		InputStream resourceAsStream = httpServletRequest.getServletContext().getResourceAsStream("/WEB-INF/thankYouEmail.txt");
    		
			String generatePdf = ReceiptGenerator.generatePdf(name, mobileNumber, email, pan, amount, transactiondate, scheme );
			if(generatePdf!= null && !generatePdf.isEmpty()) {
				
				
				System.out.println("Pushing Donor Details into stoage - Started");
				ReceiptGenerator.storeIntoRecord(name, mobileNumber, email, pan, amount, transactiondate, scheme,volunteeremail,volunteername);
				System.out.println("Pushing Donor Details into stoage - Finished");
				
				System.out.println("Emailing Reciept to Donor - Started");
				SendUtility.send(name,email, generatePdf, resourceAsStream);
				System.out.println("Emailing Reciept to Donor - Finished");
			}
			
		} catch (Exception e) {			
			e.printStackTrace();
			
			return Response.temporaryRedirect(new URI("http://localhost:8090/N_POCW/Error.html")).entity("Error.").build();
		}
    	return Response.temporaryRedirect(new URI("http://localhost:8090/N_POCW/Success.html")).entity("Email Sent Successfully.").build();
    }
    
	/*
	 * @GET
	 * 
	 * @Path("/getData")
	 * 
	 * @Produces(MediaType.APPLICATION_JSON) public Response getData() {
	 * 
	 * ReceiptData data = new ReceiptData("Shrinivas", "Test@G.com", "JJJJJJ",
	 * "212121", 2132132L, new Date(), "Ashdaks", "skjdhkasjhdsakj"); return
	 * Response.ok().entity(data).build(); }
	 */
}
