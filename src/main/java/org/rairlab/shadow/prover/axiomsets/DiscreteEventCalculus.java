package org.rairlab.shadow.prover.axiomsets;

import org.rairlab.shadow.prover.constraints.NoFreeVariablesConstraint;
import org.rairlab.shadow.prover.constraints.Signature;
import org.rairlab.shadow.prover.representations.formula.Formula;

import java.util.Set;

/**
 * Created by naveensundarg on 9/9/17.
 */
public enum DiscreteEventCalculus implements AxiomSet{


    INSTANCE;

    private final Set<Formula> axioms;

    DiscreteEventCalculus(){

        axioms = AxiomSet.readFromFile(DiscreteEventCalculus.class.getResourceAsStream("discrete-event-calculus.clj"));


        NoFreeVariablesConstraint.INSTANCE.satisfies(axioms);

        Signature signature = new Signature(Signature.class.getResourceAsStream("discrete-event-calculus.clj"));
        signature.satisfies(axioms);


    }

    @Override
    public AxiomSet getInstance() {
            return INSTANCE;
    }

    @Override
    public Set<Formula> get() {
        return axioms;
    }
}
