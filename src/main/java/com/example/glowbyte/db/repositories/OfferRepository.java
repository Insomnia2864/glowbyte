package com.example.glowbyte.db.repositories;

import com.example.glowbyte.db.entities.Offer;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OfferRepository
{
    static String COUNT_QUERY = "select count(*) from test.offer;";
    static String SELECT_PARTIAL_DATA = "select * from test.offer where exposable = 1;";
    static String INSERT_DATA = "insert into test.offer (exposable, client_fio) values (:exposable, :clientFio);";
    DataSource dataSource;
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    public Long getDataCount()
    {
        JdbcTemplate jdbcTemplate = namedParameterJdbcTemplate.getJdbcTemplate();
        return jdbcTemplate.queryForObject(COUNT_QUERY, rowMapperCount());
    }

    public List<Offer> selectAll()
    {
        String actualQuery = String.format(SELECT_PARTIAL_DATA);
        return namedParameterJdbcTemplate.query(actualQuery, fullRowMapper());
    }

    public void insertDataBatch(List<Offer> offers)
    {
        Map<String, Object>[] params = offers.stream()
                        .map(offer ->
                        {
                            Map<String, Object> param = new HashMap<>();
                            param.put("exposable", mapExposable(offer.getExposable()));
                            param.put("clientFio", offer.getClientFIO());
                            return param;
                        }).toArray(HashMap[]::new);

        namedParameterJdbcTemplate.batchUpdate(INSERT_DATA, params);
    }

    public void insertData(Offer offer)
    {
        Map<String, Object> param = new HashMap<>();
        param.put("exposable", mapExposable(offer.getExposable()));
        param.put("clientFio", offer.getClientFIO());
        namedParameterJdbcTemplate.update(INSERT_DATA, param);
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
