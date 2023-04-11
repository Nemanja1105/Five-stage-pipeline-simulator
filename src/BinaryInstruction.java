public class BinaryInstruction extends Instruction
{
    private Register operand1;
    private Register operand2;
   // private Register store;

    public BinaryInstruction(String name,int cyclesNeeded,String fullInsName,int insIndex,Register operand1,Register operand2,Register store)
    {
        super(name,cyclesNeeded,fullInsName,insIndex);
        this.operand1=operand1;
        this.operand2=operand2;
        this.store=store;
    }

    public Register getOperand1(){return this.operand1;}
    public Register getOperand2(){return this.operand2;}
    public Register getStore(){return this.store;}

    public boolean isOperandLockable()
    {
        return !operand1.getLockStatus() && !operand2.getLockStatus() && !store.getLockStatus();
    }

    public void lockOperand()
    {
        operand1.setLockStatus(true);
        operand2.setLockStatus(true);
        store.setLockStatus(true);
        operand1.setOwner(this);
        operand2.setOwner(this);
        store.setOwner(this);
    }

    @Override
    public void unlockOperand()
    {
        operand1.setLockStatus(false);
        operand2.setLockStatus(false);
        store.setLockStatus(false);
        operand1.setOwner(null);
        operand2.setOwner(null);
        store.setOwner(null);
    }

    public void setOperand1(Register value){this.operand1=value;}
    public void setOperand2(Register value){this.operand2=value;}
    public void setStore(Register value){this.store=value;}




}
