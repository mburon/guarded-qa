package uk.ac.ox.cs.guardedqa.bound_chase;

import fr.boreal.model.logicalElements.api.Variable;
import fr.boreal.model.logicalElements.impl.FreshVariableImpl;

public class FreshWithDepthVariable extends FreshVariableImpl {

    private static final String FRESH_PREFIX = "Graal:EE";
    private static int fresh_counter = 0;

    private final int depth;

	/////////////////////////////////////////////////
	// Constructors
	/////////////////////////////////////////////////

	public FreshWithDepthVariable(String label, int depth) {
		super(label);
        this.depth = depth;
	}

    public int getDepth() {
        return depth;
    }

    static Variable create(int depth) {
        return new FreshWithDepthVariable(FRESH_PREFIX + ++fresh_counter, depth);
    }
}
