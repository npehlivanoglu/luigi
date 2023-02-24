package be.vdab.luigi.domain;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PizzaPrijs {
    private final BigDecimal prijs;
    private final LocalDateTime vanaf;
    private final long pizzaId;

    public PizzaPrijs(@Positive BigDecimal prijs, LocalDateTime vanaf, @PositiveOrZero long pizzaId) {
        this.prijs = prijs;
        this.vanaf = vanaf;
        this.pizzaId = pizzaId;
    }

    public PizzaPrijs(@Positive BigDecimal prijs, @PositiveOrZero long pizzaId) {
        this(prijs, LocalDateTime.now(), pizzaId);
    }

    public BigDecimal getPrijs() {
        return prijs;
    }

    public LocalDateTime getVanaf() {
        return vanaf;
    }

    public long getPizzaId() {
        return pizzaId;
    }
}
