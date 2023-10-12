package org.rairlab.shadow.prover.core.expanders.cognitivecalculus;

import org.rairlab.shadow.prover.core.Prover;
import org.rairlab.shadow.prover.core.internals.Expander;
import org.rairlab.shadow.prover.representations.formula.Belief;
import org.rairlab.shadow.prover.representations.formula.Formula;
import org.rairlab.shadow.prover.representations.formula.Implication;
import org.rairlab.shadow.prover.representations.formula.Knowledge;
import org.rairlab.shadow.prover.utils.CommonUtils;
import org.rairlab.shadow.prover.utils.Constants;

import java.util.Set;
import java.util.stream.Collectors;

public enum R11a implements Expander {

    INSTANCE;

    @Override
    public void expand(Prover prover, Set<Formula> base, Set<Formula> added, Formula goal) {

        Set<Belief> implicationBeliefs =
                CommonUtils.level2FormulaeOfTypeWithConstraint(base, Belief.class, b -> ((Belief) b).getFormula() instanceof Implication);


        Set<Formula> validConsequentBeliefss = implicationBeliefs.stream().
                filter(b -> base.contains(new Belief(b.getAgent(), b.getTime(), ((Implication) b.getFormula()).getAntecedent()))).
                map(b -> new Belief(b.getAgent(), b.getTime(), ((Implication) b.getFormula()).getConsequent())).
                filter(f -> !added.contains(f)).
                collect(Collectors.toSet());

        base.addAll(validConsequentBeliefss);
        added.addAll(added);

    }


}
