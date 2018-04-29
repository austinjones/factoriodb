package com.factoriodb.chain;

import com.factoriodb.chain.option.ConnectionOption;

import java.util.Collection;
import java.util.Optional;

public abstract class Connection extends Entity {

    public abstract Collection<? extends ConnectionOption> options(double rate);
}
