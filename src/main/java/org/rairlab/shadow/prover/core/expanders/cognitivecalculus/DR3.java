package org.rairlab.shadow.prover.core.expanders.cognitivecalculus;

import org.rairlab.shadow.prover.core.Logic;
import org.rairlab.shadow.prover.core.Prover;
import org.rairlab.shadow.prover.core.internals.Expander;
import org.rairlab.shadow.prover.core.proof.InferenceJustification;
import org.rairlab.shadow.prover.core.proof.Justification;
import org.rairlab.shadow.prover.representations.formula.Common;
import org.rairlab.shadow.prover.representations.formula.Formula;
import org.rairlab.shadow.prover.representations.formula.Knowledge;
import org.rairlab.shadow.prover.representations.value.Value;
import org.rairlab.shadow.prover.utils.CollectionUtils;
import org.rairlab.shadow.prover.utils.CommonUtils;

import java.util.Set;

public enum DR3 implements Expander {

    INSTANCE;

    @Override
    public void expand(Prover prover, Set<Formula> base, Set<Formula> added, Formula goal) {

        Set<Common> commons = CommonUtils.level2FormulaeOfType(base, Common.class);

        for (Common c : commons) {

            Justification j = InferenceJustification.from(this.getClass().getSimpleName(), c);

            Formula formula = c.getFormula();

            if (!added.contains(formula)) {
                base.add(formula.setJustification(j));
                added.add(formula);
            }
        }

    }


}
