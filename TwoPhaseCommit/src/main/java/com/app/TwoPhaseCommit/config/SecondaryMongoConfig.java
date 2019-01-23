package com.app.TwoPhaseCommit.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import static com.app.TwoPhaseCommit.config.SecondaryMongoConfig.MONGO_TEMPLATE;


@Configuration
@EnableMongoRepositories(
		basePackages = "com.app.TwoPhaseCommit.dal.secondary",
        mongoTemplateRef = MONGO_TEMPLATE)
public class SecondaryMongoConfig {

    protected static final String MONGO_TEMPLATE = "secondaryMongoTemplate";
}
