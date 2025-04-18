package com.hotelbooking.controllers;
 
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hotelbooking.dto.FeedbackRequest;
import com.hotelbooking.dto.UserRequest;
import com.hotelbooking.models.Booking;
import com.hotelbooking.models.Customer;
import com.hotelbooking.models.Feedback;
import com.hotelbooking.models.Hotel;
import com.hotelbooking.models.Owner;
import com.hotelbooking.models.RoomType;
import com.hotelbooking.services.BookingService;
import com.hotelbooking.services.HotelService;
import com.hotelbooking.services.InventoryService;
import com.hotelbooking.services.RoomTypeService;
import com.hotelbooking.services.UserService;
 
@RestController
@RequestMapping("/api/user")
public class UserController {
	@Autowired
	private UserService userService;
	
	@Autowired
	private BookingService bookingService;
	
	@Autowired
    private InventoryService inventoryService;
	
	@Autowired
    private HotelService hotelService;
	
	 @Autowired
	 private RoomTypeService roomTypeService;
	    
	
	@GetMapping("/customer/{customerId}")
    @PreAuthorize("hasRole('CUSTOMER')")
	public ResponseEntity<Customer> getCustomerProfile(@PathVariable String customerId) {
        return ResponseEntity.ok(userService.getCustomerProfile(customerId));
    }
	
	@PutMapping("/update/customer/{customerId}")
    @PreAuthorize("hasRole('CUSTOMER')")
	public ResponseEntity<Customer> updateCustomerProfile(@PathVariable String customerId, @RequestBody UserRequest request) {
        return ResponseEntity.ok(userService.updateCustomerProfile(customerId, request));
    }
	
	@GetMapping("/owner/{ownerId}")
    @PreAuthorize("hasRole('OWNER')")
	public ResponseEntity<Owner> getOwnerProfile(@PathVariable String ownerId) {
        return ResponseEntity.ok(userService.getOwnerProfile(ownerId));
    }
	
	@PutMapping("/update/owner/{ownerId}")
    @PreAuthorize("hasRole('OWNER')")
	public ResponseEntity<Owner> updateOwnerProfile(@PathVariable String ownerId, @RequestBody UserRequest request) {
        return ResponseEntity.ok(userService.updateOwnerProfile(ownerId, request));
    }
	
	@DeleteMapping("/customer/{customerId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Void> deleteCustomer(@PathVariable String customerId) {
		bookingService.deleteBookingByCustomer(customerId);
		userService.deleteCustomer(customerId);
        return ResponseEntity.ok().build();
    }
	
	@DeleteMapping("/owner/{ownerId}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Void> deleteOwner(@PathVariable String ownerId) {
		inventoryService.deleteInventoryByOwner(ownerId);
		List<Hotel> hotels = hotelService.getHotelsByOwner(ownerId);
		for (Hotel hotel : hotels) {
			String hotelId = hotel.getHotelId();
			List<Booking> bookings = bookingService.getHotelBookings(hotelId);
			for(Booking booking : bookings) {
				String bookingId = booking.getBookingId();
				bookingService.cancelBooking(bookingId);
			}
			List<RoomType> roomTypes = roomTypeService.getRoomTypesByHotel(hotelId);
			for(RoomType roomType : roomTypes) {
				String roomTypeId = roomType.getRoomTypeId();
				roomTypeService.deleteRoomType(roomTypeId);
			}
			hotelService.deleteHotel(hotelId);
		}
		userService.deleteOwner(ownerId);
        return ResponseEntity.ok().build();
    }
	
	@GetMapping("/customer/getFeedback")
	public ResponseEntity<List<Feedback>> getfeedBack() {
		return ResponseEntity.ok(userService.getfeedBack());
    }
	
	@PostMapping("/customer/feedback/{customerId}")
    @PreAuthorize("hasRole('CUSTOMER')")
	public void feedBackCustomer(@PathVariable String customerId, @RequestBody FeedbackRequest feedbackRequest) {
		userService.feedbackCustomer(customerId, feedbackRequest);
    } 
}