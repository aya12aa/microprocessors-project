public class cache {
    public Cell[] cachearray;
    public memory[] memory;

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

    public class memory {
        public int address;
        public int value;
    
        public memory(int address, int value) {
            this.address = address;
            this.value = value;
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
    
    public boolean is_available(int address)
    {
        for (int i = 0; i < cachearray.length; i++) {
            if (cachearray[i].address == address) {
                return true;
            }
        }
        return false;
    }


    public int read(int address) {
        
        for (int i = 0; i < cachearray.length; i++) {
            if (cachearray[i].address == address) {
                return cachearray[i].value;
            }
        }
        //checking in memory for the value needed and add it also to the cache
        boolean place_available = false;
        int available_index = 0;

        for(int j = 0; j < cachearray.length;j++)
        {
            Cell x = cachearray[j];
            if (x.value == -1)
            {
                place_available = true;
                available_index = j;
                j = cachearray.length;//to terminate this loop as we get the index of the available index
            }
        }
        int t = -1;
        for (int i = 0; i < memory.length; i++) {
            if (memory[i].address == address) {
                Cell z = new Cell(address, memory[i].value, 0);
                if (place_available)
                {
                    cachearray[available_index]= z;
                }
                if(!place_available)
                {                    
                    cachearray[0]= z;
                }
                
                t = memory[i].value;
            }
        }
        if(t== -1)
        {
            return -1;//not found
        }
        else
        {
            return t;
        }
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
