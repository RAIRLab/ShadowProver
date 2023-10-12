package org.rairlab.shadow.prover.core.ccprovers;

import org.rairlab.shadow.prover.core.Logic;
import org.rairlab.shadow.prover.core.Prover;
import org.rairlab.shadow.prover.core.SnarkWrapper;
import org.rairlab.shadow.prover.core.expanders.cognitivecalculus.*;
import org.rairlab.shadow.prover.core.expanders.inductivecalculus.Generalize;
import org.rairlab.shadow.prover.core.internals.Expander;
import org.rairlab.shadow.prover.core.proof.Justification;
import org.rairlab.shadow.prover.representations.formula.Exemplar;
import org.rairlab.shadow.prover.representations.formula.Formula;
import org.rairlab.shadow.prover.utils.CollectionUtils;
import org.rairlab.shadow.prover.utils.Logger;
import org.rairlab.shadow.prover.utils.Reader;
import org.rairlab.shadow.prover.utils.Sets;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class InductiveCalculusProver implements Prover {

    public Prover prover;
    private final List<Expander> expanders;
    private static int MAX_EXPAND_FACTOR = 100;
    protected Logger logger;

    public InductiveCalculusProver() {

        prover = SnarkWrapper.getInstance();

        expanders = CollectionUtils.newEmptyList();

        expanders.add(Generalize.INSTANCE);
        expanders.add(BreakupBiConditionals.INSTANCE);
        expanders.add(ModalConjunctions.INSTANCE);
        //expanders.add(ModalImplications.INSTANCE);
        expanders.add(UniversalElim.INSTANCE);
        expanders.add(NotExistsToForallNot.INSTANCE);
        logger = new Logger();

    }

    @Override
    public Optional<Justification> prove(Set<Formula> assumptions, Formula formula) {

        return prove(assumptions, formula, Sets.newSet());

    }

    public synchronized Optional<Justification> prove(Set<Formula> assumptions, Formula formula, Set<Formula> added) {

        Set<Formula> base = CollectionUtils.setFrom(assumptions);

        while (true) {
            int sizeBeforeExpansion = base.size();
            base = expand(base, added, formula);
            int sizeAfterExpansion = base.size();
            if (sizeAfterExpansion > MAX_EXPAND_FACTOR * assumptions.size()) {
                return Optional.empty();
            }
            if (sizeAfterExpansion <= sizeBeforeExpansion) {
                return Optional.empty();
            }

            Set<Formula> order1ModalAssumptions = base.stream().map(Logic::transformSecondOrderToFirstOrder).collect(Collectors.toSet());
            Formula order1Goal = Logic.transformSecondOrderToFirstOrder(formula);

            try {
                order1ModalAssumptions.add(Reader.readFormulaFromString("(forall [?x] (= (ARGS) (ARGS ?x)))"));
            } catch (Reader.ParsingException e) {
                e.printStackTrace();
            }
            Optional<Justification> optionalJustification = prover.prove(shadow(order1ModalAssumptions), order1Goal.shadow(1));
            if (optionalJustification.isPresent()) {
                return optionalJustification;
            }

        }


    }


    public Set<Formula> expand(Set<Formula> base, Set<Formula> added, Formula goal) {

        expanders.forEach(expander -> expander.expand(this, base, added, goal));

        return base;
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    protected Set<Formula> shadow(Set<Formula> formulas) {
        return formulas.stream().map(f -> f.shadow(1)).collect(Collectors.toSet());
    }
}
