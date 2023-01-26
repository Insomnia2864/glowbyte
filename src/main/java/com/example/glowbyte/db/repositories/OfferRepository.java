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
    static String COUNT_QUERY = "select count(*) from test.offer;";
    static String SELECT_PARTIAL_DATA = "select * from test.offer limit %s offset %s;";
    static String INSERT_DATA = "insert into test.offer (exposable, client_fio) values (%s, '%s');";
    JdbcTemplate jdbcTemplate;

    public Long getDataCount()
    {
        return jdbcTemplate.queryForObject(COUNT_QUERY, rowMapperCount());
    }

    public List<Offer> partialSelect(Long pageSize, Long currentOffset)
    {
        String actualQuery = String.format(SELECT_PARTIAL_DATA, pageSize, currentOffset);
        return jdbcTemplate.query(actualQuery, fullRowMapper());
    }

    public void insertData(Offer offer)
    {
        String actualQuery = String.format(INSERT_DATA, mapExposable(offer.getExposable()), offer.getClientFIO());
        jdbcTemplate.execute(actualQuery);
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
    private Integer mapExposable(Boolean exposable)
    {
        return exposable ? 1 : 0;
    }
}
