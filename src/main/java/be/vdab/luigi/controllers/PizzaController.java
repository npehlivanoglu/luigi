package be.vdab.luigi.controllers;

import be.vdab.luigi.domain.Pizza;
import be.vdab.luigi.exceptions.PizzaNietGevondenException;
import be.vdab.luigi.services.PizzaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.stream.Stream;

@RestController
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

    @GetMapping("pizzas/aantal")
    long findAantal() {
        return pizzaService.findAantal();
    }

    @GetMapping("pizzas/{id}")
    IdnaamPrijs findById(@PathVariable long id) {
        return pizzaService.findById(id)
                .map(IdnaamPrijs::new)
                .orElseThrow(
                        () -> new PizzaNietGevondenException(id));
    }

    @GetMapping("pizzas")
    Stream<IdnaamPrijs> findAll() {
        return pizzaService.findAll()
                .stream()
                .map(IdnaamPrijs::new);
    }

    @GetMapping(value = "pizzas", params = "naamBevat")
    Stream<IdnaamPrijs> findByNaamBevat(String naamBevat) {
        return pizzaService.findByNaamBevat(naamBevat)
                .stream()
                .map(IdnaamPrijs::new);
    }

    @GetMapping(value = "pizzas", params = {"vanPrijs", "totPrijs"})
    Stream<IdnaamPrijs> findByTussenPrijs(BigDecimal vanPrijs, BigDecimal totPrijs) {
        return pizzaService.findByPrijsTussen(vanPrijs, totPrijs)
                .stream()
                .map(IdnaamPrijs::new);
    }
}
