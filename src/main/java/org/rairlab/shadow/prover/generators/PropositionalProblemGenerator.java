package org.rairlab.shadow.prover.generators;


import org.rairlab.shadow.prover.core.Logic;
import org.rairlab.shadow.prover.core.Prover;
import org.rairlab.shadow.prover.core.SnarkWrapper;
import org.rairlab.shadow.prover.representations.formula.Atom;
import org.rairlab.shadow.prover.representations.formula.Formula;
import org.rairlab.shadow.prover.representations.formula.Not;
import org.rairlab.shadow.prover.representations.formula.Or;
import org.rairlab.shadow.prover.utils.CollectionUtils;
import org.rairlab.shadow.prover.utils.CommonUtils;
import org.rairlab.shadow.prover.utils.ImmutablePair;
import org.rairlab.shadow.prover.utils.Pair;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class PropositionalProblemGenerator implements Generator {

    static Prover prover;

    private final int maxLiteralsInClause;
    private final int maxAtoms;
    private final int totalClauses;


    public PropositionalProblemGenerator(GeneratorParams generatorParams){

        this.maxAtoms = generatorParams.maxAtoms;
        this.maxLiteralsInClause = generatorParams.maxLiteralsInClause;
        this.totalClauses = generatorParams.clauses;
    }


    static{

        prover = SnarkWrapper.getInstance();
    }

    @Override
    public List<Pair<List<Formula>, Boolean>> generate(int total) {



        List<Pair<List<Formula>, Boolean>> generated = CollectionUtils.newEmptyList();


        for (int i = 0; i < total; i++){


            generated.add(generateProblem());
        }



        return generated;


    }


    private Pair<List<Formula>, Boolean> generateProblem(){

        List<Formula> clauses = CollectionUtils.newEmptyList();



        for(int i = 0; i< totalClauses; i++){

            clauses.add(generateRandomClause());

        }


       // Formula goalNeg = CommonUtils.pickRandom(clauses);

        if(prover.prove(new HashSet<>(clauses), Logic.getFalseFormula()).isPresent()){

            return ImmutablePair.from(clauses, true);

        } else {

            return ImmutablePair.from(clauses, false);
        }

    }

    private Formula generateRandomClause(){

        int totalLiteralsInClause = ThreadLocalRandom.current().nextInt(1, maxLiteralsInClause + 1 );

        List<Formula> clauseLiterals = CollectionUtils.newEmptyList();


        for(int i = 0; i< totalLiteralsInClause; i++){

            clauseLiterals.add(generateRandomLiteral());

        }

        return new Or(clauseLiterals);


    }

    private Formula generateRandomLiteral(){

        Atom atom = generateRandomAtom();
        if(ThreadLocalRandom.current().nextBoolean()){

            return atom;

        } else {

            return new Not(atom);
        }

    }


    private  Atom generateRandomAtom(){

        return new Atom(Names.NAMES[ThreadLocalRandom.current().nextInt(0, maxAtoms)]);

    }







}



