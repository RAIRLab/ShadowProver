package org.rairlab.shadow.prover.core.ccprovers;

import org.rairlab.shadow.prover.core.Logic;
import org.rairlab.shadow.prover.core.Prover;
import org.rairlab.shadow.prover.core.proof.Justification;
import org.rairlab.shadow.prover.core.sortsystem.SortSystem;
import org.rairlab.shadow.prover.representations.formula.Formula;
import org.rairlab.shadow.prover.representations.value.Value;
import org.rairlab.shadow.prover.representations.value.Variable;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by naveensundarg on 7/16/17.
 */
public class SecondOrderCognitiveCalculusProver implements Prover {

    private final CognitiveCalculusProver cognitiveCalculusProver;
    public SecondOrderCognitiveCalculusProver(){

        cognitiveCalculusProver = new CognitiveCalculusProver();
    }
    @Override
    public Optional<Justification> prove(Set<Formula> assumptions, Formula formula) {

        return cognitiveCalculusProver.
                prove(assumptions.stream().map(Logic::transformSecondOrderToFirstOrder).collect(Collectors.toSet()),
                        Logic.transformSecondOrderToFirstOrder(formula));
    }

    @Override
    public Optional<Justification> prove(SortSystem sortSystem, Set<Formula> assumptions, Formula formula) {
        return null;
    }

    @Override
    public Optional<Value> proveAndGetBinding(Set<Formula> assumptions, Formula formula, Variable variable) {
        return null;
    }

    @Override
    public Optional<Map<Variable, Value>> proveAndGetBindings(Set<Formula> assumptions, Formula formula, List<Variable> variable) {
        return null;
    }

    @Override
    public Optional<Pair<Justification, Set<Map<Variable, Value>>>> proveAndGetMultipleBindings(Set<Formula> assumptions, Formula formula, List<Variable> variable) {
        return null;
    }
}
