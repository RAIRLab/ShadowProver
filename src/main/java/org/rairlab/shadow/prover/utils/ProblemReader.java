package org.rairlab.shadow.prover.utils;

import org.rairlab.shadow.prover.axiomsets.AxiomSet;
import org.rairlab.shadow.prover.representations.Phrase;
import org.rairlab.shadow.prover.sandboxes.Sandbox;
//import org.rairlab.shadow.prover.core.sortsystem.SortSystem;
import org.rairlab.shadow.prover.representations.formula.Formula;
import org.rairlab.shadow.prover.representations.value.Value;
import org.rairlab.shadow.prover.representations.value.Variable;
import org.apache.commons.lang3.NotImplementedException;

import us.bpsm.edn.Keyword;
import us.bpsm.edn.parser.Parseable;
import us.bpsm.edn.parser.Parser;
import us.bpsm.edn.parser.Parsers;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static us.bpsm.edn.parser.Parsers.defaultConfiguration;

/** Static class for converting edn problem files (See examples)  
 * into {@link org.rairlab.shadow.prover.utils.Problem} instances.
 * @author naveensundarg 
 * @date 7/23/16
 */
public class ProblemReader {

    //Keywords in problem edn format descriptions
    private static final Keyword ASSUMPTIONS_KEY = Keyword.newKeyword("assumptions");
    private static final Keyword GOAL_KEY = Keyword.newKeyword("goal");
    private static final Keyword SORTSYSTEM_KEY = Keyword.newKeyword("sortsystem");
    private static final Keyword NAME_KEY = Keyword.newKeyword("name");
    private static final Keyword DESCRIPTION_KEY = Keyword.newKeyword("description");
    private static final Keyword ANSWER_VARIABLES = Keyword.newKeyword("answer-variables");
    private static final Keyword ANSWERS_EXPECTED = Keyword.newKeyword("answers-expected");
    private static final Keyword INPUT = Keyword.newKeyword("input");
    private static final Keyword OUTPUT = Keyword.newKeyword("output");

    /**
     * Converts an input stream of raw problem content into a list of problems.
     * @param inputStream An inputStream containing bytes that can be interpreted as valid problems.
     * @return The list of all problems that were parsed from the inputStream
     * @throws Reader.ParsingException if the inputStream is unable to be parsed
     */
    public static List<Problem> readFrom(InputStream inputStream) throws Reader.ParsingException {
        Parseable parsable = Parsers.newParseable(new InputStreamReader(inputStream));
        Parser parser = Parsers.newParser(Parsers.defaultConfiguration());

        List<Problem> problems = CollectionUtils.newEmptyList();
        Object problemDesc = parser.nextValue(parsable);

        while (problemDesc != Parser.END_OF_INPUT) {
            Problem currentProblem = ProblemReader.buildProblem((Map<?, ?>)problemDesc);
            problems.add(currentProblem);
            problemDesc = parser.nextValue(parsable);
        }
        return problems;
    }

    /**
     * Takes a problem file and gives the list of {@link org.rairlab.shadow.prover.utils.Problem} objects
     * it contains.
     * @param path An inputStream containing bytes that can be interpreted as valid problems.
     * @return The list of all {@link org.rairlab.shadow.prover.utils.Problem} that were parsed from the file
     * @throws Reader.ParsingException If the file content is unable to be parsed (Bad formatting etc)
     * @throws FileNotFoundException If the file is not found.
     */
    public static List<Problem> readFrom(String path) throws Reader.ParsingException, FileNotFoundException {
        return readFrom(new FileInputStream(path));
    }
    
    //TODO Re-used from Problem should consolodate with generics.
    /** Converts an problem in an input stream into a list of DPLChunk Problems.
     * @param inputStream
     * @return a list of DPLChunk Problems.
     * @throws Reader.ParsingException
     */
    public static List<DPLChunk> readDPLChunkFrom(InputStream inputStream) throws Reader.ParsingException {
        Parseable pbr = Parsers.newParseable(new InputStreamReader(inputStream));
        Parser p = Parsers.newParser(defaultConfiguration());
        List<DPLChunk> problems = CollectionUtils.newEmptyList();
        Object problemDesc = p.nextValue(pbr);
        while (problemDesc != Parser.END_OF_INPUT) {
            problems.add(buildChunk((Map<?, ?>) problemDesc));
            problemDesc = p.nextValue(pbr);
        }
        return problems;
    }


    private static List<Variable> readVariableList(List<?> lst) throws Reader.ParsingException {
        List<Variable> vars = lst.stream().map(x -> {
            try {
                return (Variable) Reader.readLogicValue(x);
            } catch (Reader.ParsingException e) {
                return null;
            }
        }).collect(Collectors.toList());

        if (vars.stream().anyMatch(Objects::isNull)) {
            throw new Reader.ParsingException("List has invalid variables: " + lst);
        }
        return vars;
    }

    private static List<Value> readValueList(List<?> lst) throws Reader.ParsingException {

        List<Value> vars = lst.stream().map(x -> {
            try {
                return Reader.readLogicValue(x);
            } catch (Reader.ParsingException e) {
                return null;
            }
        }).collect(Collectors.toList());

        if (vars.stream().anyMatch(Objects::isNull)) {

            throw new Reader.ParsingException("List has invalid values: " + lst);
        }
        return vars;
    }

    //DEADCODE, does not appear DPLChunk builder or its associated work is done
    @SuppressWarnings({"rawtypes","unchecked"})
    private static DPLChunk buildChunk(Map<?, ?> map) throws Reader.ParsingException {
        Set<Formula> assumptions = readAssumptions(map.get(ASSUMPTIONS_KEY));
        Formula goal = Reader.readFormula(map.get(OUTPUT));
        Phrase phrase = Reader.readPhrase(map.get(INPUT));
        //DEADCODE SortSystem has never been implemented see representations/sorts/sort.java
        if (map.containsKey(SORTSYSTEM_KEY)) {
            //exTODO: Create a sorted problem
            //exTODO: Define the class

            //SortSystem sortSystem = SortSystem.buildFrom((Map<?, ?>) map.get(SORTSYSTEM_KEY));
            throw new NotImplementedException("buildChunk");
        } else {
            if (map.containsKey(ANSWERS_EXPECTED) && map.containsKey(ANSWER_VARIABLES)) {
               /*  Set<List<Value>> expectedAnswers = ((List<?>)map.get(ANSWERS_EXPECTED))
                        .stream().
                        map(x -> {
                            try {
                                return readValueList((List<?>) x);
                            } catch (Reader.ParsingException e) {
                                return null;
                            }
                        }).collect(Collectors.toSet()); */
                return new DPLChunk(((Map) map).getOrDefault(NAME_KEY, "").toString(),
                        ((Map) map).getOrDefault(DESCRIPTION_KEY, "").toString(),
                        assumptions, phrase, goal);
            } else {
                return new DPLChunk(((Map) map).getOrDefault(NAME_KEY, "").toString(), ((Map) map).getOrDefault(DESCRIPTION_KEY, "").toString(), assumptions, phrase, goal);
            }
        }
    }

    /**
     * Creates a problem object from a dictionary created by the parser.
     * @param map a map from Keywords to  generated by the input file parser 
     * @return a problem object storing the problem from the dictionary 
     * @throws Reader.ParsingException
     */
    @SuppressWarnings({"rawtypes","unchecked"})
    private static Problem buildProblem(Map<?, ?> map) throws Reader.ParsingException {
        Set<Formula> assumptions = ProblemReader.readAssumptions(map.get(ASSUMPTIONS_KEY));
        Formula goal = Reader.readFormula(map.get(GOAL_KEY));
        
        //DEADCODE SortSystem has never been implemented see representations/sorts/sort.java
        if (map.containsKey(SORTSYSTEM_KEY)) {
            //exTODO: Create a sorted problem
            //exTODO: Define the class

            //SortSystem sortSystem = SortSystem.buildFrom((Map<?, ?>) map.get(SORTSYSTEM_KEY));
            throw new NotImplementedException("buildProblem");
        
        //DEADCODE ANSWERS_EXPECTED and ANSWER_VARIABLES just allows you to
        // store expected answers in the Problem object, it is never used except in a basic test to
        // check if you can read them. This is not a useful feature and should be removed.
        } else if (map.containsKey(ANSWERS_EXPECTED) && map.containsKey(ANSWER_VARIABLES)) {
            Set<List<Value>> expectedAnswers = ((List<?>)map.get(ANSWERS_EXPECTED))
                    .stream().
                    map(x -> {
                        try {
                            return readValueList((List<?>) x);
                        } catch (Reader.ParsingException e) {
                            return null;
                        }
                    }).collect(Collectors.toSet());
            return new Problem(((Map) map).getOrDefault(NAME_KEY, "").toString(),
                    ((Map) map).getOrDefault(DESCRIPTION_KEY, "").toString(),
                    assumptions, goal, readVariableList((List<?>) map.get(ANSWER_VARIABLES)),
                    expectedAnswers
            );
        } else { //This is all that this function is actually ever running
            return new Problem(
                ((Map) map).getOrDefault(NAME_KEY, "").toString(),
                ((Map) map).getOrDefault(DESCRIPTION_KEY, "").toString(),
                assumptions, goal
            );
        }
    }

    /**
     * @param parsedAssumptionContent a map containing parsed assumption content
     * @return A set of the formulae underlying these assumptions
     */
    private static Set<Formula> readAssumptions(Object parsedAssumptionContent) {
        if(parsedAssumptionContent instanceof Map<?, ?>){
            Map<?, ?> map = (Map<?, ?>) parsedAssumptionContent;
            return map.entrySet().stream().map(entry -> {
                try {
                    return Reader.readFormula(entry.getValue());
                } catch (Exception e) {
                    throw new AssertionError("Parsing Exception:" + e.getMessage());
                }
            }).collect(Collectors.toSet());
        } else {
            return (AxiomSet.getAxiomSetNamed(parsedAssumptionContent.toString()));
        }
    }

    //DEADCODE literally testing sandbox in production
    public static void main(String[] args) throws Reader.ParsingException {
        System.out.println(readFrom(Sandbox.class.getResourceAsStream("firstorder-completness-tests.clj")));
    }
}
