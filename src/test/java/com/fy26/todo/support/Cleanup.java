package com.fy26.todo.support;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Table;
import jakarta.persistence.metamodel.EntityType;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class Cleanup {

    private final JdbcTemplate jdbcTemplate;
    private final EntityManager entityManager;

    public Cleanup(JdbcTemplate jdbcTemplate, EntityManager entityManager) {
        this.jdbcTemplate = jdbcTemplate;
        this.entityManager = entityManager;
    }

    public void all() {
        try (final Connection conn = jdbcTemplate.getDataSource().getConnection();
            final Statement stmt = conn.createStatement()) {
            stmt.execute("SET FOREIGN_KEY_CHECKS = 0");
            for (final EntityType<?> entity : entityManager.getMetamodel().getEntities()) {
                final String table = getTableName(entity);
                stmt.execute("TRUNCATE TABLE `" + table + "`");
            }
            stmt.execute("SET FOREIGN_KEY_CHECKS = 1");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private String getTableName(final EntityType<?> entity) {
        final Table table = entity.getJavaType()
                .getAnnotation(Table.class);
        if (table != null && !table.name().isBlank()) {
            return table.name();
        }
        return entity.getName()
                .replaceAll("([a-z])([A-Z])", "$1_$2")
                .toLowerCase();
    }
}
