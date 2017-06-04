package com.factoriodb;

import com.factoriodb.chain.Entity;
import com.sun.tools.javac.util.List;

/**
 * @author austinjones
 */
public class ProblemNode {
    public Entity entity;

    public List<ProblemNode> inputs;
    public List<ProblemNode> outputs;

    public boolean isInput = false;
    public boolean isOutput = false;
}
