package org.rairlab.shadow.prover.core.expanders.cognitivecalculus;

import org.rairlab.shadow.prover.core.Prover;
import org.rairlab.shadow.prover.core.internals.Expander;
import org.rairlab.shadow.prover.representations.formula.Formula;
import org.rairlab.shadow.prover.representations.formula.Knowledge;
import org.rairlab.shadow.prover.representations.formula.Necessity;
import org.rairlab.shadow.prover.utils.Constants;
import org.rairlab.shadow.prover.utils.Sets;

import java.util.Set;
import java.util.stream.Collectors;

public enum TheoremsToNecessity implements Expander {

    INSTANCE;

    @Override
    public void expand(Prover prover, Set<Formula> base, Set<Formula> added, Formula goal) {

        Set<Formula> theorems = base.stream().map(Formula::subFormulae).reduce(Sets.newSet(), Sets::union).
                stream().filter(x -> !x.equals(goal) && prover.prove(Sets.newSet(), x).isPresent()).collect(Collectors.toSet());

        Set<Formula> necs = theorems.stream().map(Necessity::new).collect(Collectors.toSet());

        prover.getLogger().expansionLog(String.format("{} %s %s ==>  %s %s", Constants.VDASH, Constants.PHI, Constants.NEC, Constants.PHI), necs);

        base.addAll(necs);
        added.addAll(necs);
    }


}
