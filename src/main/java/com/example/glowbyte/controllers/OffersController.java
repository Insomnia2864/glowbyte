package com.example.glowbyte.controllers;

import com.example.glowbyte.services.OffersService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/send/offers")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OffersController
{
    OffersService offersService;

    @PostMapping
    public ResponseEntity sendOffers()
    {
        try
        {
            offersService.sendData();
            return new ResponseEntity(HttpStatus.OK);
        }
        catch (Exception exception)
        {
            log.error("Error occurred processing send offers request", exception);
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping
    public ResponseEntity fillRandomData(@RequestParam(value = "size") @NonNull Integer size)
    {
        try
        {
            offersService.fillData(size);
            return new ResponseEntity(HttpStatus.OK);
        }
        catch (Exception exception)
        {
            log.error("Error occurred filling Data", exception);
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
