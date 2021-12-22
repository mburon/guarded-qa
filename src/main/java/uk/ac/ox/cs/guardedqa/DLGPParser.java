package uk.ac.ox.cs.guardedqa;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Collection;
import java.lang.RuntimeException;

import org.hsqldb.lib.Set;

import fr.boreal.forward_chaining.chase.builder.ChaseBuilder;
import fr.boreal.io.api.ParseException;
import fr.boreal.io.dlgp.impl.DlgpParser;
import fr.boreal.model.kb.api.FactBase;
import fr.boreal.model.kb.api.RuleBase;
import fr.boreal.model.kb.impl.RuleBaseImpl;
import fr.boreal.model.logicalElements.api.Atom;
import fr.boreal.model.logicalElements.api.Predicate;
import fr.boreal.model.logicalElements.factory.api.PredicateFactory;
import fr.boreal.model.logicalElements.factory.api.TermFactory;
import fr.boreal.model.logicalElements.factory.impl.SameObjectPredicateFactory;
import fr.boreal.model.logicalElements.factory.impl.SameObjectTermFactory;
import fr.boreal.forward_chaining.chase.rule_applier.trigger_applier.renamer.FreshRenamer;
import fr.boreal.model.query.api.FOQuery;
import fr.boreal.model.rule.api.FORule;
import fr.boreal.storage.inmemory.impl.DefaultInMemoryAtomSet;
import fr.boreal.storage.inmemory.impl.SimpleInMemoryGraphStore;
import fr.lirmm.graphik.util.AtomType;

import fr.boreal.forward_chaining.chase.rule_applier.trigger_applier.renamer.TriggerRenamer;
import fr.boreal.forward_chaining.chase.rule_applier.trigger_checker.MultiTriggerChecker;
import fr.boreal.forward_chaining.chase.rule_applier.trigger_checker.SemiObliviousChecker;
import fr.boreal.forward_chaining.chase.rule_applier.trigger_checker.TriggerChecker;

public class DLGPParser {

    private final String filepath;
    private final TermFactory termfactory;
    private final PredicateFactory predicatefactory;

    private final Collection<Atom> atoms = new ArrayList<Atom>();
    private final Collection<FORule> rules = new ArrayList<FORule>();
    private final Collection<FOQuery> queries = new ArrayList<FOQuery>();
    // the maximal size of rule heads
    private int h;
    private final HashSet<Predicate> predicates = new HashSet<>();

    
    DLGPParser(String filePath, TermFactory termfactory, PredicateFactory predicatefactory) {
        this.termfactory = termfactory;
        this.predicatefactory = predicatefactory;
        this.filepath = filePath;
    }

    public void parse() throws Exception {

        File file = new File(filepath);

        DlgpParser dlgp_parseur = new DlgpParser(file, termfactory, predicatefactory);

        while (dlgp_parseur.hasNext()) {
            try {
                Object result = dlgp_parseur.next();
                if (result instanceof Atom a) {
                    atoms.add(a);
                } else if (result instanceof FORule r) {
                    rules.add(r);

                    for (Atom ba : r.getBody().getFormula().flatten()) {
                        predicates.add(ba.getPredicate());
                    }

                    Collection<Atom> head = r.getHead().flatten();
                    h = Math.max(h, head.size());
                    for (Atom ha : head) {
                        predicates.add(ha.getPredicate());
                    }

                } else if (result instanceof FOQuery q) {
                    queries.add(q);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        dlgp_parseur.close();
    }

    public Collection<Atom> getAtoms() {
        return atoms;
    }

    public Collection<FORule> getRules() {
        return rules;
    }

    public Collection<FOQuery> getQueries() {
        return queries;
    }

    // the maximal arity of the predicates
    public int getMaximalArity() {
        int w = 0;
        for (Predicate pred : predicates) {
            w = Math.max(w, pred.getArity());
        }

        return w;
    }

    public int getMaximalHeadSize() {
        return h;
    }

    public int getPredicateNumber() {
        return predicates.size();
    }
}
