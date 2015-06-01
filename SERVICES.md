contacts-angularjs: JAX-RS Services Documentation 
=======================================================
Author: Yang Lei

This example supports various RESTFul end points which also includes JSONP support for cross domain requests.

By default the base URL for services is `/jboss-contacts-angularjs/rest`.

HotelBookingService End Points
------------------------
##CREATE
### Create a new customer

#### /rest/customers

* Request type: POST
* Request type: JSON
* Return type: JSON
* Request example:

```JavaScript
{email: "jane.doe@company.com", id: 14, firstName: "Jane", lastName: 'Doe', phoneNumber: "03322341231"}
```

* Response example:
* Success: 200 OK
* Validation error: Collection of `<field name>:<error msg>` for each error

```JavaScript
{"email":"That email is already used, please use a unique email"}
```


##READ
### List all contacts
#### /rest/contacts

* Request type: GET
* Return type: JSON
* Response example:

```javascript
[{email: "jane.doe@company.com", id: 14, firstName: "Jane", lastName: 'Doe', phoneNumber: "01222351231"},
 {email: "john.doe@company.com", id: 15, firstName: "John", lastName: 'Doe', phoneNumber: "01355531212"}]
```

### Find a contact by it's ID.
#### /rest/contacts/\<id>
* Request type: GET
* Return type: JSON
* Response example:

```javascript
{email: "jane.doe@company.com", id: 14, firstName: "Jane", lastName: 'Doe', phoneNumber: "02232231231"}
```


##UPDATE
### Edit one contact
#### /rest/contacts

* Request type: PUT
* Return type: JSON
* Response example:

```javascrip
{email: "jane.doe@company.com", id: 14, firstName: "Jane", lastName: 'Doe', phoneNumber: "02232231231"}
```


##DELETE
### Delete one contact
#### /rest/contacts

* Request type: DELETE
* Return type: JSON
* Response example:

```javascript
{email: "jane.doe@company.com", id: 14, firstName: "Jane", lastName: 'Doe', phoneNumber: "08876764343"}
```
##CREATE
### Create a new hotel

#### /rest/hotels

* Request type: POST
* Request type: JSON
* Return type: JSON
* Request example:

```JavaScript
{postcode: "DD3FF4", id: 14, name: "Wen", phoneNumber: "03322341231"}
```

* Response example:
* Success: 200 OK
* Validation error: Collection of `<field name>:<error msg>` for each error

```JavaScript
{"phoneNumber":"That phoneNumber is already used, please use a unique phoneNumber"}
```


##READ
### List all hotels
#### /rest/hotels

* Request type: GET
* Return type: JSON
* Response example:

```javascript
[{postcode: "DD3FF4", id: 14, name: "Wen", phoneNumber: "03322341231"},
{postcode: "DD3FF4", id: 15, name: "HUA", phoneNumber: "03362341231"}]
```

### Find a hotel by it's ID.
#### /rest/hotels/\<id>
* Request type: GET
* Return type: JSON
* Response example:

```javascript
{postcode: "DD3FF4", id: 14, name: "J", phoneNumber: "03322341231"}
```


##UPDATE
### Edit one hotel
#### /rest/hotels

* Request type: PUT
* Return type: JSON
* Response example:

```javascrip
{postcode: "DG4FF4", id: 14, name: "xi", phoneNumber: "02256561231"}
```


##DELETE
### Delete one hotel
#### /rest/hotels

* Request type: DELETE
* Return type: JSON
* Response example:

```javascript
{postcode: "DD3FF4", id: 14, name: "Wen", phoneNumber: "03322341231"}
```
##CREATE
### Create a new booking

#### /rest/booking

* Request type: POST
* Request type: JSON
* Return type: JSON
* Request example:

```JavaScript
{customId: 1, hotelId: 12, bookingDate:'2016-09-09'}
```

* Response example:
* Success: 200 OK
* Validation error: Collection of `<field name>:<error msg>` for each error

```JavaScript
{"hotel&bookingDate":"That hotel is already booked on that day, please try another date"}
```


##READ
### List all bookings
#### /rest/bookings

* Request type: GET
* Return type: JSON
* Response example:

```javascript
[{customId: 1, hotelId: 12, bookingDate:'2016-09-09'},
{customId: 2, hotelId: 13, bookingDate:'2016-09-10'}]
```

### Find a hotel by it's ID.
#### /rest/hotels/\<id>
* Request type: GET
* Return type: JSON
* Response example:

```javascript
{customId: 1, hotelId: 12, bookingDate:'2016-09-09'}
```


##UPDATE
### Edit one booking
#### /rest/bookings

* Request type: PUT
* Return type: JSON
* Response example:

```javascrip
{customId: 1, hotelId: 12, bookingDate:'2016-09-09'}
```


##DELETE
### Delete one booking
#### /rest/bookings

* Request type: DELETE
* Return type: JSON
* Response example:

```javascript
{customId: 1, hotelId: 12, bookingDate:'2016-09-09'}
```




