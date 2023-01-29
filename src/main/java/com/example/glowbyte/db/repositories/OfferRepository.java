package com.example.glowbyte.db.repositories;

import com.example.glowbyte.db.entities.Offer;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Types;
import java.util.List;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OfferRepository
{
    static String COUNT_QUERY = "select count(*) from test.offer;";
    static String SELECT_DATA = "select * from test.offer where exposable = 1;"; // хотел считать данные порционно, не знаю, стоит ли выгружать в память одним запросов 10 млн записей
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
        return namedParameterJdbcTemplate.query(SELECT_DATA, fullRowMapper());
    }

    public void insertDataBatch(List<Offer> offers)
    {
        MapSqlParameterSource[] params = new MapSqlParameterSource[offers.size()];
        for(int i = 0; i < offers.size(); i++)
        {
            MapSqlParameterSource param = new MapSqlParameterSource();
            param.addValue("exposable", offers.get(i).getExposable(), Types.NUMERIC);
            param.addValue("clientFio", offers.get(i).getClientFIO(), Types.VARCHAR);
            params[i] = param;
        }

        namedParameterJdbcTemplate.batchUpdate(INSERT_DATA, params);
    }
    public void insertData(Offer offer)
    {
        MapSqlParameterSource param = new MapSqlParameterSource();
        param.addValue("exposable", offer.getExposable(), Types.NUMERIC);
        param.addValue("clientFio", offer.getClientFIO(), Types.VARCHAR);

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

    private int[] getInsertTypes()
    {
        return new int[]{Types.NUMERIC, Types.NUMERIC};
    }
}
