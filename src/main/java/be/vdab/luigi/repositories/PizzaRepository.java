package be.vdab.luigi.repositories;

import be.vdab.luigi.domain.Pizza;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class PizzaRepository {
    private final JdbcTemplate template;

    public PizzaRepository(JdbcTemplate template) {
        this.template = template;
    }

    public long findAantal() {
        var sql = """
                select count(*)
                from pizzas
                """;
        return template.queryForObject(sql, Long.class);
    }

    private final RowMapper<Pizza> pizzaMapper =
            (result, rowNum) -> new Pizza(
                    result.getLong("id"),
                    result.getString("naam"),
                    result.getBigDecimal("prijs"),
                    result.getBigDecimal("winst"));

    public Optional<Pizza> findById(long id) {
        try {
            var sql = """
                    select id,naam,prijs,winst
                    from pizzas
                    where id = ?
                    """;
            return Optional.of(template.queryForObject(sql, pizzaMapper, id));
        } catch (IncorrectResultSizeDataAccessException ex) {
            return Optional.empty();
        }
    }
}
