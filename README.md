
## ShadowProver
ShadowProver is a theorem prover (multi-modal logic with quantifiers). 

### Setup and Dependencies    
Explicit setup instructions are for Debian/Ubuntu but generalize to most platforms.
Clone the repository, be sure to use `--recurse-submodules` to ensure the snark theorem prover submodule is cloned.
```bash
git clone --recurse-submodules https://github.com/RAIRLab/ShadowProver.git
```
ShadowProver requires Java 8 and Maven.  
WARNING: ShadowProver has been known to not work on newer versions of Java. 
```bash
#Install Java 8, make sure you have the correct version on PATH
sudo apt install openjdk-8-jdk
#Install Maven
sudo apt install maven
```

### Building
Maven is used to build the prover to the `/target` directory.
```bash
mvn package
```

### Running
To run shaowprover ensure the `/target` directory has been built to, and the snark submodule has been cloned. From here, if on linux or mac, you may run the provided script in the project root to run ShadowProver on a problem file. Problem files and examples can be found in the `/problems/` directory, for basic examples see `/problems/tutorial`.
```bash
./run_shadowprover.sh [problem_file_path]
```
If on windows, you can invoke the executable directly via mvn (this is what the .sh file does for you).
```bash
mvn -q exec:java -Dexec.mainClass="org.rairlab.shadow.prover.Runner" -Dexec.args="[problem_file_path]"
``` 
Additionally a docker based python interface exists on Naveen's branch, which can be found [here](https://github.com/naveensundarg/prover).

### Under the Hood

ShadowProver is a novel multi-modal + extensional logic theorem prover that uses a technique called shadowing (the namesake of the prover) to achieve speed without sacrificing consistency in the system. For a detailed overview
see [the wiki](https://github.com/RAIRLab/ShadowProver/wiki/Old-Readme#under-the-hood).

