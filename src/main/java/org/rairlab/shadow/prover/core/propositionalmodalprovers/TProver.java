package org.rairlab.shadow.prover.core.propositionalmodalprovers;

import org.rairlab.shadow.prover.core.Prover;
import org.rairlab.shadow.prover.core.SnarkWrapper;
import org.rairlab.shadow.prover.core.proof.Justification;
import org.rairlab.shadow.prover.core.sortsystem.SortSystem;
import org.rairlab.shadow.prover.representations.formula.Formula;
import org.rairlab.shadow.prover.representations.value.Value;
import org.rairlab.shadow.prover.representations.value.Variable;
import org.rairlab.shadow.prover.utils.CollectionUtils;
import org.rairlab.shadow.prover.utils.Problem;
import org.rairlab.shadow.prover.utils.Reader;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by naveensundarg on 1/1/17.
 */
public class TProver implements Prover {

    static Formula reflexivity;

    static {

        try {
            reflexivity = Reader.readFormulaFromString("(forall (?x) (!R! ?x ?x))");
        } catch (Reader.ParsingException e) {
            e.printStackTrace();
        }

    }


    @Override
    public Optional<Justification> prove(Set<Formula> assumptions, Formula formula) {

        Problem problem = new Problem("TPROVER", "TPROVER FOL CONVERSION", assumptions, formula);



        Map<Variable, Value> map = CollectionUtils.newMap();

        map.put(PropositionalModalConverter.WORLD, PropositionalModalConverter.W);

        Set<Formula> convertedAssumptions = assumptions.stream().map(x -> PropositionalModalConverter.convert(x, problem)).
                map(x->x.apply(map)).
                collect(Collectors.toSet());
        Formula convertedGoal = PropositionalModalConverter.convert(formula, problem).apply(map);

        convertedAssumptions.add(reflexivity);
        Prover firstOrderHalo = SnarkWrapper.getInstance();

        return firstOrderHalo.prove(convertedAssumptions, convertedGoal);

    }


    @Override
    public Optional<Justification> prove(SortSystem sortSystem, Set<Formula> assumptions, Formula formula) {
        return null;
    }

    //Set<Formula> getAxioms();

}
