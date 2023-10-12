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

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CognitiveCalculusProverTests {


    Prover prover;
    Map<Problem, Pair<Clause, Clause>> used;
    CognitiveCalculusProverTests(){

        prover = new CognitiveCalculusProver();
    }

    @DataProvider(name="completenessTestsProvider")
    public Object[][] completenessTestsProvider() throws Reader.ParsingException, FileNotFoundException {

       List<Problem >tests = ProblemReader.readFrom("problems/cognitivecalculus-completness-tests.clj");
       Object[][] cases =  new Object[tests.size()][2];

        for(int  i = 0; i < tests.size(); i++){

            Problem test = tests.get(i);

            cases[i][0] =  test.getAssumptions();
            cases[i][1] = test.getGoal();

        }


        return cases;

    }


    @Test(dataProvider = "completenessTestsProvider")
    public void testCompleteness(Set<Formula> assumptions, Formula formula){


        Assert.assertTrue(prover.prove(assumptions, formula).isPresent());

    }

    @DataProvider(name="debugTestsProvider")
    public Object[][] debugTestsProvider() throws Reader.ParsingException {

        List<Problem >tests = ProblemReader.readFrom(Sandbox.class.getResourceAsStream("problems/debug.clj"));
        Object[][] cases =  new Object[tests.size()][2];

        for(int  i = 0; i < tests.size(); i++){

            Problem test = tests.get(i);

            cases[i][0] =  test.getAssumptions();
            cases[i][1] = test.getGoal();

        }


        return cases;

    }


    @DataProvider(name="soundnessTestsProvider")
    public Object[][] soundnessTestsProvider() throws Reader.ParsingException, FileNotFoundException {

        List<Problem >tests = ProblemReader.readFrom("problems/cognitivecalculus-soundness-tests.clj");
        Object[][] cases =  new Object[tests.size()][2];

        for(int  i = 0; i < tests.size(); i++){

            Problem test = tests.get(i);

            cases[i][0] =  test.getAssumptions();
            cases[i][1] = test.getGoal();

        }


        return cases;

    }


    @Test(dataProvider = "soundnessTestsProvider")
    public void testSoundess(Set<Formula> assumptions, Formula formula){

        Assert.assertFalse(prover.prove(assumptions, formula).isPresent());

    }

}
