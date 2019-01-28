package com.app.TwoPhaseCommit.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "mongodb")
public class MultipleMongoProperties {

	@Value("${mongodb.primary.host:localhost}")
	private String PRIMARY_HOST;

	@Value("${mongodb.primary.port:27017}")
	private Integer PRIMARY_PORT;

	@Value("${mongodb.primary.database:primary}")
	private String PRIMARY_DB_NAME;

	@Value("${mongodb.secondary.host:localhost}")
	private String SECONDARY_HOST;

	@Value("${mongodb.secondary.port:27018}")
	private Integer SECONDARY_PORT;

	@Value("${mongodb.secondary.database:secondary}")
	private String SECONDARY_DB_NAME;

	private MongoProperties primary = new MongoProperties();
	private MongoProperties secondary = new MongoProperties();

	public MultipleMongoProperties() {
		super();
		this.primary.setHost(PRIMARY_HOST);
		this.primary.setPort(PRIMARY_PORT);
		this.primary.setDatabase(PRIMARY_DB_NAME);

		this.secondary.setHost(SECONDARY_HOST);
		this.secondary.setPort(SECONDARY_PORT);
		this.secondary.setDatabase(SECONDARY_DB_NAME);
	}

	public MongoProperties getPrimary() {
		return primary;
	}

	public void setPrimary(MongoProperties primary) {
		this.primary = primary;
	}

	public MongoProperties getSecondary() {
		return secondary;
	}

	public void setSecondary(MongoProperties secondary) {
		this.secondary = secondary;
	}

	@Override
	public String toString() {
		return "MultipleMongoProperties [primary=" + primary + ", secondary=" + secondary + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((primary == null) ? 0 : primary.hashCode());
		result = prime * result + ((secondary == null) ? 0 : secondary.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MultipleMongoProperties other = (MultipleMongoProperties) obj;
		if (primary == null) {
			if (other.primary != null)
				return false;
		} else if (!primary.equals(other.primary))
			return false;
		if (secondary == null) {
			if (other.secondary != null)
				return false;
		} else if (!secondary.equals(other.secondary))
			return false;
		return true;
	}

}
