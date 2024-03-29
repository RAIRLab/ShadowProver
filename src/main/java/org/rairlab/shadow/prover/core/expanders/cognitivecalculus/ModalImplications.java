package org.rairlab.shadow.prover.core.expanders.cognitivecalculus;

import org.rairlab.shadow.prover.core.Logic;
import org.rairlab.shadow.prover.core.Prover;
import org.rairlab.shadow.prover.core.ccprovers.CognitiveCalculusProver;
import org.rairlab.shadow.prover.core.internals.Expander;
import org.rairlab.shadow.prover.core.proof.Justification;
import org.rairlab.shadow.prover.representations.formula.And;
import org.rairlab.shadow.prover.representations.formula.Formula;
import org.rairlab.shadow.prover.representations.formula.Implication;
import org.rairlab.shadow.prover.utils.CollectionUtils;
import org.rairlab.shadow.prover.utils.CommonUtils;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public enum ModalImplications implements Expander {

    INSTANCE;

    @Override
    public void expand(Prover prover, Set<Formula> base, Set<Formula> added, Formula goal) {

        Set<Implication> level2Ifs = CommonUtils.level2FormulaeOfType(base, Implication.class);

        if(!(prover instanceof  CognitiveCalculusProver)) {

            throw new AssertionError("ModalImplications should be used only in a cognitive calculus.");

        }
        CognitiveCalculusProver rootCognitiveCalculusProver = (CognitiveCalculusProver) prover;
        for (Implication implication : level2Ifs) {

            if (rootCognitiveCalculusProver.getProhibited().contains(implication)) {
                continue;
            }

            Formula antecedent = implication.getAntecedent();
            Formula consequent = implication.getConsequent();

            CognitiveCalculusProver cognitiveCalculusProver = new CognitiveCalculusProver(rootCognitiveCalculusProver);
            cognitiveCalculusProver.getProhibited().addAll(rootCognitiveCalculusProver.getProhibited());
            cognitiveCalculusProver.getProhibited().add(implication);

            Set<Formula> reducedBase = CollectionUtils.setFrom(base);

            reducedBase.remove(implication);

            //TODO: use actual ancestors

            boolean                 alreadyExpanded            = false;
            Optional<Justification> antecedentJustificationOpt = cognitiveCalculusProver.prove(reducedBase, antecedent, CollectionUtils.setFrom(added));
            if (antecedentJustificationOpt.isPresent()) {
                if (!added.contains(consequent)) {
                    base.add(consequent);
                    added.add(consequent);

                    alreadyExpanded = true;
                }
            }

            Set<Formula> newReducedBase = CollectionUtils.setFrom(base);
            newReducedBase.remove(implication);


            if (!alreadyExpanded) {
                Optional<Justification> negatedConsequentJustificationOpt = cognitiveCalculusProver.prove(newReducedBase, Logic.negated(consequent), CollectionUtils.setFrom(added));
                if (negatedConsequentJustificationOpt.isPresent()) {
                    if (!added.contains(consequent)) {
                        base.add(Logic.negated(antecedent));
                        added.add(Logic.negated(antecedent));
                    }
                }
            }
        }

    }

}
