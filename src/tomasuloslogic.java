import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
public class tomasuloslogic {
   
    public ArrayList<instruction> program = new ArrayList<instruction>();
    ArrayList<instruction> issued = new ArrayList<instruction>();
    ArrayList<instruction> executed = new ArrayList<instruction>();
    ArrayList<instruction> toBeWritten = new ArrayList<instruction>();

    int cycle ;
    int ADDILatency,BNEZLatency,pc,addLatency ,subLatency ,mulLatency,divLatency,ldLatency,sdLatency,numaddBuffers,nummulBuffers,numLoadBuffers,numStoreBuffers,cacheSize ;
  
    reservationstation[] addBuffers;
    reservationstation[] mulBuffers;
    loadbuffer[] loadBuffers;
    storebuffer[] storeBuffers;
    cache cache;
    registerfile registerFile = new registerfile();
    boolean branch = false;
    ArrayList<reservationstation> toBeDeleted = new ArrayList<reservationstation>();

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

    public tomasuloslogic() {
        // Read instructions from the file
        readInstructions();
    
        // Create scanner to read user input
        Scanner sc = new Scanner(System.in);
    
        // Use helper method to read latency values with validation
        this.addLatency = readLatency(sc, "ADD");
        this.subLatency = readLatency(sc, "SUB");
        this.mulLatency = readLatency(sc, "MUL");
        this.divLatency = readLatency(sc, "DIV");
        this.ldLatency = readLatency(sc, "LD");
        this.sdLatency = readLatency(sc, "SD");
    
        // Use helper method to read the number of reservation stations with validation
        this.numaddBuffers = readNumBuffers(sc, "ADD");
        this.addBuffers = new reservationstation[this.numaddBuffers];
        initializeReservationStations(this.addBuffers, "A");
    
        this.nummulBuffers = readNumBuffers(sc, "MUL");
        this.mulBuffers = new reservationstation[this.nummulBuffers];
        initializeReservationStations(this.mulBuffers, "M");
    
        this.numLoadBuffers = readNumBuffers(sc, "LD");
        this.loadBuffers = new loadbuffer[this.numLoadBuffers];
        initializeLoadStoreBuffers(this.loadBuffers, "L");
    
        this.numStoreBuffers = readNumBuffers(sc, "SD");
        this.storeBuffers = new storebuffer[this.numStoreBuffers];
        initializeLoadStoreBuffers(this.storeBuffers, "S");
    
        // Read and initialize the cache size with validation
        System.out.println("Enter the size of cache: ");
        this.cacheSize = sc.nextInt();
        if (this.cacheSize <= 0) {
            System.out.println("Invalid cache size. Using default cache size: 128.");
            this.cacheSize = 128; // Default cache size in case of invalid input
        }
        this.cache = new cache(this.cacheSize);
    
        // Close the scanner after use
        sc.close();
    }
    
    // Helper method to read latency values with validation
    private int readLatency(Scanner sc, String instructionType) {
        int latency;
        do {
            System.out.println("Enter the latency of " + instructionType + " instruction: ");
            while (!sc.hasNextInt()) {
                System.out.println("Invalid input. Please enter an integer for the latency of " + instructionType + " instruction.");
                sc.next(); // Consume the invalid input
            }
            latency = sc.nextInt();
        } while (latency <= 0); // Ensure positive latency
        return latency;
    }
    
    // Helper method to read the number of buffers with validation
    private int readNumBuffers(Scanner sc, String bufferType) {
        int numBuffers;
        do {
            System.out.println("Enter the number of " + bufferType + " reservation stations: ");
            while (!sc.hasNextInt()) {
                System.out.println("Invalid input. Please enter an integer for the number of " + bufferType + " reservation stations.");
                sc.next(); // Consume the invalid input
            }
            numBuffers = sc.nextInt();
        } while (numBuffers <= 0); // Ensure positive buffer count
        return numBuffers;
    }
    
    // Helper method to initialize reservation stations
    private void initializeReservationStations(reservationstation[] buffers, String prefix) {
        for (int i = 0; i < buffers.length; i++) {
            buffers[i] = new reservationstation();
            buffers[i].tag = prefix + (i + 1);
        }
    }
    
    // Helper method to initialize load/store buffers
    private void initializeLoadStoreBuffers(loadbuffer[] buffers, String prefix) {
        for (int i = 0; i < buffers.length; i++) {
            buffers[i] = new loadbuffer();
            buffers[i].tag = prefix + (i + 1);
        }
    }
    
    // Helper method to initialize store buffers
    private void initializeLoadStoreBuffers(storebuffer[] buffers, String prefix) {
        for (int i = 0; i < buffers.length; i++) {
            buffers[i] = new storebuffer();
            buffers[i].tag = prefix + (i + 1);
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
private boolean LoadInstorder(instruction s) {
    int issue = s.issue;
    int loadAddress = Integer.parseInt(s.getK());

    for (storebuffer buffer : storeBuffers) {
        if (buffer.isBusy() && buffer.instruction.issue < issue) {
    
            if (buffer.time > 0) {
                return false; 
            }
            if (buffer.address == loadAddress) {
                return false;
            }
        }
    }
    return true; 
}
private boolean StoreInstorder(instruction s) {
    int issue = s.issue;
    int storeAddress = Integer.parseInt(s.getK());  
    System.out.println("STORE issue is " + issue);
    
    for (loadbuffer buffer : loadBuffers) {
        if (buffer.isBusy() && buffer.instruction.issue < issue) {
            System.out.println("LOAD buffer issue is " + buffer.instruction.issue);


            if (buffer.time > 0) {
                return false;  
            }

     
            if (buffer.address == storeAddress) {
                System.out.println("STORE and LOAD address conflict detected");
                return false; 
            }
        }
    }

    return true;  
}
private boolean isthebufferempty() {
 
    List<Object> allBuffers = new ArrayList<>();
    allBuffers.addAll(Arrays.asList(addBuffers));
    allBuffers.addAll(Arrays.asList(mulBuffers));
    allBuffers.addAll(Arrays.asList(loadBuffers));
    allBuffers.addAll(Arrays.asList(storeBuffers));

    for (Object buffer : allBuffers) {
        if (buffer instanceof reservationstation) {
            if (!((reservationstation) buffer).isempty()) {
                return false; // Found a non-empty buffer
            }
        } else if (buffer instanceof loadbuffer) {
            if (!((loadbuffer) buffer).isempty()) {
                return false;
            }
        } else if (buffer instanceof storebuffer) {
            if (!((storebuffer) buffer).isempty()) {
                return false;
            }
        }
    }
    return true;
}
private void realexecution(reservationstation s, instruction inst) { 
    if (s.Qj != null || s.Qk != null) {  
        System.out.println("Operands not ready " + s.opcode);
        return;
    }
    if (s.opcode.equals("ADD.D")) {
        inst.value = s.Vj + s.Vk;
    }
    else if (s.opcode.equals("SUB.D")) {
        inst.value = s.Vj - s.Vk;
    }
    else if (s.opcode.equals("ADDI")) {
        inst.value = s.Vj + s.Vk;
    }
    else if (s.opcode.equals("SUBI")) {
        inst.value = s.Vj - s.Vk;
    }
    else if (s.opcode.equals("DADD")) {
        inst.value = s.Vj + s.Vk;
    }
    else if (s.opcode.equals("DSUB")) {
        inst.value = s.Vj - s.Vk;
    }
    else if (s.opcode.equals("DADDI")) {
        inst.value = s.Vj + s.Vk;
    }
    else if (s.opcode.equals("DSUBI")) {
        inst.value = s.Vj - s.Vk;
    }
    else if (s.opcode.equals("DIV.D")) {
        if (s.Vj == 0) {
            System.out.println("Division by zero error in DIV.D operation.");
            return; 
        }
        inst.value = s.Vj / s.Vk;
    }
    else if (s.opcode.equals("MUL.D")) {
        inst.value = s.Vj * s.Vk;
    }
    else if (s.opcode.equals("BNEZ")) {
        if (s.Vj != 0) {
            isbranchTaken(0);
            System.out.println("taken");
            branch = false;
           
        } else {
            System.out.println("taken");
            branch = false;
        
        }
    } else {
        System.out.println("Unknown operation: " + s.opcode);
    }
}
private void isbranchTaken(int address) {
    if (address < 0 || address >= program.size()) {
        System.out.println("Invalid address. Cannot process branch.");
        return;
    }
    int i = address;
    boolean branchFound = false;
    while (i < program.size() && !program.get(i).type.equals("BNEZ")) {
        System.out.println("Adding instruction " + program.get(i).type);

        instruction inst = program.get(i);
        instruction newInst;
        if (inst.type.equals("L.D") || inst.type.equals("S.D")) {
            newInst = new instruction(inst.type, inst.i, inst.value);
        } else if (inst.type.equals("ADDI") || inst.type.equals("SUBI") || inst.type.equals("DADDI") || inst.type.equals("DSUBI")) {
            newInst = new instruction(inst.type, inst.i, inst.j, inst.k);
        } else if (inst.type.equals("ADD.D") || inst.type.equals("SUB.D") || inst.type.equals("DADD") || inst.type.equals("DSUB")) {
            newInst = new instruction(inst.type, inst.i, inst.j, inst.k);
        } else {

            newInst = new instruction(inst.type, inst.i, inst.j, inst.k);
        }

        program.add(newInst);
        i++;  
    }
    if (i < program.size() && program.get(i).type.equals("BNEZ")) {
        instruction branch = new instruction(program.get(i).type, program.get(i).i, program.get(i).value);
        program.add(branch); 
        System.out.println("Branch added: " + branch.type);
    }
    branchFound = true;
    if (!branchFound) {
        System.out.println("No branch found ");
    }
}
public void execute() {
    // Iterate through each buffer array
    processExecution(addBuffers);
    processExecution(mulBuffers);
    processExecution(loadBuffers);
    processExecution(storeBuffers);
}

private void processExecution(Object[] buffers) {
    for (Object buffer : buffers) {
        if (buffer instanceof reservationstation) {
            processReservationStation((reservationstation) buffer);
        } else if (buffer instanceof loadbuffer) {
            processLoadBuffer((loadbuffer) buffer);
        } else if (buffer instanceof storebuffer) {
            processStoreBuffer((storebuffer) buffer);
        }
    }
}

private void processReservationStation(reservationstation buffer) {
    if (buffer.time == -1) {
        processInstruction(buffer);
        buffer.time--;
        return;
    }

    if (buffer.busy) {
        if (buffer.time == 0) {
            handleExecution(buffer);
        } else {
            if (buffer.Qj == null && buffer.Qk == null) {
                instruction inst = buffer.instruction;
                buffer.time--;
                executed.add(inst);
            }
        }
    }
}

private void processLoadBuffer(loadbuffer buffer) {
    if (!buffer.busy) return;

    if (buffer.time == -1) {
        processInstruction(buffer);
        buffer.time--;
        return;
    }

    if (buffer.time == 0) {
        handleLoadExecution(buffer);
    } else if (shouldExecuteLoad(buffer)) {
        handleLoadExecution(buffer);
    } else {
        System.out.println("Waiting for the correct value for load instruction at address " + buffer.address);
    }
}

private void processStoreBuffer(storebuffer buffer) {
    if (!buffer.busy) return;

    if (buffer.time == -1) {
        buffer.time--;
        return;
    }

    if (buffer.time == 0) {
        handleStoreExecution(buffer);
    } else if (shouldExecuteStore(buffer)) {
        handleStoreExecution(buffer);
    } else {
        System.out.println("Waiting for the correct value for store instruction at address " + buffer.address);
    }
}

private void handleExecution(reservationstation buffer) {
    instruction inst = buffer.instruction;
    if (inst.tag.equals(buffer.tag)) {
        inst.executionComplete = cycle;
        inst.writeResult = cycle + 1;
        executed.add(inst);
        realexecution(buffer, inst);
        buffer.time--;
    }
}

private void handleLoadExecution(loadbuffer buffer) {
    instruction inst = buffer.instruction;
    if (inst.tag.equals(buffer.tag)) {
        inst.executionComplete = cycle;
        inst.writeResult = cycle + 1;
        inst.value = cache.read(buffer.address);
        buffer.time--;
    }
}

private boolean shouldExecuteLoad(loadbuffer buffer) {
    return (cache.isBusy(buffer.address) && cache.getCell(buffer.address).tag.equals(buffer.tag)) ||
           (!cache.isBusy(buffer.address) && LoadInstorder(buffer.instruction));
}

private void handleStoreExecution(storebuffer buffer) {
    instruction inst = buffer.instruction;
    if (inst.tag.equals(buffer.tag)) {
        inst.executionComplete = cycle;
        inst.writeResult = cycle + 1;
        cache.write(buffer.address, buffer.V);
        toBeWritten.add(inst);
        buffer.time--;
    }
}

private boolean shouldExecuteStore(storebuffer buffer) {
    return buffer.Q == null &&
           ((cache.isBusy(buffer.address) && cache.getCell(buffer.address).tag.equals(buffer.tag)) ||
            (!cache.isBusy(buffer.address) && StoreInstorder(buffer.instruction)));
}

private void processInstruction(Object buffer) {
    if (buffer instanceof reservationstation) {
        instruction inst = ((reservationstation) buffer).instruction;
        toBeWritten.add(inst);
    } else if (buffer instanceof loadbuffer) {
        instruction inst = ((loadbuffer) buffer).instruction;
        toBeWritten.add(inst);
    } else if (buffer instanceof storebuffer) {
        instruction inst = ((storebuffer) buffer).instruction;
        toBeWritten.add(inst);
    }
}

public void performWriteBack() {
    ArrayList<instruction> processedInstructions = new ArrayList<>();
    int earliestIssueCycle = findEarliestIssueCycle(toBeWritten);
    
    // Process instructions that match the earliest issue cycle and current cycle
    for (instruction currentInstruction : toBeWritten) {
        if (currentInstruction.writeResult == cycle && currentInstruction.issue == earliestIssueCycle) {
            processedInstructions.add(currentInstruction);
            toBeWritten.remove(currentInstruction);
            break;
        }
    }

    // Update writeResult for remaining instructions
    updateWriteResults(toBeWritten);

    // Handle the write-back for each reservation station type (ADD, MUL, LOAD, STORE)
    processReservationStations(addBuffers, processedInstructions);
    processReservationStations(mulBuffers, processedInstructions);
    processLoadBuffers(loadBuffers, processedInstructions);
    processStoreBuffers(storeBuffers, processedInstructions);

    // Clear the processed instructions list after write-back is complete
    processedInstructions.clear();
}

private int findEarliestIssueCycle(ArrayList<instruction> instructions) {
    int earliestCycle = Integer.MAX_VALUE;
    for (instruction inst : instructions) {
        if (inst.issue < earliestCycle) {
            earliestCycle = inst.issue;
        }
    }
    return earliestCycle;
}

private void updateWriteResults(ArrayList<instruction> instructions) {
    for (instruction inst : instructions) {
        inst.writeResult = cycle + 1;
    }
}

private void processReservationStations(reservationstation[] buffers, ArrayList<instruction> processedInstructions) {
    for (reservationstation buffer : buffers) {
        handleBufferWriteBack(buffer, processedInstructions);
    }
}

private void processLoadBuffers(loadbuffer[] buffers, ArrayList<instruction> processedInstructions) {
    for (loadbuffer buffer : buffers) {
        handleBufferWriteBack(buffer, processedInstructions);
    }
}

private void processStoreBuffers(storebuffer[] buffers, ArrayList<instruction> processedInstructions) {
    for (storebuffer buffer : buffers) {
        handleBufferWriteBack(buffer, processedInstructions);
    }
}

private void handleBufferWriteBack(Object buffer, ArrayList<instruction> processedInstructions) {
    if (buffer instanceof reservationstation) {
        reservationstation resBuffer = (reservationstation) buffer;
        handleReservationStationWriteBack(resBuffer, processedInstructions);
    } else if (buffer instanceof loadbuffer) {
        loadbuffer loadBuf = (loadbuffer) buffer;
        handleLoadBufferWriteBack(loadBuf);
    } else if (buffer instanceof storebuffer) {
        storebuffer storeBuf = (storebuffer) buffer;
        handleStoreBufferWriteBack(storeBuf, processedInstructions);
    }
}

private void handleReservationStationWriteBack(reservationstation buffer, ArrayList<instruction> processedInstructions) {
    if (buffer.time == -2) {
        buffer.deleteStation();
    } else if (buffer.time == -1) {
        updateRegisterFromBuffer(buffer);
    }
    processBufferOperands(buffer, processedInstructions);
}

private void handleLoadBufferWriteBack(loadbuffer buffer) {
    if (buffer.time == -2) {
        buffer.deleteBuffer();
    } else if (buffer.time == -1) {
        updateRegisterFromBuffer(buffer);
        cache.makeNotBusy(buffer.address);
    }
}

private void handleStoreBufferWriteBack(storebuffer buffer, ArrayList<instruction> processedInstructions) {
    if (buffer.time == -2) {
        buffer.deleteBuffer();
    } else if (buffer.time == -1) {
        cache.makeNotBusy(buffer.address);
    }
    processStoreBufferOperands(buffer, processedInstructions);
}

private void updateRegisterFromBuffer(Object buffer) {
    if (buffer instanceof reservationstation) {
        reservationstation resBuffer = (reservationstation) buffer;
        updateRegisterFromInstruction(resBuffer.instruction, resBuffer.tag);
    } else if (buffer instanceof loadbuffer) {
        loadbuffer loadBuf = (loadbuffer) buffer;
        updateRegisterFromInstruction(loadBuf.instruction, loadBuf.tag);
    }
}

private void updateRegisterFromInstruction(instruction inst, String tag) {
    int registerIndex = Integer.parseInt(inst.i.substring(1));
    if (registerFile.registers[registerIndex].Qi.equals(tag)) {
        registerFile.registers[registerIndex].Qi = inst.value + "";
        registerFile.registers[registerIndex].busy = false;
    }
}

private void processBufferOperands(reservationstation buffer, ArrayList<instruction> processedInstructions) {
    if (buffer.Qj != null) {
        for (instruction inst : processedInstructions) {
            if (inst.tag.equals(buffer.Qj)) {
                buffer.Vj = inst.value;
                buffer.Qj = null;
                if (buffer.Qk == null) {
                    buffer.time = determineLatency(buffer.opcode) - 1;
                }
            }
        }
    }
    if (buffer.Qk != null) {
        for (instruction inst : processedInstructions) {
            if (inst.tag.equals(buffer.Qk)) {
                buffer.Vk = inst.value;
                buffer.Qk = null;
                if (buffer.Qj == null) {
                    buffer.time = determineLatency(buffer.opcode) - 1;
                }
            }
        }
    }
}

private void processStoreBufferOperands(storebuffer buffer, ArrayList<instruction> processedInstructions) {
    if (buffer.Q != null) {
        for (instruction inst : processedInstructions) {
            if (inst.tag.equals(buffer.Q)) {
                buffer.V = inst.value;
                buffer.Q = null;
                buffer.time = sdLatency;
            }
        }
    }
}


public void executeSimulation() {
    int currentCycle = 1;  // Initialize cycle count
    System.out.println("Simulation Starts:");

    // Keep running the simulation while instructions are in progress
    while (true) {
        // Print current cycle information
        System.out.println("Cycle: " + currentCycle);
        System.out.println("Program Counter (PC): " + pc);
        
        // Perform each step of the Tomasulo algorithm
        issueInstructions();
        executeInstructions();
        handleWriteBack();
        displayTomasuloStatus();

        // Clear the executed and issued instructions for the next cycle
        clearIssuedAndExecutedInstructions();

        // Check if the program has finished execution
        if (isProgramFinished()) {
            break;
        }

        currentCycle++;  // Increment cycle for the next iteration
    }

    // Finalizing the simulation and displaying results
    System.out.println("--------------------------------------------------");
    int totalCycles = calculateTotalCycles();
    System.out.println("The program finished execution in " + totalCycles + " cycles.");

    // Print detailed information about each instruction's lifecycle
    printInstructionLifecycleDetails();
}

private void issueInstructions() {
    issue();
}

private void executeInstructions() {
    execute();
}

private void handleWriteBack() {
    performWriteBack();
}

private void displayTomasuloStatus() {
    printTomasuloSections();
}

private void clearIssuedAndExecutedInstructions() {
    executed.clear();
    issued.clear();
}

private boolean isProgramFinished() {
    try {
        program.get(pc);  // Try to access the next instruction
    } catch (Exception e) {
        // If there's an exception, it means we've run out of instructions in the program
        return areAllBuffersEmpty();  // Check if all reservation stations and buffers are empty
    }
    return false;
}

private boolean areAllBuffersEmpty() {
    return isthebufferempty();  // Check if buffers are empty
}

private int calculateTotalCycles() {
    int maxCycles = 0;
    for (instruction inst : program) {
        if (inst.writeResult > maxCycles) {
            maxCycles = inst.writeResult;
        }
    }
    return maxCycles;
}

private void printInstructionLifecycleDetails() {
    for (instruction inst : program) {
        System.out.println(inst.type + " The instruction was issued in cycle " 
                           + inst.issue + ", executed in cycle " + inst.executionComplete 
                           + ", and wrote back in cycle " + inst.writeResult);
    }
}

}
