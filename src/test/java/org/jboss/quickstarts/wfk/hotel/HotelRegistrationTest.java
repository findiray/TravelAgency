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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.Date;
import java.util.Map;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.quickstarts.wfk.customer.Customer;
import org.jboss.quickstarts.wfk.hotel.Hotel;
import org.jboss.quickstarts.wfk.hotel.HotelRESTService;
import org.jboss.quickstarts.wfk.hotel.HotelRepository;
import org.jboss.quickstarts.wfk.hotel.HotelService;
import org.jboss.quickstarts.wfk.hotel.HotelValidator;
import org.jboss.quickstarts.wfk.util.Resources;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * <p>A suite of tests, run with {@link org.jboss.arquillian Arquillian} to test the JAX-RS endpoint for
 * Hotel creation functionality
 * (see {@link HotelRESTService#createHotel(Hotel) createHotel(Hotel)}).<p/>
 *
 * 
 * @author balunasj
 * @author Joshua Wilson
 * @see HotelRESTService
 */
@RunWith(Arquillian.class)
public class HotelRegistrationTest {

    /**
     * <p>Compiles an Archive using Shrinkwrap, containing those external dependencies necessary to run the tests.</p>
     *
     * <p>Note: This code will be needed at the start of each Arquillian test, but should not need to be edited, except
     * to pass *.class values to .addClasses(...) which are appropriate to the functionality you are trying to test.</p>
     *
     * @return Micro test war to be deployed and executed.
     */
    @Deployment
    public static Archive<?> createTestArchive() {
        //HttpComponents and org.JSON are required by HotelService
        File[] libs = Maven.resolver().loadPomFromFile("pom.xml").resolve(
                "org.apache.httpcomponents:httpclient:4.3.2",
                "org.json:json:20140107"
        ).withTransitivity().asFile();

        Archive<?> archive = ShrinkWrap
            .create(WebArchive.class, "test.war")
            .addClasses(Hotel.class, 
                        HotelRESTService.class, 
                        HotelRepository.class, 
                        HotelValidator.class, 
                        HotelService.class, 
                        Resources.class)
            .addAsLibraries(libs)
            .addAsResource("META-INF/test-persistence.xml", "META-INF/persistence.xml")
            .addAsWebInfResource("arquillian-ds.xml")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        
        return archive;
    }

    @Inject
    HotelRESTService hotelRESTService;
    
    @Inject
    @Named("logger") Logger log;

    //Set millis 498484800000 from 1985-10-10T12:00:00.000Z
    private Date date = new Date(498484800000L);

    @Test
    @InSequence(1)
    public void testRegister() throws Exception {
        Hotel hotel = createHotelInstance("qitian","02232134324", "dd3ff4");
        Response response = hotelRESTService.createHotel(hotel);

        assertEquals("Unexpected response status", 201, response.getStatus());
        log.info(" New hotel was persisted and returned status " + response.getStatus());
    }

    @SuppressWarnings("unchecked")
    @Test
    @InSequence(2)
    public void testInvalidRegister() throws Exception {
        Hotel hotel = createHotelInstance("", "", "");
        Response response = hotelRESTService.createHotel(hotel);

        assertEquals("Unexpected response status", 400, response.getStatus());
        assertNotNull("response.getEntity() should not be null", response.getEntity());
        assertEquals("Unexpected response.getEntity(). It contains " + response.getEntity(), 3,
            ((Map<String, String>) response.getEntity()).size());
        log.info("Invalid hotel register attempt failed with return code " + response.getStatus());
    }

    @SuppressWarnings("unchecked")
    @Test
    @InSequence(3)
    public void testDuplicatePhoneNumber() throws Exception {
        // Register an initial user
        Hotel hotel = createHotelInstance("rujia","02231456545","ff4ff4");
        hotelRESTService.createHotel(hotel);

        // Register a different user with the same email
        Hotel anotherHotel = createHotelInstance("ruyue","02231456545","ff3ff5");
        Response response = hotelRESTService.createHotel(anotherHotel);

        assertEquals("Unexpected response status", 409, response.getStatus());
        assertNotNull("response.getEntity() should not be null", response.getEntity());
        assertEquals("Unexpected response.getEntity(). It contains" + response.getEntity(), 1,
            ((Map<String, String>) response.getEntity()).size());
        log.info("Duplicate hotel register attempt failed with return code " + response.getStatus());
    }
    
    @Test
    @InSequence(4)
    public void testRetriveHotel() throws Exception{
    	Response response = hotelRESTService.retrieveAllHotels();
    	assertEquals("Cannot retrive all hotels",200,response.getStatus());
    	log.info("Failed to retrive all hotels"+response.getStatus());
    }
    @Test
    @InSequence(5)
    public void testInvalidName() throws Exception {
        Hotel hotel = createHotelInstance("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaqwertyuiop","02132134324", "dd3ff4");
        Response response = hotelRESTService.createHotel(hotel);

        assertEquals("Unexpected response status",400, response.getStatus());
        log.info(" New hotel was persisted and returned status " + response.getStatus());
    }
    @Test
    @InSequence(6)
    public void testInvalidPostcode() throws Exception{
    	Hotel hotel = createHotelInstance("shuijing","02455895576","e3e");
    	 Response response = hotelRESTService.createHotel(hotel);
    	assertEquals("Unexpected response status", 400, response.getStatus());
        log.info(" New hotel was persisted and returned status " + response.getStatus());
    	
    }
    @Test
    @InSequence(7)
    public void testDeleteHotel() throws Exception{
    	 Hotel hotel = createHotelInstance("jans", "02344456789", "dd3ff5");
    	 hotelRESTService.createHotel(hotel);
    	 Response response = hotelRESTService.deleteHotel(hotel.getId());
    	 assertEquals("cannot delete",400,response.getStatus());
    	 log.info("connot delete" + response.getStatus());
    }

    

    /**
     * <p>A utility method to construct a {@link org.jboss.quickstarts.wfk.hotel.Hotel Hotel} object for use in
     * testing. This object is not persisted.</p>
     *
     * @param firstName The first name of the Hotel being created
     * @param lastName  The last name of the Hotel being created
     * @param email     The email address of the Hotel being created
     * @param phone     The phone number of the Hotel being created
     * @param birthDate The birth date of the Hotel being created
     * @return The Hotel object create
     */
    private Hotel createHotelInstance(String name, String phoneNumber, String postcode) {
        Hotel hotel = new Hotel();
        hotel.setName(name);
        hotel.setPhoneNumber(phoneNumber);
        hotel.setPostcode(postcode);
        return hotel;
    }
}
