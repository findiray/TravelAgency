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
package org.jboss.quickstarts.wfk.booking;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.Date;
import java.util.Map;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.quickstarts.wfk.booking.Booking;
import org.jboss.quickstarts.wfk.booking.BookingRESTService;
import org.jboss.quickstarts.wfk.booking.BookingRepository;
import org.jboss.quickstarts.wfk.booking.BookingService;
import org.jboss.quickstarts.wfk.booking.BookingValidator;
import org.jboss.quickstarts.wfk.customer.Customer;
import org.jboss.quickstarts.wfk.customer.CustomerRESTService;
import org.jboss.quickstarts.wfk.customer.CustomerRepository;
import org.jboss.quickstarts.wfk.customer.CustomerService;
import org.jboss.quickstarts.wfk.customer.CustomerValidator;
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
 * Booking creation functionality
 * (see {@link BookingRESTService#createBooking(Booking) createBooking(Booking)}).<p/>
 *
 * 
 * @author balunasj
 * @author Joshua Wilson
 * @see BookingRESTService
 */
@RunWith(Arquillian.class)
public class BookingRegistrationTest {

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
        //HttpComponents and org.JSON are required by BookingService
        File[] libs = Maven.resolver().loadPomFromFile("pom.xml").resolve(
                "org.apache.httpcomponents:httpclient:4.3.2",
                "org.json:json:20140107"
        ).withTransitivity().asFile();

        Archive<?> archive = ShrinkWrap
            .create(WebArchive.class, "test.war")
            .addClasses(Booking.class, 
                        BookingRESTService.class, 
                        BookingRepository.class, 
                        BookingValidator.class, 
                        BookingService.class, 
                        Resources.class,
                        
                        CustomerRESTService.class, 
                        CustomerRepository.class, 
                        CustomerValidator.class, 
                        CustomerService.class,
                        Hotel.class, 
                        HotelRESTService.class, 
                        HotelRepository.class, 
                        HotelValidator.class, 
                        HotelService.class,
                        Customer.class
                       )
            .addAsLibraries(libs)
            .addAsResource("META-INF/test-persistence.xml", "META-INF/persistence.xml")
            .addAsWebInfResource("arquillian-ds.xml")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        
        return archive;
    }
    
    @Inject
    BookingRESTService bookingRESTService;
    
    @Inject
    CustomerRESTService customerRESTService;
    
    @Inject
    HotelRESTService hotelRESTService;
    
    @Inject
    @Named("logger") Logger log;

    //Set millis 498484800000 from 1985-10-10T12:00:00.000Z
    @SuppressWarnings("deprecation")
	private Date date = new Date(2018,10,9);

    @Test
    @InSequence(1)
    public void testRegister() throws Exception {
    	Customer customer = createCustomerInstance("li","xixi","lixixi@183.com","03310982734");
    	Hotel hotel = createHotelInstance("motai","03109878767","dr4fr4");
        Booking booking = createBookingInstance(customer,hotel,date);
        Response response2 = hotelRESTService.createHotel(hotel);
        Response response1 = customerRESTService.createCustomer(customer);
        Response response = bookingRESTService.createBooking(booking);
        assertEquals("Unexpected response status", 201, response1.getStatus());
        log.info (" New booking was persisted and returned status " + response1.getStatus());
        assertEquals("Unexpected response status", 201, response2.getStatus());
        log.info (" New booking was persisted and returned status " + response2.getStatus());
        assertEquals("Unexpected response status", 201, response.getStatus());
        log.info (" New booking was persisted and returned status " + response.getStatus());
    }

    @SuppressWarnings("unchecked")
   @Test
    @InSequence(2)
    public void testInvalidRegister() throws Exception {
        Booking booking = createBookingInstance(null,null,null);
        Response response = bookingRESTService.createBooking(booking);

        assertEquals("Unexpected response status", 400, response.getStatus());
        assertNotNull("response.getEntity() should not be null", response.getEntity());
        assertEquals("Unexpected response.getEntity(). It contains " + response.getEntity(),3,
            ((Map<String, String>) response.getEntity()).size());
        log.info("Invalid booking register attempt failed with return code " + response.getStatus());
    }
    @Test
    @InSequence(3)
    public void testRetriveByCustomer() throws Exception{
    	
    	Customer customer = createCustomerInstance("lin","xi","linxi@173.com","03310982734");
    	Hotel hotel = createHotelInstance("momo","04567856785","hh6jj7");
    	Booking booking = createBookingInstance(customer,hotel,date);
    	Response response1 = customerRESTService.createCustomer(customer);
    	Response response2 = hotelRESTService.createHotel(hotel);
        Response response3 = bookingRESTService.createBooking(booking);
    	
		Response response = bookingRESTService.retrieveBookingByCustomer(customer.getId());
		
    	assertEquals("Cannot retrive all bookings by customer id",200,response.getStatus());
    	log.info("cannot retrive all bookings by customer id" + response.getStatus());
    }
    
    @Test
    @InSequence(4)
  
    	public void testDeleteBooking() throws Exception{
    	@SuppressWarnings("deprecation")
		
    	Customer customer = createCustomerInstance("huang","wei","huangwei@183.com","02213431232");
    	Hotel hotel = createHotelInstance("wenhua","04432345434","dd2ff3");
    	Booking booking = createBookingInstance(customer,hotel,date);
    	Response response1 = customerRESTService.createCustomer(customer);
    	Response response2 = hotelRESTService.createHotel(hotel);
        Response response3 = bookingRESTService.createBooking(booking);
    	Response response = bookingRESTService.deleteBooking(booking.getId());
    	assertEquals("Cannot delete booking",204,response.getStatus());
    	log.info("cannot detele booking"+response.getStatus());	
    	}
    @Test
    @InSequence(5)
    public void testDuplicateDate() throws Exception {
        // Register an initial user
        Customer customer = createCustomerInstance("zhou", "yao", "zhouyao@mailinator.com", "03355531234");
        Response response1 = customerRESTService.createCustomer(customer);
        Customer customer1 = createCustomerInstance("zhou", "fei", "zhoufei@mailinator.com", "03355531211");
        Response response4 = customerRESTService.createCustomer(customer1);
        Hotel hotel = createHotelInstance("xierd","03397574678","de3gh6");
        Response response2 = hotelRESTService.createHotel(hotel);
        Booking booking = createBookingInstance(customer,hotel,date);
        Response response3 = bookingRESTService.createBooking(booking);
        Booking booking2 = createBookingInstance(customer1,hotel,date);
        Response response5 = bookingRESTService.createBooking(booking2);
      
        assertEquals("Unexpected response status", 409, response5.getStatus());
        assertNotNull("response.getEntity() should not be null", response5.getEntity());
        //assertEquals("Unexpected response.getEntity(). It contains" + response5.getEntity(), 1,
           // ((Map<String, String>) response5.getEntity()).size());
        log.info("Duplicate customer register attempt failed with return code " + response5.getStatus());
    }
    	
    
    

 

    /**
     * <p>A utility method to construct a {@link org.jboss.quickstarts.wfk.booking.Booking Booking} object for use in
     * testing. This object is not persisted.</p>
     *
     * @param firstName The first name of the Booking being created
     * @param lastName  The last name of the Booking being created
     * @param email     The email address of the Booking being created
     * @param phone     The phone number of the Booking being created
     * @param birthDate The birth date of the Booking being created
     * @return The Booking object create
     */
    @Test
    @InSequence(3)
    public void testRetriveById() throws Exception{
    	
    }
    private Customer createCustomerInstance(String firstName, String lastName, String email, String phone) {
        Customer customer = new Customer();
        customer.setFirstName(firstName);
        customer.setLastName(lastName);
        customer.setEmail(email);
        customer.setPhoneNumber(phone);
       
        return customer;
    }
    private Hotel createHotelInstance(String name, String phoneNumber, String postcode) {
        Hotel hotel = new Hotel();
        hotel.setName(name);
        hotel.setPhoneNumber(phoneNumber);
        hotel.setPostcode(postcode);
        return hotel;
    }
    private Booking createBookingInstance(Customer customer, Hotel hotel, Date date) {
        Booking booking = new Booking();
        booking.setCustomer(customer);
        booking.setHotel(hotel);
        booking.setBookingDate(date);
        return booking;
    }
}

