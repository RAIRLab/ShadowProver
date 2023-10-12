package org.rairlab.shadow.prover.representations.method;

import org.rairlab.shadow.prover.representations.Phrase;
import org.rairlab.shadow.prover.representations.formula.Formula;

import java.util.List;
import java.util.Set;

/**
 * Created by naveensundarg on 8/27/17.
 */
public abstract class PrimitiveMethod extends Phrase{


    public abstract Phrase apply (Set<Formula> assumptionBase, List<Phrase> args);


}
