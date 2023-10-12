package org.rairlab.shadow.prover.core.propositionalmodalprovers;

import org.rairlab.shadow.prover.core.Logic;
import org.rairlab.shadow.prover.representations.formula.Formula;
import org.rairlab.shadow.prover.utils.Sets;

import java.util.Set;

public class LP2 extends LP {

    @Override
    public boolean canApplyRule(Set<Formula> background, Formula f) {
        return Logic.isConsistent(Sets.add(background, f));
    }

}
