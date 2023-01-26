package com.example.glowbyte.services;

import com.example.glowbyte.db.entities.Offer;
import com.example.glowbyte.db.repositories.OfferRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OffersService
{
    static long PAGE_SIZE = 1000;
    static String BASE_FIO = "FIO";
    static Random rnd = new Random();

    OfferRepository offerRepository;

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void sendData()
    {
        Long dataCount = offerRepository.getDataCount();
        long sentCount = 0L;

        for (long currentOffset = 0L; currentOffset < dataCount; currentOffset+=PAGE_SIZE)
        {
            List<Offer> offers = offerRepository.partialSelect(PAGE_SIZE, currentOffset).stream()
                    .filter(Offer::getExposable)
                    .toList();

            sentCount += offers.size();

            sendData(offers);
        }

        log.info("Total sent data: {}", sentCount);
    }

    @Transactional
    public void fillData(Long size)
    {
        for (long i = 0; i < size; i++)
        {
            Offer offer = Offer
                    .builder()
                    .clientFIO(BASE_FIO + rnd.nextInt())
                    .exposable(rnd.nextBoolean())
                    .build();
            offerRepository.insertData(offer);
        }
    }

    private void sendData(List<Offer> offers)
    {
        log.info("Sending offers. Count: {}", offers.size());
        // Логика отправки записей
    }
}
