public class reservationstation {
    public instruction instruction;
    public int time;
    public boolean busy;
    public String opcode;
    public int Vj;
    public int Vk;
    public String Qj;
    public String Qk;  
    public String tag;

    public reservationstation(boolean busy,String opcode, int Vj, int Vk, String Qj, String Qk, String tag) {
        this.opcode = opcode;
        this.Vj = Vj;
        this.Vk = Vk;
        this.Qj = Qj;
        this.Qk = Qk;
        this.tag = tag;
        this.busy = busy;
    }

    public reservationstation() {
        this.opcode = null;
        this.Vj = 0;
        this.Vk = 0;
        this.Qj = null;
        this.Qk = null;
        this.tag = null;
        this.busy = false;
    }

    public void deleteStation() {
        this.time=0;
        this.opcode = null;
        this.Vj = 0;
        this.Vk = 0;
        this.Qj = null;
        this.Qk = null;
        this.busy = false;     
    }
    @Override
    public String toString() {
        return "Station{" +
                "opcode='" + opcode + '\'' +
                ", Vi=" + Vj +
                ", Vj=" + Vk +
                ", Qi='" + Qj + '\'' +
                ", Qj='" + Qk + '\'' +
                ", tag=" + tag +
                ", busy=" + busy +
                ", time=" + time +
              
                '}';
    }

    public boolean isempty() {
        if (this.busy == false) {
            return true;
        } else {
            return false;
        }
    }
}

