package org.jboss.quickstarts.wfk.booking;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.validation.Validator;

import org.jboss.quickstarts.wfk.customer.Customer;
import org.jboss.quickstarts.wfk.customer.CustomerRepository;
import org.jboss.quickstarts.wfk.hotel.Hotel;
import org.jboss.quickstarts.wfk.hotel.HotelRepository;


public class BookingValidator {
    @Inject
    private Validator validator;

    @Inject
    private BookingRepository crud;
    @Inject
    private CustomerRepository customercrud;
    @Inject
    private HotelRepository hotelcrud;

    /**
     * <p>Validates the given Booking object and throws validation exceptions based on the type of error. If the error is standard
     * bean validation errors then it will throw a ConstraintValidationException with the set of the constraints violated.<p/>
     *
     * @param booking The Booking object to be validated
     * @throws ConstraintViolationException If Bean Validation errors exist
     */
    void validateBooking(Booking booking) throws ConstraintViolationException, ValidationException {
        // Create a bean validator and check for issues.
        Set<ConstraintViolation<Booking>> violations = validator.validate(booking);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
        }
        if (booking != null){
        	if((hotelNotExist(booking.getHotel().getId(), booking.getId()) == false) || (customerNotExist(booking.getCustomer().getId(), booking.getId()) == false) || bookingExist(booking.getHotel().getId(), booking.getId(), booking.getBookingDate()))
        	{
        		if ((hotelNotExist(booking.getHotel().getId(), booking.getId()) == false) && (customerNotExist(booking.getCustomer().getId(), booking.getId()) == false))
        			throw new ValidationException("Hotel and customer are not exist");
        		else if ((hotelNotExist(booking.getHotel().getId(), booking.getId()) == false))
        			throw new ValidationException("Hotel is not exists");
        		else if ((customerNotExist(booking.getCustomer().getId(), booking.getId()) == false))
        			throw new ValidationException("Customer is not exists");
        	}
        	if (bookingExist(booking.getHotel().getId(), booking.getId(), booking.getBookingDate()))
    			throw new ValidationException("Unique booking Violation");
        }
    }
        
    
    boolean customerNotExist(Long customerId, Long id) {
        Customer booking = null;
        Customer bookingWithID = null;
        try {
        	booking = customercrud.findById(customerId);
        } catch (NoResultException e) {
            // ignore
        }

        if (booking != null && id != null) {
            try {
            	bookingWithID = customercrud.findById(id);
                if (bookingWithID != null && bookingWithID.getId().equals(customerId)) {
                	booking = null;
                }
            } catch (NoResultException e) {
                // ignore
            }
        }
        return booking != null;
    }
    
   
    boolean hotelNotExist(Long hotelId, Long id) {
        Hotel booking = null;
        Hotel bookingWithID = null;
        try {
        	booking = hotelcrud.findById(hotelId);
        } catch (NoResultException e) {
            // ignore
        }

        if (booking != null && id != null) {
            try {
            	bookingWithID = hotelcrud.findById(id);
                if (bookingWithID != null && bookingWithID.getId().equals(hotelId)) {
                	booking = null;
                }
            } catch (NoResultException e) {
                // ignore
            }
        }
        return booking != null;
    }
    
    boolean bookingExist(Long hotelId, Long bookingId, Date date) {
    	List<Booking> bookings = null;
        Booking booking = null;
        try {
        	bookings = crud.findByHotelId(hotelId);
        } catch (NoResultException e) {
            // ignore
        }
        
        try{
        	for(Booking temp: bookings){
        		
        		if(temp.getBookingDate().equals(date) && temp.getId()!= bookingId)
        		   booking = temp;     		
        	}
        }catch (Exception e){
        	
        }

        return booking != null;
    }
 
  
}