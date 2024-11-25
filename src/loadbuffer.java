
public class loadbuffer {
    public String tag;
    public instruction instruction;
    public boolean busy;
    public int address;


    public loadbuffer(int address) {
        
        this.busy = true;
        this.address = address;
    }

    public void deleteBuffer() {     
        this.busy = false;
        this.address = 0;

    }

    public boolean isBusy() {
        return busy;
    }

    public int getAddress() {
        return address;
    }

    public void setBusy(boolean busy) {
        this.busy = busy;
    }
    public void setAddress(int address) {
        this.address = address;
    }


    public boolean isempty() {
        if (this.busy == false) {
            return true;
        } else {
            return false;
        }
    }

    public loadbuffer() {
        this.address = 0;
        this.busy = false;
        this.tag = null;
        
    }


    @Override
    public String toString() {
        return "loadBuffer{" +
                "address=" + address +
                ", busy=" + busy +
                ", tag=" + tag +
                '}';
    }
}
