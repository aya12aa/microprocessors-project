public class instruction { 
    public String type; // Replaced enum with String
    public String i; 
    public String j; 
    public String k; 
    public int issue;
    public int executionComplete;
    public int writeResult;
    public String tag;

    // Updated constructors
    public instruction(String type, String i, String j, String k) {
        this.type = type;
        this.i = i;
        this.j = j;
        this.k = k;
    }

    public instruction(String type, String i, String j, int value) {
        this.type = type;
        this.i = i;
        this.j = j;
        this.k = String.valueOf(value);
    }

    public instruction(String type, String i, int value) {
        this.type = type;
        this.i = i;
        this.k = String.valueOf(value);
    }

    public instruction() {
    }

    // Getters and setters
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getI() {
        return i;
    }

    public void setI(String i) {
        this.i = i;
    }

    public String getJ() {
        return j;
    }

    public void setJ(String j) {
        this.j = j;
    }

    public String getK() {
        return k;
    }

    public void setK(String k) {
        this.k = k;
    }

    public int getIssue() {
        return issue;
    }

    public void setIssue(int issue) {
        this.issue = issue;
    }

    public int getExecutionComplete() {
        return executionComplete;
    }

    public void setExecutionComplete(int executionComplete) {
        this.executionComplete = executionComplete;
    }

    public int getWriteResult() {
        return writeResult;
    }

    public void setWriteResult(int writeResult) {
        this.writeResult = writeResult;
    }
    public void setTag(String tag) {
        this.tag = tag;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(type.replace('_', '.')).append(" ").append(i);

        if (type.equals("L_D") || type.equals("S_D")) {
            sb.append(" ").append(k);
        } else if (type.equals("ADDI") || type.equals("SUBI")) {
            sb.append(" ").append(j).append(" ").append(k);
        } else {
            sb.append(" ").append(j).append(" ").append(k);
        }

        return sb.toString().trim();
    }
}
