package org.rairlab.shadow.prover.core.expanders.cognitivecalculus;

import org.rairlab.shadow.prover.core.Prover;
import org.rairlab.shadow.prover.core.internals.Expander;
import org.rairlab.shadow.prover.core.proof.InferenceJustification;
import org.rairlab.shadow.prover.core.proof.Justification;
import org.rairlab.shadow.prover.representations.formula.Belief;
import org.rairlab.shadow.prover.representations.formula.Formula;
import org.rairlab.shadow.prover.representations.formula.Knowledge;
import org.rairlab.shadow.prover.representations.value.Value;
import org.rairlab.shadow.prover.utils.CommonUtils;

import java.util.Set;

public enum DR5 implements Expander {

    INSTANCE;

    @Override
    public void expand(Prover prover, Set<Formula> base, Set<Formula> added, Formula goal) {

        Set<Knowledge> knows = CommonUtils.level2FormulaeOfType(base, Knowledge.class);

        for (Knowledge k : knows) {

            Value   agent   = k.getAgent();
            Value   time    = k.getTime();
            Formula formula = k.getFormula();

            Justification j = InferenceJustification.from(this.getClass().getSimpleName(), k);

            Belief belief = new Belief(agent, time, formula);
            if (!added.contains(belief)) {
                base.add(belief.setJustification(j));
                added.add(belief);
            }

        }
    }


}
