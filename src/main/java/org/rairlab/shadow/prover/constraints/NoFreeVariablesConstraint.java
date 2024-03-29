package org.rairlab.shadow.prover.constraints;

import org.rairlab.shadow.prover.representations.formula.Formula;
import org.rairlab.shadow.prover.utils.Sets;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by naveensundarg on 9/9/17.
 */
public enum  NoFreeVariablesConstraint implements Constraint {

    INSTANCE;


    @Override
    public boolean satisfies(Set<Formula> formulae) {

        Set<Formula> violatingFormulae = formulae.stream().
                filter(f-> !(Sets.difference(f.variablesPresent(), f.boundVariablesPresent()).isEmpty())).
                collect(Collectors.toSet());

        if(!violatingFormulae.isEmpty()){

            throw new AssertionError("NoFreeVariablesConstraint violated by: " + violatingFormulae);
        }

        return true;

    }
}
