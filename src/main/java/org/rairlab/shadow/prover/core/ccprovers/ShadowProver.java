package org.rairlab.shadow.prover.core.ccprovers;

import org.rairlab.shadow.prover.core.Prover;
import org.rairlab.shadow.prover.core.SnarkWrapper;
import org.rairlab.shadow.prover.core.internals.AgentSnapShot;
import org.rairlab.shadow.prover.core.proof.Justification;
import org.rairlab.shadow.prover.core.proof.TrivialJustification;
import org.rairlab.shadow.prover.core.sortsystem.SortSystem;
import org.rairlab.shadow.prover.representations.formula.Belief;
import org.rairlab.shadow.prover.representations.formula.Formula;
import org.rairlab.shadow.prover.representations.formula.Intends;
import org.rairlab.shadow.prover.representations.formula.Knowledge;
import org.rairlab.shadow.prover.representations.value.Value;
import org.rairlab.shadow.prover.representations.value.Variable;
import org.rairlab.shadow.prover.utils.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


public class ShadowProver implements Prover {


    Prover folProver;


    public ShadowProver() {

        folProver = SnarkWrapper.getInstance();


    }

    @Override
    public Optional<Justification> prove(Set<Formula> assumptions, Formula goal) {
        Set<Formula> base = CollectionUtils.setFrom(assumptions);

        Formula shadowedGoal = goal.shadow(1);

        Optional<Justification> shadowedJustificationOpt = folProver.prove(shadow(base), shadowedGoal);
        Optional<Justification> agentClosureJustificationOpt = proveAgentClosure(base, goal);

        while (!shadowedJustificationOpt.isPresent() && !agentClosureJustificationOpt.isPresent()) {


            int sizeBeforeExpansion = base.size();
            base = expand(base, goal);
            int sizeAfterExpansion = base.size();

            if(sizeAfterExpansion == sizeBeforeExpansion){

                return Optional.empty();
            }
            if (base.contains(goal)) {
                return Optional.of(TrivialJustification.trivial(base, goal));
            }

            shadowedJustificationOpt = folProver.prove(shadow(base), shadowedGoal);
            agentClosureJustificationOpt = proveAgentClosure(base, goal);

        }

        if (shadowedJustificationOpt.isPresent()) {

            return shadowedJustificationOpt;
        }

        return agentClosureJustificationOpt;


    }

    public Set<Formula> expand(Set<Formula> base, Formula goal) {
        return base;
    }

    @Override
    public Optional<Justification> prove(SortSystem sortSystem, Set<Formula> assumptions, Formula formula) {

        return null;
    }

    @Override
    public Optional<Value> proveAndGetBinding(Set<Formula> assumptions, Formula formula, Variable variable) {
        return Optional.empty();
    }

    @Override
    public Optional<Map<Variable, Value>> proveAndGetBindings(Set<Formula> assumptions, Formula formula, List<Variable> variable) {
        return Optional.empty();
    }

    @Override
    public Optional<Pair<Justification, Set<Map<Variable, Value>>>> proveAndGetMultipleBindings(Set<Formula> assumptions, Formula formula, List<Variable> variable) {
        return Optional.empty();
    }

    protected Set<Formula> shadow(Set<Formula> formulas) {
        return formulas.stream().map(f -> f.shadow(1)).collect(Collectors.toSet());
    }


    private Optional<Justification> proveAgentClosure(Set<Formula> base, Formula goal) {

        if (goal instanceof Belief) {

            Belief belief = (Belief) goal;
            Value agent = belief.getAgent();
            Value time = belief.getTime();
            Formula goalBelief = belief.getFormula();

            AgentSnapShot agentSnapShot = AgentSnapShot.from(base);

            Set<Formula> allBelievedTillTime = agentSnapShot.allBelievedByAgentTillTime(agent, time);


            ShadowProver shadowProver = new ShadowProver();
            Optional<Justification> inner = shadowProver.prove(allBelievedTillTime, goalBelief);
            if (inner.isPresent()) {
                //TODO: Augment this

                return inner;
            }

        }

        if (goal instanceof Intends) {


            Intends intends = (Intends) goal;
            Value agent = intends.getAgent();
            Value time = intends.getTime();
            Formula goalKnowledge = intends.getFormula();

            AgentSnapShot agentSnapShot = AgentSnapShot.from(base);

            Set<Formula> allIntendedByTillTime = agentSnapShot.allIntendedByAgentTillTime(agent, time);


            ShadowProver shadowProver = new ShadowProver();
            Optional<Justification> inner = shadowProver.prove(allIntendedByTillTime, goalKnowledge);
            if (inner.isPresent()) {
                //TODO: Augment this

                return inner;
            }
        }

        if (goal instanceof Knowledge) {


            Knowledge knowledge = (Knowledge) goal;
            Value agent = knowledge.getAgent();
            Value time = knowledge.getTime();
            Formula goalKnowledge = knowledge.getFormula();

            AgentSnapShot agentSnapShot = AgentSnapShot.from(base);

            Set<Formula> allKnownByTillTime = agentSnapShot.allKnownByAgentTillTime(agent, time);


            ShadowProver shadowProver = new ShadowProver();
            Optional<Justification> inner = shadowProver.prove(allKnownByTillTime, goalKnowledge);
            if (inner.isPresent()) {
                //TODO: Augment this

                return inner;
            }
        }

        return Optional.empty();

    }


}
