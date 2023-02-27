package be.vdab.luigi.controllers;

import be.vdab.luigi.domain.Pizza;
import be.vdab.luigi.domain.PizzaPrijs;
import be.vdab.luigi.dto.NieuwePizza;
import be.vdab.luigi.exceptions.PizzaNietGevondenException;
import be.vdab.luigi.services.PizzaService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Stream;

@RestController
@RequestMapping("pizzas")
class PizzaController {
    private final PizzaService pizzaService;

    PizzaController(PizzaService pizzaService) {
        this.pizzaService = pizzaService;
    }

    private record IdnaamPrijs(long id, String naam, BigDecimal prijs) {
        IdnaamPrijs(Pizza pizza) {
            this(pizza.getId(), pizza.getNaam(), pizza.getPrijs());
        }
    }

    private record PrijsWijziging(@NotNull @Positive BigDecimal prijs) {
    }

    private record PrijsVanaf(BigDecimal prijs, LocalDateTime vanaf) {
        PrijsVanaf(PizzaPrijs pizzaPrijs) {
            this(pizzaPrijs.getPrijs(), pizzaPrijs.getVanaf());
        }
    }

    @GetMapping("aantal")
    long findAantal() {
        return pizzaService.findAantal();
    }

    @GetMapping("{id}")
    IdnaamPrijs findById(@PathVariable long id) {
        return pizzaService.findById(id)
                .map(IdnaamPrijs::new)
                .orElseThrow(
                        () -> new PizzaNietGevondenException(id));
    }

    @GetMapping
    Stream<IdnaamPrijs> findAll() {
        return pizzaService.findAll()
                .stream()
                .map(IdnaamPrijs::new);
    }

    @GetMapping( params = "naamBevat")
    Stream<IdnaamPrijs> findByNaamBevat(String naamBevat) {
        return pizzaService.findByNaamBevat(naamBevat)
                .stream()
                .map(IdnaamPrijs::new);
    }

    @GetMapping(params = {"vanPrijs", "totPrijs"})
    Stream<IdnaamPrijs> findByTussenPrijs(BigDecimal vanPrijs, BigDecimal totPrijs) {
        return pizzaService.findByPrijsTussen(vanPrijs, totPrijs)
                .stream()
                .map(IdnaamPrijs::new);
    }

    @DeleteMapping("{id}")
    void delete(@PathVariable long id) {
        pizzaService.delete(id);
    }

    @PostMapping
    long create(@RequestBody @Valid NieuwePizza nieuwePizza) {
        return pizzaService.create(nieuwePizza);
    }

    @PatchMapping("{id}/prijs")
    void updatePrijs(@PathVariable long id, @RequestBody @Valid PrijsWijziging wijziging) {
        var pizzaPrijs = new PizzaPrijs(wijziging.prijs, id);
        pizzaService.updatePrijs(pizzaPrijs);
    }

    @GetMapping("{id}/prijzen")
    Stream<PrijsVanaf> findPrijzen(@PathVariable long id) {
        return pizzaService.findPrijzen(id)
                .stream()
                .map(
                        pizzaPrijs -> new PrijsVanaf(pizzaPrijs));
    }
}
