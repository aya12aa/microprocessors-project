public class cache {
    public Cell[] cachearray;

    public class Cell {
        public int address;
        public int value;
        public boolean busy;
        public String tag;

        public Cell(int address, int value, int n) {
            this.address = address;
            this.value = value;
            this.busy = false;
            this.tag = n+"";
        }

    }

    public cache(int n) {
        this.cachearray = new Cell[n]; // Create an array of Cells with size n
        int initialValue = 1; // Default value for each Cell
        for (int i = 0; i < n; i++) {
            // Initialize each Cell with a unique index, default value, and index as the address
            this.cachearray[i] = new Cell(i, initialValue, i);
        }
    }
    
    public int read(int address) {
        for (int i = 0; i < cachearray.length; i++) {
            if (cachearray[i].address == address) {
                return cachearray[i].value;
            }
        }
        return -1;
    }

    public void write(int address, int value) {
        for (int i = 0; i < cachearray.length; i++) {
            if (cachearray[i].address == address) {
                cachearray[i].value = value;
            }
        }
    }

    public void makeBusy(int address) {
        for (int i = 0; i < cachearray.length; i++) {
            if (cachearray[i].address == address) {
                cachearray[i].busy = true;
            }
        }
    }

    public void makeNotBusy(int address) {
        for (int i = 0; i < cachearray.length; i++) {
            if (cachearray[i].address == address) {
                cachearray[i].busy = false;
            }
        }
    }

    public boolean isBusy(int address) {
        for (int i = 0; i < cachearray.length; i++) {
            if (cachearray[i].address == address) {
                return cachearray[i].busy;
            }
        }
        return false;
    }

    public Cell getCell(int address) {
        for (int i = 0; i < cachearray.length; i++) {
            if (cachearray[i].address == address) {
                return cachearray[i];
            }
        }
        return null;
    }

}
