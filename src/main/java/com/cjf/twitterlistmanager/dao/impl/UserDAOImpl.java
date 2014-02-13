package com.cjf.twitterlistmanager.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.cjf.twitterlistmanager.dao.UserDAO;
import com.cjf.twitterlistmanager.model.User;

@Repository
public class UserDAOImpl implements UserDAO {

  private NamedParameterJdbcTemplate jdbcTemplate;

  @Autowired
  public void setDataSource(DataSource dataSource) {

    jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
  }

  @Override
  public User getUserById(int userId) {

    String query = "Select * From TwitterListManager.Users Where user_id = :userId";

    SqlParameterSource namedParameters =
        new MapSqlParameterSource("userId", Integer.valueOf(userId));
    User retVal = null;
    try {
      retVal = jdbcTemplate.queryForObject(query, namedParameters, new UserRowMapper());
    } catch (EmptyResultDataAccessException e) {
    }

    return retVal;
  }

  @Override
  public User getUserByName(String screenName) {

    String query = "Select * From TwitterListManager.Users Where screen_name = :screenName";

    SqlParameterSource namedParameters = new MapSqlParameterSource("screenName", screenName);
    User retVal = null;
    try {
      retVal = jdbcTemplate.queryForObject(query, namedParameters, new UserRowMapper());
    } catch (EmptyResultDataAccessException e) {
    }

    return retVal;
  }

  @Override
  public void saveUser(User user) {

    String query =
        "Update TwitterListManager.Users "
            + "Set user_id = :userId, screen_name = :screenName, access_token = :accessToken, "
            + "token_secret = :tokenSecret, token_raw = :tokenRaw " + "Where user_id = :userId";

    // Check if Update or Insert
    User tmpUser = this.getUserById(user.getUserId());
    if (tmpUser == null) {
      // Insert
      query =
          "Insert Into TwitterListManager.Users( user_id, screen_name, access_token, token_secret, token_raw) "
              + "Values( :userId, :screenName, :accessToken, :tokenSecret, :tokenRaw )";
    }

    Map<String, Object> namedParameters = new HashMap<String, Object>();
    namedParameters.put("userId", Integer.valueOf(user.getUserId()));
    namedParameters.put("screenName", user.getScreenName());
    namedParameters.put("accessToken", user.getAccessToken());
    namedParameters.put("tokenSecret", user.getTokenSecret());
    namedParameters.put("tokenRaw", user.getTokenRaw());
    jdbcTemplate.update(query, namedParameters);

  }

  private class UserRowMapper implements RowMapper<User> {

    @Override
    public User mapRow(ResultSet resultSet, int rowNum) throws SQLException {

      return new User(resultSet.getInt("user_id"), resultSet.getString("screen_name"),
          resultSet.getString("access_token"), resultSet.getString("token_secret"),
          resultSet.getString("token_raw"));
    }

  }

}
