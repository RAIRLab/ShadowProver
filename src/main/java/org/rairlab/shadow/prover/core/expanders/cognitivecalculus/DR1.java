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
import org.rairlab.shadow.prover.utils.Constants;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public enum DR1 implements Expander {

    INSTANCE;

    int knowledgeIterationDepth = 2;
    @Override
    public void expand(Prover prover, Set<Formula> base, Set<Formula> added, Formula goal) {

        Set<Common>       commons      = CommonUtils.level2FormulaeOfType(base, Common.class);
        Set<Value>        agents       = Logic.allAgents(CollectionUtils.addToSet(base, goal));
        List<List<Value>> agent1Agent2 = CommonUtils.setPower(agents, knowledgeIterationDepth);

        for (Common c : commons) {
            for (List<Value> agentPair : agent1Agent2) {
                Formula formula = c.getFormula();
                Value   time    = c.getTime();
                Formula current = formula;

                for (int i = 0; i < knowledgeIterationDepth; i++) {
                    current = new Knowledge(agentPair.get(i), time, current);
                }

                Justification j = InferenceJustification.from(this.getClass().getSimpleName(), c);
                if (!added.contains(current)) {
                    base.add(current.setJustification(j));
                    added.add(current);
                }

            }
        }

    }


}
