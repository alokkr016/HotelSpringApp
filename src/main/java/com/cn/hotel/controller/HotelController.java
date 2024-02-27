package com.cn.hotel.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.cn.hotel.dto.HotelRequest;
import com.cn.hotel.model.Hotel;
import com.cn.hotel.service.HotelService;

@RestController
@RequestMapping("/hotel")
public class HotelController {
	@Autowired 
	HotelService hotelService;

	@PostMapping("/create")
	@PreAuthorize("hasRole('ADMIN')")
	public void createHotel(@RequestBody HotelRequest hotelRequest)
	{

		hotelService.createHotel(hotelRequest);
	} 
	
	@GetMapping("/id/{id}")
	@PreAuthorize("hasRole('NORMAL')")
	public Hotel getHotelById(@PathVariable Long id)
	{
		return hotelService.getHotelById(id);
	}
	
	@GetMapping("/getAll")
	@PreAuthorize("hasRole('ADMIN')")
	public List<Hotel> getAllHotels()
	{
		return hotelService.getAllHotels();
	}
	
	@DeleteMapping("/remove/id/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public void deleteHotelById(@PathVariable Long id)
	{
		hotelService.deleteHotelById(id);
		
	}
}
