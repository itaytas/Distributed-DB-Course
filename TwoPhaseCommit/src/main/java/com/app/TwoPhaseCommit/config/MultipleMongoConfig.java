package com.app.TwoPhaseCommit.config;

import com.mongodb.MongoClient;

import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;


@Configuration
@EnableConfigurationProperties(MultipleMongoProperties.class)
public class MultipleMongoConfig {

	private final MultipleMongoProperties mongoProperties;

	public MultipleMongoConfig(MultipleMongoProperties mongoProperties) {
		super();
		this.mongoProperties = mongoProperties;
	}

	@Primary
	@Bean(name = PrimaryMongoConfig.MONGO_TEMPLATE)
	public MongoTemplate primaryMongoTemplate() throws Exception {
		return new MongoTemplate(primaryFactory(this.mongoProperties.getPrimary()));
	}

	@Bean(name = SecondaryMongoConfig.MONGO_TEMPLATE)
	public MongoTemplate secondaryMongoTemplate() throws Exception {
		return new MongoTemplate(secondaryFactory(this.mongoProperties.getSecondary()));
	}

	@Bean
	@Primary
	public MongoDbFactory primaryFactory(final MongoProperties mongo) throws Exception {
		return new SimpleMongoDbFactory(new MongoClient(mongo.getHost(), mongo.getPort()), mongo.getDatabase());
	}

	@Bean
	public MongoDbFactory secondaryFactory(final MongoProperties mongo) throws Exception {
		return new SimpleMongoDbFactory(new MongoClient(mongo.getHost(), mongo.getPort()), mongo.getDatabase());
	}
}