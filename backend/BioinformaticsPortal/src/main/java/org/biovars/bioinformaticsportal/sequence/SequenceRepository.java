package org.biovars.bioinformaticsportal.sequence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.BiFunction;

@Repository
public class SequenceRepository {
    private final JdbcTemplate jdbcTemplate;

    private static final Logger logger = LoggerFactory.getLogger(SequenceRepository.class);

    public SequenceRepository(@Qualifier("sequenceJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    static String preprocessField(String field) {
        return (field == null) ? "%" : "%" + field + "%";
    }

    /**
     * Retrieves sequences matching the given parameters using a SQL LIKE query.
     * Null or empty parameters are treated as wildcards.
     *
     * @param sampleId The sample ID to filter sequences by (supports wildcard matching).
     * @param runId    The run ID to filter sequences by (supports wildcard matching).
     * @param lane     The lane to filter sequences by (supports wildcard matching).
     * @param type     The type to filter sequences by (supports wildcard matching, case-insensitive).
     * @return A list of Sequence objects matching the criteria.
     */
    public List<Sequence> findLikeSequence(String sampleId,
                                           String runId,
                                           String lane,
                                           String type) {
        sampleId = preprocessField(sampleId);
        runId = preprocessField(runId);
        lane = preprocessField(lane);
        type = preprocessField(type);
        String sql = "SELECT record_id, sample_id, run_id, lane, barcode, type, mate1, mate2 FROM file_locations"
                +" WHERE sample_id like ? AND run_id like ? AND lane like ? AND UPPER(type) like UPPER(?)";
        logger.info(sql);
        return jdbcTemplate.query(sql, new SequenceRowMapper(), sampleId, runId, lane, type);

    }

    
    /**
     * Performs a fuzzy search for sequences based on the criteria provided in the BatchSequenceSearchFormat.
     * Conditions are dynamically constructed for filtering sequences by sample IDs, run IDs, lanes, and types using
     * LIKE expressions.
     *
     * @param batchSearchInput The input containing search criteria, where negative filters are prefixed with '-'.
     * @return A list of matching Sequence objects, or an empty list if no matches are found.
     */
    public List<Sequence> fuzzyFindSequences(BatchSequenceSearchFormat batchSearchInput) {
        batchSearchInput = batchSearchInput.removeEmptyValues();
        if (batchSearchInput.isEmpty()) {
            return new ArrayList<>();
        }
        batchSearchInput.sortDescending();
        List<String> fuzzyFiltersStrings = generateFilterStrings(batchSearchInput.sampleIds(),
                batchSearchInput.runIds(),
                batchSearchInput.lanes(),
                batchSearchInput.types(),
                SequenceRepository::generateFuzzyFilterString);

        logger.info(fuzzyFiltersStrings.toString());
        var cleanSearchInput = batchSearchInput.removeNegateSymbol().encloseInModulus();
        return jdbcTemplate.query("SELECT record_id, sample_id, run_id, lane, barcode, type, mate1, mate2 FROM file_locations"
                + ( fuzzyFiltersStrings.size() > 0 ?
                        (" WHERE "+ String.join(" AND ", fuzzyFiltersStrings))
                : "")
                ,
                new SequenceRowMapper(), cleanSearchInput.toArray()
                );

    }

    /**
     * Finds sequences with an exact match for the given sample ID, run ID, and lane.
     *
     * @param sampleId The exact sample ID to filter by.
     * @param runId    The exact run ID to filter by.
     * @param lane     The exact lane to filter by.
     * @return A list of matching Sequence objects, or an empty list if no matches are found.
     */
    List<Sequence> findBySampleIdRunIdLane(String sampleId,
                            String runId,
                            String lane) {
        String sql = "SELECT record_id, sample_id, run_id, lane, barcode, type, mate1, mate2 "+
                "FROM file_locations WHERE sample_id = ? AND run_id = ? AND lane = ?";
        return jdbcTemplate.query(sql, new SequenceRowMapper(), sampleId, runId, lane);
    }

    private static long countValuesToFilterOut(List<String> values) {
        return values
                .stream()
                .filter(x -> !x.isEmpty() && x.charAt(0) == '-')
                .count();
    }
    
    /**
     * Generates a SQL filter string for applying fuzzy search conditions to a specified column using the given values.
     * 
     * The method constructs SQL conditions for values that should match (using LIKE) and values to exclude (using NOT LIKE).
     * - If a value begins with the '-' character, it is treated as a negative filter (NOT LIKE).
     * - Positive matches are combined with OR clauses.
     * - Negative matches are combined with AND clauses.
     * - The resulting filter includes these conditions joined by an AND clause.
     *
     * Example:
     *   For values = ["apple", "-banana"] and valueColname = "fruit", the output would be:
     *   "(fruit LIKE ? OR fruit LIKE ?) AND (fruit NOT LIKE ?)"
     *
     * @param values       A list of strings that represent the fuzzy search values.
     *                     Strings starting with '-' will be excluded using NOT LIKE in SQL.
     *                     Example: ["apple", "orange", "-banana"] treats "apple" and "orange" as inclusions
     *                     and "-banana" as an exclusion.
     * @param valueColname The database column name to which the fuzzy search conditions apply.
     * @return A SQL-compatible condition string, such as "(column_name LIKE '%' || ? || '%' OR ...)".
     */
    /**
     * Generates a SQL filter string for fuzzy search conditions applied to a column with given values.
     * Includes positive matches using LIKE and negative matches using NOT LIKE.
     *
     * @param values       A list of strings used as fuzzy filters, where negative filters are prefixed with '-'.
     * @param valueColname The column name to apply the filter on.
     * @return The SQL-compatible filter string for the provided column and values.
     */
    private static String generateFuzzyFilterString(List<String> values, String valueColname) {
    
        if (values.size() == 0) {
            return "";
        }
        List<String> filterConditions = new ArrayList();
        long valuesToFilterOut = countValuesToFilterOut(values);
        long valuesToKeep = values.size() - valuesToFilterOut;
        if (valuesToKeep > 0) {
            String runIdFilter = String
                    .join(" OR ",
                            Collections.nCopies((int) valuesToKeep, valueColname + " LIKE ?"));
            filterConditions.add("(" + runIdFilter + ")");
        }
    
        if ( valuesToFilterOut > 0 ) {
            String runIdFilter = String
                    .join(" AND ",
                            Collections.nCopies((int) valuesToFilterOut, valueColname + " NOT LIKE ?"));
            filterConditions.add("(" + runIdFilter + ")");
        }
        return String.join(" AND ", filterConditions);
    
    }

    
    
    /**
     * Generates a SQL-compatible filter string for applying exact match conditions to a specified column 
     * using the provided list of values.
     * 
     * - Positive matches (IN clause) are constructed for values that do not begin with the '-' character.
     * - Negative matches (NOT IN clause) are constructed for values starting with the '-' character.
     * - The final filter string consists of these conditions combined with AND clauses.
     *
     * Example:
     *   For values = ["apple", "-banana"] and valueColname = "fruit", the output would be:
     *   "( fruit IN (?, ?) AND fruit NOT IN (?))"
     *
     * @param values       A list of input values where values starting with '-' are treated as exclusions.
     *                     Example: ["apple", "orange", "-banana"] includes "apple" and "orange" while excluding "banana".
     * @param valueColname The name of the column in the database to filter.
     * @return A SQL fragment string for the given column and values.
     */
    /**
     * Constructs an SQL-compatible filter string for exact matches based on the provided values for a column.
     * Handles both positive (IN) and negative (NOT IN) filtering conditions.
     *
     * @param values       A list of input values where entries starting with '-' are treated as exclusions.
     * @param valueColname The database column to which filters apply.
     * @return The constructed SQL-compatible filter string.
     */
    private static String generateFilterString(List<String> values, String valueColname) {
        ArrayList<String> filterConditions = new ArrayList();
        long valuesToFilterOut = countValuesToFilterOut(values);
        long valuesToKeep = values.size() - valuesToFilterOut;
        if (valuesToKeep > 0) {
            filterConditions
                .add(" " + valueColname + " IN (" +
                        String.join(",", Collections.nCopies((int) valuesToKeep, "?")) +
                        ") ");
        }
        if (valuesToFilterOut > 0) {
            filterConditions
                    .add(" " + valueColname + " NOT IN (" +
                            String.join(",", Collections.nCopies((int) valuesToFilterOut, "?")) +
                            ") ");
        }
        return " (" + String.join(" AND ", filterConditions) + ") ";

    }

    /**
     * Constructs a list of filter strings for various columns (like sample IDs, run IDs, lanes, etc.)
     * using the provided string generation function.
     *
     * @param sampleIds The list of sample IDs to generate filter conditions for.
     * @param runIds    The list of run IDs to generate filter conditions for.
     * @param lanes     The list of lanes to generate filter conditions for.
     * @param types     The list of types to generate filter conditions for.
     * @param stringGen A BiFunction that generates filter strings for a list of values and a column name.
     * @return A list of SQL-compatible filter conditions for the provided columns.
     */
    private static List<String> generateFilterStrings( List<String> sampleIds,
                                                       List<String> runIds,
                                                      List<String> lanes,
                                                      List<String> types,
                                                      BiFunction<List<String>, String, String> stringGen
    ) {
        List<String> filters = new ArrayList();
        if (!sampleIds.isEmpty()) {
            filters.add(stringGen.apply(sampleIds, "sample_id"));
        }
        if (!runIds.isEmpty()) {
            filters.add(stringGen.apply(runIds, "run_id"));
        }
        if (!lanes.isEmpty()) {

            filters.add(stringGen.apply(lanes, "lane"));
        }
        if (!types.isEmpty()) {
            filters.add(stringGen.apply(types, "type"));
        }
        return filters;
    }

    /**
     * Creates a temporary database table and populates it with the specified sample IDs, applying unique identifiers for safety.
     *
     * @param sampleIds The list of sample IDs to insert into the temporary table.
     * @return The name of the created temporary table, derived from a unique identifier.
     */
    private String createTmpSampleIdTable(List<String> sampleIds) {
        String dbName = "tbl"+UUID.randomUUID().toString().replace("-", "");
        String createTmpTable = "CREATE TEMPORARY TABLE "+ dbName + " ( id INT PRIMARY KEY, sample_id VARCHAR(255))";
        jdbcTemplate.execute(createTmpTable);
        int id = 1;
        for (String sampleId : sampleIds) {
            jdbcTemplate.update("INSERT INTO " + dbName + " (id, sample_id) VALUES (?, ?)", id, sampleId);
            id += 1;
        }
        return dbName;
    }

    
    /**
     * Performs a batch left join operation between a temporary table of sample IDs and the filtered file_locations table.
     * This is used to retrieve sequences for a batch of samples while applying optional filters for run IDs, lanes, and types.
     * The temporary tables ensure isolation and prevent accidental clashes during concurrent execution.
     *
     * @param batchSearchInput The input containing sample IDs, run IDs, lanes, and types used for filtering.
     *                         Sample IDs must be present; other parameters are optional.
     * @return A list of {@link Sequence} objects that result from the left join operation.
     *         If no sample IDs or filters are provided, returns an empty list.
     */
    public List<Sequence> batchLeftJoinSequencesFull(BatchSequenceSearchFormat batchSearchInput) {

        batchSearchInput = batchSearchInput.removeEmptyValues();
        if (batchSearchInput.sampleIds().isEmpty()) {
            return new ArrayList<Sequence>();
        }
        batchSearchInput.sortDescending();

        List<String> filteringParams = new ArrayList<>();
        filteringParams.addAll(batchSearchInput.removeNegateSymbol().runIds());
        filteringParams.addAll(batchSearchInput.removeNegateSymbol().lanes());
        filteringParams.addAll(batchSearchInput.removeNegateSymbol().types());

        String dbName = this.createTmpSampleIdTable(batchSearchInput.sampleIds());

        String filteredFileLocTableName = "tbl"+UUID.randomUUID().toString().replace("-", "");

        boolean mustFilterFileLocTable =
                !batchSearchInput.runIds().isEmpty() ||
                !batchSearchInput.lanes().isEmpty() ||
                !batchSearchInput.types().isEmpty();

        List<String> filters = generateFilterStrings(
                new ArrayList<>(),
                batchSearchInput.runIds(),
                batchSearchInput.lanes(),
                batchSearchInput.types(),
                SequenceRepository::generateFilterString);

        String createFilteredFileLocTable = "CREATE TEMPORARY TABLE "+ filteredFileLocTableName +
                " AS SELECT * FROM file_locations " +
                (mustFilterFileLocTable ?
                        " WHERE " + String.join(" AND ", filters)
                        : "");
        logger.debug(createFilteredFileLocTable);
        logger.debug(createFilteredFileLocTable);
        logger.debug(filteringParams.toString());
        int rowsInFilteredFileLocTable = jdbcTemplate.update(createFilteredFileLocTable, filteringParams.toArray());

        String query = "SELECT "+
                dbName+".id as record_id, "+
                dbName+".sample_id as sample_id, "+
                filteredFileLocTableName+".run_id as run_id, "+
                filteredFileLocTableName+".lane as lane, "+
                filteredFileLocTableName+".barcode as barcode, "+
                filteredFileLocTableName+".type as type, "+
                filteredFileLocTableName+".mate1 as mate1,"+
                filteredFileLocTableName+".mate2 as mate2"+
                " FROM " + dbName +
                " LEFT JOIN " + filteredFileLocTableName +
                " ON " + dbName+".sample_id = "+filteredFileLocTableName+".sample_id " +
                " ORDER BY run_id, sample_id ASC";
        logger.debug("Perform query");
        logger.debug(query);
        List<Sequence> result = jdbcTemplate.query(query, new SequenceRowMapper());
        jdbcTemplate.execute("DROP TABLE " + dbName);
        return result;
    }
    /**
     * Checks if the database is in maintenance mode by querying the 'flags' table.
     * Maintenance mode is determined by the value of the 'maintenance_mode_on' flag.
     *
     * @return A {@link SequenceDbMaintenance} object indicating whether the maintenance mode is enabled.
     */
    SequenceDbMaintenance isMaintenanceModeOn() {
        String sql = "SELECT value FROM flags WHERE flag = 'maintenance_mode_on'";
        var res = jdbcTemplate.queryForList(sql);
        boolean maintenanceModeOn = res.getFirst().get("value").equals(true);
        return new SequenceDbMaintenance(maintenanceModeOn);
    }

    private static class SequenceRowMapper implements RowMapper<Sequence> {
        @Override
        public Sequence mapRow(ResultSet resultSet, int rowNum) throws SQLException {
            long id = resultSet.getLong("record_id");
            String sampleId = resultSet.getString("sample_id");
            String runId = resultSet.getString("run_id");
            String lane = resultSet.getString("lane");
            String barcode = resultSet.getString("barcode");
            String type = resultSet.getString("type");
            String mate1 = resultSet.getString("mate1");
            String mate2 = resultSet.getString("mate2");
            return new Sequence(id, sampleId, runId, lane, barcode, type, mate1, mate2);
        }
    }
}
