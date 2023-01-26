package com.example.glowbyte.db.repositories;

import com.example.glowbyte.db.entities.Offer;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OfferRepository
{
    static String COUNT_QUERY = "select count(*) from offers;";
    static String SELECT_PARTIAL_DATA = "select * from offers limit %s offset %s;";
    JdbcTemplate jdbcTemplate;

    public Long getDataCount()
    {
        return jdbcTemplate.queryForObject(COUNT_QUERY, rowMapperCount());
    }

    public List<Offer> partialSelect(Long pageSize, Long currentOffset)
    {
        String actualQuery = String.format(SELECT_PARTIAL_DATA, pageSize, currentOffset);
        return jdbcTemplate.query(SELECT_PARTIAL_DATA, fullRowMapper());
    }

    private RowMapper<Offer> fullRowMapper()
    {
        return (rs, rowNum) -> Offer.builder()
                .offerId(rs.getLong("offer_id"))
                .clientFIO(rs.getString("client_fio"))
                .exposable(mapExposable(rs.getInt("exposable")))
                .build();
    }

    private RowMapper<Long> rowMapperCount()
    {
        return (rs, rowNum) -> rs.getLong("count");
    }

    private Boolean mapExposable(Integer exposable)
    {
        return exposable != 0;
    }
}
