import java.util.*;

public class Pipeline {
    private LinkedList<String> codeLines;

    private LinkedList<String> codeBuffer;
    private LinkedList<String> instructionDecodeBuffer = new LinkedList<>();
    private LinkedList<Instruction> executeBuffer = new LinkedList<>();
    private LinkedList<Instruction> memoryAccessBuffer = new LinkedList<>();
    private LinkedList<Instruction> writeBackBuffer = new LinkedList<>();

    private HashMap<String, Register> registers = new HashMap<>();

    private int numOfInsFinished = 0;
    private int numOfInstruction;

    private boolean operandForwarding = false;

    private boolean decodeBlocked = false;

    private int currCycle = 1;

    private Instruction criticalIns = null;

    private ArrayList<ArrayList<String>> printMatrix = new ArrayList<>();

    public Pipeline(LinkedList<String> codeLines, boolean operandForwarding) {
        this.codeLines = codeLines;
        this.codeBuffer = new LinkedList<>(codeLines);
        this.initializeRegister();
        this.numOfInstruction = codeLines.size();
        this.operandForwarding = operandForwarding;

        for (int i = 0; i < this.codeLines.size(); i++) {
            printMatrix.add(i, new ArrayList<>());
            printMatrix.get(i).add(codeLines.get(i));
        }
    }

    private void initializeRegister() {
        registers.put("RAX", new Register("RAX"));
        registers.put("RBX", new Register("RBX"));
        registers.put("RCX", new Register("RCX"));
        registers.put("RDX", new Register("RDX"));
        registers.put("RIP", new Register("RIP"));
        registers.put("RBI", new Register("RBI"));
        registers.put("RCI", new Register("RCI"));
        registers.put("RAI", new Register("RAI"));
    }

    private void printRow() {
        for (var arr : this.printMatrix)
            arr.add(this.currCycle, "");
    }

    public void execute() {

        this.printRow();
        instructionFetch();
        this.currCycle++;

        this.printRow();
        instructionDecode();
        this.currCycle++;

        this.printRow();
        instructionExecute();
        this.currCycle++;

        this.printRow();
        memoryAccess();
        this.currCycle++;

        this.printRow();
        writeBack();

        this.currCycle++;
        while (this.numOfInsFinished != this.numOfInstruction) {
            this.printRow();
            writeBack();
            this.currCycle++;

        }
        this.printTable();
    }

    private void printTable() {
        System.out.println();
        System.out.println();
        System.out.print(String.format("%-20s", "Ciklus"));
        for (int i = 1; i < this.currCycle; i++)
            System.out.print(String.format("%-5s", i));
        System.out.println("\n===================");
        for (var arr : printMatrix) {
            for (int i = 0; i < arr.size(); i++) {
                if (i == 0)
                    System.out.print(String.format("%-20s", arr.get(i)));
                else
                    System.out.print(String.format("%-5s", arr.get(i)));
            }
            System.out.println();
        }
    }


    private void instructionFetch() {
        if (!codeLines.isEmpty()) {
            String instruction = codeLines.peekFirst();
            instructionDecodeBuffer.addLast((instruction));
            // System.out.println("Instruction:" + instruction + " fetched");
            this.printMatrix.get(codeBuffer.indexOf(instruction)).add(this.currCycle, "IF");
            codeLines.pollFirst();
        }
    }

    private void instructionDecode() {
        if (!this.decodeBlocked && !instructionDecodeBuffer.isEmpty()) {
            String instructionCode = instructionDecodeBuffer.peekFirst();
            var instruction = decode(instructionCode);
            //  if (instruction.isOperandLockable()) {
            if (!checkHazardType(instruction)) {
                instruction.lockOperand();
                //System.out.println("Instruction:" + instruction.getName() + " decoded");
                this.printMatrix.get(instruction.getInsIndex()).add(this.currCycle, "ID");
                instructionDecodeBuffer.pollFirst();
                executeBuffer.addLast(instruction);
            } else {
                decodeBlocked = true;
                // this.checkHazardType(instruction);
                System.out.println("Instruction " + instruction.getName() + " blocked");
            }
        }
        instructionFetch();
    }

    private boolean checkHazardType(Instruction ins) {
        Instruction ins1 = null;
        if (ins instanceof BinaryInstruction) {
            var tmp = (BinaryInstruction) ins;//znaci da su registri zauzeti neko je vec postavljen za ownera
            if (tmp.getOperand1().getOwner() != null)
                ins1 = tmp.getOperand1().getOwner();
            else if (tmp.getOperand2().getOwner() != null)
                ins1 = tmp.getOperand2().getOwner();
            else if (tmp.getStore().getOwner() != null)
                ins1 = tmp.getStore().getOwner();
            if(ins1!=null) {
                if (ins1.store.getLockStatus() && (tmp.getOperand1().equals(ins1.store) || tmp.getOperand2().equals(ins1.store))) {
                    System.out.println("RAW Hazard prevent!!");
                    return true;
                } else if (ins1.store.getLockStatus() && ins.store.getLockStatus() && ins1.store.equals(ins.store)) {
                    System.out.println("WAW Hazard prevent!!");
                    return true;
                }
            }
        }
        else {
            var tmp = (UnaryInstruction) ins;
            if (tmp.getOperand().getOwner() != null)
                ins1 = tmp.getOperand().getOwner();
            else if (tmp.getStore().getOwner() != null)
                ins1 = tmp.getStore().getOwner();
            if(ins1!=null) {
                if (ins1.store.getLockStatus() && (tmp.getOperand().equals(ins1.store))) {
                    System.out.println("RAW Hazard prevent!!");
                    return true;
                } else if (ins.store.getLockStatus() && tmp.getOperand().getLockStatus() && ins.store.equals(ins1.store)) {
                    System.out.println("WAW Hazard prevent!!");
                    return true;
                }
            }
        }

        if (ins1 instanceof BinaryInstruction) {
            var tmp = (BinaryInstruction) ins1;
            if (ins.store.getLockStatus() && (tmp.getOperand1().equals(ins.store) || tmp.getOperand2().equals(ins.store))) {
                System.out.println("WAR Hazard prevent!!");
                return true;
            }
        } else {
            var tmp = (UnaryInstruction) ins1;
            if (ins.store.getLockStatus() && (tmp.getOperand().equals(ins.store))) {
                System.out.println("WAR Hazard prevent!!");
                return true;
            }
        }
        return false;
    }

    //obavezno refactor
    private Instruction decode(String instruction) {
        var tmp = instruction.split(" ");
        int position = codeBuffer.indexOf(instruction);
        if ("LOAD".equals(tmp[0]) || "STORE".equals(tmp[0])) {
            var reg1 = registers.get(tmp[1]);
            var reg2 = registers.get(tmp[2]);
            var op = new UnaryInstruction(tmp[0], 1, instruction, position, reg1, reg2);
            return op;
        } else if ("MUL".equals(tmp[0]) || "DIV".equals(tmp[0])) {
            var reg1 = registers.get(tmp[1]);
            var reg2 = registers.get(tmp[2]);
            var reg3 = registers.get(tmp[3]);
            var op = new BinaryInstruction(tmp[0], 2, instruction, position, reg1, reg2, reg3);
            return op;
        } else {
            var reg1 = registers.get(tmp[1]);
            var reg2 = registers.get(tmp[2]);
            var reg3 = registers.get(tmp[3]);
            var op = new BinaryInstruction(tmp[0], 1, instruction, position, reg1, reg2, reg3);
            return op;
        }
    }

    private void instructionExecute() {
        Instruction ins = null;
        if (!executeBuffer.isEmpty()) {
            ins = executeBuffer.peekFirst();
            // System.out.println("Instruction " + ins.getName() + " executing");
            this.printMatrix.get(ins.getInsIndex()).add(this.currCycle, "EX");
            if (ins.getCurrCycle() < ins.getCyclesNeeded() - 1)
                ins.setCurrCycle(ins.getCurrCycle() + 1);
            else {
                executeBuffer.pollFirst();
                ins.setCurrCycle(0);
                memoryAccessBuffer.addLast(ins);
            }

        }
        this.instructionDecode();
        if (ins != null && operandForwarding && instructionDecodeBuffer.peekFirst() != null && (ins.getCurrCycle() == 0) && this.decodeBlocked == true) {
            this.decodeBlocked = false;
            ins.unlockOperand();
            System.out.println("Operand forwarding: " + ins.getFullInsName() + " -> " + instructionDecodeBuffer.peekFirst());
        }
    }

    private void memoryAccess() {
        if (!memoryAccessBuffer.isEmpty()) {
            var ins = memoryAccessBuffer.peekFirst();
            //  System.out.println("Instruction " + ins.getName() + " MA");
            this.printMatrix.get(ins.getInsIndex()).add(this.currCycle, "MEM");
            if (ins instanceof UnaryInstruction) {
                if (ins.getCurrCycle() < 2)
                    ins.setCurrCycle(ins.getCurrCycle() + 1);
                else {
                    memoryAccessBuffer.pollFirst();
                    writeBackBuffer.addLast(ins);
                }
            } else {
                memoryAccessBuffer.pollFirst();
                writeBackBuffer.addLast(ins);
            }
        }
        instructionExecute();
    }

    private void writeBack() {
        Instruction ins = null;
        if (!writeBackBuffer.isEmpty()) {
            ins = writeBackBuffer.peekFirst();
            // System.out.println("Instruction " + ins.getName() + " writeBack");
            this.printMatrix.get(ins.getInsIndex()).add(this.currCycle, "WB");
            this.numOfInsFinished++;
            writeBackBuffer.pollFirst();
        }
        memoryAccess();
        if (ins != null && !operandForwarding) {
            this.decodeBlocked = false;
            ins.unlockOperand();
        }
    }

}
