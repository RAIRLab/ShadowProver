package org.rairlab.shadow.prover.core.ccprovers;

import org.rairlab.shadow.prover.core.Logic;
import org.rairlab.shadow.prover.core.Prover;
import org.rairlab.shadow.prover.core.SnarkWrapper;
import org.rairlab.shadow.prover.core.expanders.cognitivecalculus.*;
import org.rairlab.shadow.prover.core.internals.AgentSnapShot;
import org.rairlab.shadow.prover.core.internals.ConsistentSubsetFinder;
import org.rairlab.shadow.prover.core.internals.Expander;
import org.rairlab.shadow.prover.core.proof.AtomicJustification;
import org.rairlab.shadow.prover.core.proof.CompoundJustification;
import org.rairlab.shadow.prover.core.proof.Justification;
import org.rairlab.shadow.prover.core.proof.TrivialJustification;
import org.rairlab.shadow.prover.representations.formula.*;
import org.rairlab.shadow.prover.representations.value.Constant;
import org.rairlab.shadow.prover.representations.value.Value;
import org.rairlab.shadow.prover.representations.value.Variable;
import org.rairlab.shadow.prover.utils.*;

import org.apache.commons.lang3.tuple.Pair;
import org.hamcrest.core.IsInstanceOf;

import java.util.*;
import java.util.stream.Collectors;


public class CognitiveCalculusProver implements CCProver {

    /*
     *
     */
    private static int            MAX_EXPAND_FACTOR    = 500;
    private        boolean        verbose              = true;
    private final  boolean        reductio;
    private final  boolean        theoremsToNec        = false;
    private final  List<Expander> expanders;
    private        Set<Formula>   prohibited;

    private enum solutionType {
        ENTAILMENT,
        QA_SINGLE,
        QA_MULTIPLE
    };

    protected Logger logger;

    public CognitiveCalculusProver() {
        prohibited = Sets.newSet();
        reductio = false;
        expanders = CollectionUtils.newEmptyList();

        Collections.addAll(expanders,
            BreakupBiConditionals.INSTANCE,
            UniversalElim.INSTANCE,
            NotExistsToForallNot.INSTANCE,

            R4.INSTANCE,
            SelfBelief.INSTANCE,
            PerceptionToKnowledge.INSTANCE,
            SaysToBelief.INSTANCE,
            IntentionToPerception.INSTANCE,
            ModalConjunctions.INSTANCE,
            ModalImplications.INSTANCE,

            DR1.INSTANCE,
            DR2.INSTANCE,
            DR3.INSTANCE,
            DR5.INSTANCE,

            OughtSchema.INSTANCE,
            KnowledgeConjunctions.INSTANCE,
            NecToPos.INSTANCE,
            InnerModalForward.INSTANCE
        );

        if (theoremsToNec) {
            expanders.add(TheoremsToNecessity.INSTANCE);
        }
        logger = new Logger();
    }


    public CognitiveCalculusProver(CognitiveCalculusProver parent) {
        prohibited = CollectionUtils.setFrom(parent.prohibited);
        reductio = false;
        expanders = parent.expanders;

        this.verbose = parent.verbose;
        this.logger = parent.logger;
    }

    private CognitiveCalculusProver(CognitiveCalculusProver parent, boolean reductio) {

        prohibited = CollectionUtils.setFrom(parent.prohibited);
        this.reductio = reductio;
        expanders = parent.expanders;
        this.verbose = parent.verbose;
        this.logger = parent.logger;
    }

    @Override
    public Optional<Justification> prove(Set<Formula> assumptions, Formula formula) {
        assumptions.forEach(x -> {
            if (x.getJustification() == null) {
                x.setJustification(new AtomicJustification("GIVEN"));
            }
        });
        return prove(assumptions, formula, CollectionUtils.newEmptySet());
    }

    /** Try out various strategies from elimination, expansion, and shadowing
     * to solve goal.
     * @param base Set of derived formulae 
     * @param formula Goal to prove
     * @param added Formulae added via expanders
     * @return Justification if proof is found else empty.
     */
    public synchronized Optional<Justification> prove(Set<Formula> assumptions, Formula formula, Set<Formula> added) {
        Set<Formula> base = CollectionUtils.setFrom(assumptions);
        Formula shadowedGoal = formula.shadow(1);

        Prover folProver = SnarkWrapper.getInstance();
        Optional<Justification> shadowedJustificationOpt = folProver.prove(shadow(base), shadowedGoal);
        if (shadowedJustificationOpt.isPresent()) {
            return shadowedJustificationOpt;
        }

        // Attempt to prove the statement through propogation of
        // an agent's theory of mind.
        logger.addContext();
        Optional<Justification> agentClosureJustificationOpt = this.proveAgentClosure(base, formula);
        logger.removeContext();
        if (agentClosureJustificationOpt.isPresent()) {
            return agentClosureJustificationOpt;
        }

        // We're bound by this memory limit
        while (base.size() < MAX_EXPAND_FACTOR * assumptions.size()) {
            // Grow the formula base via expanders
            int sizeBeforeExpansion = base.size();
            base = expand(base, added, formula);
            int sizeAfterExpansion = base.size();

            // Return if the assumption base contains the goal
            if (base.contains(formula)) {
                return Optional.of(TrivialJustification.trivial(base, formula));
            }

            // Try out various elimination rule strategies on the goal formula...

            Optional<Justification> andProofOpt = tryAND(base, formula, added);
            if (andProofOpt.isPresent()) {
                return andProofOpt;
            }

            Optional<Justification> necProofOpt = tryNEC(base, formula, added);
            if (necProofOpt.isPresent()) {
                return necProofOpt;
            }

            Optional<Justification> posProofOpt = tryPOS(base, formula, added);
            if (posProofOpt.isPresent()) {
                return posProofOpt;
            }

            Optional<Justification> forAllIntroOpt = tryForAllIntro(base, formula, added);
            if (forAllIntroOpt.isPresent()) {
                return forAllIntroOpt;
            }

            Optional<Justification> existsIntroOpt = tryExistsIntro(base, formula, added);
            if (existsIntroOpt.isPresent()) {
                return existsIntroOpt;
            }

            Optional<Justification> ifIntroOpt = tryIfIntro(base, formula, added);
            if (ifIntroOpt.isPresent()) {
                return ifIntroOpt;
            }

            Optional<Justification> iffIntroOpt = tryIffIntro(base, formula, added);
            if (iffIntroOpt.isPresent()) {
                return iffIntroOpt;
            }

            Optional<Justification> counterFacIntroOpt = tryCounterFactIntro(base, formula, added);
            if (counterFacIntroOpt.isPresent()) {
                return counterFacIntroOpt;
            }

            Optional<Justification> orIntro2Opt = tryOr2(base, formula, added);
            if (orIntro2Opt.isPresent()) {
                return orIntro2Opt;
            }

            // Proof by cases on disjuncts in base
            Optional<Justification> caseProofOpt = tryOR(base, formula, added);
            if (caseProofOpt.isPresent()) {
                return caseProofOpt;
            }

            // Attempt proof by contradiction
            if (base.size() < 50 && !reductio) {
                Optional<Justification> reductioProofOpt = tryReductio(base, formula, added);
                if (reductioProofOpt.isPresent()) {
                    return reductioProofOpt;
                }
            }

            // If no new formula were created via expanders, then fail
            if (sizeAfterExpansion <= sizeBeforeExpansion) {
                return Optional.empty();
            }

            // Attempt shadowing and proving the goal again
            shadowedJustificationOpt = folProver.prove(shadow(base), shadowedGoal);
            if (shadowedJustificationOpt.isPresent()) {
                return shadowedJustificationOpt;
            }

            // Attempt agent closure again
            agentClosureJustificationOpt = proveAgentClosure(base, formula);
            if (agentClosureJustificationOpt.isPresent()) {
                return agentClosureJustificationOpt;
            }
        }

        // Failed to find a proof
        return Optional.empty();
    }

    /** Try to prove the consequent of an implication from the antecedent and base,
     * if successful return If Intro justification.
     * @param base Set of derived formulae 
     * @param formula Goal to prove
     * @param added Formulae added via expanders
     * @return Justification if proof is found else empty.
     */
    protected Optional<Justification> tryIfIntro(Set<Formula> base, Formula formula, Set<Formula> added) {
        if (!(formula instanceof Implication)) {
            return Optional.empty();
        }

        Implication implication = (Implication) formula;
        Formula antecedent = implication.getAntecedent();
        Formula consequent = implication.getConsequent();

        logger.addContext();
        // logger.tryLog("Tying if intro", formula);
        Optional<Justification> consOpt = this.prove(Sets.add(base, antecedent), consequent);
        logger.removeContext();

        if (consOpt.isPresent()) {
            return Optional.of(new CompoundJustification(
                "If Intro",
                CollectionUtils.listOf(consOpt.get())
            ));
        } 
        
        return Optional.empty();
    }

    /** Try to prove both sides of a biconditional,
     * @param base Set of derived formulae 
     * @param formula Goal to prove
     * @param added Formulae added via expanders
     * @return Justification if proof is found else empty.
     */
    protected Optional<Justification> tryIffIntro(Set<Formula> base, Formula formula, Set<Formula> added) {
        if (!(formula instanceof BiConditional)) {
            return Optional.empty();
        }

        Implication implication = (Implication) formula;
        Formula antecedent = implication.getAntecedent();
        Formula consequent = implication.getConsequent();

        // Prove forward direction
        logger.addContext();
        Optional<Justification> consOpt = this.prove(Sets.add(base, antecedent), consequent);
        logger.removeContext();

        if (!consOpt.isPresent()) {
            // Proof search failed
            return Optional.empty();
        }

        // Prove backward direction
        logger.addContext();
        Optional<Justification> consOpt2 = this.prove(Sets.add(base, consequent), antecedent);
        logger.removeContext();

        if (consOpt2.isPresent()) {
            return Optional.of(new CompoundJustification(
                "Iff Intro",
                CollectionUtils.listOf(consOpt.get(), consOpt2.get())
            ));
        } 
        
        return Optional.empty();
    }

    protected Optional<Justification> tryCounterFactIntro(Set<Formula> base, Formula formula, Set<Formula> added) {
        if (! (formula instanceof CounterFactual)) {
            return Optional.empty();
        }

        CounterFactual counterFactual = (CounterFactual) formula;
        Formula antecedent = counterFactual.getAntecedent();
        Formula consequent = counterFactual.getConsequent();

        logger.addContext();
        // logger.tryLog("Tying counterfactual intro", formula);
        Optional<Justification> counterfactualIntroOpt = (new ConsistentSubsetFinder()).find(this, base, antecedent, consequent);
        logger.removeContext();

        if (counterfactualIntroOpt.isPresent()) {
            return Optional.of(new CompoundJustification(
                "Counterfactual Intro",
                CollectionUtils.listOf(counterfactualIntroOpt.get())
            ));
        } 
        
        // Failed to find a proof
        return Optional.empty();
    }

    /** Try to prove an exists intro by proving for an arbirary constant
     * @param base Set of derived formulae 
     * @param formula Goal to prove
     * @param added Formulae added via expanders
     * @return Justification if proof found, otherwise empty
     */
    protected Optional<Justification> tryExistsIntro(Set<Formula> base, Formula formula, Set<Formula> added) {
        if (! (formula instanceof Existential)) {
            return Optional.empty();
        }

        Existential existential = (Existential) formula;
        Variable[]  vars        = existential.vars();

        if (vars.length != 1) {
            return Optional.empty();
            // Question: Is it possible to instantiate new constants for
            // each variable?
        }

        // Create a new constant that replaces the bound variable
        Map<Variable, Value> subs = CollectionUtils.newMap();
        subs.put(vars[0], Constant.newConstant());

        // logger.tryLog("Trying to prove existential", formula);
        // Perform a substitution on the bound variable using the new substition mapping
        Optional<Justification> ansOpt = this.prove(base, ((Existential) formula).getArgument().apply(subs));

        if (ansOpt.isPresent()) {
            return Optional.of(new CompoundJustification(
                "ExistsIntro",
                CollectionUtils.listOf(ansOpt.get())
            ));
        }

        // Failed to find a proof
        return Optional.empty();
    }

    /** Try to prove a forall intro by proving for an arbirary constant
     * @param base Set of derived formulae 
     * @param formula Goal to prove
     * @param added Formulae added via expanders
     * @return Justification if proof found, otherwise empty
     */
    protected Optional<Justification> tryForAllIntro(Set<Formula> base, Formula formula, Set<Formula> added) {
        if (formula instanceof Universal) {

            Universal  universal = (Universal) formula;
            Variable[] vars      = universal.vars();

            // logger.tryLog("Trying to prove universal", universal);
            if (vars.length != 1) {
                return Optional.empty();
                // TODO: Handle more than one variable
            }

            Map<Variable, Value> subs = CollectionUtils.newMap();
            subs.put(vars[0], Constant.newConstant());
            //TODO: Verify this.
            Optional<Justification> ansOpt = this.prove(base, ((Universal) formula).getArgument().apply(subs));

            if (ansOpt.isPresent()) {
                return Optional.of(new CompoundJustification(
                    "ForAllIntro",
                    CollectionUtils.listOf(ansOpt.get())
                ));
            } 

            // Failed to find a proof
            return Optional.empty();
        
        } else if (formula instanceof Not && ((Not) formula).getArgument() instanceof Existential) {

            //formula = (not (exists [vars] kernel)) == (forall [vars] (not kernel))
            Formula    kernel = ((Existential) ((Not) formula).getArgument()).getArgument();
            Variable[] vars   = ((Existential) ((Not) formula).getArgument()).vars();

            if (vars.length != 1) {
                return Optional.empty();
            }

            Map<Variable, Value> subs = CollectionUtils.newMap();
            subs.put(vars[0], Constant.newConstant());
            // logger.tryLog("Trying to prove ", (new Not(kernel)).apply(subs));

            return this.prove(base, (new Not(kernel)).apply(subs));
        }

        return Optional.empty();
    
    }

    protected Optional<Justification> tryNEC(Set<Formula> base, Formula formula, Set<Formula> added) {
        if (! (formula instanceof Necessity)) {
            return Optional.empty();
        }

        // logger.tryLog("Trying to prove necessity", formula);
        Optional<Justification> innerProof = this.prove(Sets.newSet(), ((Necessity) formula).getFormula());

        if (innerProof.isPresent()) {
            return Optional.of(new CompoundJustification(
                "Nec Intro",
                CollectionUtils.listOf(innerProof.get()))
            );
        } 
        
        // Failed to find a proof
        return Optional.empty();
    }

    protected Optional<Justification> tryPOS(Set<Formula> base, Formula formula, Set<Formula> added) {
        if (formula instanceof Not && ((Not) formula).getArgument() instanceof Possibility) {

            Formula core = ((Possibility) ((Not) formula).getArgument()).getFormula();

            // logger.tryLog("Trying to prove necessity", new Necessity(new Not(core)));

            Optional<Justification> innerProof = this.prove(Sets.newSet(), new Necessity(new Not(core)));

            if (innerProof.isPresent()) {
                return Optional.of(new CompoundJustification(
                    "Pos Intro",
                    CollectionUtils.listOf(innerProof.get())
                ));
            } 
        } 
        
        // Proof search failed or not a POS formulae
        return Optional.empty();
    }

    /** Try to prove an and intro by proving both conjuncts seperately
     * @param base Set of derived formulae 
     * @param formula Goal to prove
     * @param added Formulae added via expanders
     * @return Justification if proof found, otherwise empty
     */
    protected Optional<Justification> tryAND(Set<Formula> base, Formula formula, Set<Formula> added) {

        if (! (formula instanceof And)) {
            return Optional.empty();
        }

        // logger.tryLog("Trying to prove conjunction", and);
        // Get the subarguments of And
        And and = (And) formula;
        Formula conjuncts[] = and.getArguments();
        
        // Try to prove each component
        List<Justification> conjunctProofs = new ArrayList<Justification>();
        for (Formula conjunct : conjuncts) {
            CognitiveCalculusProver cognitiveCalculusProver = new CognitiveCalculusProver(this);
            Optional<Justification> conjunctProofOpt = cognitiveCalculusProver.prove(base, conjunct);
            if (!conjunctProofOpt.isPresent()) {
                // Proof search failed
                return Optional.empty();
            }
            conjunctProofs.add(conjunctProofOpt.get());
        }

        return Optional.of(new CompoundJustification(
            "andIntro",
            conjunctProofs
        ));
    }

    /** Try to prove the goal by assuming a disjunct from the base.
     * Classic Proof by Cases Tactic
     * @param base Set of derived formulae 
     * @param formula Goal to prove
     * @param added Formulae added via expanders
     * @return Justification if proof found, otherwise empty
     */
    protected Optional<Justification> tryOR(Set<Formula> base, Formula formula, Set<Formula> added) {

        // Find all OR formulae in the base
        Set<Or> level2ORs = CommonUtils.level2FormulaeOfType(base, Or.class);
        Optional<Or> someOrOpt = level2ORs.stream().findAny();

        if (!someOrOpt.isPresent()) {
            // Can't apply this proof strategy
            return Optional.empty();
        }

        for (Or someOr : level2ORs) {
            Formula[] disjuncts = someOr.getArguments();

            // Remove disjunct from base
            Set<Formula> reducedBase = CollectionUtils.setFrom(base);
            reducedBase.remove(someOr);

            // Try to prove goal in both cases
            List<Justification> casesProofs = new ArrayList<Justification>();
            for (Formula disjunct : disjuncts) {
                CognitiveCalculusProver cognitiveCalculusProver = new CognitiveCalculusProver(this);
                
                // Add disjunct to the base
                Set<Formula> newBase = CollectionUtils.setFrom(reducedBase);
                newBase.add(disjunct);

                Optional<Justification> caseProofOpt =  cognitiveCalculusProver.prove(
                    newBase, 
                    formula,
                    CollectionUtils.setFrom(added)
                );

                if (!caseProofOpt.isPresent()) {
                    // Proof by cases using this disjunct fails
                    break;
                }

                casesProofs.add(caseProofOpt.get());
            }
    
            return Optional.of(new CompoundJustification(
                "ORIntro", 
                casesProofs
            ));

        }

        // Proof search failed
        return Optional.empty();
    }

    /** Attempt to prove either disjunct of an OR
     * @param base Set of derived formulae 
     * @param formula Goal to prove
     * @param added Formulae added via expanders
     * @return Justification if proof found, otherwise empty
     */
    protected Optional<Justification> tryOr2(Set<Formula> base, Formula formula, Set<Formula> added) {
        if (! (formula instanceof Or)) {
            return Optional.empty();
        }

        Or or = (Or) formula;
        Formula disjuncts[] = or.getArguments();
        
        // Try to prove either component
        for (Formula disjunct : disjuncts) {
            CognitiveCalculusProver cognitiveCalculusProver = new CognitiveCalculusProver(this);
            Optional<Justification> disjunctProofOpt = cognitiveCalculusProver.prove(base, disjunct);
            if (disjunctProofOpt.isPresent()) {
                // Proof search failed
                return disjunctProofOpt;
            }
        }

        // Proof search failed
        return Optional.empty();
    }

    /** Negate the goal and try to derive a contradiction from it
     * @param base Set of derived formulae 
     * @param formula Goal to prove
     * @param added Formulae added via expanders
     * @return Justification if proof found, otherwise empty
     */
    protected Optional<Justification> tryReductio(Set<Formula> base, Formula formula, Set<Formula> added) {

        Formula negated = Logic.negated(formula);
        if (base.contains(negated) || formula.toString().startsWith("$")) {
            return Optional.empty();
        }

        Atom atom = Atom.generate();

        // logger.tryLog("Reductio on", negated);


        Set<Formula> augmented = CollectionUtils.setFrom(base);

        augmented.add(negated);
        logger.addContext();
        CognitiveCalculusProver cognitiveCalculusProver = new CognitiveCalculusProver(this, true);
        Optional<Justification> reductioJustOpt         = cognitiveCalculusProver.prove(augmented, atom, added);
        logger.removeContext();

        if (reductioJustOpt.isPresent()) {
            return Optional.of(new CompoundJustification(
                "Reductio",
                CollectionUtils.listOf(reductioJustOpt.get())
            ));
        }

        // Proof search failed
        return Optional.empty();
    }

    /** Attempt to prove the goal from an agent's theory of mind. 
     * @param base Set of formulas that represent the background theory.
     * @param goal Statement to prove
     * @return Justification if proof is found, otherwise empty.
    */
    Optional<Justification> proveAgentClosure(Set<Formula> base, Formula goal) {
        // Return empty if not a theory of mind formula
        if (! (goal instanceof UnaryModalFormula)) {
            return Optional.empty();
        }

        UnaryModalFormula formula = (UnaryModalFormula) goal;
        Value    agent            = formula.getAgent();
        Value    time             = formula.getTime();
        Formula  innerGoalFormula = formula.getFormula();

        AgentSnapShot agentSnapShot = AgentSnapShot.from(base);

        Set<Formula> innerGivens = Sets.newSet();


        // Gather formula from base based on modal formula
        // from time 0 to time.

        if(formula instanceof Knowledge){
            innerGivens = agentSnapShot.allKnownByAgentTillTime(agent, time);
        }

        if(formula instanceof Belief){
            innerGivens = agentSnapShot.allBelievedByAgentTillTime(agent, time);
        }

        if(formula instanceof Intends){
            innerGivens = agentSnapShot.allIntendedByAgentTillTime(agent, time);
        }

        // Attempt to prove the goal from the gathered formulae
        CognitiveCalculusProver cognitiveCalculusProver = new CognitiveCalculusProver(this);
        Optional<Justification> inner                   = cognitiveCalculusProver.prove(innerGivens, innerGoalFormula);

        return inner.map(justification -> new CompoundJustification(
            formula.getClass().toString(),
            CollectionUtils.listOf(justification))
        );

    }

    /** Create more facts using expanders 
     * @param base Set of formulas that represent the background theory.
     * @param added Keeps track which formulae gets added via expanders
     * @param goal Statement to prove.
     * @return New formula base
    */
    public Set<Formula> expand(Set<Formula> base, Set<Formula> added, Formula goal) {

        expanders.forEach(expander -> expander.expand(this, base, added, goal));

        if (prohibited != null) {
            base.removeAll(prohibited);
        }

        return base;
    }

    /** Takes a set of formulas and shadows them to level 1
     * @param formulas Set of formulas to shadow
     * @return Set of shadowed formula
     */
    protected Set<Formula> shadow(Set<Formula> formulas) {
        return formulas.stream().map(f -> f.shadow(1)).collect(Collectors.toSet());
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    public Set<Formula> getProhibited() {
        return prohibited;
    }

    /**
     * Find a quantifier by its bound variable within a formula.
     * Note: Only returns the first instance found as two quantifiers
     * shouldn't share the same bound variable.
     * @param v variable to search for
     * @param f Formula to search for v within
     * @return a subformula representing the quantifier that
     * is bound over the specified variable if it exists.
     */
    private static Optional<Quantifier> findQuantifier(Variable v, Formula f)  {
        // Base Cases:

        // Bottom of Formula graph does not contain the bound variable
        if (f instanceof Predicate || f instanceof Atom) {
            return Optional.empty();
        }

        // Check if these quantifiers contain our bound varialbe
        if (f instanceof Quantifier) {
            Quantifier qf = (Quantifier) f;
            if (qf.variablesPresent().contains(v)) {
                return Optional.of(qf);
            }
        }

        // Recusive Case: Iterate over each subformula
        for (Formula subF : f.getArgs()) {
            Optional<Quantifier> subResult = findQuantifier(v, subF);
            if (subResult.isPresent()) {
                return subResult;
            }
        }

        // Quantifier not found at this depth
        return Optional.empty();
    }

    /**
     * Helper function, recursively checks subformulas and keeps
     * track of whether we're in a modal context using the argument
     * withinModalContext
     */
    private static boolean varWithinModalContext(Variable v, Formula f, boolean withinModalContext) {
        // Base Case 1
        if (f instanceof Atom) {
            return false;
        }

        // Base Case 2
        if (f instanceof Predicate) {
            Predicate pf = (Predicate) f;
            if (pf.variablesPresent().contains(v)) {
                return withinModalContext;
            }
            // Variable wasn't in this branch
            return false;
        }


        // Recusive Case: Iterate over each subformula
        for (Formula subF : f.getArgs()) {
            boolean subIsModal = withinModalContext || (subF instanceof UnaryModalFormula);
            if (varWithinModalContext(v, subF, subIsModal)) {
                return true;
            }
        }

        // Not within modal context at this layer
        return false;
    }

    /**
     * Returns whether or not a variable exists within a modal context of
     * a given formula.
     * @param v variable to check for
     * @param f formula to check within
     * @return a boolean representing the result
     */
    private static boolean varWithinModalContext(Variable v, Formula f) {
        return varWithinModalContext(v, f, f instanceof UnaryModalFormula);
    }

    private boolean violatesModalConstraint(Variable v, Formula f) {
        Optional<Quantifier> qfOpt = findQuantifier(v, f);
        if (!qfOpt.isPresent()) {
            return false;
        }
        Quantifier qf = qfOpt.get();
        // Fail if we're trying to find a variable within a modal context
        return varWithinModalContext(v, qf.getArgument());
    }


    /** Attempt to prove the goal from an agent's theory of mind. 
     * @param base Set of formulas that represent the background theory.
     * @param goal Statement to prove
     * @return Justification if proof is found, otherwise empty.
    */
    private Optional<Pair<Justification, Set<Map<Variable, Value>>>> proveAgentClosureBindings(Set<Formula> base, Formula goal, List<Variable> variables) {
        // Return empty if not a theory of mind formula
        if (! (goal instanceof UnaryModalFormula)) {
            return Optional.empty();
        }

        UnaryModalFormula formula = (UnaryModalFormula) goal;
        Value    agent            = formula.getAgent();
        Value    time             = formula.getTime();
        Formula  innerGoalFormula = formula.getFormula();

        AgentSnapShot agentSnapShot = AgentSnapShot.from(base);

        Set<Formula> innerGivens = Sets.newSet();


        // Gather formula from base based on modal formula
        // from time 0 to time.

        if(formula instanceof Knowledge){
            innerGivens = agentSnapShot.allKnownByAgentTillTime(agent, time);
        }

        if(formula instanceof Belief){
            innerGivens = agentSnapShot.allBelievedByAgentTillTime(agent, time);
        }

        if(formula instanceof Intends){
            innerGivens = agentSnapShot.allIntendedByAgentTillTime(agent, time);
        }

        // Attempt to prove the goal from the gathered formulae
        CognitiveCalculusProver cognitiveCalculusProver = new CognitiveCalculusProver(this);
        Optional<Pair<Justification, Set<Map<Variable, Value>>>>  inner                   = cognitiveCalculusProver.proveAndGetMultipleBindings(innerGivens, innerGoalFormula, variables);

        return inner;
        // return inner.map(justification -> new CompoundJustification(
            // formula.getClass().toString(),
            // CollectionUtils.listOf(justification))
        // );

    }


    public Optional<Pair<Justification, Set<Map<Variable, Value>>>> proveAndGetMultipleBindings(Set<Formula> assumptions, Formula formula, List<Variable> variables) {

        // Make sure we're not trying to provide substitions within modal contexts
        for (Variable v : variables) {

            // Check for each background formula
            for (Formula f : assumptions) {
                if (violatesModalConstraint(v, f)) {
                    System.out.println("[WARNING] Variable violates modal constraint");
                    return Optional.empty();
                } 
            }

            // Check goal formula
            if (violatesModalConstraint(v, formula)) {
                System.out.println("[WARNING] Variable violates modal constraint");
                return Optional.empty();
            }
        }

        // System.out.println("Trying to prove " + formula.toString());

        Set<Formula> base = CollectionUtils.setFrom(assumptions);
        Formula shadowedGoal = formula.shadow(1);

        Prover folProver = SnarkWrapper.getInstance();
        Optional<Pair<Justification, Set<Map<Variable, Value>>>> shadowedJustificationOpt = folProver.proveAndGetMultipleBindings(shadow(base), shadowedGoal, variables);
        if (shadowedJustificationOpt.isPresent()) {
            return shadowedJustificationOpt;
        }

        Optional<Pair<Justification, Set<Map<Variable, Value>>>> agentClosureJustificationOpt = this.proveAgentClosureBindings(base, formula, variables);
        if (agentClosureJustificationOpt.isPresent()) {
            return agentClosureJustificationOpt;
        }

        Set<Formula> added = new HashSet<Formula>();

        // We're bound by this memory limit
        while (base.size() < MAX_EXPAND_FACTOR * assumptions.size()) {
            // Grow the formula base via expanders
            int sizeBeforeExpansion = base.size();
            base = expand(base, added, formula);
            int sizeAfterExpansion = base.size();

            // If no new formula were created via expanders, then fail
            if (sizeAfterExpansion <= sizeBeforeExpansion) {
                return Optional.empty();
            }

            // Attempt shadowing and proving the goal again
            shadowedJustificationOpt = folProver.proveAndGetMultipleBindings(shadow(base), shadowedGoal, variables);
            if (shadowedJustificationOpt.isPresent()) {
                return shadowedJustificationOpt;
            }

            // Attempt agent closure again
            agentClosureJustificationOpt = this.proveAgentClosureBindings(base, formula, variables);
            if (agentClosureJustificationOpt.isPresent()) {
                return agentClosureJustificationOpt;
            }

        }

        // Proof Search Failed
        return Optional.empty();
    }
}
