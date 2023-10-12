package org.rairlab.shadow.prover.core.expanders.cognitivecalculus;

import org.rairlab.shadow.prover.core.Prover;
import org.rairlab.shadow.prover.core.internals.Expander;
import org.rairlab.shadow.prover.representations.formula.*;
import org.rairlab.shadow.prover.utils.Constants;

import java.util.Set;
import java.util.stream.Collectors;

public enum NecToPos implements Expander {

    INSTANCE;

    @Override
    public void expand(Prover prover, Set<Formula> base, Set<Formula> added, Formula goal) {

        Set<Formula> derived = base.
                stream().
                filter(f -> f instanceof Necessity).
                map(f -> (Necessity) f).
                filter(necessity -> necessity.getFormula() instanceof Not).
                map(necessity -> new Not(new Possibility(((Not) necessity.getFormula()).getArgument()))).collect(Collectors.toSet());

        prover.getLogger().expansionLog(" Necessarily not => Impossible", derived);
        base.addAll(derived);
        added.addAll(derived);

    }


}
