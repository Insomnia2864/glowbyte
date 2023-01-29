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
    static int PAGE_SIZE = 100000;
    static String BASE_FIO = "FIO";
    static Random rnd = new Random();

    OfferRepository offerRepository;

    // использовался serialized, чтобы во время выполнения транзакция не было новых записей, если фантомные чтения не проблема, то repeatable read подойдет
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void sendData()
    {
        List<Offer> offers = offerRepository.selectAll();
        sendData(offers);
    }


    // batch insert 14637 ms for 1m
    // single insert 280197 ms for 1m
    @Transactional
    public void fillData(Integer size)
    {
        long start = System.currentTimeMillis();
        long iterationCounter = 1;
        while (size != 0)
        {
            log.info("Iteration number {}", iterationCounter++);
            int currentValue = Math.min(size, PAGE_SIZE);
            List<Offer> offers = Arrays.stream(new Integer[currentValue])
                    .map(i -> Offer.builder()
                            .clientFIO(BASE_FIO + rnd.nextInt())
                            .exposable(getNextExposable())
                            .build())
                    .toList();

            offerRepository.insertDataBatch(offers);
            size -= currentValue;
        }

//        for (int i = 0; i < size; i++)
//        {
//            Offer offer = Offer
//                    .builder()
//                    .clientFIO(BASE_FIO + rnd.nextInt())
//                    .exposable(getNextExposable())
//                    .build();
//            offerRepository.insertData(offer);
//        }

        log.info("Times spent on insert {}", System.currentTimeMillis() - start);
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
