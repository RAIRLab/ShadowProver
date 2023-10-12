package org.rairlab.shadow.prover.core;

import org.rairlab.shadow.prover.core.proof.Justification;
import org.rairlab.shadow.prover.core.proof.TrivialJustification;
import org.rairlab.shadow.prover.representations.formula.Formula;
import org.rairlab.shadow.prover.representations.value.Value;
import org.rairlab.shadow.prover.representations.value.Variable;
import org.rairlab.shadow.prover.utils.CollectionUtils;
import org.rairlab.shadow.prover.utils.Reader;
import org.rairlab.shadow.prover.utils.Sets;

import org.apache.commons.lang3.tuple.Pair;

import org.armedbear.lisp.Fixnum;
import org.armedbear.lisp.Interpreter;
import org.armedbear.lisp.LispObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by naveensundarg on 12/4/16.
 */
public class SnarkWrapper implements Prover {
    private static AtomicBoolean local = new AtomicBoolean(true);
    private static Interpreter interpreter;
    private static final SnarkWrapper INSTANCE;

    private SnarkWrapper() {}

    static {
        INSTANCE = new SnarkWrapper();
    }

    public static SnarkWrapper getInstance() {
        return INSTANCE;
    }

    static {
        if (local.get()) {
            // Load Snark into the LISP Interpreter
            interpreter = Interpreter.createInstance();
            LispObject result = interpreter.eval("(load \"snark/snark-system.lisp\")");
            result = interpreter.eval("(make-snark-system)");
            result = interpreter.eval("(load \"snark/snark-interface.lisp\")");
            result = interpreter.eval("(load \"snark/commons.lisp\")");
        } else {
            interpreter = null;
        }
    }

    public static boolean isLocal() {
        return local.get();
    }

    public static void setLocal(boolean local) {
        SnarkWrapper.local.set(local);
    }

    @Override
    public Optional<Integer> proofLength(Set<Formula> assumptions, Formula formula) {
        if (!local.get()) {
            System.out.println("[Warning] ShadowProver is not setup to compute proofLength remotely.");
            return null;
        }

        String assumptionsListString = assumptionsToString(assumptions);
        String goalString = removeWhiteSpace("'" + formula.toSnarkString());

        // Call the Lisp interpreter
        synchronized (interpreter) {
            LispObject resultVal = interpreter.eval(
                "(prove-from-axioms-and-get-complexity " + 
                assumptionsListString + 
                goalString + 
                " :verbose nil)"
            );
            return Optional.of(Fixnum.getInt(resultVal));
        }
    }

    /** Prove a formula from a set of assumptions
     * @param assumptions Set of formulas representing the assumption base
     * @param formula The formula to attempt to prove
     * @return Justification if found
     */
    public Optional<Justification> prove(Set<Formula> assumptions, Formula formula) {
        // Transform problem to string for input
        String assumptionsListString = assumptionsToString(assumptions);
        String goalString = removeWhiteSpace("'" + formula.toSnarkString());

        String resultString = "";
        if (local.get()) {
            // Use the internal LISP Interpreter
            synchronized (interpreter) {
                LispObject result = interpreter.eval(
                    "(prove-from-axioms-yes-no " +
                    assumptionsListString +
                    goalString +
                    " :verbose nil)"
                );
                resultString = result.toString();
            }
        } else {
            // Call external solver via HTTP
            try {
                String url = "http://localhost:8000/prove?assumptions=" +
                    URLEncoder.encode(assumptionsListString, "UTF-8") +
                    "&goal=" + URLEncoder.encode(goalString, "UTF-8");
                resultString += queryServer(url); 
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        if (resultString.equals("YES")) {
            return Optional.of(new TrivialJustification(assumptions, formula, ":FOLFromSnark"));
        }
        
        // Proof search failed
        return Optional.empty();
    }

    /** Attempt to prove a formula and generate a witness for a specific variable.
     * @param assumptions Set of formula that represents the assumption base
     * @param formula The formula to attempt to prove
     * @param variable The variable to attempt to bind or generate a witness for.
     * @return A valuation or binding for specified variable if found.
     */
    @Override
    public Optional<Value> proveAndGetBinding(Set<Formula> assumptions, Formula formula, Variable variable) {
        String assumptionsListString = assumptionsToString(assumptions);
        String goalString = removeWhiteSpace("'" + formula.toSnarkString());

        String resultString = "";
        if (local.get()) {
            // Use internal lisp interpreter
            synchronized (interpreter) {
                LispObject result = interpreter.eval("(prove-from-axioms-and-get-answer " + assumptionsListString + goalString + " '" + variable.toString() + " :verbose nil)");
                resultString = result.toString();
            }
        } else {
            // Call external solver
            try {
                String url = "http://localhost:8000/prove?assumptions=" + URLEncoder.encode(assumptionsListString, "UTF-8") + "&goal=" + URLEncoder.encode(goalString, "UTF-8");
                resultString += queryServer(url);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        if (resultString.isEmpty()) {
            return Optional.empty();
        } 

        try {
            return Optional.of(Reader.readLogicValueFromString(resultString));
        } catch (Reader.ParsingException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }


    /** Attempt to prove a formula and generate a witness for a specific list of variables.
     * @param assumptions Set of formula that represents the assumption base
     * @param formula The formula to attempt to prove
     * @param variables The list of variables to attempt to bind or generate a witnesses for.
     * @return A mapping that binds a variable to a specific valuation or constant.
     */
    @Override
    public Optional<Map<Variable, Value>> proveAndGetBindings(Set<Formula> assumptions, Formula formula, List<Variable> variables) {
        String varListString = "(" + variables.stream().map(Variable::toString).reduce(" ", (x, y) -> x + " " + y) + ")";
        String assumptionsListString = assumptionsToString(assumptions);
        String goalString = removeWhiteSpace("'" + formula.toSnarkString());

        String resultString = "";
        if (local.get()) {
            // Use internal lisp interpreter
            synchronized (interpreter) {
                LispObject result = interpreter.eval(
                    "(prove-from-axioms-and-get-answers " +
                    assumptionsListString +
                    goalString +
                    " '" + varListString +
                    " :verbose nil)"
                );
                resultString = result.toString();
            }
        } else {
            // Call external solver via HTTP
            try {
                String url = "http://localhost:8000/prove?assumptions=" + URLEncoder.encode(assumptionsListString, "UTF-8") + "&goal=" + URLEncoder.encode(goalString, "UTF-8");
                resultString = queryServer(url);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        if (resultString.isEmpty()) {
            return Optional.empty();
        } 

        if (resultString.toLowerCase().equals("nil")) {
            return Optional.of(CollectionUtils.newMap());
        }

        try {
            List<?> resultLst = (List<?>) Reader.readFromString(resultString);
            return getMappingFromResult(resultLst, variables);
        } catch (Reader.ParsingException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }


    /** Attempt to prove a formula and generate a witness for a specific list of variables.
     * This method attempts to do this multiple times, resulting in a set of bindings.
     * @param assumptions Set of formula that represents the assumption base
     * @param formula The formula to attempt to prove
     * @param variables The list of variables to attempt to bind or generate a witnesses for.
     * @return A pair that contains a justification and set of bindings if found.
     */
    @Override
    public Optional<Pair<Justification, Set<Map<Variable, Value>>>> proveAndGetMultipleBindings(Set<Formula> assumptions, Formula formula, List<Variable> variables) {
        String varListString = "(" + variables.stream().map(Variable::toString).reduce(" ", (x, y) -> x + " " + y) + ")";
        String assumptionsListString = assumptionsToString(assumptions);
        String goalString = removeWhiteSpace("'" + formula.toSnarkString());

        String resultString = "";
        if (local.get()) {
            synchronized (interpreter) {
                LispObject result = interpreter.eval(
                    "(prove-from-axioms-and-get-multiple-answers " +
                    assumptionsListString +
                    goalString +
                    " '" + varListString +
                    " :verbose nil)"
                );
                resultString = result.toString();
            }
        } else {
            try {
                String url = "http://localhost:8000/prove?assumptions=" + URLEncoder.encode(assumptionsListString, "UTF-8") + "&goal=" + URLEncoder.encode(goalString, "UTF-8");
                resultString += queryServer(url);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        if (resultString.isEmpty()) {
            return Optional.empty();
        } 

        if (resultString.toLowerCase().equals("(nil)")) {
            return Optional.of(Pair.of(null, CollectionUtils.newEmptySet()));
        }

        try {
            List<?> answerList = (List<?>) Reader.readFromString(resultString);
            Set<Map<Variable, Value>> answers = Sets.newSet();

            for (Object ans : answerList) {
                List<?> resultLst = (List<?>) ans;
                Optional<Map<Variable, Value>> varValueMapOpt = getMappingFromResult(resultLst, variables);
                if (!varValueMapOpt.isPresent()) {
                    return Optional.empty();
                }
                answers.add(varValueMapOpt.get());
            }

            Justification trivialJustification = TrivialJustification.trivial(assumptions, formula);
            return Optional.of(Pair.of(trivialJustification, answers));
        } catch (Reader.ParsingException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    private String removeWhiteSpace(String expression) {
        return expression
            .replace("\n", "")
            .replace("\r", "");
    }

    private String assumptionsToString(Set<Formula> assumptions) {
        return removeWhiteSpace(
            assumptions.stream()
            .map(Formula::toSnarkString)
            .reduce("'(", (x, y) -> x + " " + y) + ") "
        );
    }

    private Optional<Map<Variable, Value>> getMappingFromResult(List<?> resultLst, List<Variable> variables) throws Reader.ParsingException {
        if (resultLst.size() != variables.size()) {
            return Optional.empty();
        }

        Map<Variable, Value> variableValueMap = CollectionUtils.newMap();
        for (int i = 0; i < variables.size(); i++) {
            variableValueMap.put(variables.get(i), Reader.readLogicValue(resultLst.get(i)));
        }
        return Optional.of(variableValueMap);
    }

    private String queryServer(String url) {
        String resultString = "";
        try {
            URL proverURL = new URL(url);
            BufferedReader in = new BufferedReader(
                new InputStreamReader(proverURL.openStream())
            );
            String inputLine = null;
            while ((inputLine = in.readLine()) != null) {
                resultString += inputLine;
            }
            in.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultString;
    }
}
