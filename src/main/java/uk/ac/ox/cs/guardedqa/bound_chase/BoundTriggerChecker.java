package uk.ac.ox.cs.guardedqa.bound_chase;

import fr.boreal.model.kb.api.FactBase;
import fr.boreal.model.logicalElements.api.Substitution;
import fr.boreal.model.rule.api.FORule;

import fr.boreal.model.logicalElements.api.Term;
import fr.boreal.model.logicalElements.api.Variable;

import fr.boreal.forward_chaining.chase.rule_applier.trigger_checker.TriggerChecker;

public class BoundTriggerChecker implements TriggerChecker {

    private static double bound;

    public BoundTriggerChecker(double bound) {
        this.bound = bound;
    }

    public boolean check(FORule rule, Substitution substitution, FactBase fb) {

        for (Variable variable : substitution.keys()) {
            Term img = substitution.createImageOf(variable);
            if ((img instanceof FreshWithDepthVariable) &&
                ((FreshWithDepthVariable) img).getDepth() > bound)
                return false;
        }
        
        return true;
    }
}
