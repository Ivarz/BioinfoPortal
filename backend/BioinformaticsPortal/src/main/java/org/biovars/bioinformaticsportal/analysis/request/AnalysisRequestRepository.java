package org.biovars.bioinformaticsportal.analysis.request;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.shaded.gson.Gson;
import org.biovars.bioinformaticsportal.analysis.request.details.AnalysisRequestDetails;
import org.biovars.bioinformaticsportal.analysis.request.statusupdate.AnalysisRequestStatusUpdateDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Repository class for performing database operations on the "analysis_requests" table.
 * This class provides methods to save, retrieve, update, and delete analysis request records.
 * It uses JDBC for interacting with the database and maps query results to the
 * {@code AnalysisRequest} domain object.
 *
 * Annotations:
 * - {@code @Repository}: Marks this class as a Spring-managed bean and a Data Access Object (DAO).
 */
@Repository
public class AnalysisRequestRepository {
    private final JdbcTemplate jdbcTemplate;

    private static final Logger logger = LoggerFactory.getLogger(AnalysisRequestRepository.class);

    AnalysisRequestRepository(@Qualifier("portalJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    List<AnalysisRequest> findByUserIdAndAnlysisId(String userId, String analysisId) {
        String sql = "SELECT * FROM analysis_requests WHERE user_id = ? AND id = ?";
        return jdbcTemplate.query(sql, new AnalysisRequestRepository.AnalysisRowMapper(), userId, analysisId);
    }
    void save(AnalysisRequest analysisRequest) {
        logger.info("saving analysis request");
        logger.info(analysisRequest.toString());
        String query = "INSERT INTO analysis_requests " +
                " (id, user_id, status_id, fulfilled, failed, details)" +
                " VALUES(?, ?, ?, ?, ?, ?::jsonb)";
        var mapper = new ObjectMapper();
        try {
            var jsonString = mapper.writeValueAsString(analysisRequest.details());
            jdbcTemplate.update(query,
                    analysisRequest.id(),
                    analysisRequest.userId(),
                    analysisRequest.statusId(),
                    false,
                    false,
                    jsonString);
        } catch(JsonProcessingException e) {
            logger.error(e.getMessage());
        }
    }

    List<AnalysisRequest> findAll() {
        String sql = "SELECT * FROM analysis_requests";
        return jdbcTemplate.query(sql, new AnalysisRequestRepository.AnalysisRowMapper());

    }

    List<AnalysisRequest> findByUser(String userId) {
        String sql = "SELECT * FROM analysis_requests WHERE user_id = ?";
        return jdbcTemplate.query(sql, new AnalysisRequestRepository.AnalysisRowMapper(), userId);
    }

    void updateStatus(AnalysisRequestStatusUpdateDTO statusUpdateDTO) {
        String sql = "UPDATE analysis_requests SET status_id = ?, fulfilled = ?, failed = ? WHERE id = ? and user_id = ?";
        jdbcTemplate.update(sql,
                statusUpdateDTO.statusId(),
                statusUpdateDTO.fulfilled(),
                statusUpdateDTO.failed(),
                statusUpdateDTO.id(),
                statusUpdateDTO.userId()
        );
    }
    void update(AnalysisRequest analysisRequest) {
        logger.info("updating analysis request");
        logger.debug(analysisRequest.toString());
        String sql = "UPDATE analysis_requests SET status_id = ?, fulfilled = ?, failed = ?, details = ?::jsonb WHERE id = ? and user_id = ?";
        var mapper = new ObjectMapper();
        try {
            var jsonString = mapper.writeValueAsString(analysisRequest.details());
            jdbcTemplate.update(sql,
                    analysisRequest.statusId(),
                    analysisRequest.fulfilled(),
                    analysisRequest.failed(),
                    jsonString,
                    analysisRequest.id(),
                    analysisRequest.userId()
            );
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage());
        }
    }

    public void deleteByUserIdAndAnalysisId(String userId, String analysisId) {
        String sql = "DELETE FROM analysis_requests WHERE user_id = ? AND id = ?";
        jdbcTemplate.update(sql, userId, analysisId);
    }


    private static class AnalysisRowMapper implements RowMapper<AnalysisRequest> {
        @Override
        public AnalysisRequest mapRow(ResultSet resultSet, int rowNum) throws SQLException {
            var mapper = new ObjectMapper();
            String id = resultSet.getString("id");
            String userId = resultSet.getString("user_id");
            long statusId = resultSet.getLong("status_id");
            boolean fulfilled = resultSet.getBoolean("fulfilled");
            boolean failed = resultSet.getBoolean("failed");
            String details = resultSet.getString("details");
            try {
                return new AnalysisRequest(id,
                        userId,
                        statusId,
                        fulfilled,
                        failed,
                        mapper.readValue(details, AnalysisRequestDetails.class)
                );
            } catch (JsonProcessingException e) {
                logger.error(e.getMessage());
                return null;
            }
        }
    }
}
