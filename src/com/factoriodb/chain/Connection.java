package com.factoriodb.chain;

import java.util.Collection;
import java.util.Optional;

import com.factoriodb.chain.option.ConnectionOption;
import com.factoriodb.model.ItemsFlow;

public abstract class Connection extends Entity {
	
	@Override
	public Optional<Connection> getConnection() {
		return Optional.of(this);
	}
	

}
