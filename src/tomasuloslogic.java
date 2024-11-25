import java.util.Scanner; 
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class tomasuloslogic {

    // Instance variables for latency and buffer sizes
    int addLatency, subLatency, mulLatency, divLatency, ldLatency, sdLatency,pc;
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
    
   //  print the register file
private void printRegisterFile() {
    System.out.println("Register file:");
    // Iterate through the 'registers' array via the instance
    for (registerfile.Register reg : registerFile.registers) { 
        System.out.println(reg); // Uses the overridden toString() in Register
    }
    System.out.println(); // Empty line for better readability
}


    

    }
