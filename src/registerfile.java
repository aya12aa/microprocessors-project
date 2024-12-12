public class registerfile {
    public static class Register {
        public String name; 
        public String Qi;
        public boolean busy;

        // Constructor 
        public Register(int index) {
            this.name = "F" + index; 
            this.Qi = "0";          
            this.busy = false;
        }

        // Getter and Setter 
        public String getName() {
            return name;
        }


        public String getQi() {
            return Qi;
        }

        public void setQi(String Qi) {
            this.Qi = Qi;
        }

        
        public boolean isBusy() {
            return busy;
        }

        public void setBusy(boolean busy) {
            this.busy = busy;
        }

        @Override
        public String toString() {
            return "Register{" +
                    "name='" + name + '\'' +
                    ", Qi='" + Qi + '\'' +
                    ", busy=" + busy + // Added busy status in toString
                    '}';
        }
    }

    public  Register[] registers;

    // Constructor 
    public registerfile() {
        this.registers = new Register[32];
        for (int i = 0; i < 32; i++) {
            registers[i] = new Register(i);
        }
    }

    public void updateQi(int index, String Qi) {
        if (index >= 0 && index < registers.length) {
            registers[index].setQi(Qi);
        } else {
            System.out.println("Invalid register index: " + index);
        }
    }

    
    public void setBusy(int index, boolean busy) {
        if (index >= 0 && index < registers.length) {
            registers[index].setBusy(busy);
        } else {
            System.out.println("Invalid register index: " + index);
        }
    }


    public Register getRegister(int index) {
        if (index >= 0 && index < registers.length) {
            return registers[index];
        } else {
            System.out.println("Invalid register index: " + index);
            return null;
        }
    }

    public void printRegisters() {
        for (Register reg : registers) {
            System.out.println(reg);
        }
    }
}
