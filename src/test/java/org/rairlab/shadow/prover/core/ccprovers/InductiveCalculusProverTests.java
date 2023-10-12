package org.rairlab.shadow.prover.core.ccprovers;

import org.rairlab.shadow.prover.core.Prover;
import org.rairlab.shadow.prover.representations.cnf.Clause;
import org.rairlab.shadow.prover.representations.formula.Formula;
import org.rairlab.shadow.prover.sandboxes.Sandbox;
import org.rairlab.shadow.prover.utils.Pair;
import org.rairlab.shadow.prover.utils.Problem;
import org.rairlab.shadow.prover.utils.ProblemReader;
import org.rairlab.shadow.prover.utils.Reader;
import junit.framework.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class InductiveCalculusProverTests {


    Prover prover;
    Map<Problem, Pair<Clause, Clause>> used;
    InductiveCalculusProverTests(){

        prover = new InductiveCalculusProver();
    }

    @DataProvider(name="completenessTestsProvider")
    public Object[][] completenessTestsProvider() throws Reader.ParsingException {

       List<Problem >tests = ProblemReader.readFrom(InductiveCalculusProver.class.getResourceAsStream("inductivecalculus-completness-tests.clj"));
       Object[][] cases =  new Object[tests.size()][3];

        for(int  i = 0; i < tests.size(); i++){

            Problem test = tests.get(i);

            cases[i][0] = test.getName();
            cases[i][1] =  test.getAssumptions();
            cases[i][2] = test.getGoal();

        }


        return cases;

    }


    @Test(dataProvider = "completenessTestsProvider")
    public void testCompleteness(String name, Set<Formula> assumptions, Formula formula){


        Assert.assertTrue(prover.prove(assumptions, formula).isPresent());

    }

}
