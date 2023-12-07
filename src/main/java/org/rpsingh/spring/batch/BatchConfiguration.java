package org.rpsingh.spring.batch;

import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.batch.BatchProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
@EnableConfigurationProperties(BatchProperties.class)
public class BatchConfiguration {

    @Value("${file.input}")
    private String fileInput;

    @Bean
    public FlatFileItemReader<Coffee> reader() {
        return new FlatFileItemReaderBuilder<Coffee>().name("coffeeItemReader").resource(new ClassPathResource(fileInput)).delimited().names(
                new String[]{"brand", "origin", "characteristics"}).fieldSetMapper(new BeanWrapperFieldSetMapper<Coffee>() {{
            setTargetType(Coffee.class);
        }}).build();
    }


    @Bean
    public CoffeeItemProcessor processor() {
        return new CoffeeItemProcessor();
    }


}
