package org.rairlab.shadow.prover.core.expanders.cognitivecalculus;

import org.rairlab.shadow.prover.core.Prover;
import org.rairlab.shadow.prover.core.internals.Expander;
import org.rairlab.shadow.prover.core.internals.UniversalInstantiation;
import org.rairlab.shadow.prover.representations.formula.Formula;
import org.rairlab.shadow.prover.representations.formula.Knowledge;
import org.rairlab.shadow.prover.representations.formula.Universal;
import org.rairlab.shadow.prover.representations.value.Value;
import org.rairlab.shadow.prover.representations.value.Variable;
import org.rairlab.shadow.prover.utils.CollectionUtils;
import org.rairlab.shadow.prover.utils.Constants;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.rairlab.shadow.prover.utils.Sets.cartesianProduct;

public enum UniversalElim implements Expander {

    INSTANCE;

    @Override
    public void expand(Prover prover, Set<Formula> base, Set<Formula> added, Formula goal) {


        //TODO: Less stupid elimination

        Set<Formula> formulae = CollectionUtils.setFrom(base);
        formulae.add(goal);

        Set<Universal> universals = base.stream().filter(f -> f instanceof Universal).map(f -> (Universal) f).collect(Collectors.toSet());


        universals.forEach(universal -> {


            List<Set<Value>> smartValues = UniversalInstantiation.smartHints(universal, formulae);

            Set<List<Value>> substitutions = cartesianProduct(smartValues);
            Variable[]       vars          = universal.vars();

            substitutions.forEach(substitution -> {
                Map<Variable, Value> mapping = CollectionUtils.newMap();

                        for (int i = 0; i < vars.length; i++) {

                            mapping.put(vars[i], substitution.get(vars.length - 1 - i));

                        }

                        Formula derived = universal.getArgument().apply(mapping);

                        if (!added.contains(derived)) {
                            base.add(derived);
                            added.add(derived);
                        }

                    }

            );


        });

    }


}
