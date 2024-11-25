public class storebuffer {
    public String tag;
    public instruction instruction;
    public boolean busy;
    public int address;
    public int V;
    public String Q;


    public storebuffer(int address, int V, boolean busy, String tag, String Q) {
        this.busy = busy;
        this.address = address;
        this.V = V;
        this.Q = Q;
    }

    public void deleteBuffer() {
        this.busy = false;
        this.address = 0;
        this.V = 0;
        this.Q = null;
    }
    
    public boolean isBusy() {
        return busy;
    }
    public int getAddress() {
        return address;
    }

    public int getV() {
        return V;
    }

    public String getQ() {
        return Q;
    }
    public void setBusy(boolean busy) {
        this.busy = busy;
    }
    public void setAddress(int address) {
        this.address = address;
    }

    public void setV(int V) {
        this.V = V;
    }
    public void setQ(String Q) {
        this.Q = Q;
    }

    @Override
    public String toString() {
        return "storeBuffer{" +
        "tag=" + tag +
                "address=" + address +
                ", V=" + V +
                ", busy=" + busy +
                ", Q='" + Q + '\'' +
                '}';
    }

    public boolean isempty() {
        if (this.busy == false) {
            return true;
        } else {
            return false;
        }
    }

    public storebuffer() {
        this.address = 0;
        this.V = 0;
        this.busy = false;
        this.Q = null;

    }

}
