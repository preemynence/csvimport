package com.preemynence.csvimport.dao;

public enum DataTypes {
	INTEGER(""), VARCHAR("'"), SERIAL(""), TIMESTAMP("'"), INT4("");


	String isQuotes;

	DataTypes(String isQuotes) {
		this.isQuotes = isQuotes;
	}

	public String isQuotesRequired() {
		return this.isQuotes;
	}
}
