import java.util.Scanner; 
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class tomasuloslogic {
    private int cycle = 0;
    // Instance variables for latency and buffer sizes
    int addLatency, subLatency, mulLatency, divLatency, ldLatency, sdLatency,pc , BNEZLatency;  // Latency for BNEZ (Branch Not Equal Zero)
    ;
    int numaddBuffers, nummulBuffers, numLoadBuffers, numStoreBuffers, cacheSize;
    boolean branch = false;

    // Arrays for reservation stations and buffers
    reservationstation[] addBuffers;
    reservationstation[] mulBuffers;
    loadbuffer[] loadBuffers;
    storebuffer[] storeBuffers;
    cache cache;
    registerfile registerFile = new registerfile();
    // List to hold instructions
    public ArrayList<instruction> program = new ArrayList<instruction>();
    public ArrayList<instruction> issued = new ArrayList<instruction>();
    public ArrayList<instruction> executed = new ArrayList<instruction>();
    public ArrayList<instruction> toBeWritten = new ArrayList<instruction>();
    //  read instructions from file
    public void readInstructions() {
        String file = "src/instructions.txt"; // File path (adjust as necessary)
        String line;

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            while ((line = bufferedReader.readLine()) != null) {
                String[] tokens = line.trim().split("\\s+");
                if (tokens.length < 2) {
                    System.out.println("Invalid instruction format: " + line);
                    continue;
                }

                String type = tokens[0];
                String i = tokens[1];

                try {
                    switch (type) {
                        case "L.D":
                        case "S.D":
                        case "BNEZ": {
                            if (tokens.length < 3) {
                                System.out.println("Invalid instruction format: " + line);
                                continue;
                            }
                            int value = Integer.parseInt(tokens[2]);
                            instruction inst = new instruction(type.valueOf(type.replace('.', '_')), i, value);
                            program.add(inst);
                            break;
                        }
                        case "ADDI":
                        case "SUBI":
                        case "DADDI":
                        case "DSUBI": {
                            if (tokens.length < 4) {
                                System.out.println("Invalid instruction format: " + line);
                                continue;
                            }
                            String j = tokens[2];
                            int value = Integer.parseInt(tokens[3]);
                            instruction inst = new instruction(type.valueOf(type), i, j, value);
                            program.add(inst);
                            break;
                        }
                        default: {
                            if (tokens.length < 4) {
                                System.out.println("Invalid instruction format: " + line);
                                continue;
                            }
                            String j = tokens[2];
                            String k = tokens[3];
                            instruction inst = new instruction(type.valueOf(type), i, j, k);
                            program.add(inst);
                            break;
                        }
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid numeric value in line: " + line);
                } catch (IllegalArgumentException e) {
                    System.out.println("Unknown instruction type: " + type + " in line: " + line);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading file '" + file + "': " + e.getMessage());
        }
    }

    //  take user inputs 
    public void getUserInputs() {
        readInstructions();
        try (Scanner sc = new Scanner(System.in)) {
            System.out.println("Enter latency of ADD : ");
            this.addLatency = sc.nextInt();
            System.out.println("Enter latency of SUB : ");
            this.subLatency = sc.nextInt();
            System.out.println("Enter latency of MUL : ");
            this.mulLatency = sc.nextInt();
            System.out.println("Enter latency of DIV : ");
            this.divLatency = sc.nextInt();
            System.out.println("Enter latency of LD : ");
            this.ldLatency = sc.nextInt();
            System.out.println("Enter latency of SD : ");
            this.sdLatency = sc.nextInt();
            System.out.println("Enter latency of BNEZ: ");
            this.BNEZLatency = sc.nextInt();
           System.out.println("Enter number of reservation stations of ADD : ");
            this.numaddBuffers = sc.nextInt();
            this.addBuffers = new reservationstation[numaddBuffers];
            for (int i = 0; i < numaddBuffers; i++) {
                addBuffers[i] = new reservationstation();
                addBuffers[i].tag = "A" + (i + 1);
            }

            System.out.println("Enter number of reservation stations of MUL: ");
            this.nummulBuffers = sc.nextInt();
            this.mulBuffers = new reservationstation[nummulBuffers];
            for (int i = 0; i < nummulBuffers; i++) {
                mulBuffers[i] = new reservationstation();
                mulBuffers[i].tag = "M" + (i + 1);
            }

            System.out.println("Enter number of reservation stations of LD: ");
            this.numLoadBuffers = sc.nextInt();
            this.loadBuffers = new loadbuffer[numLoadBuffers];
            for (int i = 0; i < numLoadBuffers; i++) {
                loadBuffers[i] = new loadbuffer();
            }

            System.out.println("Enter number of reservation stations of SD: ");
            this.numStoreBuffers = sc.nextInt();
            this.storeBuffers = new storebuffer[numStoreBuffers];
            for (int i = 0; i < numStoreBuffers; i++) {
                storeBuffers[i] = new storebuffer();
            }

            System.out.println("Enter size of cache: ");
            this.cacheSize = sc.nextInt();
            this.cache = new cache(cacheSize);
        } catch (Exception e) {
            System.out.println("Error processing user input: " + e.getMessage());
        }
    }
   
    public void printTomasuloSections() {
        printSection("Instructions", program);
        printSection("Issued", issued);
        printSection("Executing instructions", executed);
        printSection("To be written", toBeWritten);
        
        printBuffers("Add buffers", addBuffers);
        printBuffers("Mul buffers", mulBuffers);
        printBuffers("Load buffers", loadBuffers);
        printBuffers("Store buffers", storeBuffers);
        
        printRegisterFile();
    }
    
   
    private <T> void printSection(String sectionName, Iterable<T> items) {
        System.out.println(sectionName + ":");
        for (T item : items) {
            System.out.println(item);
        }
        System.out.println(); // Empty line for better readability
    }
    
    // Helper method to print the details of reservation buffers
    private void printBuffers(String sectionName, Object[] buffers) {
        System.out.println(sectionName + ":");
        for (Object buffer : buffers) {
            System.out.println(buffer);
        }
        System.out.println(); // Empty line for better readability
    }
 
private void printRegisterFile() {
    System.out.println("Register file:");
    // Iterate through the 'registers' array via the instance
    for (registerfile.Register reg : registerFile.registers) { 
        System.out.println(reg); // Uses the overridden toString() in Register
    }
    System.out.println(); // Empty line for better readability
}


private int determineLatency(String operation) {
    int operationLatency = 1; 

    switch (operation) {
        case "ADD.D":
        case "SUB.D":
            operationLatency = (operation.equals("ADD.D")) ? addLatency : subLatency;
            break;

        case "MUL.D":
        case "DIV.D":
            operationLatency = (operation.equals("MUL.D")) ? mulLatency : divLatency;
            break;

        case "L.D":
            operationLatency = ldLatency;
            break;

        case "S.D":
            operationLatency = sdLatency;
            break;

        default:
            break;
    }

    System.out.println("Latency for " + operation + ": " + operationLatency);
    return operationLatency;
}



public void issue() {
    if (pc >= program.size()) {
        System.out.println("No more instructions to issue.");
        return;
    }

    instruction currentInstruction = program.get(pc);

    if (branch) {
        System.out.println("Currently processing a branch; cannot issue new instructions.");
        return;
    }

    String operationType = currentInstruction.getType();
    boolean issuedflag = false;
    if (isArithmeticOperation(operationType)) {
        issuedflag = tryIssueToReservationStation(currentInstruction, addBuffers, operationType, addLatency, subLatency);
    }

    else if (isMultiplicativeOperation(operationType)) {
        issuedflag= tryIssueToReservationStation(currentInstruction, mulBuffers, operationType, mulLatency, divLatency);
    }
 
    else if (operationType.equals("L.D")) {
        issuedflag = tryIssueToBuffer(currentInstruction, loadBuffers, ldLatency, true);
    }
  
    else if (operationType.equals("S.D")) {
        issuedflag = tryIssueToBuffer(currentInstruction, storeBuffers, sdLatency, false);
    }
   
    else if (operationType.equals("BNEZ")) {
        issuedflag = handleBranchInstruction(currentInstruction);
    }

   
    if (issuedflag) {
        issued.add(currentInstruction);
        pc++;
        System.out.println("Instruction issued successfully: " + currentInstruction);
    } else {
        System.out.println("Failed to issue instruction: " + currentInstruction);
    }
    cycle++; 
}
private boolean isArithmeticOperation(String operationType) {
    return operationType.equals("ADD.D") || operationType.equals("SUB.D") ||
           operationType.equals("ADDI") || operationType.equals("SUBI") ||
           operationType.equals("DADD") || operationType.equals("DSUB") ||
           operationType.equals("DADDI") || operationType.equals("DSUBI");
}

private boolean isMultiplicativeOperation(String operationType) {
    return operationType.equals("MUL.D") || operationType.equals("DIV.D");
}
private boolean tryIssueToReservationStation(instruction inst, reservationstation[] buffers, 
                                             String operationType, int latency1, int latency2) {
    for (reservationstation rs : buffers) {
        if (!rs.isBusy()) {
            // Fetch operands and handle dependencies
            int firstIndex = extractRegisterIndex(inst.getJ());
            int secondIndex = extractRegisterIndex(inst.getK());
            int resultIndex = extractRegisterIndex(inst.getI());

            rs.Vj = fetchOperandValueOrTag(registerFile.registers[firstIndex], true, rs);
            rs.Vk = fetchOperandValueOrTag(registerFile.registers[secondIndex], false, rs);
            rs.opcode = operationType;
            rs.busy = true;
            rs.instruction = inst;
            rs.time = operationType.equals("ADD.D") ? latency1 : latency2;

            registerFile.registers[resultIndex].setBusy(true);
            registerFile.registers[resultIndex].setQi(rs.tag);

            inst.setIssue(cycle);
            inst.setTag(rs.tag);
            return true;
        }
    }
    return false;
}



private boolean tryIssueToBuffer(instruction inst, Object[] buffers, int latency, boolean isLoad) {
    for (Object buffer : buffers) {
        if (buffer instanceof loadbuffer && isLoad && !((loadbuffer) buffer).isBusy()) {
            loadbuffer lb = (loadbuffer) buffer;
            lb.address = Integer.parseInt(inst.getK());
            lb.instruction = inst;
            lb.busy = true;
            lb.time = latency;

         
            int resultIndex = extractRegisterIndex(inst.getI());
            registerFile.registers[resultIndex].setQi(lb.tag);
            registerFile.registers[resultIndex].setBusy(true);

            inst.setIssue(cycle);
            inst.setTag(lb.tag);
            return true;
        } else if (buffer instanceof storebuffer && !isLoad && !((storebuffer) buffer).isBusy()) {
            storebuffer sb = (storebuffer) buffer;
            sb.address = Integer.parseInt(inst.getK());
            sb.instruction = inst;
            sb.busy = true;
            sb.time = latency;

            int operandIndex = extractRegisterIndex(inst.getI());
            if (registerFile.registers[operandIndex].isBusy()) {
                sb.Q = registerFile.registers[operandIndex].getQi();
            } else {
                sb.V = Integer.parseInt(registerFile.registers[operandIndex].getQi());
            }

            inst.setIssue(cycle);
            inst.setTag(sb.tag);
            return true;
        }
    }
    return false;
}


private boolean handleBranchInstruction(instruction inst) {
    for (reservationstation rs : addBuffers) {
        if (!rs.isBusy()) {
            int firstIndex = extractRegisterIndex(inst.getI());

            rs.Vj = fetchOperandValueOrTag(registerFile.registers[firstIndex], true, rs);
            rs.opcode = inst.getType();
            rs.instruction = inst;
            rs.busy = true;
            rs.time = BNEZLatency;

            inst.setIssue(cycle);
            inst.setTag(rs.tag);
            branch = true;
            return true;
        }
    }
    return false;
}


private int fetchOperandValueOrTag(registerfile.Register reg, boolean isFirst, reservationstation rs) {
    if (reg.isBusy()) {
        if (isFirst) rs.Qj = reg.getQi();
        else rs.Qk = reg.getQi();
        return 0; 
    }
    return Integer.parseInt(reg.getQi());
}

private int extractRegisterIndex(String operand) {
    return Integer.parseInt(operand.substring(1)); 
}

    }
