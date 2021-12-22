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
import uk.ac.ox.cs.guardedqa.bound_chase.*;

public class App {

    public static void main(String[] args) throws Exception {

        ///////////////
        // Factories //
        ///////////////

        TermFactory termfactory = SameObjectTermFactory.instance();
        PredicateFactory predicatefactory = SameObjectPredicateFactory.instance();

        //////////////////
        // DLGP parsing //
        //////////////////

        String filepath = args[0];

        DLGPParser parser = new DLGPParser(filepath, termfactory, predicatefactory);
        parser.parse();
        // run the chase
            
        FactBase fb = new SimpleInMemoryGraphStore(parser.getAtoms());
        RuleBase rb;


        System.out.println("Initial factbase size : " + fb.size());

        TriggerRenamer tr;
        TriggerChecker tch;

        switch(Configuration.getQAType()) {
        case BOUND:
            int h = parser.getMaximalHeadSize();
            int p = parser.getPredicateNumber();
            int w = parser.getMaximalArity();
        
            double bound = h * Math.pow(2, p * Math.pow(2 * w, w));
            System.out.println("Computed bound: " + bound);
            
            tch = new MultiTriggerChecker(new SemiObliviousChecker(), new BoundTriggerChecker(bound));
            tr = new FreshWithDepthRenamer();
            rb = new RuleBaseImpl(parser.getRules());
            break;
        case SAT:
            tch = new SemiObliviousChecker();
            tr = new FreshRenamer(termfactory);
            // TODO change this 
            rb = new RuleBaseImpl(parser.getRules());
            break;
        default:
            throw new RuntimeException("the QA type is not supported");
        }


        ChaseBuilder builder = ChaseBuilder.defaultBuilder(fb, rb, termfactory)
            // .useGRDRuleScheduler()
            .useDirectApplication()
            .useSemiObliviousChecker()
            .setExistentialsRenamer(tr)
            .setTriggerChecker(tch);

        if (Configuration.isDebugMode())
            builder.debug();

        long time = System.currentTimeMillis();
        builder
            .build()
            .get()
            .execute();

        System.out.println("---");
        System.out.println("Time : " + (System.currentTimeMillis() - time));
        System.out.println("Final factbase size : " + fb.size());
        System.out.println(fb);

    }

}
