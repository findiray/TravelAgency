/*
 * JBoss, Home of Professional Open Source
 * Copyright 2014, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.quickstarts.wfk.hotel;


import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;

import java.net.URI;
import java.util.List;

import java.util.logging.Logger;

/**
 * <p>This Service assumes the Control responsibility in the ECB pattern.</p>
 *
 * <p>The validation is done here so that it may be used by other Boundary Resources. Other Business Logic would go here
 * as well.</p>
 *
 * <p>There are no access modifiers on the methods, making them 'package' scope.  They should only be accessed by a
 * Boundary / Web Service class with public methods.</p>
 *
 * @author Joshua Wilson
 * @see HotelValidator
 * @see HotelRepository
 */

//@Dependent annotation designates the default scope, listed here so that you know what scope is being used.
@Dependent
public class HotelService {

    @Inject  //create logger instance??
    private @Named("logger") Logger log;

    @Inject
    private HotelValidator validator;

    @Inject //four operations
    private HotelRepository crud;

    @Inject
    private @Named("httpClient") CloseableHttpClient httpClient;
    
    /**
     * <p>Returns a List of all persisted {@link Hotel} objects, sorted alphabetically by name.<p/>
     * 
     * @return List of Hotel objects
     */
    List<Hotel> findAllOrderedByName() {
        return crud.findAllOrderedByName();
    }

    /**
     * <p>Returns a single Hotel object, specified by a Long id.<p/>
     * 
     * @param id The id field of the Hotel to be returned
     * @return The Hotel with the specified id
     */
    Hotel findById(Long id) {
        return crud.findById(id);
    }

    /**
     * <p>Returns a single Hotel object, specified by a phone number.<p/>
     *
     * <p>If there is more then one, only the first will be returned.<p/>
     * 
     * @param name The name field of the Hotel to be returned
     * @return The first Hotel with the specified phone number
     */
    Hotel findByPhoneNumber(String phoneNumber){
    	return crud.findByPhoneNumber(phoneNumber);
    }
    Hotel findByPostcode(String postcode){
    	return crud.findByPostcode(postcode);
    }

    /**
     * <p>Returns a single Hotel object, specified by a String name.<p/>
     *
     * <p>If there is more then one, only the first will be returned.<p/>
     * 
     * @param name The name field of the Hotel to be returned
     * @return The first Hotel with the specified name
     */
    Hotel findByName(String name) {
        return crud.findByName(name);
    }

   

    /**
     * <p>Writes the provided Hotel object to the application database.<p/>
     *
     * <p>Validates the data in the provided Hotel object using a {@link HotelValidator} object.<p/>
     * 
     * @param hotel The Hotel object to be written to the database using a {@link HotelRepository} object
     * @return The Hotel object that has been successfully written to the application database
     * @throws ConstraintViolationException, ValidationException, Exception
     */
    Hotel create(Hotel hotel) throws ConstraintViolationException, ValidationException, Exception {
        log.info("HotelService.create() - Creating " + hotel.getName());
        
        // Check to make sure the data fits with the parameters in the Hotel model and passes validation.
        validator.validateHotel(hotel);

        //Perform a rest call to get the state of the hotel from the allareacodes.com API
       /** URI uri = new URIBuilder()
                .setScheme("http")
                .setHost("www.allareacodes.com")
                .setPath("/api/1.0/api.json")
                .setParameter("npa", hotel.getPhoneNumber().substring(1,4))
                .setParameter("tracking_email", "h.firth@ncl.ac.uk")
                .setParameter("tracking_url", "http://www.ncl.ac.uk/undergraduate/modules/module/CSC8104")
                .build();
        HttpGet req = new HttpGet(uri);
        CloseableHttpResponse response = httpClient.execute(req);
        String responseBody = EntityUtils.toString(response.getEntity());
        JSONObject responseJson = new JSONObject(responseBody);
        JSONArray areaCodes = responseJson.getJSONArray("area_codes");
        hotel.setState(areaCodes.getJSONObject(0).getString("state"));
        HttpClientUtils.closeQuietly(response);**/
       


        // Write the hotel to the database.
        return crud.create(hotel);
    }

    /**
     * <p>Updates an existing Hotel object in the application database with the provided Hotel object.<p/>
     *
     * <p>Validates the data in the provided Hotel object using a HotelValidator object.<p/>
     * 
     * @param hotel The Hotel object to be passed as an update to the application database
     * @return The Hotel object that has been successfully updated in the application database
     * @throws ConstraintViolationException, ValidationException, Exception
     */
    Hotel update(Hotel hotel) throws ConstraintViolationException, ValidationException, Exception {
        log.info("HotelService.update() - Updating " + hotel.getName());
        
        // Check to make sure the data fits with the parameters in the Hotel model and passes validation.
        validator.validateHotel(hotel);

        //Perform a rest call to get the state of the hotel from the allareacodes.com API
       /** URI uri = new URIBuilder()
                .setScheme("http")
                .setHost("www.allareacodes.com")
                .setPath("/api/1.0/api.json")
                .setParameter("npa", hotel.getPhoneNumber().substring(1,4))
                .setParameter("tracking_email", "h.firth@ncl.ac.uk")
                .setParameter("tracking_url", "http://www.ncl.ac.uk/undergraduate/modules/module/CSC8104")
                .build();
        HttpGet req = new HttpGet(uri);
        CloseableHttpResponse response = httpClient.execute(req);
        String responseBody = EntityUtils.toString(response.getEntity());
        JSONObject responseJson = new JSONObject(responseBody);
        JSONArray areaCodes = responseJson.getJSONArray("area_codes");
        hotel.setState(areaCodes.getJSONObject(0).getString("state"));
        HttpClientUtils.closeQuietly(response);**/

        // Either update the hotel or add it if it can't be found.
        return crud.update(hotel);
    }

    /**
     * <p>Deletes the provided Hotel object from the application database if found there.<p/>
     * 
     * @param hotel The Hotel object to be removed from the application database
     * @return The Hotel object that has been successfully removed from the application database; or null
     * @throws Exception
     */
    Hotel delete(Hotel hotel) throws Exception {
        log.info("HotelService.delete() - Deleting " + hotel.getName());
        
        Hotel deletedHotel = null;
        
       if (hotel.getId() != null) {
            deletedHotel = crud.delete(hotel);
        } else {
            log.info("HotelService.delete() - No ID was found so can't Delete.");
        }
        
        return deletedHotel;
    }

}
