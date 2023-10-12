package org.rairlab.shadow.prover.core.expanders.cognitivecalculus;

import org.rairlab.shadow.prover.core.Prover;
import org.rairlab.shadow.prover.core.internals.Expander;
import org.rairlab.shadow.prover.representations.formula.Formula;
import org.rairlab.shadow.prover.representations.formula.Knowledge;
import org.rairlab.shadow.prover.utils.Constants;
import org.rairlab.shadow.prover.utils.Logger;

import java.util.Set;
import java.util.stream.Collectors;

public enum R4 implements Expander {

    INSTANCE;

    @Override
    public void expand(Prover prover, Set<Formula> base, Set<Formula> added, Formula goal) {


        Set<Formula> derived = base.
                stream().
                filter(f -> f instanceof Knowledge).
                map(f -> ((Knowledge) f).getFormula()).
                collect(Collectors.toSet());

        if (!base.containsAll(derived)) {
            prover.getLogger().expansionLog("Knows(P) ==> P "  + Constants.VDASH + Constants.PHI + Constants.NEC + Constants.PHI, derived);

        }

        base.addAll(derived);
        added.addAll(derived);

    }


}
