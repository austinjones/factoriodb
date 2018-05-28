package org.factoriodb.chain;

import org.factoriodb.chain.option.ConnectionOption;

import java.util.Collection;

public abstract class Connection extends Entity {

    public abstract Collection<? extends ConnectionOption> options(double rate);
}
