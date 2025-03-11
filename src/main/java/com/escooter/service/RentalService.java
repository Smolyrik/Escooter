package com.escooter.service;

import com.escooter.dto.RentalDto;

import java.util.List;
import java.util.UUID;

public interface RentalService {

    RentalDto rentScooter(UUID userId, UUID scooterId);

    List<RentalDto> getAllRentals();

    RentalDto endRental(UUID rentalId);

    List<RentalDto> getRentalsByUserId(UUID userId);

    List<RentalDto> getRentalsByScooterId(UUID scooterId);

}
