package uk.ac.ox.cs.guardedqa.bound_chase;

import fr.boreal.forward_chaining.chase.rule_applier.trigger_applier.renamer.TriggerRenamer;
import fr.boreal.model.logicalElements.api.Substitution;
import fr.boreal.model.logicalElements.api.Variable;
import fr.boreal.model.logicalElements.api.Term;
import fr.boreal.model.logicalElements.factory.api.TermFactory;
import fr.boreal.model.logicalElements.impl.SubstitutionImpl;
import fr.boreal.model.rule.api.FORule;

public class FreshWithDepthRenamer implements TriggerRenamer {

	@Override
	public Substitution renameExitentials(FORule rule, final Substitution substitution) {
		Substitution s = new SubstitutionImpl();

        int depth = 0;
        for (Variable k : substitution.keys()) {
            Term img = substitution.createImageOf(k);

            if (img instanceof FreshWithDepthVariable) {
                depth = Math.max(depth, ((FreshWithDepthVariable) img).getDepth());
            }
        }

        depth++;

		for(Variable v : rule.getExistentials()) {
			s.add(v, FreshWithDepthVariable.create(depth));
		}
		
		// In case an existential have a value associated in the initial substitution
		// Could be the case during rewriting
		for(Variable v : substitution.keys()) {
			s.remove(v);
		}

		return s.merged(substitution).get();
	}
}
