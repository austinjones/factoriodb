package com.factoriodb.chain;

import java.util.Optional;

public abstract class Connection extends Entity {
	
	@Override
	public Optional<Connection> getConnection() {
		return Optional.of(this);
	}
	

}
