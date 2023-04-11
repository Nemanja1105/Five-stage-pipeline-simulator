public abstract class Instruction {
    private String name;
    private int cyclesNeeded;
    private int currCycle = 0;

    protected Register store;

    private String fullInsName;

    private int insIndex;

    public Runnable action;



    public Instruction(String name, int cyclesNeeded, String fullInsName, int insIndex) {
        this.name = name;
        this.cyclesNeeded = cyclesNeeded;
        this.fullInsName = fullInsName;
        this.insIndex = insIndex;
    }

    public Instruction(String name, int cyclesNeeded, Runnable action, String fullInsName, int insIndex) {
        this(name, cyclesNeeded, fullInsName, insIndex);
        this.action = action;
    }

    public String getName() {
        return this.name;
    }

    public int getCyclesNeeded() {
        return this.cyclesNeeded;
    }

    public Runnable getAction() {
        return this.action;
    }

    public int getCurrCycle() {
        return this.currCycle;
    }

    public int getInsIndex() {
        return this.insIndex;
    }

    public String getFullInsName() {
        return this.fullInsName;
    }

    public abstract boolean isOperandLockable();

    public abstract void lockOperand();

    public abstract void unlockOperand();

    public void setName(String value) {
        this.name = value;
    }

    public void setCyclesNeeded(int value) {
        this.cyclesNeeded = value;
    }

    public void setAction(Runnable action) {
        this.action = action;
    }

    public void setCurrCycle(int currCycle) {
        this.currCycle = currCycle;
    }

    @Override
    public String toString() {
        return this.name + " " + this.cyclesNeeded;
    }
}
