package org.rairlab.shadow.prover.utils;

import org.rairlab.shadow.prover.representations.formula.Formula;
import org.rairlab.shadow.prover.representations.value.Value;
import org.rairlab.shadow.prover.representations.value.Variable;

import java.util.List;
import java.util.Optional;
import java.util.Set;


//DEADCODE everything related to answerVariable and getAnswersExpected is dead code
//it is used exclusively in AnswerExtractionTests but nothing is really done with them here

/** This class represents a proof search problem
 * @author naveensundarg
 * @date 4/13/16
 */
public class Problem {

    //Basic properties of the problem, given assumptions need to reach the goal with a proof.
    private final Set<Formula> assumptions;
    private final Formula goal;
    private final String name;
    private final String description;

    //TODO: not clear what these are -James
    private final Optional<List<Variable>> answerVariable;
    private final Optional<Set<List<Value>>> answerExpected;

    /**
     * Constructs a problem with the goal of finding a proof of the goal from the assumptions
     * @param name Name of the problem
     * @param description Description of the problem
     * @param assumptions The set of assumption formulae
     * @param goal The goal formulae
     */
    public Problem(String name, String description, Set<Formula> assumptions, Formula goal) {
        this.assumptions = assumptions;
        this.goal = goal;
        this.name = name;
        this.description = description;
        answerExpected = Optional.empty();
        answerVariable = Optional.empty();
    }

    // DEADCODE
    /**
     * Constructs a problem with the goal of finding a proof of the goal from the assumptions
     * Additionally takes ???
     * @param name Name of the problem
     * @param description Description of the problem
     * @param assumptions The set of assumption formulae
     * @param goal The goal formulae
     * @param answerVariables ???
     * @param expectedAnswers ???
     */
    public Problem(String name, String description, Set<Formula> assumptions, Formula goal,
                   List<Variable> answerVariables, Set<List<Value>> expectedAnswers) {
        this.assumptions = assumptions;
        this.goal = goal;
        this.name = name;
        this.description = description;
        this.answerExpected = Optional.of(expectedAnswers);
        this.answerVariable = Optional.of(answerVariables);
    }

    // DEADCODE
    public Optional<List<Variable>> getAnswerVariables() {
        return answerVariable;
    }

    // DEADCODE
    public Optional<Set<List<Value>>> getAnswersExpected() {
        return answerExpected;
    }

    public Set<Formula> getAssumptions() {
        return assumptions;
    }

    public Formula getGoal() {
        return goal;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) 
            return true;
        if (o == null || getClass() != o.getClass()) 
            return false;

        Problem problem = (Problem) o;
        if (!assumptions.equals(problem.assumptions)) 
            return false;
        return goal.equals(problem.goal);
    }

    @Override
    public int hashCode() {
        int result = assumptions.hashCode();
        result = 31 * result + goal.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Problem{" +
                "assumptions=" + assumptions +
                ", goal=" + goal +
                '}';
    }
}
