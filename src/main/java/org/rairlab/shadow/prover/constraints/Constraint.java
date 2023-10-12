package org.rairlab.shadow.prover.constraints;

import org.rairlab.shadow.prover.representations.formula.Formula;

import java.util.Set;

/**
 * Created by naveensundarg on 9/9/17.
 */
public interface Constraint {

    boolean satisfies (Set<Formula> formulae);
}
