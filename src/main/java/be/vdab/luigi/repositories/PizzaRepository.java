package be.vdab.luigi.repositories;

import be.vdab.luigi.domain.Pizza;
import be.vdab.luigi.exceptions.PizzaNietGevondenException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@Repository
public class PizzaRepository {
    private final JdbcTemplate template;

    public PizzaRepository(JdbcTemplate template) {
        this.template = template;
    }
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
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

    public List<Pizza> findAll() {
        var sql = """
                select id,naam,prijs,winst
                from pizzas
                order by naam
                """;
        return template.query(sql, pizzaMapper);
    }

    public List<Pizza> findByNaamBevat(String woord) {
        var sql = """
                select id,naam,prijs,winst
                from pizzas
                where naam like ?
                order by naam
                """;
        return template.query(sql, pizzaMapper, "%" + woord + "%");
    }

    public List<Pizza> findByPrijsTussen(BigDecimal van, BigDecimal tot) {
        var sql = """
                select id,naam,prijs,winst
                from pizzas
                where prijs between ? and ?
                order by prijs
                """;
        return template.query(sql, pizzaMapper, van, tot);
    }

    public void delete(long id) {
        var sql = """
                delete from pizzas
                where id = ?
                """;
        template.update(sql, id);
    }

    public long create(Pizza pizza) {
        var sql = """
                insert into pizzas(naam,prijs,winst)
                values(?,?,?)
                """;
        var keyHolder = new GeneratedKeyHolder();
        template.update(con -> {
            var statement = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            statement.setString(1, pizza.getNaam());
            statement.setBigDecimal(2, pizza.getPrijs());
            statement.setBigDecimal(3, pizza.getWinst());
            return statement;
        }, keyHolder);
        return keyHolder.getKey().longValue();
    }

    public void updatePrijs(long id, BigDecimal prijs) {
        var sql = """
                update pizzas
                set prijs = ?
                where id = ?
                """;
        if (template.update(sql, prijs, id) == 0) {
            logger.info("update poging van onbestaande pizza {}",id);
            throw new PizzaNietGevondenException(id);
        }
    }
}
