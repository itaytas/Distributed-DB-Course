package com.app.TwoPhaseCommit.api;

import com.app.TwoPhaseCommit.logic.transactions.TransactionEntity;
import com.app.TwoPhaseCommit.logic.transactions.TransactionState;

public class TransactionTO {

	private String source;
	private String destination;
	private double value;
	private TransactionState state;
	private long lastModified;

	public TransactionTO() {
	}

	public TransactionTO(String source, String destination, double value, TransactionState state, long lastModified) {
		this.source = source;
		this.destination = destination;
		this.value = value;
		this.state = state;
		this.lastModified = lastModified;
	}

	public TransactionTO(TransactionEntity entity) {
		this(
				entity.getSource(),
				entity.getDestination(),
				entity.getValue(),
				entity.getState(),
				entity.getLastModified());
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public TransactionState getState() {
		return state;
	}

	public void setState(TransactionState state) {
		this.state = state;
	}

	public long getLastModified() {
		return lastModified;
	}

	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}

	@Override
	public String toString() {
		return "TransactionTO [source=" + source + ", destination=" + destination + ", value=" + value + ", state="
				+ state + ", lastModified=" + lastModified + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((destination == null) ? 0 : destination.hashCode());
		result = prime * result + (int) (lastModified ^ (lastModified >>> 32));
		result = prime * result + ((source == null) ? 0 : source.hashCode());
		result = prime * result + ((state == null) ? 0 : state.hashCode());
		long temp;
		temp = Double.doubleToLongBits(value);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		TransactionTO other = (TransactionTO) obj;
		if (destination == null) {
			if (other.destination != null)
				return false;
		} else if (!destination.equals(other.destination))
			return false;
		if (lastModified != other.lastModified)
			return false;
		if (source == null) {
			if (other.source != null)
				return false;
		} else if (!source.equals(other.source))
			return false;
		if (state != other.state)
			return false;
		if (Double.doubleToLongBits(value) != Double.doubleToLongBits(other.value))
			return false;
		return true;
	}

}
