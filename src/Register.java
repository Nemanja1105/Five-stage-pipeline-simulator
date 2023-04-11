public class Register
{
    private String name;
    private boolean lock=false;

    private Instruction owner;

    public Register(String name)
    {
        this.name=name;
    }

    public String getName(){return this.name;}
    public void setName(String value){this.name=value;}
    public Instruction getOwner(){return this.owner;}

    public boolean getLockStatus(){return this.lock;}
    public void setLockStatus(boolean value){this.lock=value;}
    public void setOwner(Instruction value){this.owner=value;}


    @Override
    public String toString()
    {
        return this.name;
    }

    @Override
    public boolean equals(Object other)
    {
        if(!(other instanceof Register))
            return false;
        var tmp=(Register)other;
        return this.name.equals(((Register) other).name);
    }
}
