package com.cjf.twitterlistmanager.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.cjf.twitterlistmanager.dao.KeyDAO;
import com.cjf.twitterlistmanager.model.Key;

@Repository
public class KeyDAOImpl implements KeyDAO {

  private NamedParameterJdbcTemplate jdbcTemplate;

  @Autowired
  public void setDataSource(DataSource dataSource) {

    jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
  }

  @Override
  public Key getKeyById(int id) {

    String query = "Select * From TwitterListManager.Keys Where id = :id";

    SqlParameterSource namedParameters = new MapSqlParameterSource("id", Integer.valueOf(id));
    Key retVal = null;
    try {
      retVal = jdbcTemplate.queryForObject(query, namedParameters, new KeyRowMapper());
    } catch (EmptyResultDataAccessException e) {
    }

    return retVal;
  }

  @Override
  public Key getKeyByServiceName(String service) {

    String query = "Select * From TwitterListManager.Keys Where service = :service";

    SqlParameterSource namedParameters = new MapSqlParameterSource("service", service);
    Key retVal = null;
    try {
      retVal = jdbcTemplate.queryForObject(query, namedParameters, new KeyRowMapper());
    } catch (EmptyResultDataAccessException e) {
    }

    return retVal;
  }

  private class KeyRowMapper implements RowMapper<Key> {

    @Override
    public Key mapRow(ResultSet resultSet, int rowNum) throws SQLException {

      return new Key(resultSet.getInt("id"), resultSet.getString("service"),
          resultSet.getString("api_key"), resultSet.getString("api_secret"));
    }

  }

}
