package org.rairlab.shadow.prover;

import org.rairlab.shadow.prover.core.ccprovers.CognitiveCalculusProver;
import org.rairlab.shadow.prover.core.proof.Justification;
import org.rairlab.shadow.prover.representations.formula.Formula;
import org.rairlab.shadow.prover.utils.Problem;
import org.rairlab.shadow.prover.utils.ProblemReader;
import org.rairlab.shadow.prover.utils.Reader;
import py4j.GatewayServer;

import java.io.ByteArrayInputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.stream.Collectors;

/** The class representing an instance of a Py4J server
 * for interfacing ShadowProver with python. It contains
 * a main method as the entrypoint for starting the server
 * as well as methods for external use to be called by python
 */
public final class Py4JServer {
    /** The prover used by this Py4JServer */
    private CognitiveCalculusProver cognitiveCalculusProver;

    /** Constructs the server by initializing the prover */
    public Py4JServer(){
        cognitiveCalculusProver = new CognitiveCalculusProver();
    }

    /** Accessor for internal prover
     * @return The internal CognitiveCalculusProver
     */
    public CognitiveCalculusProver getCognitiveCalculusProver(){
        return cognitiveCalculusProver;
    }

    /** Entrypoint for starting the gateway P4J gateway */
    public static void main(String[] args) throws UnknownHostException {
        System.out.println("--------------- Starting GatewayServer --------------- ");
        InetAddress addr;
        System.setProperty("java.net.preferIPv4Stack", "true");
        addr = Inet4Address.getByName("0.0.0.0");
        GatewayServer server = new GatewayServer(new Py4JServer(), 25333, 25334, addr, addr, 0, 0, null);
        System.out.println("--------------- Started Py4J Gateway   --------------- ");
        server.start();
    }

    /** FOR EXTERNAL USE FROM PYTHON
     *  creates and returns an empty ArrayList with no set type
     *  @return An empty array list
     */
    @SuppressWarnings("rawtypes")
    public ArrayList newEmptyList(){
        return new ArrayList();
    }

    //DEADCODE this method is never actually called in any of the python examples
    /** FOR EXTERNAL USE FROM PYTHON
     *  Uses the CognitiveCalculusProver to find a proof given a file name
     *  @param fileString the name of a problem
     *  @return A string with the proof found, if not found returns "FAILED"
     *  @exception Reader.ParsingException if 
     */
    public String proveFromDescription(String fileString){
        try {
            List<Problem> problems = ProblemReader.readFrom(new ByteArrayInputStream(fileString.getBytes()));
            Problem problem = problems.get(0);
            Optional<Justification> optionalJustification =
                cognitiveCalculusProver.prove(problem.getAssumptions(), problem.getGoal());
            if(optionalJustification.isPresent()) {
                return optionalJustification.get().toString();
            } else {
                return "FAILED";
            }
        } catch (Reader.ParsingException e) {
            e.printStackTrace();
            return null;
        }
    }

    /*
        TODO: should really throw an expection rather than returning an error string as it is impossibe
        for the end user to know if the proof fails by java exception automatically 
    */ 
    /** FOR EXTERNAL USE FROM PYTHON
     *  Uses the CognitiveCalculusProver to find a proof given raw assumptions and a goal 
     *  (formatted as DCEC S-Expressions? see examples)
     *  @param assumptionsArrayList a list of assumption strings (formatted as DCEC SExprs?)
     *  @param goal the goal to prove from the assumptions (formatted as DCEC SExprs?)
     *  @return the output from the underlying CognitiveCalculusProver
     *  if a proof is found or "FAILED" if none is found.
     *  It will also return a raw error message string if any exceptions occur.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public String prove(ArrayList assumptionsArrayList, String goal) {

        boolean error;
        StringBuilder errorMessageBuilder = new StringBuilder("");
        ArrayList<String> assumptionsArrayStringList = new ArrayList<String>();

        //Runtime polymorphism :skull:
        assumptionsArrayList.forEach(x->{
            assumptionsArrayStringList.add(x.toString());
        });

        //Convert assumptions to Formula
        Set<Formula> assumptionsSet = assumptionsArrayStringList.stream().map(x->{
            try {
                return Reader.readFormulaFromString(x);
            } catch (Reader.ParsingException e) {
                errorMessageBuilder.append(e.getMessage());
                return null;
            }
        }).collect(Collectors.toSet());;

        error = assumptionsSet.stream().anyMatch(Objects::isNull);

        Formula goalFormula = null;
        try {
            goalFormula = Reader.readFormulaFromString(goal);
        } catch (Reader.ParsingException e) {
            errorMessageBuilder.append(e.getMessage());
            error = true;
        }

        if(error) {
            return errorMessageBuilder.toString();
        }

        Optional<Justification> optionalJustification = cognitiveCalculusProver.prove(assumptionsSet, goalFormula);

        if(optionalJustification.isPresent()) {
            return optionalJustification.get().toString();
        } else {
            return "FAILED";
        }
    }
}