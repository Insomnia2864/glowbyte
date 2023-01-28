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

import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OffersService
{
    static long PAGE_SIZE = 100000;
    static String BASE_FIO = "FIO";
    static Random rnd = new Random();

    OfferRepository offerRepository;

    // использовался serialized, чтобы во время выполнения транзакция не было новых записей, если фантомные чтения не проблема, то repeatable read подойдет
    @Transactional(isolation = Isolation.REPEATABLE_READ)
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
    public void fillData(Integer size)
    {
        long start = System.currentTimeMillis();

        List<Offer> offers = Arrays.stream(new Integer[size])
                .map(i -> Offer.builder()
                        .clientFIO(BASE_FIO + rnd.nextInt())
                        .exposable(getNextExposable())
                        .build())
                .toList();

        offerRepository.insertDataBatch(offers);

//        for (int i = 0; i < size; i++)
//        {
//            Offer offer = Offer
//                    .builder()
//                    .clientFIO(BASE_FIO + rnd.nextInt())
//                    .exposable(getNextExposable())
//                    .build();
//            offerRepository.insertData(offer);
//        }

        log.debug("Times spent on insert {}", System.currentTimeMillis() - start);
    }

    private boolean getNextExposable()
    {
        return rnd.nextInt(10) == 0;
    }

    private void sendData(List<Offer> offers)
    {
        log.info("Sending offers. Count: {}", offers.size());
        // Логика отправки записей
    }
}
