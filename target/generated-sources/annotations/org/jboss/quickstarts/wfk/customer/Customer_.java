package org.jboss.quickstarts.wfk.customer;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Customer.class)
public abstract class Customer_ {

	public static volatile SingularAttribute<Customer, String> firstName;
	public static volatile SingularAttribute<Customer, String> lastName;
	public static volatile SingularAttribute<Customer, String> phoneNumber;
	public static volatile SingularAttribute<Customer, Long> id;
	public static volatile SingularAttribute<Customer, String> state;
	public static volatile SingularAttribute<Customer, String> email;

}

