package org.rairlab.shadow.prover.core.expanders.cognitivecalculus;

import org.rairlab.shadow.prover.core.Prover;
import org.rairlab.shadow.prover.core.internals.Expander;
import org.rairlab.shadow.prover.core.proof.CompoundJustification;
import org.rairlab.shadow.prover.representations.formula.Belief;
import org.rairlab.shadow.prover.representations.formula.Formula;
import org.rairlab.shadow.prover.representations.formula.Knowledge;
import org.rairlab.shadow.prover.representations.formula.Perception;
import org.rairlab.shadow.prover.utils.CollectionUtils;
import org.rairlab.shadow.prover.utils.Constants;
import org.rairlab.shadow.prover.utils.Reader;

import java.util.Set;
import java.util.stream.Collectors;

public enum PerceptionToKnowledge implements Expander {

    INSTANCE;

    @Override
    public void expand(Prover prover, Set<Formula> base, Set<Formula> added, Formula goal) {


        Set<Formula> derived = base.
                stream().
                filter(f -> f instanceof Perception).
                map(f -> {
                    Perception p = (Perception) f;
                    Knowledge  k = new Knowledge(p.getAgent(), p.getTime(), p.getFormula());
                    k.setJustification(new CompoundJustification("Perception to knowledge " + p, CollectionUtils.listOf(p.getJustification())));
                    return k;
                }).
                collect(Collectors.toSet());

        prover.getLogger().expansionLog(String.format("Perceives(P) ==> P", Constants.VDASH, Constants.PHI, Constants.NEC, Constants.PHI), derived);

        base.addAll(derived);
        added.addAll(derived);

    }
}
