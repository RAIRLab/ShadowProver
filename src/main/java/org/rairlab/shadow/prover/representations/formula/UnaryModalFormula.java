package org.rairlab.shadow.prover.representations.formula;

import org.rairlab.shadow.prover.representations.value.Value;

public interface UnaryModalFormula {
    Value getAgent();

    Value getTime();

    Formula getFormula();

}
