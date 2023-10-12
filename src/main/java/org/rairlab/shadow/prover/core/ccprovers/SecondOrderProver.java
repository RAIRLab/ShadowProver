package org.rairlab.shadow.prover.core.ccprovers;

import org.rairlab.shadow.prover.core.Logic;
import org.rairlab.shadow.prover.core.Prover;
import org.rairlab.shadow.prover.core.SnarkWrapper;
import org.rairlab.shadow.prover.core.proof.Justification;
import org.rairlab.shadow.prover.core.sortsystem.SortSystem;
import org.rairlab.shadow.prover.representations.formula.Formula;
import org.rairlab.shadow.prover.representations.value.Value;
import org.rairlab.shadow.prover.representations.value.Variable;
import org.rairlab.shadow.prover.utils.Reader;
import org.rairlab.shadow.prover.utils.Sets;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by naveensundarg on 7/16/17.
 */
public class SecondOrderProver implements Prover {

    private final Prover folProver;
    public SecondOrderProver(){

        folProver = SnarkWrapper.getInstance();
    }
    @Override
    public Optional<Justification> prove(Set<Formula> assumptions, Formula formula) {

        try {
            Formula args1 = Reader.readFormulaFromString("(forall (x y z) (= (ARGS x) (ARGS y z)))");

            Formula args2 = Reader.readFormulaFromString("(forall (x y) (= (ARGS x) y))");

            Formula args3 = Reader.readFormulaFromString("(forall (x y1 y2 y3) (= (ARGS x) (ARGS y1 y2 y3)))");

            Set<Formula> argAxioms = Sets.fromArray(new Formula[]{});
            return folProver.
                    prove(Sets.union(assumptions.stream().map(Logic::transformSecondOrderToFirstOrderDeep).collect(Collectors.toSet()), argAxioms),
                            Logic.transformSecondOrderToFirstOrderDeep(formula));

        } catch (Reader.ParsingException e) {
            e.printStackTrace();

            return Optional.empty();
        }

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
