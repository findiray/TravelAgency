package org.jboss.quickstarts.wfk.booking;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;

import org.jboss.quickstarts.wfk.customer.Customer;
import org.jboss.quickstarts.wfk.hotel.Hotel;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

public class BookingRepository {
    
    @Inject
    private @Named("logger") Logger log;
    
    @Inject
    private EntityManager em;
    
    List<Booking> findAllOrderedById() {
        TypedQuery<Booking> query = em.createNamedQuery(Booking.FIND_ALL, Booking.class);
        return query.getResultList();
    }
    
    List<Booking> findByHotelId(Long hotelId) {
        TypedQuery<Booking> query = em.createNamedQuery(Booking.FIND_BY_HOTEL, Booking.class).setParameter("hotelId", hotelId);
        return query.getResultList();
    }
    /**
     * <p>Returns a single Booking object, specified by a Long id.<p/>
     *
     * @param id The id field of the Booking to be returned
     * @return The Booking with the specified id
     */
    Booking findById(Long id) {
        return em.find(Booking.class, id);
    }
    
    Booking findByDate(Date bookingDate){
        TypedQuery<Booking> query = em.createNamedQuery(Booking.FIND_BY_DATE, Booking.class) .setParameter("bookingDate", bookingDate);
        return query.getSingleResult();
    }
    
    Booking findByHotel(Long hotelId){
        TypedQuery<Booking> query = em.createNamedQuery(Booking.FIND_BY_HOTEL, Booking.class) .setParameter("hotelId", hotelId);
        return query.getSingleResult();
    }
    Booking findByCustomer(Long customerId){
        TypedQuery<Booking> query = em.createNamedQuery(Booking.FIND_BY_CUSTOMER, Booking.class) .setParameter("customerId", customerId);
        return query.getSingleResult();
    }
    
    
    /**
     * <p>Persists the provided Booking object to the application database using the EntityManager.</p>
     *
     * <p>{@link javax.persistence.EntityManager#persist(Object) persist(Object)} takes an entity instance, adds it to the
     * context and makes that instance managed (ie future updates to the entity will be tracked)</p>
     *
     * <p>persist(Object) will set the @GeneratedValue @Id for an object.</p>
     *
     * @param booking The Booking object to be persisted
     * @return The Booking object that has been persisted
     * @throws ConstraintViolationException, ValidationException, Exception
     */
    Booking create(Booking booking) throws ConstraintViolationException, ValidationException, Exception {
        log.info("BookingRepository.create() - Creating " + booking.getId());
        
        // Write the booking to the database.
        em.persist(booking);
        
        return booking;
    }
    
    /**
     * <p>Updates an existing Booking object in the application database with the provided Booking object.</p>
     *
     * <p>{@link javax.persistence.EntityManager#merge(Object) merge(Object)} creates a new instance of your entity,
     * copies the state from the supplied entity, and makes the new copy managed. The instance you pass in will not be
     * managed (any changes you make will not be part of the transaction - unless you call merge again).</p>
     *
     * <p>merge(Object) however must have an object with the @Id already generated.</p>
     *
     * @param booking The Booking object to be merged with an existing Booking
     * @return The Booking that has been merged
     * @throws ConstraintViolationException, ValidationException, Exception
     */
    Booking update(Booking booking) throws ConstraintViolationException, ValidationException, Exception {
        log.info("BookingRepository.update() - Updating " + booking.getId());
        
        // Either update the booking or add it if it can't be found.
        em.merge(booking);
        
        return booking;
    }
    
    /**
     * <p>Deletes the provided Booking object from the application database if found there</p>
     *
     * @param booking The Booking object to be removed from the application database
     * @return The Booking object that has been successfully removed from the application database; or null
     * @throws Exception
     */
    Booking delete(Booking booking) throws Exception {
        log.info("BookingRepository.delete() - Deleting " + booking.getId());
        
        if (booking.getId() != null) {
            /*
             * The Hibernate session (aka EntityManager's persistent context) is closed and invalidated after the commit(),
             * because it is bound to a transaction. The object goes into a detached status. If you open a new persistent
             * context, the object isn't known as in a persistent state in this new context, so you have to merge it.
             *
             * Merge sees that the object has a primary key (id), so it knows it is not new and must hit the database
             * to reattach it.
             *
             * Note, there is NO remove method which would just take a primary key (id) and a entity class as argument.
             * You first need an object in a persistent state to be able to delete it.
             *
             * Therefore we merge first and then we can remove it.
             */
            em.remove(em.merge(booking));
            
        } else {
            log.info("BookingRepository.delete() - No ID was found so can't Delete.");
        }
        
        return booking;
    }
    
}