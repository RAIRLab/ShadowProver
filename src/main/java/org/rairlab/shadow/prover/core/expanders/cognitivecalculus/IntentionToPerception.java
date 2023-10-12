package org.rairlab.shadow.prover.core.expanders.cognitivecalculus;

import org.rairlab.shadow.prover.core.Prover;
import org.rairlab.shadow.prover.core.internals.Expander;
import org.rairlab.shadow.prover.core.proof.CompoundJustification;
import org.rairlab.shadow.prover.core.proof.InferenceJustification;
import org.rairlab.shadow.prover.core.proof.Justification;
import org.rairlab.shadow.prover.representations.formula.Formula;
import org.rairlab.shadow.prover.representations.formula.Intends;
import org.rairlab.shadow.prover.representations.formula.Knowledge;
import org.rairlab.shadow.prover.representations.formula.Perception;
import org.rairlab.shadow.prover.utils.CollectionUtils;
import org.rairlab.shadow.prover.utils.Constants;

import java.util.Set;
import java.util.stream.Collectors;

public enum IntentionToPerception implements Expander {

    INSTANCE;

    @Override
    public void expand(Prover prover, Set<Formula> base, Set<Formula> added, Formula goal) {


        Set<Formula> derived = base.
                stream().
                filter(f -> f instanceof Intends).
                map(f -> {
                    Intends    i = (Intends) f;
                    Perception k = new Perception(i.getAgent(), i.getTime(), i.getFormula());
                    k.setJustification(new CompoundJustification("Intention to Perception " + i, CollectionUtils.listOf(i.getJustification())));

                    Justification j = InferenceJustification.from(this.getClass().getSimpleName(), i);

                    return k.setJustification(j);
                }).
                collect(Collectors.toSet());

        prover.getLogger().expansionLog(String.format("Intends(P) ==> Perceives(P)", Constants.VDASH, Constants.PHI, Constants.NEC, Constants.PHI), derived);

        base.addAll(derived);
        added.addAll(derived);

    }
}
