package org.rairlab.shadow.prover.core.expanders.cognitivecalculus;

import org.rairlab.shadow.prover.core.Logic;
import org.rairlab.shadow.prover.core.Prover;
import org.rairlab.shadow.prover.core.internals.Expander;
import org.rairlab.shadow.prover.core.proof.CompoundJustification;
import org.rairlab.shadow.prover.core.proof.InferenceJustification;
import org.rairlab.shadow.prover.core.proof.Justification;
import org.rairlab.shadow.prover.representations.formula.*;
import org.rairlab.shadow.prover.representations.value.Variable;
import org.rairlab.shadow.prover.utils.CommonUtils;

import java.util.Set;
import java.util.stream.Collectors;

public enum BreakupBiConditionals implements Expander {

    INSTANCE;

    @Override
    public void expand(Prover prover, Set<Formula> base, Set<Formula> added, Formula goal) {



        Set<BiConditional> biConditionals = CommonUtils.formulaOfType(base, BiConditional.class);

        biConditionals.forEach(biConditional -> {

            Justification j = InferenceJustification.from(this.getClass().getSimpleName(), biConditional);

            base.add(new Implication(biConditional.getLeft(), biConditional.getRight()).setJustification(j));
            base.add(new Implication(biConditional.getRight(), biConditional.getLeft()).setJustification(j));
            base.add(new Implication(Logic.negated(biConditional.getLeft()), Logic.negated(biConditional.getRight())).setJustification(j));
            base.add(new Implication(Logic.negated(biConditional.getRight()), Logic.negated(biConditional.getLeft())).setJustification(j));

        });

    }


}
