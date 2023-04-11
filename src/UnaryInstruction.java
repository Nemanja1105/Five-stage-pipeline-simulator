public class UnaryInstruction extends Instruction
{
    private Register operand;
   // private Register store;

    public UnaryInstruction(String name,int cyclesNeeded,String fullInsName,int insIndex,Register operand,Register store)
    {
        super(name,cyclesNeeded,fullInsName,insIndex);
        this.operand=operand;
        this.store=store;
    }

    public Register getOperand(){return this.operand;}
    public Register getStore(){return this.store;}

    @Override
    public boolean isOperandLockable()
    {
        return !operand.getLockStatus() && !store.getLockStatus();
    }

    @Override
    public void lockOperand()
    {
        operand.setLockStatus(true);
        store.setLockStatus(true);
        this.operand.setOwner(this);
        this.store.setOwner(this);
    }

    @Override
    public void unlockOperand()
    {
        operand.setLockStatus(false);
        store.setLockStatus(false);
        this.operand.setOwner(null);
        this.store.setOwner(null);
    }

    public void setOperand(Register value){this.operand=value;}
    public void setStore(Register value){this.store=value;}

}
