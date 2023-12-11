package org.rpsingh.spring.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ItemProcessListener;
import org.springframework.batch.item.ItemProcessor;


@Slf4j
public class CoffeeItemProcessor implements ItemProcessor<Coffee, Coffee>, ItemProcessListener<Coffee,Coffee> {

    @Override
    public Coffee process(final Coffee coffee) {
        String brand = coffee.getBrand().toUpperCase();
        String origin = coffee.getOrigin().toUpperCase();

        String characteristics = coffee.getCharacteristics().toUpperCase();

        Coffee transformedCoffee = new Coffee(brand, origin, characteristics);
        log.info("Converting ( {} ) into ( {} )", coffee, transformedCoffee);

        return transformedCoffee;
    }

    @Override
    public void onProcessError(Coffee item, Exception exception) {
        log.error(" error message "+item.toString(), exception);
    }
}
