package org.rairlab.shadow.prover;

import org.rairlab.shadow.prover.utils.Problem;
import org.rairlab.shadow.prover.core.Prover;
import org.rairlab.shadow.prover.core.ccprovers.CognitiveCalculusProver;
import org.rairlab.shadow.prover.representations.cnf.Clause;
import org.rairlab.shadow.prover.representations.value.Value;
import org.rairlab.shadow.prover.representations.value.Variable;
import org.rairlab.shadow.prover.sandboxes.Sandbox;
import org.rairlab.shadow.prover.utils.*;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by naveensundarg on 4/9/16.
 */
public class AnswerExtractionTests {


    Prover prover;
    Map<Problem, Pair<Clause, Clause>> used;
    AnswerExtractionTests(){

        prover = new CognitiveCalculusProver();
    }

    @DataProvider(name="testsProvider")
    public Object[][] completenessTestsProvider() throws Reader.ParsingException {

        List<Problem>tests = ProblemReader.readFrom(Sandbox.class.getResourceAsStream("../debug.clj"));
        Object[][] cases =  new Object[tests.size()][2];

        for(int  i = 0; i < tests.size(); i++){

            Problem test = tests.get(i);

            cases[i][0] =  test;
            cases[i][1] = test.getAnswerVariables().isPresent();

        }


        return cases;

    }



    @Test(dataProvider = "testsProvider")
    public void testCompleteness(Problem problem, boolean answerVariableGiven){

        Set<Map<Variable,Value>> answerMap = prover.proveAndGetMultipleBindings(problem.getAssumptions(), problem.getGoal(),
                problem.getAnswerVariables().get()).get().getRight();

        List<Variable> answerVariables = problem.getAnswerVariables().get();
        Set<List<Value>> expectedAnswers = problem.getAnswersExpected().get();

        Set<Map<Variable,Value>> expectedAnswersMap = Sets.newSet();

        for(List<Value> expectedAnswer: expectedAnswers){

            expectedAnswersMap.add(makeMap(answerVariables, expectedAnswer));

        }

        Assert.assertEquals(answerMap, expectedAnswersMap);

    }


    private Map<Variable, Value> makeMap(List<Variable> variables, List<Value> values){

        Map<Variable, Value> answer = CollectionUtils.newMap();


        if(variables.size()!=values.size()){
            throw new AssertionError("Should be the same size: " + variables + " and " + values);
        }

        for(int i = 0; i< variables.size(); i++){

            answer.put(variables.get(i), values.get(i));

        }

        return answer;
    }
}
