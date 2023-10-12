package org.rairlab.shadow.prover.core;

import org.rairlab.shadow.prover.core.proof.Justification;
import org.rairlab.shadow.prover.core.sortsystem.SortSystem;
import org.rairlab.shadow.prover.representations.formula.Formula;
import org.rairlab.shadow.prover.representations.value.Value;
import org.rairlab.shadow.prover.representations.value.Variable;
import org.rairlab.shadow.prover.utils.Logger;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Created by naveensundarg on 4/8/16.
 */
public interface Prover {

    default Optional<Integer> proofLength(Set<Formula> assumptions, Formula formula){
        throw new NotImplementedException("Proof length not implemented in: " + this.getClass());
    }

    Optional<Justification> prove(Set<Formula> assumptions, Formula formula);


    default Optional<Justification> prove(SortSystem sortSystem, Set<Formula> assumptions, Formula formula){
        throw new NotImplementedException("Sorted proof system not implemented in: " + this.getClass());
    }

    default Optional<Value> proveAndGetBinding(Set<Formula> assumptions, Formula formula, Variable variable){
        return Optional.empty();
    }

    default Optional<Map<Variable, Value>> proveAndGetBindings(Set<Formula> assumptions, Formula formula, List<Variable> variable){
        return Optional.empty();
    }

    default Optional<Pair<Justification, Set<Map<Variable, Value>>>> proveAndGetMultipleBindings(Set<Formula> assumptions, Formula formula, List<Variable> variable){
        return Optional.empty();
    }

    default Logger getLogger(){
        throw new NotImplementedException("No logger set.");
    }
}
