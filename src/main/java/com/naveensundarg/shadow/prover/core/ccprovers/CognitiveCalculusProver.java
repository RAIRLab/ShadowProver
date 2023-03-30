package com.naveensundarg.shadow.prover.core.ccprovers;

import com.naveensundarg.shadow.prover.core.Logic;
import com.naveensundarg.shadow.prover.core.Prover;
import com.naveensundarg.shadow.prover.core.SnarkWrapper;
import com.naveensundarg.shadow.prover.core.expanders.cognitivecalculus.*;
import com.naveensundarg.shadow.prover.core.internals.AgentSnapShot;
import com.naveensundarg.shadow.prover.core.internals.ConsistentSubsetFinder;
import com.naveensundarg.shadow.prover.core.internals.Expander;
import com.naveensundarg.shadow.prover.core.internals.UniversalInstantiation;
import com.naveensundarg.shadow.prover.core.proof.AtomicJustification;
import com.naveensundarg.shadow.prover.core.proof.CompoundJustification;
import com.naveensundarg.shadow.prover.core.proof.Justification;
import com.naveensundarg.shadow.prover.core.proof.TrivialJustification;
import com.naveensundarg.shadow.prover.representations.formula.*;
import com.naveensundarg.shadow.prover.representations.value.Compound;
import com.naveensundarg.shadow.prover.representations.value.Constant;
import com.naveensundarg.shadow.prover.representations.value.Value;
import com.naveensundarg.shadow.prover.representations.value.Variable;
import com.naveensundarg.shadow.prover.utils.*;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.naveensundarg.shadow.prover.utils.Sets.cartesianProduct;



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


    protected Logger logger;

    public CognitiveCalculusProver() {

        prohibited = Sets.newSet();
        reductio = false;
        expanders = CollectionUtils.newEmptyList();

        Collections.addAll(expanders,
            BreakupBiConditionals.INSTANCE,
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

            UniversalElim.INSTANCE,
            KnowledgeConjunctions.INSTANCE,

            NotExistsToForallNot.INSTANCE,

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


    public synchronized Optional<Justification> prove(Set<Formula> assumptions, Formula formula, Set<Formula> added) {


        Prover folProver = SnarkWrapper.getInstance();

        Set<Formula> base = CollectionUtils.setFrom(assumptions);

        Formula shadowedGoal = formula.shadow(1);

        Optional<Justification> shadowedJustificationOpt = folProver.prove(shadow(base), shadowedGoal);

        // Attempt to prove the statement through propogation of
        // an agent's theory of mind.
        logger.addContext();
        Optional<Justification> agentClosureJustificationOpt = this.proveAgentClosure(base, formula);
        logger.removeContext();
        if (agentClosureJustificationOpt.isPresent()) {
            return agentClosureJustificationOpt;
        }

        while (!shadowedJustificationOpt.isPresent()) {

            // Grow the formula base via expanders
            int sizeBeforeExpansion = base.size();
            base = expand(base, added, formula);
            int sizeAfterExpansion = base.size();

            // If we're passed our memory limit, return empty
            if (sizeAfterExpansion > MAX_EXPAND_FACTOR * assumptions.size()) {
                return Optional.empty();
            }

            // Return if the new assumption base contains the goal
            if (base.contains(formula)) {
                return Optional.of(TrivialJustification.trivial(base, formula));
            }

            // Try out various strategies...

            Optional<Justification> andProofOpt = tryAND(base, formula, added);
            if (andProofOpt.isPresent()) {
                return andProofOpt;
            }

            if (!reductio) {

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

                Optional<Justification> counterFacIntroOpt = tryCounterFactIntro(base, formula, added);
                if (counterFacIntroOpt.isPresent()) {
                    return counterFacIntroOpt;
                }
            }


            Optional<Justification> caseProofOpt = tryOR(base, formula, added);
            if (caseProofOpt.isPresent()) {
                return caseProofOpt;
            }


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


    /**
     * Try to prove the consequent of an implication from the antecedant and base,
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
        } else {
            return Optional.empty();
        }
    }

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
        return this.prove(base, ((Existential) formula).getArgument().apply(subs));
    }

    protected Optional<Justification> tryForAllIntro(Set<Formula> base, Formula formula, Set<Formula> added) {
        if (formula instanceof Universal) {

            Universal  universal = (Universal) formula;
            Variable[] vars      = universal.vars();

            // logger.tryLog("Trying to prove universal", universal);
            if (vars.length == 1) {

                Map<Variable, Value> subs = CollectionUtils.newMap();
                subs.put(vars[0], Constant.newConstant());
                //TODO: Verify this.
                Optional<Justification> ansOpt = this.prove(base, ((Universal) formula).getArgument().apply(subs));

                if (ansOpt.isPresent()) {

                    return Optional.of(new CompoundJustification("ForAllIntro", CollectionUtils.listOf(ansOpt.get())));
                } else {

                    return Optional.empty();
                    //TODO: Handle more than one variable
                }
            } else {

                return Optional.empty();
                //TODO: Handle more than one variable
            }


        } else if (formula instanceof Not && ((Not) formula).getArgument() instanceof Existential) {

            //formula = (not (exists [vars] kernel)) == (forall [vars] (not kernel))
            Formula    kernel = ((Existential) ((Not) formula).getArgument()).getArgument();
            Variable[] vars   = ((Existential) ((Not) formula).getArgument()).vars();


            if (vars.length == 1) {

                Map<Variable, Value> subs = CollectionUtils.newMap();
                subs.put(vars[0], Constant.newConstant());
                // logger.tryLog("Trying to prove ", (new Not(kernel)).apply(subs));

                return this.prove(base, (new Not(kernel)).apply(subs));

            } else {

                return Optional.empty();
                //TODO: Handle more than one variable
            }
        } else {

            return Optional.empty();
        }
    }

    protected Optional<Justification> tryNEC(Set<Formula> base, Formula formula, Set<Formula> added) {
        if (formula instanceof Necessity) {

            // logger.tryLog("Trying to prove necessity", formula);
            Optional<Justification> innerProof = this.prove(Sets.newSet(), ((Necessity) formula).getFormula());

            if (innerProof.isPresent()) {

                return Optional.of(new CompoundJustification("Nec Intro", CollectionUtils.listOf(innerProof.get())));
            } else {

                return Optional.empty();
            }

        } else {

            return Optional.empty();
        }
    }

    protected Optional<Justification> tryPOS(Set<Formula> base, Formula formula, Set<Formula> added) {
        if (formula instanceof Not && ((Not) formula).getArgument() instanceof Possibility) {

            Formula core = ((Possibility) ((Not) formula).getArgument()).getFormula();

            // logger.tryLog("Trying to prove necessity", new Necessity(new Not(core)));

            Optional<Justification> innerProof = this.prove(Sets.newSet(), new Necessity(new Not(core)));

            if (innerProof.isPresent()) {

                return Optional.of(new CompoundJustification("Pos Intro", CollectionUtils.listOf(innerProof.get())));
            } else {

                return Optional.empty();
            }

        } else {

            return Optional.empty();
        }
    }

    protected Optional<Justification> tryAND(Set<Formula> base, Formula formula, Set<Formula> added) {

        if (formula instanceof And) {

            And and = (And) formula;
            // logger.tryLog("Trying to prove conjunction", and);


            Formula conjuncts[] = and.getArguments();

            List<Optional<Justification>> conjunctProofsOpt = Arrays.stream(conjuncts).map(conjunct -> {

                CognitiveCalculusProver cognitiveCalculusProver = new CognitiveCalculusProver(this);
                return cognitiveCalculusProver.prove(base, conjunct);
            }).collect(Collectors.toList());


            if (conjunctProofsOpt.stream().allMatch(Optional::isPresent)) {

                return Optional.of(
                        new CompoundJustification("andIntro",
                                conjunctProofsOpt.stream().map(Optional::get).collect(Collectors.toList())));
            }
        }
        return Optional.empty();
    }

    protected Optional<Justification> tryOR(Set<Formula> base, Formula formula, Set<Formula> added) {

        Set<Or> level2ORs = CommonUtils.level2FormulaeOfType(base, Or.class);

        Optional<Or> someOrOpt = level2ORs.stream().findAny();

        if (someOrOpt.isPresent()) {

            Or        someOr    = someOrOpt.get();
            Formula[] disjuncts = someOr.getArguments();

            Set<Formula> reducedBase = CollectionUtils.setFrom(base);
            reducedBase.remove(someOr);

            List<Optional<Justification>> casesOpt = Arrays.stream(disjuncts).map(disjunct -> {
                CognitiveCalculusProver cognitiveCalculusProver = new CognitiveCalculusProver(this);

                Set<Formula> newBase = CollectionUtils.setFrom(reducedBase);
                newBase.add(disjunct);

                return cognitiveCalculusProver.prove(newBase, formula, CollectionUtils.setFrom(added));

            }).collect(Collectors.toList());

            boolean proved = casesOpt.stream().allMatch(Optional::isPresent);

            if (proved) {
                return Optional.of(new CompoundJustification("ORIntro", casesOpt.stream().map(Optional::get).collect(Collectors.toList())));
            } else {
                return Optional.empty();
            }

        } else {

            return Optional.empty();
        }

    }

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

        return reductioJustOpt.isPresent() ? Optional.of(new CompoundJustification("Reductio", CollectionUtils.listOf(reductioJustOpt.get()))) :
               Optional.empty();
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

}
