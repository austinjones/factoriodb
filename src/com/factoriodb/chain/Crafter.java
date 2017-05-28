package com.factoriodb.chain;

import java.util.Optional;

public abstract class Crafter extends Entity {
	
	@Override
	public Optional<Crafter> getCrafter() {
		return Optional.of(this);
	}
	
}
