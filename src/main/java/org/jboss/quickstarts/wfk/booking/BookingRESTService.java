package org.jboss.quickstarts.wfk.booking;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.ejb.NoSuchEntityException;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.NoResultException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.WebApplicationException;

/*
 * The Path annotation defines this as a REST Web Service using JAX-RS.
 * 
 * By placing the Consumes and Produces annotations at the class level the methods all default to JSON.  However, they 
 * can be overridden by adding the Consumes or Produces annotations to the individual method.
 * 
 * It is Stateless to "inform the container that this RESTful web service should also be treated as an EJB and allow 
 * transaction demarcation when accessing the database." - Antonio Goncalves
 * 
 */
@Path("/bookings")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Stateless
public class BookingRESTService {
    @Inject
    private @Named("logger") Logger log;
    
    @Inject
    private BookingService service;
    
    /**
     * <p>Search for and return all the Bookings.  They are sorted alphabetically by name.</p>
     * 
     * @return A Response containing a list of Bookings
     */
    @GET
    public Response retrieveAllBookings() {
        List<Booking> bookings = service.findAllOrderedById();
        return Response.ok(bookings).build();
    }

    
    /**
     * <p>Search for and return a Booking identified by id.</p>
     * 
     * @param id The long parameter value provided as a Booking's id
     * @return A Response containing a single Booking
     */
    @GET
    @Path("/{id:[0-9]+}")
    public Response retrieveBookingById(@PathParam("id") long id) {
        Booking booking = service.findById(id);
        if (booking == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        log.info("findById " + id + ": found Booking = " + booking.getId() + " " + booking.getCustomer()+ " " + booking.getHotel() + " " + booking.getBookingDate());
        
        return Response.ok(booking).build();
    }
    
    @GET
    @Path("/customers/{id:[0-9]+}")
    public Response retrieveBookingByCustomer(@PathParam("id") long id) {
        Booking booking = service.findByCustomer(id);
        if (booking == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
       // log.info("findByCustomer " + customerId + ": found Booking = " + booking.getId() + " " + booking.getCustomer()+ " " + booking.getHotel() + " " + booking.getBookingDate());
        
        return Response.ok(booking).build();
    }
    @GET
    @Path("/hotels/{id:[0-9]+}")
    public Response retrieveBookingByHotel(@PathParam("id") long id) {
        Booking booking = service.findByHotel(id);
        if (booking == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
      //  log.info("findByHotel " + hotelId + ": found Booking = " + booking.getId() + " " + booking.getCustomer()+ " " + booking.getHotel() + " " + booking.getBookingDate());
        
        return Response.ok(booking).build();
    }

    /**
     * <p>Creates a new booking from the values provided. Performs validation and will return a JAX-RS response with either 200 (ok)
     * or with a map of fields, and related errors.</p>
     * 
     * @param booking The Booking object, constructed automatically from JSON input, to be <i>created</i> via {@link BookingService#create(Booking)}
     * @return A Response indicating the outcome of the create operation
     */
    @SuppressWarnings("unused")
    @POST
    public Response createBooking(Booking booking) {
        log.info("createBooking started. Booking = " + booking.getId() + " " + booking.getCustomer()+ " " + booking.getHotel() + " " + booking.getBookingDate());
        if (booking == null) {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        
        Response.ResponseBuilder builder = null;

        try {
            // Go add the new Booking.
            service.create(booking);

            // Create a "Resource Created" 201 Response and pass the booking back in case it is needed.
            builder = Response.status(Response.Status.CREATED).entity(booking);
            
            log.info("createBooking completed. Booking = " + booking.getId() + " " + booking.getCustomer()+ " " + booking.getHotel() + " " + booking.getBookingDate());
        } catch (ConstraintViolationException ce) {
            log.info("ConstraintViolationException - " + ce.toString());
            // Handle bean validation issues
            builder = createViolationResponse(ce.getConstraintViolations());
        } catch (ValidationException e) {
            log.info("ValidationException - " + e.toString());
            // Handle the unique constrain violation
            Map<String, String> responseObj = new HashMap<String, String>();
            //responseObj.put("bookingDate", "That hotel is already booked that day, please use book another day");
            //builder = Response.status(Response.Status.CONFLICT).entity(responseObj);
            if(e.toString().contains("and"))
                responseObj.put("and", "Hotel and customer are not  existed");  
                builder = Response.status(Response.Status.CONFLICT).entity(responseObj);
            if(e.toString().contains("Customer"))
               responseObj.put("Customer", "The customer is not existed");
                builder = Response.status(Response.Status.CONFLICT).entity(responseObj);
            if(e.toString().contains("Hotel"))
               responseObj.put("Hotel", "The hotel is not existed"); 
               builder = Response.status(Response.Status.CONFLICT).entity(responseObj);
            if(e.toString().contains("booking"))
                responseObj.put("booking", "The hotel is already booked on that day");
                builder = Response.status(Response.Status.CONFLICT).entity(responseObj);
        } catch (Exception e) {
            log.info("Exception - " + e.toString());
            // Handle generic exceptions
            Map<String, String> responseObj = new HashMap<String, String>();
            responseObj.put("error", e.getMessage());
            builder = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
        }

        return builder.build();
    }

    /**
     * <p>Updates a booking with the ID provided in the Booking. Performs validation, and will return a JAX-RS response with either 200 ok,
     * or with a map of fields, and related errors.</p>
     * 
     * @param booking The Booking object, constructed automatically from JSON input, to be <i>updated</i> via {@link BookingService#update(Booking)}
     * @param id The long parameter value provided as the id of the Booking to be updated
     * @return A Response indicating the outcome of the create operation
     */
    @PUT
    @Path("/{id:[0-9][0-9]*}")
    public Response updateBooking(@PathParam("id") long id, Booking booking) {
        if (booking == null) {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        log.info("updateBooking started. Booking = " +  booking.getId() + " " + booking.getCustomer()+ " " + booking.getHotel() + " " + booking.getBookingDate());

        if (booking.getId() != id) {
            // The client attempted to update the read-only Id. This is not permitted.
            Response response = Response.status(Response.Status.CONFLICT).entity("The booking ID cannot be modified").build();
            throw new WebApplicationException(response);
        }
        if (service.findById(booking.getId()) == null) {
            // Verify if the contact exists. Return 404, if not present.
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        
        Response.ResponseBuilder builder = null;
        
        try {
            // Apply the changes the Booking.
            service.update(booking);
            

            // Create an OK Response and pass the booking back in case it is needed.
            builder = Response.ok(booking);

            log.info("updateBooking completed. Booking = " + booking.getId()+ " " + booking.getCustomer()+ " " + booking.getHotel() + " " + booking.getBookingDate());
        } catch (ConstraintViolationException ce) {
            log.info("ConstraintViolationException - " + ce.toString());
            // Handle bean validation issues
            builder = createViolationResponse(ce.getConstraintViolations());
        } catch (ValidationException e) {
            log.info("ValidationException - " + e.toString());
            // Handle the unique constrain violation
            Map<String, String> responseObj = new HashMap<String, String>();
            responseObj.put("error", "This is where errors are displayed that are not related to a specific field");
            responseObj.put("anotherError", "You can find this error message in /src/main/java/org/jboss/quickstarts/wfk/rest/ContactRESTService.java line 242.");
            builder = Response.status(Response.Status.CONFLICT).entity(responseObj);
        } catch (Exception e) {
            log.info("Exception - " + e.toString());
            // Handle generic exceptions
            Map<String, String> responseObj = new HashMap<String, String>();
            responseObj.put("error", e.getMessage());
            builder = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
        }

        return builder.build();
    }

    /**
     * <p>Deletes a booking using the ID provided. If the ID is not present then nothing can be deleted.</p>
     *
     * <p>Will return a JAX-RS response with either 200 OK or with a map of fields, and related errors.</p>
     * 
     * @param id The Long parameter value provided as the id of the Booking to be deleted
     * @return A Response indicating the outcome of the delete operation
     */
    @DELETE
    @Path("/{id:[0-9][0-9]*}")
    public Response deleteBooking(@PathParam("id") Long id) {
        log.info("deleteBooking started. Booking ID = " + id);
        Response.ResponseBuilder builder = null;

        Map<String, String> responseObj = new HashMap<String, String>();
        try {
        	Booking booking = service.findById(id);
            if (booking != null) {
                service.delete(booking);
            } else {
                log.info("BookingRESTService - deleteBooking - No booking with matching ID was found so can't Delete.");
                throw new WebApplicationException(Response.Status.NOT_FOUND);
            }

            builder = Response.noContent();
            log.info("deleteBooking completed. Booking = " + booking.getId() + " " + booking.getCustomer()+ " " + booking.getHotel() + " " + booking.getBookingDate());
        } catch (Exception e) {
            log.info("Exception - " + e.toString());
            // Handle generic exceptions
            responseObj.put("error", e.getMessage());
            builder = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
        }

        return builder.build();
    }
    
    /**
     * <p>Creates a JAX-RS "Bad Request" response including a map of all violation fields, and their message. This can be used
     * by calling client applications to display violations to users.<p/>
     * 
     * @param violations A Set of violations that need to be reported in the Response body
     * @return A Bad Request (400) Response containing all violation messages
     */
    private Response.ResponseBuilder createViolationResponse(Set<ConstraintViolation<?>> violations) {
        log.fine("Validation completed. violations found: " + violations.size());

        Map<String, String> responseObj = new HashMap<String, String>();

        for (ConstraintViolation<?> violation : violations) {
            responseObj.put(violation.getPropertyPath().toString(), violation.getMessage());
        }

        return Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
    }


}