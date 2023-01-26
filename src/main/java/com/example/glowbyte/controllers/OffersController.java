package com.example.glowbyte.controllers;

import com.example.glowbyte.services.OffersService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/send/offers")
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
}
