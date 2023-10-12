package org.rairlab.shadow.prover;

import org.rairlab.shadow.prover.core.ccprovers.CognitiveCalculusProver;
import org.rairlab.shadow.prover.core.proof.Justification;
import org.rairlab.shadow.prover.utils.Problem;
import org.rairlab.shadow.prover.utils.ProblemReader;
import org.rairlab.shadow.prover.utils.Reader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import java.util.List;
import java.util.Optional;

/** The class containing the main method and the primary entrypoint of the ShadowProver application */
public final class Runner {

    /** The entrypoint to ShadowProver 
     * @param args an array containing a single string with the path to the problem file
    */
    public static void main(String[] args) {

        System.out.println("--------------- Starting ShadowProver --------------- ");

        // Grab filename from argument list
        if (args.length < 1) {
            System.out.println("Need to include filename containing problem.");
            return;
        }
        String fileName = args[0];

        // Read File
        FileInputStream fileStream;
        try {
            fileStream = new FileInputStream(fileName);
        } catch (FileNotFoundException e){
            e.printStackTrace();
            return;
        }
        
        // Files can contain multiple problems, extract all of them
        List<Problem> problems;
        try {
            problems = ProblemReader.readFrom(fileStream);
        } catch (Reader.ParsingException e) {
            e.printStackTrace();
            return;
        }

        CognitiveCalculusProver cognitiveCalculusProver = new CognitiveCalculusProver();
        
        //Find a proof for each problem using cognitiveCalculusProver else fail
        for (Problem problem : problems) {
            Optional<Justification> optionalJustification = cognitiveCalculusProver.prove(
                problem.getAssumptions(),
                problem.getGoal()
            );

            if(optionalJustification.isPresent()) {
                System.out.println(optionalJustification.get().toString());
            } else {
                System.out.println("FAILED");
            }
        }
    }
}
