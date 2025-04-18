package com.hotelbooking.controllers;

import com.hotelbooking.dto.HotelRequest;
import com.hotelbooking.models.Booking;
import com.hotelbooking.models.Hotel;
import com.hotelbooking.models.RoomType;
import com.hotelbooking.services.BookingService;
import com.hotelbooking.services.HotelService;
import com.hotelbooking.services.InventoryService;
import com.hotelbooking.services.RoomTypeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hotels")
@CrossOrigin(origins = "http://localhost:3000")
public class HotelController {

    @Autowired
    private HotelService hotelService;
    
    @Autowired
    private BookingService bookingService;
    
    @Autowired
    private RoomTypeService roomTypeService;
    
    @Autowired
    private InventoryService inventoryService;

    @PostMapping
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Hotel> createHotel(@RequestParam String ownerId, 
                                           @RequestBody HotelRequest request) {
        return ResponseEntity.ok(hotelService.createHotel(ownerId, request));
    }

    @PutMapping("/{hotelId}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Hotel> updateHotel(@PathVariable String hotelId, 
                                           @RequestBody HotelRequest request) {
        return ResponseEntity.ok(hotelService.updateHotel(hotelId, request));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Hotel>> searchHotels(@RequestParam String searchTerm) {
        return ResponseEntity.ok(hotelService.searchHotels(searchTerm));
    }

    @GetMapping("/owner/{ownerId}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<List<Hotel>> getHotelsByOwner(@PathVariable String ownerId) {
        return ResponseEntity.ok(hotelService.getHotelsByOwner(ownerId));
    }

    @GetMapping("/city/{city}")
    public ResponseEntity<List<Hotel>> getHotelsByCity(@PathVariable String city) {
        return ResponseEntity.ok(hotelService.getHotelsByCity(city));
    }

    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<Hotel> getHotelById(@PathVariable String hotelId) {
        return ResponseEntity.ok(hotelService.getHotelById(hotelId));
    }
    
    @GetMapping("/getCity")
    public ResponseEntity<List<String>> getCities() {
        return ResponseEntity.ok(hotelService.getCities());
    }

    @DeleteMapping("/{hotelId}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Void> deleteHotel(@PathVariable String hotelId) {
			List<Booking> bookings = bookingService.getHotelBookings(hotelId);
			for(Booking booking : bookings) {
				String bookingId = booking.getBookingId();
				bookingService.cancelBooking(bookingId);
			}
			List<RoomType> roomTypes = roomTypeService.getRoomTypesByHotel(hotelId);
			for(RoomType roomType : roomTypes) {
				String roomTypeId = roomType.getRoomTypeId();
				inventoryService.deleteInventoryByRoomType(roomTypeId);
				roomTypeService.deleteRoomType(roomTypeId);
			}
        hotelService.deleteHotel(hotelId);
        return ResponseEntity.ok().build();
    }
} 