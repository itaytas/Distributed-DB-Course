package com.app.TwoPhaseCommit.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import static com.app.TwoPhaseCommit.config.PrimaryMongoConfig.MONGO_TEMPLATE;


@Configuration
@EnableMongoRepositories(
		basePackages = "com.app.TwoPhaseCommit.dal.primary",
        mongoTemplateRef = MONGO_TEMPLATE)
public class PrimaryMongoConfig {

    protected static final String MONGO_TEMPLATE = "primaryMongoTemplate";
}
