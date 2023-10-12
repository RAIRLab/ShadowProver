package org.rairlab.shadow.prover.representations.cnf;

import org.rairlab.shadow.prover.utils.Problem;
import org.rairlab.shadow.prover.representations.formula.And;
import org.rairlab.shadow.prover.representations.formula.Formula;
import org.rairlab.shadow.prover.representations.formula.Predicate;
import org.rairlab.shadow.prover.core.Logic;
import org.rairlab.shadow.prover.utils.Sets;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by naveensundarg on 4/10/16.
 */
public class CNFFormula {

    private final Set<Clause> clauses;


    public CNFFormula(Predicate P){

        this.clauses = Sets.with(new Clause(P));
    }

    public CNFFormula(Set<Clause> clauses){
        this.clauses  = Collections.unmodifiableSet(clauses);
    }

    public Set<Clause> getClauses() {
        return clauses;
    }

    public CNFFormula renameVars(Problem problem){

        return new CNFFormula(clauses.stream().map(x->Logic.renameVars(x, problem)).collect(Collectors.toSet()));
    }

    public Formula toFormula(){

        List<Formula> conjuncts = clauses.stream().map(Clause::toFormula).collect(Collectors.toList());
        if(conjuncts.size()==1){

          return conjuncts.get(0);

        }
        return new And(conjuncts);

    }

    @Override
    public String toString() {
        return clauses.stream().map(Clause::toString).reduce("", (x,y)-> x.isEmpty()? y: x + "\n" +y);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CNFFormula that = (CNFFormula) o;

        return clauses.equals(that.clauses);

    }

    @Override
    public int hashCode() {
        return clauses.hashCode();
    }
}
